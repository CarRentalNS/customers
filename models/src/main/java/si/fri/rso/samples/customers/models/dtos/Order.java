package si.fri.rso.samples.customers.models.dtos;

import java.time.Instant;
import java.util.List;

public class Order {

    private Integer id;

    private String customerId;

    private String carId;

    private Instant time_from;

    private Instant time_to;

    private String pickup_location;

    private String drop_location;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getCarId() {
        return carId;
    }

    public void setCarId(Integer id1) { this.carId = carId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Instant getTimeFrom() {
        return time_from;
    }

    public void setTimeFrom(Instant time_from) {
        this.time_from = time_from;
    }

    public Instant getTimeTo() {
        return time_to;
    }

    public void setTimeTo(Instant time_to) {
        this.time_to = time_to;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public String getDrop_location() {
        return drop_location;
    }
}