package parking.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.RecallParking;
import parking.beans.request.SetUnusedRequest;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import parking.utils.EliminateDateTimestamp;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@Service
public class ParkingService {

    @Autowired
    private LotsRepository lotsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ExceptionHandler exceptionHandler;

    public List<ParkingLot> getAvailable(HttpServletRequest request) throws ApplicationException {

        Account currentUser = userService.getCurrentUser(request);
        List<ParkingLot> parkingLots = lotsRepository.searchAllFields(currentUser);

        List<ParkingLot> filteredLots =  parkingLots.stream()
                .filter(val -> val.getUser() != null && currentUser.getUsername().equals(val.getUser().getUsername()))
                .collect(Collectors.toList());

        if (filteredLots.size() > 0) {
            return filteredLots;
        }
        return parkingLots;
    }

    public void freeOwnersParking(Date freeFrom, Date freeTill, HttpServletRequest httpRequest) throws ApplicationException {

        ParkingLot parking = getParkingNumberByUser();
        if(parking == null){
            return;
        }

        Date currentDate = new Date();
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        Calendar cal = eliminateDateTimestamp.formatDateForDatabase(currentDate);

        if (freeFrom.compareTo(freeTill) > 0){
            throw exceptionHandler.handleException(ExceptionMessage.START_DATE_LATER_THAN_END_DATE, httpRequest);
        }
        else if ((freeTill.compareTo(cal.getTime()) < 0) && (freeFrom.compareTo(freeTill) < 1)) {
            throw exceptionHandler.handleException(ExceptionMessage.END_DATE_IN_THE_PAST, httpRequest);
        }
        else {
            lotsRepository.freeOwnersParking(parking.getNumber(), freeFrom, freeTill);
        }
    }

//    public void freeOwnersParking(List<Date> availableDates, HttpServletRequest httpRequest) throws ApplicationException {
//
//        ParkingLot parking = getParkingNumberByUser();
//        if(parking == null){
//            return;
//        }
//
//        Date currentDate = new Date();
//        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
//        Calendar cal = eliminateDateTimestamp.formatDateForDatabase(currentDate);
//
//        for (Date d: availableDates)
//        if ((d.compareTo(cal.getTime()) < 0)) {
//            throw exceptionHandler.handleException(ExceptionMessage.DATE_IN_THE_PAST, httpRequest);
//        }
//        else {
//            lotsRepository.freeOwnersParking(parking.getNumber(), d);
//        }
//    }

    public void recallParking(Date freeFrom, Date freeTill) {
        ParkingLot parking = getParkingNumberByUser();
        if(parking == null){
            return;
        }
        lotsRepository.recallParking(parking.getNumber(), freeFrom, freeTill);
    }

    public void recallParking(List<Date> availableDates) {
        ParkingLot parking = getParkingNumberByUser();
        if(parking == null){
            return;
        }
        for (Date d: availableDates) {
            lotsRepository.recallParking(parking.getNumber(), d);
        }
    }

    public void reserve(Integer lotNumber, HttpServletRequest httpRequest) throws ApplicationException {
        lotsRepository.reserve(lotNumber, userService.getCurrentUser(httpRequest));
    }

    public ParkingLot createLot(ParkingLot parkingLot, HttpServletRequest request) throws ApplicationException {
        ParkingLot existParkingLot;
        try {
            existParkingLot =  getParkingByNumber(parkingLot.getNumber(), request);
        } catch (ApplicationException e) {
            existParkingLot = null;
        }
        if(Optional.ofNullable(existParkingLot).isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_ALREADY_EXISTS, request);
        }
        parkingLot.setId(new ObjectId());
        return lotsRepository.insert(parkingLot);
    }

    public ParkingLot getParkingByNumber(Integer number, HttpServletRequest request) throws ApplicationException {
        Optional<ParkingLot> parkingLot = Optional.ofNullable(lotsRepository.findByNumber(number));
        if (!parkingLot.isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_DID_NOT_EXIST, request);
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

    private ParkingLot getParkingNumberByUser() {
        return accountRepository.findByUsername(getCurrentUserName()).getParking();
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}