package Classes;

    public class Customer {

    private String custName;
    private String custId;
    private String custTel;
    private String custType;

    public Customer(String custId, String custName, String custTel, String custType) {
        this.custName = custName;
        this.custId = custId;
        this.custTel = custTel;
        this.custType = custType;
    }

    public Customer() {

        this.custName = "";
        this.custId = "";
        this.custTel = "";
        this.custType = "";
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustTel() {
        return custTel;
    }

    public void setCustTel(String custTel) {
        this.custTel = custTel;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }
}
