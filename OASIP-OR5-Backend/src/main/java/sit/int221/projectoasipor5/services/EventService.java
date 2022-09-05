package sit.int221.projectoasipor5.services;

import org.springframework.data.domain.Sort;
import sit.int221.projectoasipor5.utils.ListMapper;
import sit.int221.projectoasipor5.dto.EventDTO;
import sit.int221.projectoasipor5.dto.EventUpdateDTO;
import sit.int221.projectoasipor5.entities.Event;
import sit.int221.projectoasipor5.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventRepository repository;

    //Get all Event
    public List<EventDTO> getAllEvent(String sortBy) {
        List<Event> event = repository.findAll(Sort.by(sortBy).descending());
        return listMapper.mapList(event, EventDTO.class, modelMapper);
    }

    //Get Event with id
    public EventDTO getEventById(Integer id) {
        Event event = repository.findById(id)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event id "+ id +
                        "Does Not Exist !!!"
                ));
        return modelMapper.map(event, EventDTO.class);
    }

    //Add new Event
    public Event save(Event event) {
        Event e = modelMapper.map(event, Event.class);
        return repository.saveAndFlush(e);
    }

    //Delete event with id
    public void deleteById(Integer id) {
        repository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
        repository.deleteById(id);
    }

    //Update event with id
    public EventDTO update(EventUpdateDTO updateEvent, Integer id){
        Event booking = modelMapper.map(updateEvent, Event.class);
        Event e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "EventId" + id +
                        "not found to edit"
                ));
        e.setEventStartTime(booking.getEventStartTime());
        e.setEventNotes(booking.getEventNotes());
        repository.saveAndFlush(e);
        return modelMapper.map(e,EventDTO.class);
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
