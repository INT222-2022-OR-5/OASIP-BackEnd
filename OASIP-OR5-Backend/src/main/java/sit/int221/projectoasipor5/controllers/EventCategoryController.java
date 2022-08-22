package sit.int221.projectoasipor5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.projectoasipor5.dto.EventCategoryDTO;
import sit.int221.projectoasipor5.entities.EventCategory;
import sit.int221.projectoasipor5.services.EventCategoryService;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/eventCategory") //แสดงรายการของ เวลาการนัดหมายทั้งหมดใน Event
public class EventCategoryController {
    @Autowired
    EventCategoryService eventCategoryService;

    //Get all EventCategory
    @GetMapping("")
    public List<EventCategoryDTO> getAllCategory(){
        return eventCategoryService.getAllCategory();
    }

}
