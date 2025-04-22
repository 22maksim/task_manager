package system.task_management.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class UtilStandard {

    public static String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
