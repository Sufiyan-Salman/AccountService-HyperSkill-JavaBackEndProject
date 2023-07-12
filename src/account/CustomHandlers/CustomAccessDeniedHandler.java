package account.CustomHandlers;

import account.Services.SecurityEventLoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    SecurityEventLoggerService securityEventLoggerService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
//to try:
        System.out.println("ye aaya acces den me req get param se : "+request.getParameter("Username"));
//        request.getParameter("Username");
        System.out.println("ye aaya acces den me req get attr se : "+request.getAttribute("Username"));
//        request.getAttribute("Username");
        //as authenticated user is saved in authentication object ,(that is created by authentication provided ),that is contained by securityContext holder so thats the way to get the authenticated user details

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        System.out.println("ye item accden handler se gaye: ");
        securityEventLoggerService.logSecurityEvent(LocalDate.now(),"ACCESS_DENIED",userEmail,request.getRequestURI(), request.getRequestURI());
        //isme check krna prhega k subject me kia ayega , wese to payments me sjirf admin hi allowed nai h ,islie "johndoe@acme.com" maybe , lekin agar kabhi koi aur b allowed na hua
//        to islie isme mere hisab se jis email se access denied hua hai , wo aani chahiye
        //object me wo endpoint ayega jahan access denied hua
//



        errorResponse.put("timestamp", OffsetDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_FORBIDDEN);
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "Access Denied!"); // test 59
        errorResponse.put("path", request.getRequestURI());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        //=========================
        //another way, we will check it later
        //gives this output: <403 FORBIDDEN Forbidden,Access denied!,
//        ResponseEntity<Object> errorResponse = new ResponseEntity<>(
//                "Access denied!",
//                HttpStatus.FORBIDDEN
//        );
//
//        response.setContentType("application/json");
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        response.getWriter().write(errorResponse.toString());
    }

//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//
//    }
}
