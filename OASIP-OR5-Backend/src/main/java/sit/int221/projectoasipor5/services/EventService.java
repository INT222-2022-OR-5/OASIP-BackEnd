package sit.int221.projectoasipor5.services;

import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sit.int221.projectoasipor5.entities.EventCategory;
import sit.int221.projectoasipor5.entities.Role;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.exception.HandleExceptionBadRequest;
import sit.int221.projectoasipor5.exception.HandleExceptionForbidden;
import sit.int221.projectoasipor5.exception.OverlappedExceptionHandler;
import sit.int221.projectoasipor5.repositories.EventCategoryRepository;
import sit.int221.projectoasipor5.repositories.UserRepository;
import sit.int221.projectoasipor5.dto.Event.EventDTO;
import sit.int221.projectoasipor5.entities.Event;
import sit.int221.projectoasipor5.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EventService {
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    private final EventRepository repository;

    private final UserRepository userRepository;

    private final EventCategoryRepository categoryRepository;

    private EmailService emailService;

    //Get all Event
    public List<EventDTO> getAllEvent() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString().contains(Role.admin.name()));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            return this.listMapper.mapList(this.repository.findAll(Sort.by("eventStartTime").descending()), EventDTO.class, this.modelMapper);

        } else if (userLogin.getRole().equals(Role.student)) {
            return this.listMapper.mapList(this.repository.findByBookingEmail(userLogin.getEmail(), Sort.by("eventStartTime").descending()), EventDTO.class, this.modelMapper);

        } else if (userLogin.getRole().equals(Role.lecturer)) {
            List<Event> eventListByCategoryOwner = repository.findEventCategoryOwnerByEmail(userLogin.getEmail());
            return listMapper.mapList(eventListByCategoryOwner, EventDTO.class, modelMapper);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, userLogin.getEmail() + "is not owner of this event");
        }
    }

    //Get Event with id
    public EventDTO getEventById(Integer id) throws HandleExceptionForbidden {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            Event event = this.repository.findById(id).orElseThrow(() -> {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, id + " Does Not Exist !!!");
            });
            return this.modelMapper.map(event, EventDTO.class);
        } else if (userLogin.getRole().equals(Role.student)) {
            Event event = this.repository.findById(id).orElseThrow(() -> {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, id + " Does Not Exist !!!");
            });
            if (Objects.equals(event.getUser().getEmail(), userLogin.getEmail())) {
                return this.modelMapper.map(event, EventDTO.class);
            } else {
                throw new HandleExceptionForbidden("You are not allowed to access this event");
            }
        } else if (userLogin.getRole().equals(Role.lecturer)) {
            List<Event> eventListByCategoryOwner = repository.findEventCategoryOwnerByEmail(userLogin.getEmail());
            Event eveventListByCategoryOwnerent = this.repository.findById(id).orElseThrow(() -> {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, id + " Does Not Exist !!!");
            });
            if (eventListByCategoryOwner.contains(eveventListByCategoryOwnerent)) {
                return this.modelMapper.map(eveventListByCategoryOwnerent, EventDTO.class);
            }
            throw new HandleExceptionForbidden("You are not allowed to access this event");
        }
        return null;
    }

    //Add new Event
    public Event save(EventDTO newEvent) throws OverlappedExceptionHandler, HandleExceptionForbidden, HandleExceptionBadRequest {
        Date newEventStartTime = Date.from(newEvent.getEventStartTime());
        Date newEventEndTime = findEndDate(Date.from(newEvent.getEventStartTime()), newEvent.getEventDuration());
        List<EventDTO> eventList = getEventToCheckOverlap();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());

        if (userLogin != null) {
            if (userLogin.getRole().equals(Role.admin)) {
                checkOverlapCreate(newEvent, newEventStartTime, newEventEndTime, eventList);
                User addByAdmin = userRepository.findByEmail(newEvent.getBookingEmail());
                newEvent.setUserId(addByAdmin.getUserId());
                Event event = modelMapper.map(newEvent, Event.class);
                repository.saveAndFlush(event);
                sendEmail(newEvent, "Your appointment is confirmed.");
                return ResponseEntity.status(HttpStatus.OK).body(event).getBody();

            } else if (userLogin.getRole().equals(Role.student)) {
                if (Objects.equals(userLogin.getEmail(), newEvent.getBookingEmail())) {
                    checkOverlapCreate(newEvent, newEventStartTime, newEventEndTime, eventList);
                    newEvent.setUserId(userLogin.getUserId());
                    Event event = modelMapper.map(newEvent, Event.class);
                    repository.saveAndFlush(event);
                    sendEmail(newEvent ,"Your appointment is confirmed.");
                    return ResponseEntity.status(HttpStatus.OK).body(event).getBody();
                } else {
                    throw new HandleExceptionBadRequest("The booking email must be the same as the student's email");
                }
            }
        }
        checkOverlapCreate(newEvent, newEventStartTime, newEventEndTime, eventList);
        Event event = modelMapper.map(newEvent, Event.class);
        repository.saveAndFlush(event);
        sendEmail(newEvent, "Your appointment is confirmed.");
        return ResponseEntity.status(HttpStatus.OK).body(event).getBody();
    }

    private void sendEmail(EventDTO newEvent, String message) {
        int categoryId = Integer.parseInt(newEvent.getEventCategory().getId().toString());
        EventCategory eventCategory = categoryRepository.findById(categoryId).orElseThrow(()->new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Category Id: "+ categoryId + "Does Not Exist!"));
        ZonedDateTime time = ZonedDateTime.ofInstant(newEvent.getEventStartTime(), ZoneId.of("Asia/Bangkok"));
        String dateTime = time.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm"));
        String endTime = findEndTime(time, newEvent.getEventDuration()).toString().substring(11,16);
        String zone = time.format(DateTimeFormatter.ofPattern("(z)"));
        String subject = "[OASIP] " + eventCategory.getEventCategoryName() + " @ " + dateTime + " - " + endTime + " " + zone ;
        String body = message + "\n \n" +
                "Reply-to: noreply@intproj21.sit.kmutt.ac.th" + "\n" +
                "Booking Name: " + newEvent.getBookingName() + "\n" +
                "Event Category: " + eventCategory.getEventCategoryName() + "\n" +
                "When: " + dateTime + " - " + endTime + " " + zone  + "\n" +
                "Event Notes: " + newEvent.getEventNotes();
        emailService.sendEmail(newEvent.getBookingEmail() , subject , body);
    }

    public List<EventDTO> getEventToCheckOverlap(){
        return this.listMapper.mapList(this.repository.findAll(Sort.by("eventStartTime").descending()), EventDTO.class, this.modelMapper);
    }

    //Delete event with id
    public void deleteById(Integer id) throws HandleExceptionForbidden {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            repository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            id + " does not exist !!!"));
            repository.deleteById(id);
        } else if (userLogin.getRole().equals(Role.student)) {
            Event event = repository.findById(id).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            id + " does not exist !!!"));
            if (Objects.equals(event.getUser().getEmail(), userLogin.getEmail())) {
                repository.deleteById(id);
            } else {
                throw new HandleExceptionForbidden("You are not owner of this event");
            }
        } else {
            throw new HandleExceptionForbidden("You are not allowed to delete event");
        }
    }

    public Date findEndDate(Date date, Integer duration) {
        return new Date(date.getTime() + (duration * 60000 + 60000));
    }

    public ZonedDateTime findEndTime(ZonedDateTime eventStartTime, Integer duration) {
        ZonedDateTime EventEndTime = eventStartTime.plusMinutes(duration);
        return EventEndTime;
    }

    private Event mapEvent(Event existingEvent , Event updateEvent){
        existingEvent.setBookingEmail(updateEvent.getBookingEmail());
        existingEvent.setEventCategory(updateEvent.getEventCategory());
        existingEvent.setBookingName(updateEvent.getBookingName());
        existingEvent.setEventDuration(updateEvent.getEventDuration());
        existingEvent.setEventStartTime(updateEvent.getEventStartTime());
        existingEvent.setEventNotes(updateEvent.getEventNotes());
        return existingEvent;
    }

    private void checkOverlapCreate(EventDTO newEvent, Date newEventStartTime, Date newEventEndTime, List<EventDTO> eventList) throws OverlappedExceptionHandler {
        for (EventDTO eventDTO : eventList) {
            if (Objects.equals(newEvent.getEventCategory().getId(), eventDTO.getEventCategory().getId())) {
                Date eventStartTime = Date.from(eventDTO.getEventStartTime());
                Date eventEndTime = findEndDate(Date.from(eventDTO.getEventStartTime()), eventDTO.getEventDuration());
                checkIfTwoDateRanges(newEventStartTime, newEventEndTime, eventStartTime, eventEndTime);
            }
        }
    }

    public static void checkIfTwoDateRanges(Date newEventStartTime, Date newEventEndTime, Date eventStartTime, Date eventEndTime) throws OverlappedExceptionHandler {
        checkTimeOverlap(newEventStartTime, newEventEndTime, eventStartTime, eventEndTime);
    }

    public static void checkTimeOverlap(Date newEventStartTime, Date newEventEndTime, Date eventStartTime, Date eventEndTime) throws OverlappedExceptionHandler {
        if (newEventStartTime.before(eventStartTime) && newEventEndTime.after(eventStartTime) ||
                newEventStartTime.before(eventEndTime) && newEventEndTime.after(eventEndTime) ||
                newEventStartTime.before(eventStartTime) && newEventEndTime.after(eventEndTime) ||
                newEventStartTime.after(eventStartTime) && newEventEndTime.before(eventEndTime) ||
                newEventStartTime.equals(eventStartTime)) {
            throw new OverlappedExceptionHandler("Time is Overlapped");
        }
    }
}
