import com.sun.deploy.util.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;


/**
 * Created by mcneelypj on 11/23/2016.
 */

public class UserInterface extends JFrame {

    private JFrame addFrame;
    private JPanel shoppingList;
    private Driver driver;
    private JButton addButton;
    private JButton redoButton;
    private JButton undoButton;
    private JButton eMailButton;
    private JButton printButton;
    private JTextField addItem;
    private Stack<String> undo;
    private Stack<String> redo;
    private JTextArea currentCart;
    private ArrayList<String> cartContents;
    private ArrayList<Double> sortingContents;
    private JComboBox jBox;
    private JTextField item;



    public UserInterface(Driver driver){
        this.driver = driver;
        undo = new Stack<>();
        redo = new Stack<>();
        cartContents = new ArrayList<>();
        sortingContents = new ArrayList<>();
        buildUI();
        setVisible(true);
    }

    private void buildUI(){
        //Adding the main skeleton
        setTitle("Shopping List APP");
        setSize(620,400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //adding the components

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        //menu bars
        JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_E);
        JMenuItem email = new JMenuItem("E-mail", KeyEvent.VK_M);
        JMenuItem print = new JMenuItem("Print", KeyEvent.VK_P);

        //action listeners for menu bars
        exit.addActionListener(e-> this.dispose());
        email.addActionListener(e-> sendEmail());
        print.addActionListener(e-> printList());
        fileMenu.add(email);
        fileMenu.add(print);
        fileMenu.add(exit);
        setJMenuBar(menuBar);


        //shopping list
        shoppingList = new JPanel();
        shoppingList.setLayout(new FlowLayout());

        JLabel currentList = new JLabel("       Items on your list:");

        currentCart = new JTextArea(40,50);
        currentCart.setEditable(false);

        shoppingList.add(currentCart);
        addItem = new JTextField(20);

        addItem.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                String item = StringUtils.trimWhitespace(addItem.getText());
                if (item.length() == 0){
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
                if ( e.getKeyChar() == KeyEvent.VK_ENTER ){
                    addButtonHandler();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        // Buttons
        undoButton = new JButton("Undo");
        undoButton.addActionListener(e-> undoButtonHandler());
        redoButton = new JButton("Redo");
        redoButton.addActionListener(e-> redoButtonHandler());
        addButton = new JButton("Add item");
        addButton.addActionListener(e-> addButtonHandler());
        eMailButton = new JButton("E-Mail");
        eMailButton.addActionListener(e-> sendEmail());
        printButton = new JButton("Print");
        printButton.addActionListener(e-> printList());
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        addButton.setEnabled(false);
        printButton.setEnabled(false);
        eMailButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addItem);
        buttonPanel.add(addButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(redoButton);
        buttonPanel.add(eMailButton);
        buttonPanel.add(printButton);

        add(currentList, BorderLayout.NORTH);
        add(shoppingList, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean addItemHandler(String item) {
        boolean contents = false;
        if(driver.lookupList(item) != -3.0){
            contents = true;
        }
        return contents;
    }

    private void addButtonHandler() {
        String contents = "";
        if (addItemHandler(addItem.getText())) {
            String nextItem = addItem.getText().toLowerCase();
            if (undo.isEmpty()) {
                undoButton.setEnabled(true);
            }
            sortingContents.add(driver.getIsle(nextItem));
            cartContents.add(nextItem);
            List<String> tempArray = customSort(sortingContents, cartContents);
            for (String item : tempArray){
                contents +=  item + "\n";
            }
            currentCart.setText(contents);
            undo.push("Isle: " + convertIsle(driver.getIsle(nextItem)) + " Item: " + nextItem);
            addItem.setText("");
            eMailButton.setEnabled(true);
            printButton.setEnabled(true);
        } else {
            addAlert(addItem.getText());
        }
    }

    /**
     * Re-adds the most recent item that was removed from the list
     */
    private void redoButtonHandler() {
        String recentItem = redo.pop();
        undo.push(recentItem);
        cartContents.add(recentItem);
        undoButton.setEnabled(true);
        if(redo.isEmpty()){
            redoButton.setEnabled(false);
        }
        if(!cartContents.isEmpty()){
            eMailButton.setEnabled(true);
            printButton.setEnabled(true);
        }
        String contents = "";
        for (String item : cartContents){
            contents += item + "\n";
        }
        currentCart.setText(contents);
    }

    /**
     * Removes the last item from the list
     */
    private void undoButtonHandler() {
        String lastItem = undo.pop();
        cartContents.remove(lastItem);
        redo.push(lastItem);
        redoButton.setEnabled(true);
        if (undo.isEmpty()){
            undoButton.setEnabled(false);
        }
        if(cartContents.isEmpty()){
            eMailButton.setEnabled(false);
            printButton.setEnabled(false);
        }
        String contents = "";
        for (String item : cartContents){
            contents += item + "\n";
        }
        currentCart.setText(contents);
    }

    /**
     * Askes the user if they would like to add the specified item to the store
     * @param item the new item to be added
     */
    public void addAlert(String item){
        int answer = JOptionPane.showConfirmDialog(null, "The item: " +  item + " is not " +
                "found, would you like to add to the store?", "Item not Found" ,JOptionPane
                .YES_NO_CANCEL_OPTION);
        if (answer == 0){
            //add an item to the store
            addItemUI(item);
            System.out.println("Yes");
        }
    }

    /**
     * The UI that will ask the user to add the specified item in the store
     * @param object the object that will be added
     */
    private void addItemUI(String object) {
        addFrame = new JFrame();
        addFrame.setSize(300, 200);
        addFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addFrame.setLayout(new GridLayout(3, 2));
        JButton addItemButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        jBox = new JComboBox<>();
        Map<String, Double> list = driver.getList();
        ArrayList<Double> isleList = new ArrayList<>();
        for (Map.Entry<String, Double> item : list.entrySet()) {
            Double isle = item.getValue();
            if (!isleList.contains(isle)) {
                isleList.add(isle);
            }
        }
        Collections.sort(isleList);
        for (Double isle : isleList) {
            jBox.addItem(convertIsle(isle));
        }

        JLabel itemToAdd = new JLabel("Item: ");
        JLabel isle = new JLabel("Isle: ");
        item = new JTextField(object);
        addFrame.add(itemToAdd);
        addFrame.add(item);
        addFrame.add(isle);
        addFrame.add(jBox);
        addFrame.add(addItemButton);
        addFrame.add(cancelButton);

        addItemButton.addActionListener(e -> addItemButtonHandler());
        cancelButton.addActionListener(e -> addFrame.dispose());
        addFrame.setVisible(true);
    }

    /**
     * Adds an item to the store to be looked up later.
     */
    private void addItemButtonHandler(){
        System.out.println(item.getText());
        String nextItem = item.getText().trim();
        String contents = "";
        if(!nextItem.isEmpty()){
            double isleNumber =  undoIsle((String) jBox.getSelectedItem());
            sortingContents.add(isleNumber);
            cartContents.add(nextItem);
            List<String> tempArray = customSort(sortingContents, cartContents);
            for (String item : tempArray){
                contents +=  item + "\n";
            }
            currentCart.setText(contents);
            undo.push("Isle: " + convertIsle(isleNumber) + " Item: " + nextItem);
            addItem.setText("");
            driver.addItem(nextItem,isleNumber);
            addFrame.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "You must have an item in the item box");
        }
    }

    /**
     * Sends an email to the specified email address.
     */
    private void sendEmail() {
        String address = JOptionPane.showInputDialog(null, "What email would you like to send " +
                "an email to?");
        if (validEmail(address)) {
            new SendEmail(address, cartContents);
        } else {
            JOptionPane.showMessageDialog(null, "That email address is not valid please try " +
                    "again");
            sendEmail();
        }
    }

    /**
     * Checks to see if an email is valid or not
     * @param from the senders address
     * @return whether the email is valid or not
     */
    private boolean validEmail(String from){
        boolean valid = true;
        try{
            InternetAddress emailAddress = new InternetAddress(from);
            emailAddress.validate();
        } catch ( AddressException ex){
            valid = false;
        }
        return valid;
    }

    /**
     * Prints your current list to your printer.
     */
    private void printList(){

    }

    /**
     * Corrects the item to what we know as being the correct department
     * @param isle the isle that is contained inside the store
     * @return the String associated with where the item is lcoated in the store.
     */
    private String convertIsle(double isle){
        String correctedIsle = "" + isle;
        switch (correctedIsle){
            case "-1.0":
                correctedIsle = "produce";
                break;
            case "0.0":
                correctedIsle = "bakery";
                break;
            case "2.5":
                correctedIsle = "seafood";
                break;
            case "2.75":
                correctedIsle = "meat";
                break;
            case"18.0":
                correctedIsle = "dairy";
                break;
            case "19.0":
                correctedIsle = "frozen Department";
                break;
            case "20.0":
                correctedIsle = "liquor department";
                break;
            case "21.0":
                correctedIsle = "registers";
                break;
            default:
                correctedIsle = correctedIsle.substring(0, correctedIsle.lastIndexOf("."));
        }
        return correctedIsle;
    }

    private double undoIsle(String isle){
        double correctedIsle = -3.0;
        switch (isle){
            case "produce":
                correctedIsle = -1.0;
                break;
            case "bakery":
                correctedIsle = 0.0;
                break;
            case "seafood":
                correctedIsle = 2.5;
                break;
            case "meat":
                correctedIsle = 2.75;
                break;
            case "dairy":
                correctedIsle = 18.0;
                break;
            case "frozen department":
                correctedIsle = 19.0;
                break;
            case "liquor department":
                correctedIsle = 20.0;
                break;
            case "registers":
                correctedIsle = 21.0;
                break;
            default:
                correctedIsle = Double.parseDouble(isle);
        }
        return correctedIsle;
    }

    private List<String> customSort(ArrayList<Double> sortingContents, ArrayList<String>
            cartContents ){
        ArrayList<String> tempReturnList = new ArrayList<>();
        ArrayList<Double> tempSortingList = new ArrayList<>();
        tempSortingList.addAll(sortingContents);
        ArrayList<String> tempCartList = new ArrayList<>();
        tempCartList.addAll(cartContents);
        //sort the double list
        double last = 30.0;
        int index = -1;
        int counter = 0;
        int iterations = tempSortingList.size();
        while (counter < iterations) {
            for (int i = 0; i < tempSortingList.size(); i++) {
                double temp = tempSortingList.get(i);
                if (temp < last) {
                    last = temp;
                    index = i;
                }
            }
            tempReturnList.add("Isle: " + convertIsle(tempSortingList.get(index)) + " Item: " +
                    tempCartList.get(index));
            tempSortingList.remove(index);
            tempCartList.remove(index);
            last = 30.0;
            ++counter;
        }
        return tempReturnList;
    }
}


