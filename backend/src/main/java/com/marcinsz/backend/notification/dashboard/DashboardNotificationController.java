package com.marcinsz.backend.notification.dashboard;

import com.marcinsz.backend.guarantee.GuaranteeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardNotificationController {
    private final DashboardNotificationService dashboardNotificationService;

    @GetMapping
    public ResponseEntity<Page<GuaranteeResponse>>fetchGuaranteesExpiresIn7Days(Authentication connectedUser,
                                                                                @RequestParam(name = "pageNumber", defaultValue = "0")int pageNumber,
                                                                                @RequestParam(name = "pageSize", defaultValue = "3") int pageSize){
        return ResponseEntity.ok().body(dashboardNotificationService
                .fetchGuaranteesExpiresIn7Days(
                        connectedUser,
                        pageNumber,
                        pageSize));

    }
}
