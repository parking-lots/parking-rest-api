package parking.repositories;

import com.mongodb.BasicDBObject;
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
import parking.helper.ToolHelper;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class LotsRepositoryImpl implements CustomLotsRepository {

    private final MongoOperations operations;

    @Autowired
    public LotsRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Override
    public List<ParkingLot> searchAllFields(final Account user) {
        Query searchQuery = new Query();

        Date currentDate = ToolHelper.getCurrentDate();

        searchQuery.addCriteria(Criteria.where("availablePeriods.freeTill").gte(currentDate));
        searchQuery.addCriteria(Criteria.where("availablePeriods.freeFrom").lte(currentDate));
        searchQuery.addCriteria(new Criteria().orOperator(
                Criteria.where("reserved").is(null),
                Criteria.where("user.$id").is(user.getId()))
        );
        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);
        return lots;
    }

    @Override
    public void freeOwnersParking(Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        if (lots.get(0).getAvailablePeriods() != null) {
            for (AvailablePeriod availablePeriod : lots.get(0).getAvailablePeriods()) {

                if ((int) ((freeFrom.getTime() - availablePeriod.getFreeTill().getTime()) / (1000 * 60 * 60 * 24)) == 1) {
                    for (AvailablePeriod availablePeriod1 : lots.get(0).getAvailablePeriods()) {
                        if ((int) ((availablePeriod1.getFreeFrom().getTime() - freeTill.getTime()) / (1000 * 60 * 60 * 24)) == 1) {
                            updateAvailablePeriods(availablePeriod.getFreeFrom(), availablePeriod.getFreeTill(), availablePeriod1.getFreeFrom(), availablePeriod1.getFreeTill(), searchQuery, freeFrom, freeTill);
                            return;
                        }
                    }

                    updateAvailablePeriods(availablePeriod.getFreeFrom(), availablePeriod.getFreeTill(), searchQuery, freeFrom, freeTill);
                    return;
                }

                if ((int) ((availablePeriod.getFreeFrom().getTime() - freeTill.getTime()) / (1000 * 60 * 60 * 24)) == 1) {
                    for (AvailablePeriod availablePeriod1 : lots.get(0).getAvailablePeriods()) {
                        if ((int) ((freeFrom.getTime() - availablePeriod1.getFreeTill().getTime()) / (1000 * 60 * 60 * 24)) == 1) {
                            updateAvailablePeriods(availablePeriod.getFreeFrom(), availablePeriod.getFreeTill(), availablePeriod1.getFreeFrom(), availablePeriod1.getFreeTill(), searchQuery, freeFrom, freeTill);
                            return;
                        }
                    }

                    updateAvailablePeriods(availablePeriod.getFreeFrom(), availablePeriod.getFreeTill(), searchQuery, freeFrom, freeTill);
                    return;
                }
            }
        }

        Update updateFields = new Update();
        AvailablePeriod newAvailablePeriod = new AvailablePeriod(freeFrom, freeTill);
        updateFields.addToSet("availablePeriods", newAvailablePeriod);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void checkPeriod(Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        if (lots.get(0).getAvailablePeriods() != null) {
            for (AvailablePeriod availablePeriod : lots.get(0).getAvailablePeriods()) {
                if (availablePeriod.getFreeFrom().compareTo(freeTill) <= 0 && availablePeriod.getFreeTill().compareTo(freeFrom) >= 0) {
                    throw exceptionHandler.handleException(ExceptionMessage.OVERLAPPING_PERIOD, httpRequest);
                }
            }
        }
    }

    private void updateAvailablePeriods(Date dbFreeFrom, Date dbFreeTill, Query searchQuery, Date newFreeFrom, Date newFreeTill) {
        BasicDBObject obj = new BasicDBObject();
        obj.put("freeFrom", dbFreeFrom);
        obj.put("freeTill", dbFreeTill);
        Update updateFields = new Update();
        updateFields.pull("availablePeriods", obj);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);


        updateFields = new Update();
        AvailablePeriod newAvailablePeriod = null;
        if (newFreeFrom.compareTo(dbFreeFrom) < 0) {
            newAvailablePeriod = new AvailablePeriod(newFreeFrom, dbFreeTill);
        } else {
            newAvailablePeriod = new AvailablePeriod(dbFreeFrom, newFreeTill);
        }

        updateFields.addToSet("availablePeriods", newAvailablePeriod);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }


    private void updateAvailablePeriods(Date dbFreeFrom1, Date dbFreeTill1, Date dbFreeFrom2, Date dbFreeTill2, Query searchQuery, Date newFreeFrom, Date newFreeTill) {
        BasicDBObject obj = new BasicDBObject();
        obj.put("freeFrom", dbFreeFrom1);
        obj.put("freeTill", dbFreeTill1);
        Update updateFields = new Update();
        updateFields.pull("availablePeriods", obj);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);

        obj = new BasicDBObject();
        obj.put("freeFrom", dbFreeFrom2);
        obj.put("freeTill", dbFreeTill2);
        updateFields = new Update();
        updateFields.pull("availablePeriods", obj);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);

        updateFields = new Update();
        AvailablePeriod newAvailablePeriod = null;

        if (dbFreeFrom1.compareTo(dbFreeTill2) < 0) {
            newAvailablePeriod = new AvailablePeriod(dbFreeFrom1, dbFreeTill2);
        } else {
            newAvailablePeriod = new AvailablePeriod(dbFreeFrom2, dbFreeTill1);
        }

        updateFields.addToSet("availablePeriods", newAvailablePeriod);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    public void freeOwnersParking(Integer lotNumber, Date availableDate) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        updateFields.addToSet("availableDates", availableDate);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void recallParking(Integer lotNumber, Date freeFrom, Date freeTill) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        if (freeFrom == null && freeTill == null) {
            updateFields.unset("availablePeriods");
        } else {
            searchQuery.addCriteria(new Criteria().andOperator(
                    Criteria.where("availablePeriods.freeFrom").is(freeFrom),
                    Criteria.where("availablePeriods.freeTill").is(freeTill)
            ));

            BasicDBObject obj = new BasicDBObject();
            obj.put("freeFrom", freeFrom);
            obj.put("freeTill", freeTill);
            updateFields.pull("availablePeriods", obj);
        }

        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    private void removeAvailablePeriod(Integer lotNumber, Date availableDate) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        searchQuery.addCriteria(Criteria.where("availablePeriods.freeFrom").lte(availableDate));
        searchQuery.addCriteria(Criteria.where("availablePeriods.freeTill").gte(availableDate));

        BasicDBObject obj = new BasicDBObject();
        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        Date queryFreeFrom = null;
        Date queryFreeTill = null;

        for (AvailablePeriod period : lots.get(0).getAvailablePeriods()) {
            if (period.getFreeFrom().compareTo(availableDate) <= 0 && period.getFreeTill().compareTo(availableDate) >= 0) {
                queryFreeFrom = period.getFreeFrom();
                queryFreeTill = period.getFreeTill();
            }
        }

        obj.put("freeFrom", queryFreeFrom);
        obj.put("freeTill", queryFreeTill);

        updateFields.pull("availablePeriods", obj);

        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    public void recallParking(Integer lotNumber, Date availableDate, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        if (availableDate == null) {
            updateFields.unset("availablePeriods");
        } else {
            availableDate = ToolHelper.formatDate(availableDate);

            List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

            Date queryFreeFrom = null;
            Date queryFreeTill = null;

            if (lots.size() > 0) {
                for (AvailablePeriod period : lots.get(0).getAvailablePeriods()) {
                    if (period.getFreeFrom().compareTo(availableDate) <= 0 && period.getFreeTill().compareTo(availableDate) >= 0) {
                        queryFreeFrom = period.getFreeFrom();
                        queryFreeTill = period.getFreeTill();
                    }
                }
            }

            AvailablePeriod availablePeriod;

            Calendar before = Calendar.getInstance();
            before.setTime(availableDate);
            before.add(Calendar.DATE, -1);

            Calendar after = Calendar.getInstance();
            after.setTime(availableDate);
            after.add(Calendar.DATE, 1);

            if (queryFreeFrom.equals(availableDate) && queryFreeTill.equals(availableDate)) {
                removeAvailablePeriod(lotNumber, availableDate);
                return;
            } else if (queryFreeFrom.equals(availableDate)) {

                availablePeriod = new AvailablePeriod(after.getTime(), queryFreeTill);
                updateFields.push("availablePeriods", availablePeriod);
            } else if (queryFreeTill.equals(availableDate)) {

                availablePeriod = new AvailablePeriod(queryFreeFrom, before.getTime());
                updateFields.push("availablePeriods", availablePeriod);
            } else {

                availablePeriod = new AvailablePeriod(queryFreeFrom, before.getTime());
                AvailablePeriod availablePeriod1 = new AvailablePeriod(after.getTime(), queryFreeTill);
                AvailablePeriod[] periods = {availablePeriod, availablePeriod1};
                updateFields.pushAll("availablePeriods", periods);
            }

            operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
            removeAvailablePeriod(lotNumber, availableDate);
        }
    }

    public void checkRecallDate(Integer lotNumber, Date availableDate, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));

        if (availableDate != null) {
            availableDate = ToolHelper.formatDate(availableDate);

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
        Query searchQuery = new Query();
        Date currentDate = ToolHelper.getCurrentDate();
        searchQuery.addCriteria(new Criteria()
                .andOperator(
                        Criteria.where("number").is(lotNumber),
                        Criteria.where("reserved").is(null)
                ));

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        if (lots.size() == 0) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_NOT_AVAILABLE, httpRequest);
        }

        Update updateFields = new Update();
        updateFields.set("user", user);
        updateFields.set("reserved", currentDate);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
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
}