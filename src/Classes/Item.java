package Classes;


public class Item {

    private String itemType;
    private String itemSize;
    private String itemBranch;
    private String itemAmount;
    private String itemUnitPrice;
    private String itemPartNumber;


    public Item( String itemType, String itemSize, String itemBranch, String itemAmount, String itemUnitPrice, String itemPartNumber ) {
        this.itemType = itemType;
        this.itemSize = itemSize;
        this.itemBranch = itemBranch;
        this.itemAmount = itemAmount;
        this.itemUnitPrice = itemUnitPrice;
        this.itemPartNumber = itemPartNumber;
    }

    public Item() {
        this.itemType = "";
        this.itemSize = "";
        this.itemBranch = "";
        this.itemAmount = "";
        this.itemUnitPrice = "";
        this.itemPartNumber = "";
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getItemBranch() {
        return itemBranch;
    }

    public void setItemBranch(String itemBranch) {
        this.itemBranch = itemBranch;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemUnitPrice() {
        return itemUnitPrice;
    }

    public void setItemUnitPrice(String itemUnitPrice) {
        this.itemUnitPrice = itemUnitPrice;
    }

    public String getItemPartNumber() {
        return itemPartNumber;
    }

    public void setItemPartNumber(String itemPartNumber) {
        this.itemPartNumber = itemPartNumber;
    }
}
