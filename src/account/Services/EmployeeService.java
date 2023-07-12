package account.Services;

import account.Dao.EmployeeRepo;
import account.Dao.UserRepo;
import account.Entities.Employee;
import account.Entities.User;
import account.ModelsForJson.UserEmployeeModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeService
{
    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserEmployeeModel userEmployeeModel;


    //add payroll
    //update payroll
    //show payroll

    public UserEmployeeModel showEmployeePayments(Date period , User user){
            //agar period specify kia hai to to zahir h ek hi row ayegi mtlb ek record

            Employee employee=employeeRepo.findByEmailIgnoreCaseAndPeriod(user.getEmail(), period);
        System.out.println("The retrieved employee is: "+employee);
            userEmployeeModel.setName(user.getName());
            userEmployeeModel.setLastname(user.getLastname());
            userEmployeeModel.setSalary(employee.getSalary());
            userEmployeeModel.setPeriod(employee.getPeriod());
            return userEmployeeModel;
    }
    public <T> T showEmployeePayments(User user) {
        //this is when period is not specified and we have to return list in desc order by period
        List<Employee> employeePaymentList=new ArrayList<>();
        System.out.println(user.getEmail());
        employeePaymentList= employeeRepo.findByEmailIgnoreCaseOrderByPeriodDesc(user.getEmail());
        System.out.println("The retrieved employee list is: "+employeePaymentList);
        if (employeePaymentList.isEmpty()){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"List is empty"); //generates error for test 19
            return (T) employeePaymentList;
        }
        else if(employeePaymentList.size()>1){
            //ek email se related bht se payment records hoskte hen
            System.out.println("List me ek se zydah element hai");
            return (T) userEmployeeModel.toUserEmplModelList(employeePaymentList , user);
//            return (T) "hi";
        //should be in decending order

        }
        else{
            System.out.println("List me ek ya us se km element hai");

            //agar ek hi record nikle to
            userEmployeeModel.setName(user.getName());
            userEmployeeModel.setLastname(user.getLastname());
            userEmployeeModel.setSalary(employeePaymentList.get(0).getSalary());
            userEmployeeModel.setPeriod(employeePaymentList.get(0).getPeriod());
            return (T) userEmployeeModel;
        }
    }


    @Transactional
    public void addEmployeesPayment(List<Employee> employeePaymentList){
        //employe and payment shuld be unique , for this  , mene is method me b logic likhi aur unique ka constraint bhi daala hai
        for ( Employee employee  : employeePaymentList   ){

            System.out.println("The list to be added: "+employeePaymentList);
            System.out.println("Recieved Period is: "+employee.getPeriod());
            if (!userRepo.existsByEmailIgnoreCase(employee.getEmail()))
            {

                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No such user i.e: " +employee.getEmail()+"  exist for which payment entry can be made!!");
            }
            //===================
//             // mene unique pair ka constraint lagaya hua hai at entity , islie is ki zarurt nai prhi , wrna ye use hota
//            if(employeeRepo.findByEmailIgnoreCaseAndPeriod(employee.getEmail(),employee.getPeriod())!=null){
//                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This payment entry has already been made!");
//            }
            //=====================
            else{

                employeeRepo.save(employee);
            }


        }

    }

    @Transactional
    public void updateEmployeePayment(Employee employee) {
        //1. ek triqa ye k jis employee ko update krna hai , use retrieve kro , usi me salary change kro then usi ko dubara save kro , which gets updated autmatically as Id is same
        //2.dusra triqa ye k hamari custom update query se update krden direct
        //3. teesra trriqa ye k pehle wale ko repo query k zariye del kro then new ko save krlo , jis se Id gets changed
        Employee employeeToBeUpdated=employeeRepo.findByEmailIgnoreCaseAndPeriod(employee.getEmail(),employee.getPeriod()); //for 1.
        if (employeeToBeUpdated==null){ // for 1.
//        if (!employeeRepo.existsByEmailIgnoreCase(employee.getEmail())){ // for 2. , 3.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No such entry found to update"); // for all
        }
        //==================
        //this section is for 1.
        employeeToBeUpdated.setSalary(employee.getSalary());
        employeeRepo.save(employeeToBeUpdated); //  ye update islie  krrha q k empl ko id k hisab se idetify krega and as they are same so it gets updated
        //==================
        //THis sesction is for 2.
//        System.out.println("Updated with rows no: "+employeeRepo.updateEmployeeSalary(employee.getEmail().toLowerCase() , employee.getPeriod() ,employee.getSalary()));
//       //====================
        //This section is for 3.
//        System.out.println("Deleted with rows no: "+employeeRepo.deleteByEmailAndPeriod(employee.getEmail().toLowerCase() , employee.getPeriod()));
//        System.out.println("Deletion done");
//        employeeRepo.save(employee); // as the method is transactional , we save here as transaction is not completed yet , so controller se hi phir save ka method call krnap rhega
        //======================

    }

}
