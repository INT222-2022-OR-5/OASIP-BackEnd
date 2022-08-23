package sit.int221.projectoasipor5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.projectoasipor5.dto.EventCategoryDTO;
import sit.int221.projectoasipor5.dto.EventCategoryUpdateDTO;
import sit.int221.projectoasipor5.entities.EventCategory;
import sit.int221.projectoasipor5.services.EventCategoryService;

import javax.validation.Valid;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/eventCategory") //แสดงรายการของ เวลาการนัดหมายทั้งหมดใน Event
public class EventCategoryController {
    @Autowired
    EventCategoryService eventCategoryService;

    //Get all category
    @GetMapping("")
    public List<EventCategoryDTO> getAllCategory(){
        return eventCategoryService.getAllCategory();
    }

    //Get category with id
    @GetMapping("/{id}")
    public EventCategoryDTO getEventcategoryById(@PathVariable Integer id) {
        return eventCategoryService.getEventcategoryById(id);
    }

    //Update category with id
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventCategoryUpdateDTO edit(@Valid @RequestBody EventCategoryUpdateDTO updateCategory, @PathVariable Integer id) {
        return eventCategoryService.update(updateCategory,id);
    }
}
