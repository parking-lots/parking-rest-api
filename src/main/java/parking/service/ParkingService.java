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
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.utils.ActionType;
import parking.utils.EliminateDateTimestamp;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    public void freeOwnersParking(ObjectId ownerId, Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpRequest) throws ApplicationException {

        validatePeriod(lotNumber, freeFrom, freeTill, httpRequest);

        lotsRepository.freeOwnersParking(lotNumber, freeFrom, freeTill, httpRequest);

        Account user = userService.getCurrentUser(httpRequest);
        ObjectId userId = user.getId();
        if (userId != null) {
            logRepository.insertActionLog(ActionType.SHARE, ownerId, lotNumber, freeFrom, freeTill, null, userId, null);
        }
    }

    public void validatePeriod(Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpServletRequest) throws ApplicationException {
        Date currentDate = new Date();
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        Calendar cal = eliminateDateTimestamp.formatDateForDatabase(currentDate);

//        if(freeFrom != null) {
            if (freeFrom.compareTo(freeTill) > 0)
                throw exceptionHandler.handleException(ExceptionMessage.START_DATE_LATER_THAN_END_DATE, httpServletRequest);

            if ((freeTill.compareTo(cal.getTime()) < 0) && (freeFrom.compareTo(freeTill) < 1))
                throw exceptionHandler.handleException(ExceptionMessage.END_DATE_IN_THE_PAST, httpServletRequest);
       // }
        lotsRepository.checkPeriod(lotNumber, freeFrom, freeTill, httpServletRequest);

    }

    public void recallParking(Date freeFrom, Date freeTill, HttpServletRequest request) throws ApplicationException {

        ParkingLot parking = getParkingNumberByUser();
        if (parking == null) {
            return;
        }

        lotsRepository.recallParking(parking.getNumber(), freeFrom, freeTill);
    }

    public void recallParking(List<Date> availableDates, HttpServletRequest request) throws ApplicationException {

        ParkingLot parking = getParkingNumberByUser();
        if (parking == null) {
            return;
        }

        for (Date d : availableDates) {
            lotsRepository.checkRecallDate(parking.getNumber(), d, request);
        }

        for (Date d : availableDates) {
            lotsRepository.recallParking(parking.getNumber(), d, request);
        }

        Account user = userService.getCurrentUser(request);
        ObjectId userId = user.getId();
        ObjectId targetUserId = parking.getOwner().getId();

        List<AvailablePeriod> availablePeriods;
        AvailableDatesConverter converter = new AvailableDatesConverter();
        availablePeriods = converter.convertToInterval(availableDates);

        for (AvailablePeriod availablePeriod : availablePeriods) {
                logRepository.insertActionLog(ActionType.UNSHARE, targetUserId, parking.getNumber(), availablePeriod.getFreeFrom(), availablePeriod.getFreeTill(), null, userId, null);
            }
    }

    public void reserve(Integer lotNumber, HttpServletRequest httpRequest) throws ApplicationException {
        ParkingLot lot = lotsRepository.findByNumber(lotNumber);

        if (lot == null) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, httpRequest);
        } else {
            lotsRepository.reserve(lotNumber, userService.getCurrentUser(httpRequest), httpRequest);
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

    public void cancelReservation(HttpServletRequest request) throws ApplicationException {
        lotsRepository.cancelReservation(userService.getCurrentUser(request));
    }

    public ParkingLot setOwner(Account account, ParkingLot parkingLot) {
        parkingLot.setOwner(account);
        return lotsRepository.save(parkingLot);
    }

    public ParkingLot getParkingNumberByUser() {
        return accountRepository.findByUsername(getCurrentUserName()).getParking();
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}