package account.Controller.Security;

import account.Services.SecurityEventLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('AUDITOR')")
@RestController
public class SecurityController {
    @Autowired
    SecurityEventLoggerService securityEventLoggerService;

    @GetMapping("/api/security/events/")
    public ResponseEntity getSecurityEvents(){

        return ResponseEntity.ok(securityEventLoggerService.getAllSecurityEventsInfo());
    }
}
