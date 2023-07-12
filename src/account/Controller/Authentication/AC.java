package account.Controller.Authentication;

import account.Entities.User;
import account.Exceptions.UserExistsException;
import account.Services.SecurityEventLoggerService;
import account.Services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
// dto kiahota hai?
// @data and @ builder annotations , lombok annotations
//mapper
//what are above written things?


@RestController
@RequestMapping("/api/auth")
//@Validated
public class AC {

    @Autowired
    UserService userService;
    @Autowired
    SecurityEventLoggerService securityEventLoggerService;



//    @PostMapping("/api/auth/signup")
    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody User user ) {
//    securityEventLoggerService.logSecurityEvent(LocalDate.now(),"CREATE_USER","Anonymous", user.getEmail().toLowerCase(), "/api/auth/signup");
//    securityEventLoggerService.logSecurityEvent(LocalDate.now(),"CREATE_USER","Anonymous","johndoe@acme.com", "/api/auth/signup");
    //obj me wo email , jise register kia hai , IMO
        return ResponseEntity.ok(userService.addUser(user));


//==================================
    }
    //@PostMapping("/changepass")
    @PreAuthorize("hasAnyRole('USER','ACCOUNTANT','ADMINISTRATOR')")
    //allowed by these
        @PostMapping("/changepass")
        public ResponseEntity changePassword(@AuthenticationPrincipal User user ,  @RequestBody Map<String, String> requestBody){
        //====================================
        //another way of using authentication principle
        //  (@AuthenticationPrincipal UserDetails userDetails_ofLoggedInUser , @Request...){
//       //is principle se hamen wo user miilega jo hamne abhi hhtp basic auth me username password me dal ahai , agar to wo exist krta hoga to
//        User = (User) userDetails_ofLoggedInUser;
        //================================
            String new_password = requestBody.get("new_password");
            //test 29
//        securityEventLoggerService.logSecurityEvent(LocalDate.now(),"CHANGE_PASSWORD", user.getEmail(), "johndoe@acme.com", "/api/auth/changepass");


        System.out.println(new_password);

        //ye service me likha hona chaiye , likdhia mene
//        if(new_password==null || new_password.isEmpty() || new_password.length()<12){
////              throw ResponseStatusException
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password length must be 12 chars minimum!");
////             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The password length must be at least 12 chars!");
//        }
//        else{
           userService.changeUserPassword(user, new_password , user.getEmail());

//        }


         return ResponseEntity.ok(Map.of("email", user.getEmail(),
                "status", "The password has been updated successfully"));
//                "status", "Password length must be 12 chars minimum!"));


    }
}
