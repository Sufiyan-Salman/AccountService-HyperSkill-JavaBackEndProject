package account.Services;

import account.Dao.RoleRepo;
import account.Entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RoleService {

    @Autowired
    RoleRepo roleRepo;


    public void addRoleInGroup(Set<Role> SetOfRolesUserHas){
        //do we have to return it?
        for (Role role:SetOfRolesUserHas      ) {


            if(role.getRole().equals("ADMINISTRATOR")){
                role.setRoleGroup("Administrative");
            }
            else{
                role.setRoleGroup("Business");

            }


        }

    }
    public Role getRole(String roleToBeRetrieved){
        Role role=roleRepo.findByRole(roleToBeRetrieved);
        if(role==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found!");
        }
        else{
            return role;
        }

    }
    public List<Role> getAllRoles(){
        //i thinkbetter approach is to return map but it would take alot of time for me rn so I will return list
        List<Role> roles=roleRepo.findAll();
        if(roles.isEmpty()){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No roles are added yet!");
            return roles;
        }
        else{
            return roles;
        }

    }
    public void setAllRoles(){
        if (getAllRoles().isEmpty())
        //AGAR db me roles na hue to add krdega ye wrna nai krega

        {
            roleRepo.save(new Role("ADMINISTRATOR","Administrative"));
            roleRepo.save(new Role("USER","Business"));
            roleRepo.save(new Role("ACCOUNTANT","Business"));
            roleRepo.save(new Role("AUDITOR","Business"));

        }



    }


    public Role findByRole( String roleThatIsToRetrieve) {
        return roleRepo.findByRole(roleThatIsToRetrieve);
    }
}


//======================================
