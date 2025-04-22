package system.task_management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import system.task_management.model.TaskComment;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    Page<TaskComment> findByOurTaskId(Long taskId, Pageable pageable);
}
