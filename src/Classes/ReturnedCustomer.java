package Classes;

public class ReturnedCustomer extends Customer {

    private final static double DISCOUNT = 0.3;


    public ReturnedCustomer(String custId, String custName, String custTel, String custType) {

        setCustName(custName);
        setCustId(custId);
        setCustTel(custTel);
        setCustTel(custType);
    }

    public ReturnedCustomer() {

        setCustName("");
        setCustId("");
        setCustTel("");
        setCustTel("");
    }

/*
    @Override
    public String PriceAfterDiscount(String itemOriginalPrice) {

        int convertItemOriginalPrice = Integer.parseInt(itemOriginalPrice);
        double itemAfterDiscount = convertItemOriginalPrice*DISCOUNT;
        String convertItemAfterDiscount = Double.toString(itemAfterDiscount);

        return convertItemAfterDiscount;
    }


    @Override
    public String toString() {
        return "Returned customer : get an item discount of 30% !";
    }
*/
}
