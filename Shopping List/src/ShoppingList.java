import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ShoppingList {
    private Map<String, Double> storeLayout;


    public ShoppingList(){
        storeLayout = new HashMap<>();
        try (Scanner in = new Scanner(new File("pnsList.txt"))){
            addList(in);
        } catch (FileNotFoundException ex){
            ex.getMessage();
        }
    }

    public Double lookup(String product){
        product = product.toLowerCase();
        Double item = -3.0;
        if (storeLayout.containsKey(product)){
            item = storeLayout.get(product);
        }
        return item;
    }

    public Double getIsle(String item){
        return storeLayout.get(item);
    }

    private void addList(Scanner in){
        while (in.hasNextLine()){
            String[] item = in.nextLine().split("\\s",2);
            storeLayout.put(item[1].trim(),Double.parseDouble(item[0]));
        }
    }

    public void addItem(Double isle, String item){
        try {
            storeLayout.put(item, isle);
            String list = "\n" + isle + " " + item;
            Files.write(Paths.get("pnsList.txt"), list.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getStoreLayout(){
        return storeLayout;
    }
}
