package account.Services;

import account.Dao.SecurityEventLoggerRepo;
import account.Entities.SecurityEventsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SecurityEventLoggerService {
    @Autowired
    SecurityEventLoggerRepo securityEventLoggerRepo;

    public void logSecurityEvent(LocalDate date,String action,String subject, String object,String path){
        securityEventLoggerRepo.save(new SecurityEventsLogger(date,action,subject ,object, path));
    }

    public List<SecurityEventsLogger> getAllSecurityEventsInfo(){
        return securityEventLoggerRepo.findAll();
    }


}
