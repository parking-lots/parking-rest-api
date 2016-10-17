package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LotsRepositoryImpl implements CustomLotsRepository {

    private final MongoOperations operations;

    @Autowired
    public LotsRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Override
    public List<ParkingLot> searchAllFields(final Account user) {
        Query searchQuery = new Query();

        searchQuery.addCriteria(Criteria.where("dates").is(LocalDate.now()));
        searchQuery.addCriteria(new Criteria().orOperator(
                Criteria.where("reserved").is(null),
                Criteria.where("user.$id").is(user.getId()))
        );

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        return lots;
    }

    @Override
    public void shareParking(Integer lotNumber, List<LocalDate> dates) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));

        ParkingLot lot = operations.find(searchQuery, ParkingLot.class).get(0);
        Optional<LinkedList<LocalDate>> existDates = Optional.ofNullable(lot.getDates());

        lot.setDates(mergeDates(existDates.orElse(new LinkedList<LocalDate>()), dates));
        operations.save(lot);
    }

    @Override
    public void unshareParking(Integer lotNumer, List<LocalDate> unshareDates) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumer));

        ParkingLot lot = operations.findOne(searchQuery,ParkingLot.class);
        Optional<LinkedList<LocalDate>> existDates = Optional.ofNullable(lot.getDates());

        lot.setDates(removeDates(existDates.orElse(new LinkedList<LocalDate>()), unshareDates));
        operations.save(lot);
    }




    public void checkRecallDate(Integer lotNumber, LocalDate availableDate, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));

        if (availableDate != null) {

            List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

            boolean dateExists = false;

            if (lots.size() > 0) {
                for (AvailablePeriod period : lots.get(0).getAvailablePeriods()) {
                    if (period.getFreeFrom().compareTo(availableDate) <= 0 && period.getFreeTill().compareTo(availableDate) >= 0)
                        dateExists = true;
                }
                if (dateExists == false)
                    throw exceptionHandler.handleException(ExceptionMessage.DATE_DOES_NOT_EXIST, httpRequest);
            }
        }
    }

    @Override
    public void reserve(Integer lotNumber, Account user, HttpServletRequest httpRequest) throws ApplicationException {
//        Query searchQuery = new Query();
//        Date currentDate = ToolHelper.getCurrentDate();
//        searchQuery.addCriteria(new Criteria()
//                .andOperator(
//                        Criteria.where("number").is(lotNumber),
//                        Criteria.where("reserved").is(null)
//                ));
//
//        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);
//
//        if (lots.size() == 0) {
//            throw exceptionHandler.handleException(ExceptionMessage.PARKING_NOT_AVAILABLE, httpRequest);
//        }
//
//        boolean parkingAvailable = false;
//        for (ParkingLot lot : lots) {
//            for (AvailablePeriod availablePeriod : lot.getAvailablePeriods()) {
//                if (availablePeriod.getFreeFrom().compareTo(currentDate) <= 0 && availablePeriod.getFreeTill().compareTo(currentDate) >= 0) {
//                    parkingAvailable = true;
//                    break;
//                }
//            }
//        }
//
//        if (!parkingAvailable) {
//            throw exceptionHandler.handleException(ExceptionMessage.PARKING_NOT_AVAILABLE, httpRequest);
//        }
//
//        Update updateFields = new Update();
//        updateFields.set("user", user);
//        updateFields.set("reserved", currentDate);
//        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void cancelReservation(Account user) {
        Query searchQuery = new Query(Criteria.where("user.$id").is(user.getId()));
        Update updateFields = new Update();
        updateFields.unset("user");
        updateFields.unset("reserved");
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public List<ParkingLot> findParking(ParkingType type) {
        Query searchQuery;

        if (type.equals(ParkingType.unassigned)) {
            searchQuery = new Query(Criteria.where("owner").is(null));
        } else {
            searchQuery = new Query();
        }

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);
        return lots;
    }

    public void setParkingOwner(Integer lotNumber, String username) {
        Query query = new Query(Criteria.where("number").is(lotNumber));

        Account owner = accountRepository.findByUsername(username);

        Update update = new Update();
        update.set("owner", owner);

        operations.updateFirst(query, update, ParkingLot.class);
    }

    public void removeParkingOwner(Integer lotNumber) {
        Query query = new Query(Criteria.where("number").is(lotNumber));

        Update update = new Update();
        update.unset("owner");

        operations.updateFirst(query, update, ParkingLot.class);
    }

    public LinkedList<LocalDate> mergeDates(List<LocalDate> oldDates, List<LocalDate> newDates) {
        LinkedList<LocalDate> merged = Stream.concat(oldDates.stream(), newDates.stream())
                .distinct()
                .filter(d -> LocalDate.now().equals(d) || d.isAfter(LocalDate.now()))
                .collect(Collectors.toCollection(LinkedList::new));

        Collections.sort(merged);

        return merged;
    }

    private LinkedList<LocalDate> removeDates(LinkedList<LocalDate> oldDates, List<LocalDate> removeDates) {
        LinkedList<LocalDate> removed = oldDates.stream()
                .filter(d -> !removeDates.contains(d))
                .filter(d -> LocalDate.now().equals(d) || d.isAfter(LocalDate.now()))
                .collect(Collectors.toCollection(LinkedList::new));

        Collections.sort(removed);

        return removed;
    }
}