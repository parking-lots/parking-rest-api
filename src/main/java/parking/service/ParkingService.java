package parking.service;

import parking.beans.request.parkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.ParkingLot;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingService {

    @Autowired
    private LotsRepository lotsRepository;
    @Autowired
    private AccountRepository accountRepository;

    public List<ParkingLot> getAvailable() {
        return lotsRepository.searchAllFields(getCurrentUserName());
    }

    public void freeOwnersParking(SetUnusedRequest request){
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
        parkingNumberRequest request = new parkingNumberRequest();
        request.setNumber(parkingNumber);
        lotsRepository.recallParking(request);
    }

    public void reserve(parkingNumberRequest request) {
        lotsRepository.reserve(request, getCurrentUserName());
    }

    private Integer getParkingNumberByUser(){
        return accountRepository.findByUsername(getCurrentUserName()).getParkingNumber();
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public void cancelRezervation() {
        lotsRepository.cancelReservation(getCurrentUserName());
    }
}