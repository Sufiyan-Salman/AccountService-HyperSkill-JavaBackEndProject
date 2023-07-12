package account.CustomHandlers;

import account.Entities.User;
import account.Services.SecurityEventLoggerService;
import account.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
// ye tab use kia jata h jab hamara koi login page ho aur http basic kbajaye ham form based login kren
    //currently useless
    @Autowired
    private UserService userService;

    @Autowired
    SecurityEventLoggerService securityEventLoggerService;
    //ye b nai chal rha this and failure

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User user =  (User) authentication.getPrincipal();
//        User user = userDetails.getUser();
        if (user.getNoOfRepetitiveWrongLoginAttempts() > 0) {
            userService.resetNoOfConsecutiveFailedAttempts(user.getEmail());
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
//    As you can see, upon the user’s successful logi