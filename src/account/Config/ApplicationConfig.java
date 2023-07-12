package account.Config;

import account.Dao.UserRepo;
//import com.java.springclasses.Security.CustomUserDetailService;
//import com.java.springclasses.jwt.user.UserRepositoryOFAmigo;
//import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@RequiredArgsConstructor
// final walo ka cons banadega
public class ApplicationConfig {


  @Autowired
  CustomUserDetailService customUserDetailService;
//=============
//    private UserRepo userRepository;
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//
//        //agar ham new userdetaillsservice banate to hme loadusername ko override krna prnta hai , as we did in costumUserDetails service , but hamne lambda use kia to hmen ye nai krna prha
//    }
  //==============

//=========================
  //dusre triqe se kia hai , hamne custom user details ko autowire kia aur usko http basic me user details me dalidia , aur ye ese bbhi chal rha hai
// ise use krne klie ise aur uske neeche ko uncmment krenge aur phir authenticatin provider ko bhi uncomment krenge from security filter chain
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////        authProvider.setUserDetailsService(userDetailsService());
//    authProvider.setUserDetailsService(customUserDetailService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }

  //==========================

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

}
