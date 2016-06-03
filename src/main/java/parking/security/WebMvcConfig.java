package parking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import parking.service.UserService;

/**
 * Created by Lina on 23/05/16.
 */

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Autowired
    HttpRequestInterceptor httpRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpRequestInterceptor);
    }

}
