package parking.service;

import org.springframework.security.core.context.SecurityContextHolder;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.ParkingLot;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingService {

    @Autowired
    private LotsRepository lotsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserService userService;

    public List<ParkingLot> getAvailable() throws UserException {
        return lotsRepository.searchAllFields(userService.getCurrentUser());
    }

    public void freeOwnersParking(SetUnusedRequest request) {
        Integer parkingNumber = getParkingNumberByUser();
        if(parkingNumber == null){
            return; //throw new Exception("Customer doesn't have parking assigned, so can't share anything");
        }
        request.setNumber(parkingNumber);
        lotsRepository.freeOwnersParking(request);
    }

    public void recallParking() {
        Integer parkingNumber = getParkingNumberByUser();
        if(parkingNumber == null){
            return; //throw new Exception("Customer doesn't have parking assigned, so can't share anything");
        }
        ParkingNumberRequest request = new ParkingNumberRequest();
        request.setNumber(parkingNumber);
        lotsRepository.recallParking(request);
    }

    public void reserve(ParkingNumberRequest request) throws UserException {
        lotsRepository.reserve(request, userService.getCurrentUser());
    }

    private Integer getParkingNumberByUser(){
        return accountRepository.findByUsername(getCurrentUserName()).getParkingNumber();
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public void cancelRezervation() throws UserException {
        lotsRepository.cancelReservation(userService.getCurrentUser());
    }
}