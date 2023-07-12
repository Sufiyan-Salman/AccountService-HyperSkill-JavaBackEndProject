package account.Dao;

import account.Entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//@Repository
public interface UserRepo extends JpaRepository <User, Integer>{
    public User findByEmail(String email);
    public User findByEmailIgnoreCase(String email);

//    public List<User> findAllOrderByUserId();

    boolean existsByEmailIgnoreCase(String email);

    @Transactional
    int deleteByEmail(String userEmail);
    //is se case insensitive hojayega
}
