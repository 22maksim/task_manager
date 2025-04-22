package system.task_management.specification;

import org.springframework.data.jpa.domain.Specification;
import system.task_management.model.OurTask;
import system.task_management.model.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class TaskSpecifications {

    public static Specification<OurTask> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }

    public static Specification<OurTask> hasStatus(TaskStatus taskStatus) {
        return (root, query, cb) -> cb.equal(root.get("taskStatus"), taskStatus);
    }

    public static Specification<OurTask> inDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) ->
                cb.between(root.get("createdAt"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public static Specification<OurTask> hasTitleLike(String title) {
        return (root, query, cb) -> cb.equal(root.get("title"), title);
    }

}
