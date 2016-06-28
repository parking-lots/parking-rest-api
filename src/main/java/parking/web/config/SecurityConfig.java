package parking.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import parking.security.HttpAuthenticationEntryPoint;
import parking.security.ParkingAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private HttpAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private ParkingAuthenticationProvider authenticationProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authenticationProvider(authenticationProvider)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/admin/users").hasAnyRole("CAN_CREATE_NEW_USER")
                .antMatchers(HttpMethod.PUT, "/admin/users").hasRole("CAN_CREATE_NEW_USER")
                .antMatchers(HttpMethod.POST, "/admin/users/{username}").hasAnyRole("CAN_CREATE_NEW_USER")
                .antMatchers(HttpMethod.DELETE, "/admin/users/{username}").hasAnyRole("CAN_CREATE_NEW_USER")
                .antMatchers(HttpMethod.GET, "/admin/parkings/{type}").hasAnyRole("CAN_CREATE_NEW_USER")
                .antMatchers(HttpMethod.PUT, "/parking/availability").hasRole("CAN_SHARE_PARKING")
                .antMatchers(HttpMethod.DELETE, "/parking/availability").hasRole("CAN_SHARE_PARKING")
                .antMatchers(HttpMethod.GET, "/parking/available").hasAnyRole("CAN_ATTEND_PARKING", "CAN_SHARE_PARKING")
                .antMatchers(HttpMethod.PUT, "/parking/{lotNumber}/reservation").hasRole("CAN_ATTEND_PARKING")
                .antMatchers(HttpMethod.DELETE, "/parking/reservation").hasRole("CAN_SHARE_PARKING")
                .antMatchers(HttpMethod.POST, "/profile").hasRole("CAN_ATTEND_PARKING")
                .antMatchers(HttpMethod.POST, "/admin/users/{username}/parking/detach").hasAnyRole("CAN_CREATE_NEW_USER");
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
