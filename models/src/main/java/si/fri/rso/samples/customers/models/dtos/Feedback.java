package si.fri.rso.samples.customers.models.dtos;

public class Feedback {

    private Integer id;

    private String customerId;

    private String orderId;

    private String comment;

    private String satisfactionGrade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getSatisfactionGrade() { return satisfactionGrade; }

    public void setSatisfactionGrade(String satisfactionGrade) { this.satisfactionGrade = satisfactionGrade; }
}
