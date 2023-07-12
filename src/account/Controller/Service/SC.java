package account.Controller.Service;

import account.Entities.User;
import account.Services.SecurityEventLoggerService;
import account.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;

@RestController
//@RolesAllowed("ADMINISTRATOR")
//@Secured("ADMINISTRATOR")
@PreAuthorize("hasRole('ADMINISTRATOR')")
//@PreAuthorize("hasRole('ADMINISTRATOR')")

//should be accesed only by admin
public class SC {
     //subjectUser is the user that is currently logged in and sending requests
     @Autowired
     SecurityEventLoggerService securityEventLoggerService;

     @Autowired
     UserService userService;
//    PUT api/admin/user/role changes user roles;
//    DELETE api/admin/user deletes a user;
//    GET api/admin/user displays information about all users.
     @PutMapping("/api/admin/user/access")
     public ResponseEntity lockUnlockUser(@RequestBody Map<String,String> request , @AuthenticationPrincipal User subjectUser){
//          securityEventLoggerService.logSecurityEvent(LocalDate.now(),"REMOVE_ROLE","johndoe@acme.com","Remove role "+req.get("role")+" to"+req.get("email"), "/api/admin/user/role");
          String userEmail=request.get("user").toLowerCase();
          String operation=request.get("operation");
//          if (operation.equalsIgnoreCase( "LOCK")) {
//          securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER","johndoe@acme.com","Lock user "+userEmail, "/api/admin/user/access"); // lekin example k hisab se lock , sirf brute force k baad horhra h , to hmen wahan b dalna h or yahan b daldenge ehtiyatan , we will check if it is better to rightit here or in service

//          }else if (operation.equalsIgnoreCase( "UNLOCK")) securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER","johndoe@acme.com" ,"Unlock user "+userEmail, "/api/admin/user/access");
//          }else  securityEventLoggerService.logSecurityEvent(LocalDate.now(),operation.toUpperCase()+"_USER",userEmail ,"Unlock user "+userEmail, "/api/admin/user/access");
          //isme sub me wo anan chahiye jis ne lock kia ya unlock kia , aur ye admin hi krta hai , to either me AuthenticationPrinciple se authenticated user ko nikaal k
          // likhun ya filhaal john doe ko hi likhdete hen q k ek hi admin h hamare case me


          userService.changeAccessOfUser(userService.getUser(userEmail), operation , subjectUser.getEmail());
//          userService.changeAccessOfUser(userEmail,operation);
          //lock , unlck , dono me ek hi status jarha h, mene setting ki hai k lock or unlock to aa hi rhe hen , bas ed ka farq h to wo laga dia
          return ResponseEntity.ok(Map.of("status","User "+ userEmail.toLowerCase() +" "+operation.toLowerCase()+"ed!"));
     }

     @PutMapping("/api/admin/user/role") // required = false for 59
     public ResponseEntity changeUserRole(@RequestBody(required = false) Map<String, String> req , @AuthenticationPrincipal User subjectUser  )
     {
          // we will see k inhen idhr dalna h ya service me
          if(req.get("operation").equalsIgnoreCase("grant")){
//               securityEventLoggerService.logSecurityEvent(LocalDate.now(),"GRANT_ROLE","johndoe@acme.com","Grant role "+req.get("role")+" to "+req.get("user").toLowerCase(), "/api/admin/user/role");
//test 24
          }
          else if(req.get("operation").equalsIgnoreCase("remove")){

//          securityEventLoggerService.logSecurityEvent(LocalDate.now(),"REMOVE_ROLE","johndoe@acme.com","Remove role "+req.get("role")+" from "+req.get("user").toLowerCase(), "/api/admin/user/role");
          }

          //isme sub me wo ana chahiye jis ne grant kia ya remove kia , aur ye admin hi krta hai , to either me AuthenticationPrinciple se authenticated user ko nikaal k
          // likhun ya filhaal john doe ko hi likhdete hen q k ek hi admin h hamare case me

          return ResponseEntity.ok( userService.changeUserRole(req.get("user").toLowerCase(),req.get("role").toUpperCase(),req.get("operation").toUpperCase(),subjectUser.getEmail()));

     }
     @DeleteMapping(value = {"api/admin/user/{userEmail}", "api/admin/user/","api/admin/user"}) // for test 34 method not foudn error
//     @DeleteMapping("/api/admin/user/{userEmail}")
     public ResponseEntity deleteUser(@PathVariable(value = "userEmail" ,required = false) String userEmail , @AuthenticationPrincipal User subjectUser) //required= false for test 34 ,500 serever error, reuqired userEmail error
     { //is ko find kr k del krna hai , if everything ok , else 404 not found , admin cant delete admin 400. "Can't remove ADMINISTRATOR role!"

//          securityEventLoggerService.logSecurityEvent(LocalDate.now(),"DELETE_USER","johndoe@acme.com",userEmail, "/api/admin/user");
          if (userEmail==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No user email provided to delete!");
          userService.deleteUser(userEmail.toLowerCase() , subjectUser.getEmail());
          return ResponseEntity.ok(Map.of("user",userEmail,"status","Deleted successfully!"));

     }
     @GetMapping("/api/admin/user/")//test 31 user k baad slash lagani thi
     public ResponseEntity showAllUsers()
     {


          System.out.println("yahan get user me aya" );
          return ResponseEntity.ok( userService.getAllUsers());

          //should return all users in ascending order by id
//          Return an empty JSON array if there's no information.

     }

}
