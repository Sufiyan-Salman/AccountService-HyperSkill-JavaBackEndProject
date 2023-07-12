package account.Entities;

import account.CustomSerializers.SETofRoleSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@JsonPropertyOrder({"id", "name", "lastname", "email", "roles"})
@Table(name = "Users")
public class User implements UserDetails {

//     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_ID")
    private Long Id;
//    @NotEmpty // is se jo mene apne controller me itni sari conditions likhi hen , us se chutkara mil jayega
//    @NotNull // is se jo mene apne controller me itni sari conditions likhi hen , us se chutkara mil jayega , lekin wahi error show horhe hen , faida nai horha
    // in 3no annotations k sath @valid ki annotation use krenge ham before request body in argument list of our endpoint
    @NotBlank//(message = "Should not be empty") // agar do 3 whitespaces hongi to wo not empty consider hoga , jab k it'd be blank , so not blank is better approach
    private String name;
    @NotBlank(message = "Should not be empty")
    private String lastname;
    @NotBlank(message="email cannot be empty")
    @Pattern(regexp = ".+@acme\\.com") // ye istrha bhi kia ja skta hai
//    @Column(unique = true) ////we will check this if this works or not
    private String email;
//    @JsonIgnore , ye ignore krne se jo incoming passwrd tha jo reuqest me arha tha , wo bhi ignore horha tha , mtlb read and write dono block horhe the
     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY )
     @NotBlank(message = "Should not be empty")
     @Size(min = 12 , message = "The password length must be at least 12 chars!")
    private String password;


//     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private String role;
    @JsonProperty(value = "roles")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(cascade = {  CascadeType.PERSIST, CascadeType.MERGE   } , fetch = FetchType.EAGER)// eager islie q k proxy and lazy intializer ka error arhaa tha , is se chale gaya
    @JoinTable(name = "user_Role",
            joinColumns =@JoinColumn(name = "UserId"),
            inverseJoinColumns = @JoinColumn(name = "roleId" ))
    private SortedSet<Role> userRoles= new TreeSet<>() ; // test48, role implements comparator so that it can be sorted
//    private Set<Role> userRoles= new TreeSet<>(new RoleComparator()) ; // test48, role implements comparator so that it can be sorted


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "is_Account_Locked")
    private boolean isAccountNonLocked=true;

//    @Transient
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

    private int noOfRepetitiveWrongLoginAttempts; //can be max 5 acc to our logic

//==========================
    //hamari jo payment ya employee ki cheezen hen ,wese is trha linkhona chahiye unehn
//    @OneToMany(cascade = {CascadeType.ALL})
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    private List<Employee> payments = new ArrayList<>();
    //========================

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public int getNoOfRepetitiveWrongLoginAttempts() {
        return noOfRepetitiveWrongLoginAttempts;
    }

    public void setNoOfRepetitiveWrongLoginAttempts(int noOfRepetitiveWrongLoginAttempts) {
        this.noOfRepetitiveWrongLoginAttempts = noOfRepetitiveWrongLoginAttempts;
    }

    //{
//    @Override
//    public int compare(Role role1, Role role2) {
//        return role1.getRole().compareTo(role2.getRole());
//
//    }
//});
//    private Set<Role> userRoles= new HashSet<>();

//===========================
    // when I was returning string[] , now no need
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
////    @JsonSerialize(using = SETofRoleSerializer.class)// test 12
//    public Set<Role> getUserRoles2() {
//        return userRoles;
//    }
    //aboove is for my user service

    //===================
    //both of them are solving //test 12 , but I htink first 1 is better
    @JsonSerialize(using = SETofRoleSerializer.class)// test 12
    public SortedSet<Role> getUserRoles() {// this corrccted test 12
        //i have done it for json response

        return userRoles;
    }
//    public String[] getUserRoles() {// this corrccted test 12
//        //i have done it for json response , due to thsi I have also created userROles2 , but now not needed
//        String[] stringRole=new String[userRoles.size()];
//        int i=0;
//        for (Role role: userRoles         ) {
//            stringRole[i]="ROLE_"+role.getRole();
//            i++;
//        }
//        return stringRole;
//    }
    //========================

    public void setUserRoles(TreeSet<Role> userRoles) {
        //isme masla ye hoskta hai k array ki form me aye , to hmen array ko covnert krna prega in set
        this.userRoles = userRoles;
    }




     @JsonProperty(value = "id")
    public Long getUserId() {
        //
        System.out.println("get user call hua");
        return Id;
    }
    public void setUserId(Long Id) {

        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastName) {
        this.lastname = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)


//     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //yahan cahnges krni hen

//        Set<Role> userRoles=getUserRoles();
        Collection<GrantedAuthority> hasAuthorities= new ArrayList<>();
        for (Role userRole:   userRoles          ) {
            System.out.println("The authorities user has: "+userRole.getRole().toUpperCase());
            hasAuthorities.add(new SimpleGrantedAuthority("ROLE_"+userRole.getRole().toUpperCase()));

        }
        return hasAuthorities;
//        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

    }

    /*private Collection<GrantedAuthority> getAuthorities(UserEntity user){
        Set<Group> userGroups = user.getUserGroups();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());
        for(Group userGroup : userGroups){
            authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));
        }

        return authorities;
    }
}*/

//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return getEmail();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {

        return isAccountNonLocked;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }



    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", role='" + userRoles + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
