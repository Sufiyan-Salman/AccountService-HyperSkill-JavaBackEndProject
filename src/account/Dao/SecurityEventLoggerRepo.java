package account.Dao;

import account.Entities.SecurityEventsLogger;
import account.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//@Repository
public interface SecurityEventLoggerRepo extends JpaRepository <SecurityEventsLogger, Integer>{
    public SecurityEventsLogger findById(Long id);
    public List<SecurityEventsLogger> findAll();

//    public List<User> findAllOrderByUserId();


}
