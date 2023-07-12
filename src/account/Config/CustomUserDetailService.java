package account.Config;

import account.Dao.UserRepo;
import account.Entities.User;
import account.Exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//ye hamne http basic and form k lie banaya tha

@Service
@ComponentScan
public class CustomUserDetailService implements UserDetailsService {
    // jab hamne ise implement krlia , to ab hamara in mememory wala function joke mesucrtiyconfig me hai , wo nai chalega , hmen authentication provider apna dena prega
    // phir authenticatino provider jo ham apna banayenge , usme ham wo inmemeory wala userdetails pass krskte hen
     //this is userdetail service impl
    @Autowired
    private UserRepo userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        email=email.toLowerCase();
//        User user = this.userRepository.findByEmail(email); //interface repo me ek alag se method banana prega with name findByUsername(String username)
        //=======================
        User user = this.userRepository.findByEmailIgnoreCase(email); //interface repo me ek alag se method banana prega with name findByUsername(String username)
        if (user == null) {
//            throw new UserNotFoundException ();
//            throw new UsernameNotFoundException("No such user exists");
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No such user exist");// we can use either of them
        }

        return user;
    }
//    above is for original data fetching from db and below is just for testing without db

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        //username or email
//        if (username.equals("hell")) {
//            System.out.println("jiji");
//            return new org.springframework.security.core.userdetails.User("hell", passwordEncoder().encode("hell"),new ArrayList<>());
//            //encode krna zaruri hai q k spring encoded password se hi compare krega
//        }else {
//
//            throw new UsernameNotFoundException("No such user exists");
//
//        }
//    }
    //ye islie banaya hai taake test walel load functaion me ham password encode kr k bhej sken
}
