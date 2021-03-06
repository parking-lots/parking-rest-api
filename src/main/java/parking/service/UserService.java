package parking.service;


import org.bson.types.ObjectId;
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
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Permission;
import parking.beans.document.Role;
import parking.beans.request.ChangePassword;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.ApplicationException;
import parking.exceptions.UserException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.helper.ProfileHelper;
import parking.repositories.AccountRepository;
import parking.repositories.RoleRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private ExceptionHandler exceptionHandler;


    public Optional<Account> getLoggedUser() throws UserException {
        return Optional.ofNullable(accountRepository.findByUsername(getCurrentUserName()));
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public Profile getCurrentUserProfile() throws UserException {
        return new Profile(getLoggedUser().get(), true);
    }

    public Account getCurrentUser(HttpServletRequest request) throws ApplicationException {
        Optional<Account> currentUser = getLoggedUser();
        if (!currentUser.isPresent()) {
            //throw new UserException("user_not_found");
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        }

        return getLoggedUser().get();
    }

    public void login(LoginForm user, HttpServletRequest request) throws AuthenticationCredentialsNotFoundException, ApplicationException {

        LoginForm userToValidate = new LoginForm();
        userToValidate.setPassword(user.getPassword());
        userToValidate.setUsername(user.getUsername().toLowerCase());

        Account userAccount = validateUser(userToValidate, request);

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

//        RequestContextUtils.getLocaleResolver(request)
       // new SessionLocaleResolver().setLocale(request, null, Locale.FRENCH);
    }

    public Account validateUser(LoginForm loginForm, HttpServletRequest request) throws AuthenticationCredentialsNotFoundException, ApplicationException {
        if (getLoggedUser().isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_ALREADY_LOGGED, request);
        }

        Account account = accountRepository.findByUsername(loginForm.getUsername());

        if (account == null || !ProfileHelper.checkPassword(loginForm.getPassword(), account.getPassword())) {
            throw exceptionHandler.handleException(ExceptionMessage.WRONG_CREDENTIALS, request);
        }

        return account;
    }

    public void changePassword(ChangePassword password, HttpServletRequest request) throws ApplicationException {
        Account account = getCurrentUser(request);

        account.setPassword(ProfileHelper.encryptPassword(password.getNewPassword()));

        accountRepository.save(account);
    }


    private Optional<Account> getUserByUsername(String username) {
        Account userName = accountRepository.findByUsername(username);

        return Optional.ofNullable(userName);
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

    public Account createUser(Account newAccount, HttpServletRequest request) throws ApplicationException {

       /* Account userToCreate = new Account();
        userToCreate.setUsername(newAccount.getUsername().toLowerCase());
        userToCreate.setFullName(newAccount.getFullName());
        userToCreate.setParking(newAccount.getParking());
        */
        newAccount.setUsername(newAccount.getUsername().toLowerCase());

        if (getUserByUsername(newAccount.getUsername()).isPresent()) {
            String msg;

            throw exceptionHandler.handleException(ExceptionMessage.USER_ALREADY_LOGGED, request);
        }

        newAccount.setId(new ObjectId());
        newAccount.setPassword(ProfileHelper.encryptPassword(newAccount.getPassword()));
        newAccount.addRole(roleRepository.findByName(Role.ROLE_USER));

        return accountRepository.insert(newAccount);
    }

    public void attachParking(Account user, Integer number, HttpServletRequest request) throws ApplicationException {
        Optional<ParkingLot> parking = Optional.ofNullable(parkingService.getParkingByNumber(number, request));
        if (Optional.ofNullable(parking.get().getOwner()).isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_OWNED_BY_ANOTHER, request);

        }
        user.addRole(roleRepository.findByName(Role.ROLE_OWNER));
        user.setParking(parking.get());

        accountRepository.save(user);
    }

}
