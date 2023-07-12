package account.Entities;

import account.CustomSerializers.RoleSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;

@Entity
@Component
public class Role implements /*Comparator<Role>*/ Comparable<Role> {
// yeh aru security event ki types i.e create_user etc ko enum class bana k b hi rkh skte the
    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //identity mtlb sql wala autoincrement

    private Long roleId;

//    @JsonSerialize(using = RoleSerializer.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String role;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String roleGroup;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "userRoles")
    private Set<User> users;

    public Role() {
    }

    public Role(String role, String roleGroup) {
        this.role = role;
        this.roleGroup = roleGroup;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(String roleGroup) {
        this.roleGroup = roleGroup;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    @Override
    public int compareTo(Role role1) {
//        System.out.println("comparbale chala");
        //comparable baar bar chal rha hai
        return role.compareTo(role1.role);
    }


}
