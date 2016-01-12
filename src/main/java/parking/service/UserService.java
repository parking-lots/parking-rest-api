package parking.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import parking.beans.document.Permission;
import parking.beans.document.Role;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.UserException;
import parking.beans.document.Account;
import parking.repositories.AccountRepository;
import parking.repositories.PermissionRepository;
import parking.repositories.RoleRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.*;


@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Optional<Account> getLoggedUser() throws UserException {
        return Optional.ofNullable(accountRepository.findByUsername(getCurrentUserName()));
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public Profile getCurrentUserProfile() throws UserException {
        return new Profile(getLoggedUser().get());
    }

    public Account getCurrentUser() throws UserException {
        Optional<Account> currentUser = getLoggedUser();
        if (!currentUser.isPresent()) {
            throw new UserException("user_not_found");
        }

        return getLoggedUser().get();
    }

    public void login(LoginForm user, HttpServletRequest request) throws AuthenticationCredentialsNotFoundException, UserException {
        Account userAccount = validateUser(user);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userAccount.getUsername(),
                                userAccount.getPassword(),
                                getAuthorities(userAccount.getRoles())
                                )
                )
        );

        request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    public Account validateUser(LoginForm loginForm) throws AuthenticationCredentialsNotFoundException, UserException {
        if (getLoggedUser().isPresent()) {
            throw new UserException("User already logged");
        }

        Account account = accountRepository.findByUsername(loginForm.getUsername());

        if (account == null || !loginForm.getPassword().equals(account.getPassword())) {
            throw new UserException("Wrong credentials");
        }

        return account;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {
        List<String> privileges = new ArrayList<String>();
        List<Permission> collection = new ArrayList<Permission>();
        for (Role role : roles) {
            collection.addAll(role.getPermissions());
        }
        for (Permission item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String privilege : privileges) {
            SimpleGrantedAuthority uniqPrivelege = new SimpleGrantedAuthority("ROLE_" + privilege);
            if (!authorities.contains(uniqPrivelege))
                authorities.add(uniqPrivelege);
        }
        return authorities;
    }

    //TODO: remove when registration will be done
    public void createUser() {
        Account user = new Account();
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(roleRepository.findByName("ROLE_OWNER"));
        roleList.add(roleRepository.findByName("ROLE_USER"));

        List<Account> users = accountRepository.findAll();

        System.out.print(user);

    }
}
