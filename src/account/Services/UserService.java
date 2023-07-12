package account.Services;

import account.Dao.UserRepo;
import account.Entities.Role;
import account.Entities.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;


@Service
public class UserService {
    //subjectEmail jo mene use kia hai parameter , usme wo email ayegi jiske user ne ye method invoke kia
    public static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    public UserRepo userRepo;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    Role role; //I have autowired this to use same instance for retrieving role from repo and using it to save in user db


    @Autowired
    RoleService roleService;

    @Autowired
    SecurityEventLoggerService securityEventLoggerService;

    List<String> breachedPasswords= List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    public User addUser(User user)
    {
        if (userRepo.findByEmailIgnoreCase(user.getEmail()) != null ) {
            System.out.println("Already existing user");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User exist!");
//            throw new UserExistsException();// this is working too , lekin i see no use of it
        } else if (breachedPasswords.contains(user.getPassword())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The password is in the hacker's database!");
        }
        //if useer is not already existing , password is ok , then we will see if this is the first user or not
        List<User> userList=userRepo.findAll();
        roleService.setAllRoles();
        // 0 . Admin
        // 01 . user
        // 0 2. Accountant
        // 0 3. Auditor

//        Role role;
        if (userList.isEmpty()) {

            //================
            role=roleService.findByRole("ADMINISTRATOR");
            //===============


        }else{

            //=========================
            role=roleService.findByRole("USER");
            //===============


        }
        user.setEmail(user.getEmail().toLowerCase());// take saari emails short format me hi save hon
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.getUserRoles().add(role);
        userRepo.save(user);
        securityEventLoggerService.logSecurityEvent(LocalDate.now(),"CREATE_USER","Anonymous", user.getEmail().toLowerCase(), "/api/auth/signup");
//        String.format("Grant role %s to %s", role, user.getEmail()),
//                path,
//                SecurityEventType.GRANT_ROLE
//        ); // ham its trha bhi string ko likh stke hen bjaye iske k + + use kren
        return user;

    }
//    @Transactional // repo me lagao direct , ya isme lagao , ek hi baat hai , I prefer in repo
    public void deleteUser(String userEmail , String subjectEmail){
        //if user found and deleted , return true else false
        System.out.println(userEmail);
        User userToBeDeleted=userRepo.findByEmailIgnoreCase(userEmail);
        if(userToBeDeleted==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        //Admin cant be deleted by anyone
        if (hasAdministrativeRole(userToBeDeleted)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        System.out.println("No of users deleted: "+userRepo.deleteByEmail(userEmail));
        securityEventLoggerService.logSecurityEvent(LocalDate.now(),"DELETE_USER",subjectEmail,userEmail, "/api/admin/user");



    }
    public User getUser(String userEmail){
//
////        if user found , return the user , else return null
        return userRepo.findByEmailIgnoreCase(userEmail);
    }
    public List<User> getAllUsers(){
        List<User> userList=new ArrayList<>();
        userList=userRepo.findAll();
        //ise sort krne klie hamne comparable ka use kia ,it would have been possible using Sort class as well
        return userList;
    }
    public User changeUserRole(String userEmail, String roleThatIsToBeGrantedOrRemoved, String operationToBePerfomed, String subjectEmail){
        //=====================
        //user should exist in DB
        User userWhoseRoleIsToBeChanged= userRepo.findByEmail(userEmail);
        if (userWhoseRoleIsToBeChanged==null)    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        //=====================
        //role should exist in DB
//        Role role; // as I am using autowired role , therefore nno use of it
        role=roleService.findByRole(roleThatIsToBeGrantedOrRemoved);
        if (role==null)    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found!");

        //=====================
        //else
        //if user exists in DB and Also role , then
        SortedSet<Role> userRoles =  userWhoseRoleIsToBeChanged.getUserRoles();


        if(operationToBePerfomed.equals("GRANT")){
            //if we have to grant some role to user

            //=====================
            // ek user dono roles k group se belong nai krskta
            if (hasAdministrativeRole(userWhoseRoleIsToBeChanged) && role.getRoleGroup().equals("Business") ||  !hasAdministrativeRole(userWhoseRoleIsToBeChanged) && role.getRoleGroup().equals("Administrative")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user cannot combine administrative and business roles!");

            }
            //=====================
            //if all ok then add
            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"GRANT_ROLE",subjectEmail,"Grant role "+roleThatIsToBeGrantedOrRemoved+" to "+userEmail, "/api/admin/user/role");
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"GRANT_ROLE","johndoe@acme.com","Grant role "+roleThatIsToBeGrantedOrRemoved+" to "+userEmail, "/api/admin/user/role");

            userRoles.add(role);
            //=====================
        }
        else if(operationToBePerfomed.equals("REMOVE"))
        {
            //if we have to remove a role from user
            //=====================
            //"Can't remove Admin
            if (roleThatIsToBeGrantedOrRemoved.equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            //=====================
            //if user does not have the role that is to be removed
            if (!userHasRole(userWhoseRoleIsToBeChanged,roleThatIsToBeGrantedOrRemoved)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user does not have a role!"); // 57 test
            //=====================
            //if user has only 1 role
            if (userRoles.size()==1) {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user does not have a role!");// 56
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user must have at least one role!"); // test 57
            }
            //=====================
            //if all ok then remove
            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"REMOVE_ROLE",subjectEmail,"Remove role "+roleThatIsToBeGrantedOrRemoved+" from "+userEmail, "/api/admin/user/role");
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"REMOVE_ROLE","johndoe@acme.com","Remove role "+roleThatIsToBeGrantedOrRemoved+" from "+userEmail, "/api/admin/user/role");

            removeRoleFromUser(userWhoseRoleIsToBeChanged,roleThatIsToBeGrantedOrRemoved);
            //=====================
        }
        //make changes to DB
        userRepo.save(userWhoseRoleIsToBeChanged);
        return userWhoseRoleIsToBeChanged;
    }

    //checks if user has a specific role or not
    public boolean userHasRole(User user,String roleWhoseAvailibilityIsToBeChecked) {
        boolean isAvailable=false;
        for (Role roleofUser: user.getUserRoles()           ) {
            if (roleofUser.getRole().equals(roleWhoseAvailibilityIsToBeChecked)) {
                isAvailable=true;
                break;
            }
        }
        return isAvailable;
    }
    //there are only two groups , adminitrative and bussniess
    public boolean hasAdministrativeRole(User user) {
        boolean hasAdminRoles=false;
        for (Role roleofUser: user.getUserRoles()           ) {
            if (roleofUser.getRoleGroup().equals("Administrative")) {
                hasAdminRoles=true;
                break;
            }
        }
        return hasAdminRoles;
    }
    public void removeRoleFromUser(User user , String roleWhichIsToBeRemoved) {
        for (Role roleofUser: user.getUserRoles()           ) {
            if (roleofUser.getRole().equals(roleWhichIsToBeRemoved)) {
                user.getUserRoles().remove(roleofUser);
                break;
            }
        }
    }
    //    public boolean changeUserPasswprd(User user){
    public void changeUserPassword(User user, String new_password , String subjectEmail){
//        if user found and password gets changed , return true else false
        if(new_password==null || new_password.isEmpty() || new_password.length()<12){
//              throw ResponseStatusException
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password length must be 12 chars minimum!");
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The password length must be at least 12 chars!");
        }
        if(breachedPasswords.contains(new_password)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The password is in the hacker's database!");

        }
        else{

        }
        //encoder.matches("new_password", hashOfOldPassword)//check krne k lie k kaheen pichle wale jesa to nai hai , if it then
        boolean isSame=bCryptPasswordEncoder.matches(new_password,user.getPassword());


        if (isSame) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The passwords must be different!");
            //               throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The passwords must be different!");
        else {
            user.setPassword(bCryptPasswordEncoder.encode(new_password));
            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"CHANGE_PASSWORD", user.getEmail(), subjectEmail, "/api/auth/changepass");
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"CHANGE_PASSWORD", user.getEmail(), "johndoe@acme.com", "/api/auth/changepass");

            userRepo.save(user);
//            System.out.println(user.getPassword());
        }
    }


    public void changeAccessOfUser(User user, String operation, String subjectEmail) {
//    public void changeAccessOfUser(String userEmail, String operation) {
//        User user=userRepo.findByEmailIgnoreCase(userEmail);
        if (hasAdministrativeRole(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        if(operation.equalsIgnoreCase("LOCK")){
            user.setAccountNonLocked(false);
            userRepo.save(user);
            securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER",subjectEmail,"Lock user "+user.getEmail(), "/api/admin/user/access"); // lekin example k hisab se lock , sirf brute force k baad horhra h , to hmen wahan b dalna h or yahan b daldenge ehtiyatan , we will check if it is better to rightit here or in service
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER","johndoe@acme.com","Lock user "+user.getEmail(), "/api/admin/user/access"); // lekin example k hisab se lock , sirf brute force k baad horhra h , to hmen wahan b dalna h or yahan b daldenge ehtiyatan , we will check if it is better to rightit here or in service


        }
        else if(operation.equalsIgnoreCase("UNLOCK")) {

            user.setAccountNonLocked(true);
            user.setNoOfRepetitiveWrongLoginAttempts(0);
            userRepo.save(user);
            securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER",subjectEmail ,"Unlock user "+user.getEmail(), "/api/admin/user/access");
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER","johndoe@acme.com" ,"Unlock user "+user.getEmail(), "/api/admin/user/access");
        }


    }

    public void saveUserChanges(User user){
        userRepo.save(user);
    }

    public void increaseNoOfConsecutiveFailedAttempts(User user){
        System.out.println("ab itni attempt save honge from service: "+(user.getNoOfRepetitiveWrongLoginAttempts()+1));
        user.setNoOfRepetitiveWrongLoginAttempts(user.getNoOfRepetitiveWrongLoginAttempts()+1);
        userRepo.save(user);
    }
    public void resetNoOfConsecutiveFailedAttempts(String userEmail){
        User user=userRepo.findByEmailIgnoreCase(userEmail);
        user.setNoOfRepetitiveWrongLoginAttempts(0);
        userRepo.save(user);
    }


}
//=========================================================
//package account.Services;
//
//import account.Dao.UserRepo;
//import account.Entities.Role;
//import account.Entities.User;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@Service
//public class UserService {
//    public static final int MAX_FAILED_ATTEMPTS = 5;
//    @Autowired
//    public UserRepo userRepo;
//    @Autowired
//    BCryptPasswordEncoder bCryptPasswordEncoder;
////    @Autowired
////    Role role;
//
//    @Autowired
//    RoleService roleService;
//
//    @Autowired
//    SecurityEventLoggerService securityEventLoggerService;
//    List<String> breachedPasswords= List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
//            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
//            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
//    //add, update, get specific , get all, delete
////    public boolean addUser(User user)
//    public User addUser(User user)
//    {
////        boolean isFirstUser=true;
//
////        user.setRole("ROLE_ADMIN");
////      user.setEmail(user.getEmail().toLowerCase());// either we use this with findByEmail
//        Role role=new Role();
//        if (userRepo.findByEmailIgnoreCase(user.getEmail()) != null ) {
//            System.out.println("Already existing user");
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User exist!");
////            throw new UserExistsException();// this is working too , lekin i see no use of it
//        } else if (breachedPasswords.contains(user.getPassword())) {
//
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The password is in the hacker's database!");
//        }
//        //if useer is not already existing , password is ok , then we will see if this is the first user or not
//        List<User> userList=userRepo.findAll();
//        if (userList.isEmpty()) {
//            //if user is first one
//            role.setRole("ADMINISTRATOR");
//            role.setRoleGroup("Administrative");
//
//        }else{
//            //if user is not the first user then
//            role.setRole("USER");
//            role.setRoleGroup("Business");
//
//        }
//        user.setEmail(user.getEmail().toLowerCase());// take saari emails short format me hi save hon
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        user.getUserRoles().add(role);
////        roleService.addRoleInGroup(user.getUserRoles()); // role me grp dalne k baad save kra rhe hen // rquest me nai araha role , hmen khud assign krna hai
//        userRepo.save(user);
//        return user;
//
//    }
//    @Transactional
//    public void deleteUser(String userEmail){
//        //if user found and deleted , return true else false
////        boolean userExists=userRepo.existsByEmailIgnoreCase(userEmail);
//        System.out.println(userEmail);
//        User userToBeDeleted=userRepo.findByEmailIgnoreCase(userEmail);
//        if(userToBeDeleted==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
////        if(userToBeDeleted==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"This user does not exist!");
//
//        //Admin cant be deleted by himselfe , is hisab se koi aur admin ksi dusre admin ko del krskta hai
//        if (hasAdministrativeRole(userToBeDeleted)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
//
//        System.out.println("No of users deleted: "+userRepo.deleteByEmail(userEmail));
//
//    }
//    public User getUser(String userEmail){
//
////        if user found , return the user , else return null
//        return userRepo.findByEmailIgnoreCase(userEmail);
//    }
//    public List<User> getAllUsers(){
////might be wrong
////        return userRepo.findAll(Sort.by("UserId"));//not working correclty
//        System.out.println(userRepo.findByEmailIgnoreCase("johndoe@acme.com").getAuthorities());
//        List<User> userList=new ArrayList<>();
//        userList=userRepo.findAll();
////        Collections.sort(userList,new RoleComparator());
//        return userList;
//    }
//    public User changeUserRole(String userEmail, String roleThatIsToBeGrantedOrRemoved, String operationToBePerfomed){
////        if user found and role gets changed , return true else false
///*{
//             "user": "<String value, not empty>",
//   "role": "<User role>",
//   "operation": "<[GRANT, REMOVE]>"
//
//   "user": "ivanivanov@acme.com",
//   "role": "ACCOUNTANT",
//   "operation": "GRANT"
//}*/
//        //==========
//        //grant or removee
//        //if op successsfull then returnn full user
//        //user not found 404. "User not found!"----done
//        //role not found 404. "Role not found!"----done
//        //role in user not found 400. "The user does not have a role!" ------done
//        //only existing role not removable 400 . "The user must have at least one role!" ---done
//        //"The user cannot combine administrative and business roles!" 400--done , but during saving of user , we have to specify role groups
//        //"Can't remove ADMINISTRATOR role!" 400 ---done  //lekin bas admin role hatega hi nai istrha , shayad esa ho k apna na hat skta ho lekin dusre ka hata sken
//        //=====================
//        //=====================
//        //user should exist
//        User userWhoseRoleIsToBeChanged= userRepo.findByEmail(userEmail);
//        Role role=new Role();
//        //temporary
//        if (roleThatIsToBeGrantedOrRemoved.equals("ACCOUNTANT")) {
//            role.setRole(roleThatIsToBeGrantedOrRemoved);
//            role.setRoleGroup("Business");
//        }
//        else if(roleThatIsToBeGrantedOrRemoved.equals("USER")) {//for user
//            role.setRole(roleThatIsToBeGrantedOrRemoved);
//            role.setRoleGroup("Business");
//        } else if(roleThatIsToBeGrantedOrRemoved.equals("AUDITOR")) {//for user
//            role.setRole(roleThatIsToBeGrantedOrRemoved);
//            role.setRoleGroup("Business");
//        }
//        else if (roleThatIsToBeGrantedOrRemoved.equals("ADMINISTRATOR")) {
//            role.setRole(roleThatIsToBeGrantedOrRemoved);
//            role.setRoleGroup("Administrative");
//        }
//        else
//        {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found!");
//        }
//
//        if (userWhoseRoleIsToBeChanged==null)    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
////        boolean userExists=userRepo.existsByEmailIgnoreCase(userEmail);
////        if (!userExists)    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
//        //=====================
////        if(userExists){
////        User userWhoseRoleIsToBeChanged= userRepo.findByEmail(userEmail);
//        SortedSet<Role> userRoles =  userWhoseRoleIsToBeChanged.getUserRoles();
//
//        if(operationToBePerfomed.equals("GRANT")){
//            //if we have to grant some role to user
//            //=====================
//            //role should exist in DB
//            //ham ise abhi nai krenge , q k isme phir hamen roles pehle se add krne prenge, to ham bas user k sath sath hi add krenge
////                    Role roleRetrieved=  roleService.getRole(roleThatIsToBeGrantedOrRemoved);
//            //=====================
//            // ek user dono roles k group se belong nai krskta
//            if (hasAdministrativeRole(userWhoseRoleIsToBeChanged) && role.getRoleGroup().equals("Business") ||  !hasAdministrativeRole(userWhoseRoleIsToBeChanged) && role.getRoleGroup().equals("Administrative")) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user cannot combine administrative and business roles!");
//
//            }
//            //=====================
//            //if all ok then add
////                    if (roleThatIsToBeGrantedOrRemoved.equals("ACCOUNTANT")) {
////                          role.setRole(roleThatIsToBeGrantedOrRemoved);
////                          role.setRoleGroup("Business");
////                    }
//
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"GRANT_ROLE","johndoe@acme.com","Grant role "+roleThatIsToBeGrantedOrRemoved+" to "+userEmail, "/api/admin/user/role");
//            userRoles.add(role);
//            //=====================
//        }
//        else if(operationToBePerfomed.equals("REMOVE"))
//        {
//            //if we have to remove a role from user
//            //=====================
//            //"Can't remove Admin
//            if (roleThatIsToBeGrantedOrRemoved.equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
//            //=====================
//            //if user does not have the role that is to be removed
//            if (!userHasRole(userWhoseRoleIsToBeChanged,roleThatIsToBeGrantedOrRemoved)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user does not have a role!"); // 57 test
//            //=====================
//            //if user has only 1 role
//            if (userRoles.size()==1) {
////                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user does not have a role!");// 56
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user must have at least one role!"); // test 57
//            }
//            //=====================
//            //if all ok then remove
//            securityEventLoggerService.logSecurityEvent(LocalDate.now(),"REMOVE_ROLE","johndoe@acme.com","Remove role "+roleThatIsToBeGrantedOrRemoved+" from "+userEmail, "/api/admin/user/role");
//            removeRoleFromUser(userWhoseRoleIsToBeChanged,roleThatIsToBeGrantedOrRemoved);
//            //=====================
//        }
//        //make changes to DB
////        Collections.sort(Arrays.asList();
//        userRepo.save(userWhoseRoleIsToBeChanged);
//        return userWhoseRoleIsToBeChanged;
//    }
//
//    //checks if user has a specific role or not
//    public boolean userHasRole(User user,String roleWhoseAvailibilityIsToBeChecked) {
//        boolean isAvailable=false;
//        for (Role roleofUser: user.getUserRoles()           ) {
//            if (roleofUser.getRole().equals(roleWhoseAvailibilityIsToBeChecked)) {
//                isAvailable=true;
//                break;
//            }
//        }
//        return isAvailable;
//    }
//    //there are only two groups , adminitrative and bussniess
//    public boolean hasAdministrativeRole(User user) {
//        boolean hasAdminRoles=false;
//        for (Role roleofUser: user.getUserRoles()           ) {
//            if (roleofUser.getRoleGroup().equals("Administrative")) {
//                hasAdminRoles=true;
//                break;
//            }
//        }
//        return hasAdminRoles;
//    }
//    public void removeRoleFromUser(User user , String roleWhichIsToBeRemoved) {
////        boolean isDeleted=false;
//        for (Role roleofUser: user.getUserRoles()           ) {
//            if (roleofUser.getRole().equals(roleWhichIsToBeRemoved)) {
////                isDeleted=true;
//                user.getUserRoles().remove(roleofUser);
//                break;
//            }
//        }
//    }
//    //    public boolean changeUserPasswprd(User user){
//    public void changeUserPassword(User user, String new_password){
////        if user found and password gets changed , return true else false
//        if(breachedPasswords.contains(new_password)){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The password is in the hacker's database!");
//
//        }
//        else{
//
//        }
//        //encoder.matches("new_password", hashOfOldPassword)//check krne k lie k kaheen pichle wale jesa to nai hai , if it then
//        boolean isSame=bCryptPasswordEncoder.matches(new_password,user.getPassword());
//
//
//        if (isSame) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The passwords must be different!");
//            //               throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The passwords must be different!");
//        else {
//            user.setPassword(bCryptPasswordEncoder.encode(new_password));
//            userRepo.save(user);
////            System.out.println(user.getPassword());
//        }
//    }
//
//    public void changeAccessOfUser(User user, String operation) {
////    public void changeAccessOfUser(String userEmail, String operation) {
////        User user=userRepo.findByEmailIgnoreCase(userEmail);
//        if (hasAdministrativeRole(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
//        if(operation.equalsIgnoreCase("LOCK")){
//            user.setAccountNonLocked(false);
//            userRepo.save(user);
//
//        }
//        else if(operation.equalsIgnoreCase("UNLOCK")) {
//
//            user.setAccountNonLocked(true);
//            user.setNoOfRepetitiveWrongLoginAttempts(0);
//            userRepo.save(user);
//        }
//
//
//    }
//
//    public void increaseNoOfConsecutiveFailedAttempts(User user){
//        System.out.println("ab itni attempt save honge from service: "+(user.getNoOfRepetitiveWrongLoginAttempts()+1));
//        user.setNoOfRepetitiveWrongLoginAttempts(user.getNoOfRepetitiveWrongLoginAttempts()+1);
//        userRepo.save(user);
//    }
//    public void resetNoOfConsecutiveFailedAttempts(String userEmail){
//        User user=userRepo.findByEmailIgnoreCase(userEmail);
//        user.setNoOfRepetitiveWrongLoginAttempts(0);
//        userRepo.save(user);
//    }
//}
