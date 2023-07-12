package account.Config;

//import lombok.RequiredArgsConstructor;
import account.CustomHandlers.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity()
@EnableMethodSecurity(prePostEnabled = true)

//@EnableWebSecurity  //(prepostEnabled=true ) , jab hemen ye krna hia to jis controller pe ye laga a hai wahan preauthrorize lagate hen as written in line 55

//@RequiredArgsConstructor
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;


//    @Autowired
//    private final JwtAuthenticationFilter jwtAuthFilter;


//    @Autowired//ye na lagao to masla krrha tha
//    private AuthenticationProvider authenticationProvider;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;//// reason neeche haai
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                // jo mene new exceptions banae theen , ye use use krne klie mene dala tha , lekin comment out krne k baad b chal rha hai se bhi chal rha hai , aur iski zarurt prh rhi
                .and()
                .csrf()
                .disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .authorizeHttpRequests()
                //ye jo controller hai m ye open for all hai
//                .requestMatchers("/**")
                .requestMatchers(HttpMethod.POST,"/api/auth/signup","/h2", "/actuator/shutdown","/error"/*,"/api/acct/payments"*/ ).permitAll()
                .requestMatchers(/*HttpMethod.PUT,"/api/acct/payments",*/"/error" ).permitAll()
                .requestMatchers(toH2Console())
                //sirf in url ko permit krega for outsiders
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //hamne stateless isliye rkha take server kuch b na rkhe aur har baar token ko validate  , means new session for each request
                .and()
                .userDetailsService(customUserDetailService); // srif is se bhi chal rha hai , is ke zariye application config me kuch b nai krna  prh rha hai
//                .authenticationProvider(authenticationProvider) ;// application config me hai , is se bhi chalega lekin phir config me jake sab uncomment krna prega



        return http.build();
    }

}
