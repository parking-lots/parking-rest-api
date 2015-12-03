package loans.repositories;

import loans.beans.response.ParkingLot;

import java.util.List;

/**
 * Created by svdleo on 03/12/15.
 */
public interface CustomLostRepository {
    List<ParkingLot> searchAllFields();
}
