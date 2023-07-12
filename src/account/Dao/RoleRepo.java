package account.Dao;

import account.Entities.Role;
import account.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface RoleRepo extends JpaRepository <Role, Long>{

    public Role findByRole(String role);
}
