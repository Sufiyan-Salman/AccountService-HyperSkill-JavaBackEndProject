package account.CustomHandlers;

import java.io.IOException;
import java.time.LocalDate;

import account.Entities.User;
import account.Services.SecurityEventLoggerService;
import account.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
// ye tab use kia jata h jab hamara koi login page ho aur http basic kbajaye ham form based login kren
    //currently useless
    //as it was used with login and I am using hhtp basic auth , so what I did was , I used rest Authentication entry point and wrote my authentication failure logic there
    @Autowired
    private UserService userService;

    @Autowired
    SecurityEventLoggerService securityEventLoggerService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        System.out.println("ye aaya faillure me req get param se : "+request.getParameter("Username"));

        System.out.println("ye aaya faillure me req get attr se : "+request.getAttribute("Username"));
        String email = request.getParameter("email");
        User user = userService.getUser(email);




        System.out.println("wo url jiski request gae thi , sending from failure handler"+request.getRequestURI());
        if (user != null) {
            if (user.isAccountNonLocked()) {
                //agar locked account se login kia to login failed ka event generate nai hona chaihye //test 57 maybe
                if (user.getNoOfRepetitiveWrongLoginAttempts() < UserService.MAX_FAILED_ATTEMPTS ) {
                    securityEventLoggerService.logSecurityEvent(LocalDate.now(),"LOGIN_FAILED",request.getParameter("email"),request.getRequestURI(), request.getRequestURI());
                    //isme obj wo path ana chahiye jahan req jane pe fail hua hai
                    userService.increaseNoOfConsecutiveFailedAttempts(user);
                } else if((user.getNoOfRepetitiveWrongLoginAttempts() == UserService.MAX_FAILED_ATTEMPTS ) && !userService.hasAdministrativeRole(user)){
//                    userService.lock(user);
                    //in dono ko sath me ana h
                    //agar administrator nai h to hi lock hoga, wrna nai
                    securityEventLoggerService.logSecurityEvent(LocalDate.now(),"BRUTE_FORCE",request.getParameter("email"),request.getRequestURI(), request.getRequestURI());
                    securityEventLoggerService.logSecurityEvent(LocalDate.now(),"LOCK_USER",request.getParameter("email"),"Lock user +"+request.getParameter("email"), request.getRequestURI());
                    //isme obj wo path ana chahiye jahan req jane pe block hua hai
                    userService.changeAccessOfUser(user,"LOCK" , user.getEmail());
                    exception = new LockedException("Your account has been locked due to 5 failed attempts.");
                }
            }
//            else if (!user.isAccountNonLocked()) {
            //test
//                if (userService.unlockWhenTimeExpired(user)) {
//                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
//                }
//            }

        }

//        super.setDefaultFailureUrl("/login?error");

        super.onAuthenticationFailure(request, response, exception);
    }

}