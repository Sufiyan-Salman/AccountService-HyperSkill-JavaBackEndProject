package account.Dao;

import account.Entities.Employee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeRepo extends CrudRepository<Employee, Integer> {
//public interface EmployeeRepo extends JpaRepository <Employee, Integer>{

    public List<Employee> findByEmailIgnoreCaseOrderByPeriodDesc(String employeeEmail);


    public Employee findByEmailIgnoreCaseAndPeriod(String employeeEmail, Date period);
//======================
    //This is for 3.    wrt updateEMployee in Emplservice
    int deleteByEmailAndPeriod(String emplEmail, Date period);
//=====================
    //This is for 2. , 3.   wrt updateEMployee in Emplservice
    boolean existsByEmailIgnoreCase(String email);


//====================
    //This section is for 2. wrt updateEMployee in Emplservice
    @Modifying(/*clearAutomatically = true   */)
    @Query("update Employee e set e.salary=:salary where e.email =:email and e.period=:period")
    int updateEmployeeSalary(@Param("email") String employeeEmail,@Param("period")  Date period , @Param("salary")  Long salary);
//======================
}
