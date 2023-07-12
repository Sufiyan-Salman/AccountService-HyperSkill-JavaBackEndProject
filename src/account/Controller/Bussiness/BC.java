package account.Controller.Bussiness;

import account.Entities.Employee;
import account.Services.EmployeeService;
import account.Services.SecurityEventLoggerService;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import account.Entities.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Validated
    @PreAuthorize("hasAnyRole('ACCOUNTANT')")
public class BC {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    SecurityEventLoggerService securityEventLoggerService;


    @PreAuthorize("hasAnyRole('USER','ACCOUNTANT')")
    @GetMapping("/api/empl/payment")
    //form se data ayegi isme
    public ResponseEntity showEmployeePayroll(@AuthenticationPrincipal User user, @RequestParam(value = "period" , required=false) /*@JsonFormat(pattern = "MM-yyyy")*/ @DateTimeFormat(pattern = "MM-yyyy")/* @Temporal(TemporalType.DATE)*/ Date period) throws ParseException {

        if(period != null){

          //date time format incoming period ko khud format krrha hai and then ham usi ko use kr k DB se search krrhe hen as it is exactly same to what we saved after deserializing
            System.out.println("period incoming: "+period);

            System.out.println("There is period available in request, so responding with specific record");
           return ResponseEntity.ok( employeeService.showEmployeePayments(period , user));

        }
        else{
            System.out.println("There is no period available in request, so responding with full list");
           return ResponseEntity.ok( employeeService.showEmployeePayments(user));
//             employeeService.showEmployeePayments(user);
        }


    }


    //=====================



//    @RolesAllowed("ROLE_ACCOUNTANT")
    @PreAuthorize("hasRole('ACCOUNTANT')")

    //only accountant allowed
    @PostMapping("/api/acct/payments")
    public ResponseEntity uploadEmployeesPayroll(@RequestBody(required = false) List<@Valid Employee> employeeList){ // false for test 62
       // uploads payrolls

        employeeService.addEmployeesPayment(employeeList);

        return ResponseEntity.ok(Map.of("status","Added successfully!"));


    }

//    @RolesAllowed("ROLE_ACCOUNTANT")
    @PreAuthorize("hasRole('ACCOUNTANT')")
    //only accountant allowed
    @PutMapping("/api/acct/payments")
    public ResponseEntity updateEmployeePayroll(@RequestBody @Valid Employee employee){
     //updates payment information
        employeeService.updateEmployeePayment(employee);
//        employeeService.addEmployeesPayment(List.of(employee));// used when I also use delete query, For 3. wrt updatemethod in EMPLservice
        return ResponseEntity.ok(Map.of("status","Updated successfully!"));
    }
}
