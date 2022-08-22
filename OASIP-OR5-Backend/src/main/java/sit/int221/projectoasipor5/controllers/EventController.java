package sit.int221.projectoasipor5.controllers;

import sit.int221.projectoasipor5.dto.EventDTO;
import sit.int221.projectoasipor5.dto.EventUpdateDTO;
import sit.int221.projectoasipor5.entities.Event;
import sit.int221.projectoasipor5.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/events") //แสดงรายการของ เวลาการนัดหมายทั้งหมดใน Event
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService){
        this.eventService = eventService;
    };

    //Get all Event
    @GetMapping()
    public List<EventDTO> getAllEvent(@RequestParam(defaultValue = "eventStartTime") String sortBy){
        return eventService.getAllEvent(sortBy);
    }

    //Get Event with id
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Integer id){
        return eventService.getEventById(id);
    }

    //Add new Event
    @PostMapping("")
    public Event create(@Valid @RequestBody Event event) {
        return eventService.save(event);
    }

    //Delete an event with id = ?
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        eventService.deleteById(id);
    }

    //Update an event with id = ?
    @PutMapping("/{id}")
    public EventDTO update(@Valid  @RequestBody EventUpdateDTO updateEvent, @PathVariable Integer id){
        return eventService.update(updateEvent ,id);
    }

}
