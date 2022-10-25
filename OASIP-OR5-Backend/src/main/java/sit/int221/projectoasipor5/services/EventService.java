package sit.int221.projectoasipor5.services;

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
        List<EventDTO> eventList = getAllEvent();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());

        if (userLogin.getRole().equals(Role.admin)) {
            for (EventDTO eventDTO : eventList) {
                if (Objects.equals(newEvent.getEventCategory().getId(), eventDTO.getEventCategory().getId())) {
                    Date eventStartTime = Date.from(eventDTO.getEventStartTime());
                    Date eventEndTime = findEndDate(Date.from(eventDTO.getEventStartTime()), eventDTO.getEventDuration());
                    if (newEventStartTime.before(eventStartTime) && newEventEndTime.after(eventStartTime) ||
                            newEventStartTime.before(eventEndTime) && newEventEndTime.after(eventEndTime) ||
                            newEventStartTime.before(eventStartTime) && newEventEndTime.after(eventEndTime) ||
                            newEventStartTime.after(eventStartTime) && newEventEndTime.before(eventEndTime) ||
                            newEventStartTime.equals(eventStartTime)) {
                        throw new OverlappedExceptionHandler("Time is Overlapped");
                    }
                }
            }

            User addByAdmin = userRepository.findByEmail(newEvent.getBookingEmail());
            newEvent.setUserId(addByAdmin.getUserId());
            Event e = modelMapper.map(newEvent, Event.class);
            repository.saveAndFlush(e);
            return ResponseEntity.status(HttpStatus.OK).body(e).getBody();

        } else if (userLogin.getRole().equals(Role.student)) {
            if (Objects.equals(userLogin.getEmail(), newEvent.getBookingEmail())) {
                for (EventDTO eventDTO : eventList) {
                    if (Objects.equals(newEvent.getEventCategory().getId(), eventDTO.getEventCategory().getId())) {
                        Date eventStartTime = Date.from(eventDTO.getEventStartTime());
                        Date eventEndTime = findEndDate(Date.from(eventDTO.getEventStartTime()), eventDTO.getEventDuration());
                        if (newEventStartTime.before(eventStartTime) && newEventEndTime.after(eventStartTime) ||
                                newEventStartTime.before(eventEndTime) && newEventEndTime.after(eventEndTime) ||
                                newEventStartTime.before(eventStartTime) && newEventEndTime.after(eventEndTime) ||
                                newEventStartTime.after(eventStartTime) && newEventEndTime.before(eventEndTime) ||
                                newEventStartTime.equals(eventStartTime)) {
                            throw new OverlappedExceptionHandler("Time is Overlapped");
                        }
                    }
                }
                newEvent.setUserId(userLogin.getUserId());
                Event e = modelMapper.map(newEvent, Event.class);
                repository.saveAndFlush(e);
                return ResponseEntity.status(HttpStatus.OK).body(e).getBody();
            } else {
                throw new HandleExceptionBadRequest("The booking email must be the same as the student's email");
            }
        } else {
            throw new HandleExceptionForbidden("You are not allowed to add event");
        }
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

    public List<EventDTO> getEventByCategoryId(EventCategory eventCategoryId) throws HandleExceptionForbidden {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            List<Event> eventByCategory = repository.findAllByEventCategoryOrderByEventCategoryDesc(eventCategoryId);
            return listMapper.mapList(eventByCategory, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.student)) {
            List<Event> eventByCategory = repository.
                    findAllByBookingEmailAndEventCategoryOrderByEventCategoryDesc(auth.getPrincipal().toString(), eventCategoryId);
            return listMapper.mapList(eventByCategory, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.lecturer)) {
            List<Integer> eventByCategory = userLogin.getEventCategories().stream().map(EventCategory::getId).collect(Collectors.toList());
            if (eventByCategory.contains(eventCategoryId.getId())) {
                List<Event> eventByCategoryId = repository.findAllByEventCategoryOrderByEventCategoryDesc(eventCategoryId);
                return listMapper.mapList(eventByCategoryId, EventDTO.class, modelMapper);
            } else {
                throw new HandleExceptionForbidden("You are not owner of this category");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, userLogin.getEmail() + "is not owner of this event");
        }
    }

    public List<EventDTO> getPastEvent(Instant instant) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            List<Event> pastEvent = repository.findAllByEventStartTimeBeforeOrderByEventStartTimeDesc(instant);
            return listMapper.mapList(pastEvent, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.student)) {
            List<Event> pastEvent =
                    repository.findAllByBookingEmailAndEventStartTimeBeforeOrderByEventStartTimeDesc(auth.getPrincipal().toString(), instant);
            return listMapper.mapList(pastEvent, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.lecturer)) {
            List<Integer> eventByCategory = userLogin.getEventCategories().stream().map(EventCategory::getId).collect(Collectors.toList());
            List<Event> pastEvent = repository.findAllByEventCategory_IdInAndEventStartTimeBeforeOrderByEventStartTimeDesc(eventByCategory, instant);
            return listMapper.mapList(pastEvent, EventDTO.class, modelMapper);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, userLogin.getEmail() + "is not owner of this event");
        }
    }

    public List<EventDTO> getUpcomingEvent(Instant instant) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            List<Event> pastEvent = repository.findAllByEventStartTimeAfterOrderByEventStartTimeAsc(instant);
            return listMapper.mapList(pastEvent, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.student)) {
            List<Event> pastEvent = repository.
                    findAllByBookingEmailAndEventStartTimeAfterOrderByEventStartTimeAsc(auth.getPrincipal().toString(), instant);
            return listMapper.mapList(pastEvent, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.lecturer)) {
            List<Integer> eventByCategory = userLogin.getEventCategories().stream().map(EventCategory::getId).collect(Collectors.toList());
            List<Event> pastEvent = repository.findAllByEventCategory_IdInAndEventStartTimeAfterOrderByEventStartTimeAsc(eventByCategory, instant);
            return listMapper.mapList(pastEvent, EventDTO.class, modelMapper);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, userLogin.getEmail() + "is not owner of this event");
        }
    }

    public List<EventDTO> getEventByDateTime(String startTime, String endTime) throws HandleExceptionForbidden {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());
        if (userLogin.getRole().equals(Role.admin)) {
            List<Event> eventByDateTime = repository.findAllByEventStartTimeBetween(Instant.parse(startTime), Instant.parse(endTime));
            return listMapper.mapList(eventByDateTime, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.student)) {
            List<Event> eventByDateTime = repository.
                    findAllByBookingEmailAndEventStartTimeBetween(auth.getPrincipal().toString(), Instant.parse(startTime), Instant.parse(endTime));
            return listMapper.mapList(eventByDateTime, EventDTO.class, modelMapper);
        } else if (userLogin.getRole().equals(Role.lecturer)) {
            List<Event> eventList = repository.findByEventCategory_IdInAndEventStartTimeBetween(userLogin.getEventCategories().stream().map(EventCategory::getId).collect(Collectors.toList()),
                    Instant.parse(startTime), Instant.parse(endTime));
            if(eventList != null){
                return listMapper.mapList(eventList, EventDTO.class, modelMapper);
            } else {
                throw new HandleExceptionForbidden("You are not category owner of this event");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, userLogin.getEmail() + "is not owner of this event");
        }
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
}
