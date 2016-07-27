package parking.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;
import parking.helper.AvailableDatesConverter;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.helper.ToolHelper;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.utils.ActionType;
import parking.utils.EliminateDateTimestamp;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParkingService {

    @Autowired
    private LotsRepository lotsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ExceptionHandler exceptionHandler;

    public List<ParkingLot> getAvailable(HttpServletRequest request) throws ApplicationException {

        Account currentUser = userService.getCurrentUser(request);
        List<ParkingLot> parkingLots = lotsRepository.searchAllFields(currentUser);

        List<ParkingLot> filteredLots = parkingLots.stream()
                .filter(val -> val.getUser() != null && currentUser.getUsername().equals(val.getUser().getUsername()))
                .collect(Collectors.toList());

        if (filteredLots.size() > 0) {
            return filteredLots;
        }
        return parkingLots;
    }

    public void freeOwnersParking(Account owner, Integer lotNumber, List<Date> availableDates, HttpServletRequest httpRequest) throws ApplicationException {

        if (owner.getParking() == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, httpRequest);
        }

        if (ToolHelper.hasDuplicates(availableDates)) {
            throw exceptionHandler.handleException(ExceptionMessage.DUBLICATE_DATES, httpRequest);
        }

        AvailableDatesConverter converter = new AvailableDatesConverter();
        List<AvailablePeriod> availablePeriods;

        if (availableDates.size() > 0) {

            availablePeriods = converter.convertToInterval(availableDates);

            for (AvailablePeriod p : availablePeriods) {
                validatePeriod(owner.getParking().getNumber(), p.getFreeFrom(), p.getFreeTill(), httpRequest);
            }

            for (AvailablePeriod p : availablePeriods) {

                validatePeriod(lotNumber, p.getFreeFrom(), p.getFreeTill(), httpRequest);
                lotsRepository.freeOwnersParking(lotNumber, p.getFreeFrom(), p.getFreeTill(), httpRequest);

                Account user = userService.getCurrentUser(httpRequest);
                String userAgent = httpRequest.getHeader("User-Agent");
                logRepository.insertActionLog(ActionType.SHARE, owner, lotNumber, p.getFreeFrom(), p.getFreeTill(), null, user, userAgent);
            }
        }
    }

    public void validatePeriod(Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpServletRequest) throws ApplicationException {
        Date currentDate = new Date();
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        Calendar cal = eliminateDateTimestamp.formatDateForDatabase(currentDate);

        if (freeFrom.compareTo(freeTill) > 0)
            throw exceptionHandler.handleException(ExceptionMessage.START_DATE_LATER_THAN_END_DATE, httpServletRequest);

        if ((freeTill.compareTo(cal.getTime()) < 0) && (freeFrom.compareTo(freeTill) < 1))
            throw exceptionHandler.handleException(ExceptionMessage.END_DATE_IN_THE_PAST, httpServletRequest);

        lotsRepository.checkPeriod(lotNumber, freeFrom, freeTill, httpServletRequest);

    }

    public void recallParking(Date freeFrom, Date freeTill, HttpServletRequest request) throws ApplicationException {

        ParkingLot parking = getParkingNumberByUser();
        if (parking == null) {
            return;
        }

        lotsRepository.recallParking(parking.getNumber(), freeFrom, freeTill);
    }

    public void recallParking(ParkingLot parking, List<Date> availableDates, HttpServletRequest request) throws ApplicationException {
        if (parking == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, request);
        }

        for (Date d : availableDates) {
            lotsRepository.checkRecallDate(parking.getNumber(), d, request);
        }

        for (Date d : availableDates) {
            lotsRepository.recallParking(parking.getNumber(), d, request);
        }

        Account user = userService.getCurrentUser(request);

        List<AvailablePeriod> availablePeriods;
        AvailableDatesConverter converter = new AvailableDatesConverter();
        availablePeriods = converter.convertToInterval(availableDates);

        String userAgent = request.getHeader("User-Agent");
        for (AvailablePeriod availablePeriod : availablePeriods) {
            logRepository.insertActionLog(ActionType.UNSHARE, parking.getOwner(), parking.getNumber(), availablePeriod.getFreeFrom(), availablePeriod.getFreeTill(), null, user, userAgent);
        }
    }

    public void reserve(Integer lotNumber, Account account, HttpServletRequest httpRequest) throws ApplicationException {

        if (account == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, httpRequest);
        }

        ParkingLot lot = lotsRepository.findByNumber(lotNumber);

        if (lot == null) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, httpRequest);
        } else {
            lotsRepository.reserve(lotNumber, account, httpRequest);

            Date currentDate = ToolHelper.getCurrentDate();
            Account user = userService.getCurrentUser(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            logRepository.insertActionLog(ActionType.RESERVE, account, lot.getNumber(), currentDate, currentDate, null, user, userAgent);
        }
    }

    public ParkingLot createLot(ParkingLot parkingLot, HttpServletRequest request) throws ApplicationException {
        ParkingLot existParkingLot;
        try {
            existParkingLot = getParkingByNumber(parkingLot.getNumber(), request);
        } catch (ApplicationException e) {
            existParkingLot = null;
        }
        if (Optional.ofNullable(existParkingLot).isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_ALREADY_EXISTS, request);
        }
        parkingLot.setId(new ObjectId());
        return lotsRepository.insert(parkingLot);
    }

    public ParkingLot getParkingByNumber(Integer number, HttpServletRequest request) throws ApplicationException {
        Optional<ParkingLot> parkingLot = Optional.ofNullable(lotsRepository.findByNumber(number));
        if (!parkingLot.isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, request);
        }
        return parkingLot.get();
    }

    public void cancelReservation(ParkingLot parkingLot, Account account, HttpServletRequest request) throws ApplicationException {

        if (account == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        }
        if (parkingLot == null) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, request);
        }

        lotsRepository.cancelReservation(account);

        Date currentDate = ToolHelper.getCurrentDate();
        Account user = userService.getCurrentUser(request);
        String userAgent = request.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.UNRESERVE, account, parkingLot.getNumber(), currentDate, currentDate, null, user, userAgent);
    }

    public ParkingLot setOwner(Account account, ParkingLot parkingLot) {
        parkingLot.setOwner(account);
        return lotsRepository.save(parkingLot);
    }

    public ParkingLot getParkingNumberByUser() {
        String username = getCurrentUserName();
        return accountRepository.findByUsername(username).getParking();
    }

    public String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}