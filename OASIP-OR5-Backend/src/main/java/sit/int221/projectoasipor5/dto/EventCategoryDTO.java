package sit.int221.projectoasipor5.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCategoryDTO {
    private Integer id;

    @NotBlank(message = "CategoryName must not be blank")
    @Size(min = 1 , max = 100 , message = "CategoryName must between 1 - 100")
    private String eventCategoryName;

    @Size(max = 500 , message = "CategoryDescription must between 0 - 500")
    private String eventCategoryDescription;

    @Max(value = 480, message = "Duration have more than 480 minute")
    @Min(value = 1, message = "Duration have less 1 minute")
    @NotNull(message = "Duration must not be null")
    private Integer eventDuration;

}
