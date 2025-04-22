package system.task_management.model.dto;

import java.io.Serializable;

public record OurTaskShortDto(
        Long id,
        String title
) implements Serializable {

}
