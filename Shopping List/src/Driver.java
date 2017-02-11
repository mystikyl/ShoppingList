/**
 * Created by mcneelypj on 11/23/2016.
 */
public class Driver {

    private ShoppingList shoppingList;
    public static void main(String[] args) {
        Driver driver = new Driver();
        UserInterface ui = new UserInterface(driver);
    }


    public Driver(){
        shoppingList = new ShoppingList();
    }

    public String lookupList(String item){
        String contents = "";
        if(!shoppingList.lookup(item).equals("")){
            contents = shoppingList.lookup(item);
        }
        return contents;
    }

    public void addItem(String item, String isle){
        shoppingList.addItem(isle, item);
    }
}
