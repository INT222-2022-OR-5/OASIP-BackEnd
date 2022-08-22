package sit.int221.projectoasipor5.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.Utils.ListMapper;
import sit.int221.projectoasipor5.dto.EventCategoryDTO;
import sit.int221.projectoasipor5.entities.EventCategory;
import sit.int221.projectoasipor5.repositories.EventCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCategoryService {
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventCategoryRepository repository;

    //Get all Category
    public List<EventCategoryDTO> getAllCategory() {
        List<EventCategory> eventcategories = repository.findAll();
        return listMapper.mapList(eventcategories, EventCategoryDTO.class , modelMapper);
    }

    private EventCategory mapCategory(EventCategory existingEvent, EventCategory updateEvent) {
        existingEvent.setEventCategoryName(updateEvent.getEventCategoryName());
        existingEvent.setEventCategoryDescription(updateEvent.getEventCategoryDescription());
        existingEvent.setEventDuration(updateEvent.getEventDuration());
        return existingEvent;
    }
}
