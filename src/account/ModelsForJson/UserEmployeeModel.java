package account.ModelsForJson;

import account.Entities.Employee;
import account.Entities.User;
import org.springframework.stereotype.Component;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component // to autowire it in EMPL SErvice
public class UserEmployeeModel {
    //ye class bas hamare mtlb ka response generate krn ek lie banae hai
    private String name;
    private String lastname;
    private Date period;
    private Long salary;

    public UserEmployeeModel() {}


    public UserEmployeeModel(String name ,String lastname, Date period, Long salary ){

        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    public List<UserEmployeeModel> toUserEmplModelList(List<Employee> employeeList , User user){
            List<UserEmployeeModel> userEmployeeModelList=new ArrayList<>();
        for (Employee employee: employeeList             ) {
            System.out.println("Retrieved period is: "+employee.getPeriod());
            UserEmployeeModel userEmployeeModel=new UserEmployeeModel(
                    user.getName(),
                    user.getLastname(),
                    employee.getPeriod(),
                    employee.getSalary() );
            userEmployeeModelList.add(userEmployeeModel);
            System.out.println("Responded from userEmpModel to web period is: "+userEmployeeModelList.get(userEmployeeModelList.indexOf(userEmployeeModel)).getPeriod());

        }
        return userEmployeeModelList;
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

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

//    public Date getPeriod() {
    public String getPeriod() {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.US);
        String[] monthNames = symbols.getMonths();
        int monthIndex = period.getMonth();
        String monthName = monthNames[monthIndex];
        int year = period.getYear() + 1900; // Adding 1900 as Date.getYear() returns the year relative to 1900

        return monthName + "-" + year;
//        return period;
    }

    public String getSalary() {
//        Long salary=   ;//12456;
                Long dollar=salary/100; // gives number 124
                Long cent=salary%100;   // givees last two number 56
//                System.out.println(dollar+" dollar(s) "+cent+" cent(s)");
        return dollar+" dollar(s) "+cent+" cent(s)";
    }

    public void setPeriod(Date period) {
        this.period = period;
    }


    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
