package system.task_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import system.task_management.model.OurTask;

import java.util.Optional;

public interface OurTaskRepository extends JpaRepository<OurTask, Long>, JpaSpecificationExecutor<OurTask> {
    Optional<OurTask> findByTitle(String title);
}
