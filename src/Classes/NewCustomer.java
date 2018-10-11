package Classes;

public class NewCustomer extends Customer {


    public NewCustomer(String custId, String custName, String custTel, String custType) {

       setCustName(custName);
       setCustId(custId);
       setCustTel(custTel);
       setCustTel(custType);
    }

    public NewCustomer() {

        setCustName("");
        setCustId("");
        setCustTel("");
        setCustTel("");
    }
/*

    @Override
    public String PriceAfterDiscount(String itemOriginalPrice) {
        return itemOriginalPrice;
    }


    @Override
    public String toString() {
        return "New customer : No discount !";
    }
*/

}
