package loans.service;

import loans.beans.request.parkingNumberRequest;
import loans.beans.request.setUnusedRequest;
import loans.beans.response.ParkingLot;
import loans.repositories.LotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
