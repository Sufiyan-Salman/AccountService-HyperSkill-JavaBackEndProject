package account.Entities;

import account.CustomDeserializers.CustomMonthYearDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.Locale;

//@Transient // ye us fijeld pe lagta hia jiska DB me coloumn na banana ho

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "UniqueEmployeeAndPeriod",
                columnNames = { "email", "period"})})
public class Employee {
    //bassically it is a record for employee(user) payments
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //identity mtlb sql wala autoincrement
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emplid;

//    @Column(name = "user_id")
//    private Long userId;
    //ye aur jo user me mene one to many wala payments(employee) ki list hai commented out , inka hia relation aapas me

    @NotBlank(message = "Employee must contain email")
    @JsonProperty("employee")
    private String email;    //it should be user email
    @NotNull(message = "Employee must contain Salary") // doesnt work on long datatype
    @Min(value = 0,message = "Salary should not be negative")
    private Long salary;

//===========================
//    @Temporal(TemporalType.DATE)//test 41  //is se database me bas date store hoti hai andd time is  0 0  0 , lekin comment out krne k vawajood bhi 0 hi jarhahi. But according to hyper skill , jdk8 k baad local date is enough , no need to use this annotation
//    @DateTimeFormat(pattern = "MM-yyyy" ) // this is done by my custom serializer
//    @JsonFormat(pattern = "MM-yyyy")//serialize ye krega , deserialzie is se islie nai krawaya q k wrong month ko validate nai krparha tha ye, balke ye zydah month be accept krrha hai aur saal ko aage bara k savekrrha tha, comment out kro tb b chalra hai nai kro tab b , reason is k ham respon krne k lie userEMp model use krrhe hen to uska getPeriod method khud hamare hisab se bana k respond krrha hai

    @JsonDeserialize(using=CustomMonthYearDeserializer.class)//solved test 41 , deserialize , to format incoming data object
    @NotNull(message = "Period of salary must be defined") // doesnt work on long datatype
    private Date period;
//=========================

    public Long getEmplid() {
        return emplid;
    }

    public void setEmplid(Long emplid) {
        this.emplid = emplid;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
//        x dollars and y cents ka format chhiaye
    }

    public Date getPeriod() {

        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
        // mm yyyy ka format chahiye
    }

    @Override
    public String toString() {
        return "Employee{" +
                "emplid=" + emplid +
                ", employee Email='" + email + '\'' +
                ", salary=" + salary +
                ", period=" + period +
                '}';
    }
}
