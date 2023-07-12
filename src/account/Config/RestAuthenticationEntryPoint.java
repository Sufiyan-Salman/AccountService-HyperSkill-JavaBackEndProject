package account.Config;

import account.Entities.User;
import account.Services.SecurityEventLoggerService;
import account.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;

// jab auntuhorized hota h to wo yahan se response jata h , agar ham ise custom kr k ye wala bhejen to hamare hisab ka response jayega , failiure handle krne klie banaya  h ye

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Autowired
        private UserService userService;

        @Autowired
        SecurityEventLoggerService securityEventLoggerService;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {


        //========================
        String authorizationHeader = request.getHeader("Authorization");
        String email=null;
        if (authorizationHeader != null) { //test 18, kabhi bina user k agar bheja to kahli ayega islie

        email = extractUsername(authorizationHeader); //hmne email islie likha q k hamari email is ourusername
        System.out.println("ye aaya entry point me req header se : "+ email);
        }

//jo basic auth me ham username password bhejte hen , wo header me jata h ,islie header se decode kr k nikala hamen , parameter ya attribute me naihota hia wo
//        System.out.println("ye aaya entry point me req get param se : "+request.getParameter("Username"));
//        System.out.println("ye aaya entry point me req get attr se : "+request.getAttribute("Username"));
//        System.out.println("ye aaya entry point me req get param se : "+request.getParameter("email"));
//        System.out.println("ye aaya entry point me req get attr se : "+request.getAttribute("email"));
//        String email = request.getParameter("email");
        User user = userService.getUser(email);




        System.out.println("wo url jiski request gae thi , sending from entrypoint"+request.getRequestURI());
//        if (email != null) securityEventLoggerService.logSecurityEvent(LocalDate.now(), "LOGIN_FAILED", email, request.getRequestURI(), request.getRequestURI()); //test 19
        if (user != null) {
            if (user.isAccountNonLocked()) {
                //agar locked account se login kia to login failed ka event generate nai hona chaihye //test 57 maybe
                if (user.getNoOfRepetitiveWrongLoginAttempts() < UserService.MAX_FAILED_ATTEMPTS) {
                    //isme obj wo path ana chahiye jahan req jane pe fail hua hai
                    System.out.println("ek attempt barh gaya");
                    securityEventLoggerService.logSecurityEvent(LocalDate.now(), "LOGIN_FAILED", email, request.getRequestURI(), request.getRequestURI());
                    userService.increaseNoOfConsecutiveFailedAttempts(user);
                } else if ((user.getNoOfRepetitiveWrongLoginAttempts() == UserService.MAX_FAILED_ATTEMPTS) && !userService.hasAdministrativeRole(user)) {
                    //agar administrator nai h to hi lock hoga, wrna nai
                    System.out.println("attempts max hogaye");
                    securityEventLoggerService.logSecurityEvent(LocalDate.now(), "LOGIN_FAILED", email, request.getRequestURI(), request.getRequestURI()); // test 54
                    securityEventLoggerService.logSecurityEvent(LocalDate.now(), "BRUTE_FORCE", email, request.getRequestURI(), request.getRequestURI());
                    //==================
                    //this
                    userService.changeAccessOfUser(user, "LOCK" , user.getEmail());
                    //================
                    //orthis : both will perfofrm in the same way , but neeche wale me ham direct user ko access krrhe hen while upr wale me ham user service ko access krrrhe hen
//                    securityEventLoggerService.logSecurityEvent(LocalDate.now(), "LOCK_USER", email, "Lock user " + email, request.getRequestURI());
//                    user.setAccountNonLocked(false);
//                    userService.saveUserChanges(user);
                    //================

                    authException = new LockedException("Your account has been locked due to 5 failed attempts.");
                    //jo hamara authorized k baad msg araha tha , wo yahan se change hojayega
                }
            }
        }
//        else securityEventLoggerService.logSecurityEvent(LocalDate.now(), "LOGIN_FAILED", email, request.getRequestURI(), request.getRequestURI()); // agar headder khali aaya to ye tab b save krlega jab k us waqt ye save nai krna hai , to islie hamne neeche wali logic use ki
        else if (email !=null )securityEventLoggerService.logSecurityEvent(LocalDate.now(), "LOGIN_FAILED", email, request.getRequestURI(), request.getRequestURI()); // to header jab khali na hua aur lekin esa attempt aaya jo hamara db ka user b na hua to uslie ham use krenge , when I email is not null but the user does not exist
        //========================
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
//yahn changes jo ki to wo post man me show hui , unauthorized ki jaga bad request gae , as we wanted

    }
    private String extractUsername(String authorizationHeader) {
        // Authorization header format: Basic base64EncodedCredentials
        // Extract base64 encoded credentials
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();

        // Decode base64 encoded credentials to obtain username:password
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes);

        // Extract username from credentials
        String[] parts = credentials.split(":", 2);
        return parts[0];
    }
}
