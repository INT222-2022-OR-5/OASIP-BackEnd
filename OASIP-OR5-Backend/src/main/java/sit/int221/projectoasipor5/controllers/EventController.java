package sit.int221.projectoasipor5.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.dto.Event.EventDTO;
import sit.int221.projectoasipor5.dto.Event.EventUpdateDTO;
import sit.int221.projectoasipor5.entities.Event;
import sit.int221.projectoasipor5.entities.EventCategory;
import sit.int221.projectoasipor5.entities.Role;
import sit.int221.projectoasipor5.entities.User;
import sit.int221.projectoasipor5.exception.HandleExceptionBadRequest;
import sit.int221.projectoasipor5.exception.HandleExceptionForbidden;
import sit.int221.projectoasipor5.exception.OverlappedExceptionHandler;
import sit.int221.projectoasipor5.repositories.EventRepository;
import sit.int221.projectoasipor5.repositories.UserRepository;
import sit.int221.projectoasipor5.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/events") //แสดงรายการของ เวลาการนัดหมายทั้งหมดใน Event
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserRepository userRepository;

    //Get all Event
    @GetMapping({""})
    public List<EventDTO> getEvents() {
        return this.eventService.getAllEvent();
    }

    //Get Event with id
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Integer id) throws HandleExceptionForbidden {
        return this.eventService.getEventById(id);
    }

    //Add new Event
    @PostMapping({""})
    public Event create(@Valid @RequestBody EventDTO event) throws OverlappedExceptionHandler, HandleExceptionForbidden, HandleExceptionBadRequest {
        return eventService.save(event);
    }

    //Add new Event as guest
    @PostMapping({"/guest"})
    public Event guestCreate(@Valid @RequestBody EventDTO event) throws OverlappedExceptionHandler, HandleExceptionForbidden, HandleExceptionBadRequest {
        return eventService.save(event);
    }

    //Delete an event with id = ?
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws HandleExceptionForbidden {
        eventService.deleteById(id);
    }

    //Update an event with id = ?
    @PutMapping({"/{Id}"})
    public ResponseEntity<Event> update(@Valid @RequestBody EventUpdateDTO updateEvent, @PathVariable Integer Id) throws OverlappedExceptionHandler, HandleExceptionForbidden {
        Date newEventStartTime = Date.from(updateEvent.getEventStartTime());
        Date newEventEndTime = eventService.findEndDate(Date.from(updateEvent.getEventStartTime()), updateEvent.getEventDuration());
        List<EventDTO> eventList = getEvents();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = userRepository.findByEmail(auth.getPrincipal().toString());

        if (userLogin.getRole().equals(Role.admin)) {
            return checkOverlapUpdate(updateEvent, Id, newEventStartTime, newEventEndTime, eventList);
        } else if (userLogin.getRole().equals(Role.student)) {
            Event eventForCheck =  repository.findById(Id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
            );
            if (Objects.equals(updateEvent.getBookingEmail(), userLogin.getEmail())) {
                if(Objects.equals(updateEvent.getBookingEmail(), eventForCheck.getBookingEmail())) {
                    return checkOverlapUpdate(updateEvent, Id, newEventStartTime, newEventEndTime, eventList);
                } else {
                    throw new HandleExceptionForbidden("You are not owner of this event");
                }
            } else {
                throw new HandleExceptionForbidden("The booking email must be the same as the student's email");
            }
        } else {
            throw new HandleExceptionForbidden("You are not allowed to update this event");
        }
    }

    private ResponseEntity<Event> checkOverlapUpdate(@RequestBody @Valid EventUpdateDTO updateEvent, @PathVariable Integer Id, Date newEventStartTime, Date newEventEndTime, List<EventDTO> eventList) throws OverlappedExceptionHandler {
        for (EventDTO eventDTO : eventList) {
            if (Objects.equals(updateEvent.getEventCategory().getId(), eventDTO.getEventCategory().getId()) && !Objects.equals(eventDTO.getId(), Id)) { //เช็คเฉพาะ EventCategory เดียวกัน และถ้าอัพเดตตัวเดิมไม่ต้องเช็ค overlapped
                Date eventStartTime = Date.from(eventDTO.getEventStartTime());
                Date eventEndTime = eventService.findEndDate(Date.from(eventDTO.getEventStartTime()), eventDTO.getEventDuration());
                EventService.checkTimeOverlap(newEventStartTime, newEventEndTime, eventStartTime, eventEndTime);
            }
        }
        Event event = repository.findById(Id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
        );
        modelMapper.map(updateEvent, event);
        repository.saveAndFlush(event);
        return ResponseEntity.status(200).body(event);
    }
}
