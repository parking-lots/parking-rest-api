package parking.service;

import parking.beans.request.parkingNumberRequest;
import parking.beans.request.setUnusedRequest;
import parking.beans.response.ParkingLot;
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

    public List<ParkingLot> getAvailable() {
        return lotsRepository.searchAllFields();
    }

    public void freeOwnersParking(setUnusedRequest request) {
        lotsRepository.freeOwnersParking(request);
    }

    public void recallParking(parkingNumberRequest request) {
        lotsRepository.recallParking(request);
    }

    public void reserve(parkingNumberRequest request) {
        lotsRepository.reserve(request);
    }

    private Integer getParkingNumberByUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return lotsRepository.getParkingNumberByUser(name);
    }
}
