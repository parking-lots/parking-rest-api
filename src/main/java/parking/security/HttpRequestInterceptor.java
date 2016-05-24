package parking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import parking.exceptions.ApplicationException;
import parking.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpRequestInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserService userService;

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws ApplicationException {

        if(request.getUserPrincipal() == null){
            try {
                userService.reinstateSession(request);
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
