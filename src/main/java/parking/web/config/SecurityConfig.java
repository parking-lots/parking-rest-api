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
import parking.security.ParkingAuthenticationProvider;
import parking.security.HttpAuthenticationEntryPoint;

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
                .antMatchers(HttpMethod.PUT, "/parking/available").hasRole("CAN_SHARE_PARKING")
                .antMatchers(HttpMethod.PUT, "/parking/reserved").hasRole("CAN_ATTEND_PARKING")
                .antMatchers(HttpMethod.PUT, "/profile/password").hasRole("CAN_ATTEND_PARKING")
                .antMatchers(HttpMethod.GET, "/parking/available").hasAnyRole("CAN_ATTEND_PARKING", "CAN_SHARE_PARKING")
                .antMatchers(HttpMethod.POST, "/admin/user/create").hasRole("CAN_CREATE_NEW_USER");
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
