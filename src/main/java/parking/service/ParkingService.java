package parking.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;

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

    public List<ParkingLot> getAvailable() throws UserException {
        Account currentUser = userService.getCurrentUser();
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

    public void freeOwnersParking(SetUnusedRequest request) {

        ParkingLot parking = getParkingNumberByUser();
        if(parking == null){
            return; //throw new Exception("Customer doesn't have parking assigned, so can't share anything");
        }
        request.setNumber(parking.getNumber());
        lotsRepository.freeOwnersParking(request);
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

    public void reserve(ParkingNumberRequest request) throws UserException {
        lotsRepository.reserve(request, userService.getCurrentUser());
    }

    public ParkingLot createLot(ParkingLot parkingLot) throws ParkingException {
        ParkingLot existParkingLot;
        try {
            existParkingLot =  getParkingByNumber(parkingLot.getNumber());
        } catch (ParkingException e) {
            existParkingLot = null;
        }
        if(Optional.ofNullable(existParkingLot).isPresent()) {
            throw new ParkingException("Parking already exist");
        }
        parkingLot.setId(new ObjectId());
        return lotsRepository.insert(parkingLot);
    }

    public ParkingLot getParkingByNumber(Integer number) throws ParkingException {
        Optional<ParkingLot> parkingLot = Optional.ofNullable(lotsRepository.findByNumber(number));
        if (!parkingLot.isPresent()) {
            throw new ParkingException("Parking did't exist");
        }
        return parkingLot.get();
    }

    public void cancelRezervation() throws UserException {
        lotsRepository.cancelReservation(userService.getCurrentUser());
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