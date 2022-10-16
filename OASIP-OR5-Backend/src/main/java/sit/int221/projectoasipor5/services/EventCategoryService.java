package sit.int221.projectoasipor5.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.projectoasipor5.dto.EventCategory.EventCategoryDTO;
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
    public EventCategory update(EventCategory updateCategory, Integer id){
        EventCategory category = repository.findById(id).map(c->mapCategory(c, updateCategory))
                .orElseGet(()->
                {
                    updateCategory.setId(id);
                    return updateCategory;
                });
        return repository.saveAndFlush(category);
    }

    private EventCategory mapCategory(EventCategory existingEvent, EventCategory updateCategory) {
        existingEvent.setEventCategoryName(updateCategory.getEventCategoryName());
        existingEvent.setEventCategoryDescription(updateCategory.getEventCategoryDescription());
        existingEvent.setEventDuration(updateCategory.getEventDuration());
        return existingEvent;
    }
}
