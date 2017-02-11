import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ShoppingList {
    private Map<String, String> storeLayout;


    public ShoppingList(){
        storeLayout = new HashMap<>();
        try (Scanner in = new Scanner(new File("pnsList.txt"))){
            addList(in);
        } catch (FileNotFoundException ex){
            ex.getMessage();
        }
    }

    public String lookup(String product){
        product = product.toLowerCase();
        String item = "";
        if (storeLayout.containsKey(product)){
            item = storeLayout.get(product);
        }
        return item;
    }

    private void addList(Scanner in){
        while (in.hasNextLine()){
            String[] item = in.nextLine().split("\\s",2);
            storeLayout.put(item[1],item[0]);
        }
    }
    public void addItem(String isle, String item){
        storeLayout.put(isle,item);
    }
}
