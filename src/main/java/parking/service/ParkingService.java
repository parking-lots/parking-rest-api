package parking.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.RecallSingleParking;
import parking.beans.request.SetUnusedRequest;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
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
    private UserService userService;
    @Autowired
    private ExceptionHandler exceptionHandler;

    public List<ParkingLot> getAvailable(HttpServletRequest request) throws ApplicationException {

        //throw new UserException("test");
        Account currentUser = userService.getCurrentUser(request);
        List<ParkingLot> parkingLots = lotsRepository.searchAllFields(currentUser);

        // Check if current user using one of parking
        List<ParkingLot> filteredLots =  parkingLots.stream()
                .filter(val -> val.getUser() != null && currentUser.getUsername().equals(val.getUser().getUsername()))
                .collect(Collectors.toList());

        if (filteredLots.size() > 0) {
            return filteredLots;
        }

        return parkingLots;
    }

    public void freeOwnersParking(SetUnusedRequest request, HttpServletRequest httpRequest) throws ApplicationException {

        ParkingLot parking = getParkingNumberByUser();
        if(parking == null){
            return; //throw new Exception("Customer doesn't have parking assigned, so can't share anything");
        }
        request.setNumber(parking.getNumber());

        Date currentDate = new Date();
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        Calendar cal = eliminateDateTimestamp.formatDateForDatabase(currentDate);

        if (request.getFreeFrom().compareTo(request.getFreeTill()) > 0){
            throw exceptionHandler.handleException(ExceptionMessage.START_DATE_LATER_THAN_END_DATE, httpRequest);
        }
        else if ((request.getFreeTill().compareTo(cal.getTime()) > 0) && (request.getFreeFrom().compareTo(request.getFreeTill()) < 1)) {
            throw exceptionHandler.handleException(ExceptionMessage.END_DATE_IN_THE_PAST, httpRequest);
        }
        else {
            lotsRepository.freeOwnersParking(request);
        }
    }

    public void recallParking() {
        ParkingLot parking = getParkingNumberByUser();
        if(parking == null){
            return; //throw new Exception("Customer doesn't have parking assigned, so can't share anything");
        }
        ParkingNumberRequest request = new ParkingNumberRequest();
        request.setNumber(parking.getNumber());
        lotsRepository.recallParking(request);
    }

    public void recallSingleParking(RecallSingleParking recallSingleParking) {
        lotsRepository.recallSingleParking(recallSingleParking);
    }

    public void reserve(ParkingNumberRequest request, HttpServletRequest httpRequest) throws ApplicationException {
        lotsRepository.reserve(request, userService.getCurrentUser(httpRequest));
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

    public void cancelRezervation(HttpServletRequest request) throws ApplicationException {
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