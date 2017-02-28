import java.util.Map;

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

    public Double lookupList(String item){
        Double contents = -3.0;
        if(shoppingList.lookup(item) != -3.0){
            contents = shoppingList.lookup(item);
        }
        return contents;
    }

    public void addItem(String item, double isle){
        shoppingList.addItem(isle, item);
    }

    public Double getIsle(String item){
        return shoppingList.getIsle(item);
    }

    public Map<String, Double> getList(){
        return shoppingList.getStoreLayout();
    }
}
