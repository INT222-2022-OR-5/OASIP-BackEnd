package sit.int221.projectoasipor5.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.utils.ListMapper;
import sit.int221.projectoasipor5.dto.EventCategoryDTO;
import sit.int221.projectoasipor5.dto.EventCategoryUpdateDTO;
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

    //Get Category with id
    public EventCategoryDTO getEventcategoryById(Integer id) {
        EventCategory eventcategory = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " Does Not Exist !!!"));

        return modelMapper.map(eventcategory, EventCategoryDTO.class);
    }

    //Update event with id
    public EventCategoryUpdateDTO update(EventCategoryUpdateDTO update, Integer id){
        EventCategory category = modelMapper.map(update, EventCategory.class);
        EventCategory ct = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Booking id" + id +
                        "Not found ID to Edit"
                ));
        ct.setEventCategoryName(category.getEventCategoryName());
        ct.setEventDuration(category.getEventDuration());
        ct.setEventCategoryDescription(category.getEventCategoryDescription());

        repository.saveAndFlush(ct);
        return modelMapper.map(ct,EventCategoryUpdateDTO.class);
    }

    private EventCategory mapCategory(EventCategory existingEvent, EventCategory updateEvent) {
        existingEvent.setEventCategoryName(updateEvent.getEventCategoryName());
        existingEvent.setEventCategoryDescription(updateEvent.getEventCategoryDescription());
        existingEvent.setEventDuration(updateEvent.getEventDuration());
        return existingEvent;
    }
}
