package Classes;

public class VipCustomer extends Customer {

    private final static double DISCOUNT = 0.5;


    public VipCustomer(String custId, String custName, String custTel, String custType) {

        setCustName(custName);
        setCustId(custId);
        setCustTel(custTel);
        setCustTel(custType);
    }

    public VipCustomer() {

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
        return "VIP customer : get an item discount of 50% !";
    }

*/


}
