import com.sun.deploy.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by mcneelypj on 11/23/2016.
 */
public class UserInterface extends JFrame {

    private JPanel shoppingList;
    private Driver driver;
    private JButton addButton;
    private JButton redoButton;
    private JButton undoButton;
    private JTextField addItem;
    private Stack<String> undo;
    private Stack<String> redo;
    private JTextArea currentCart;
    private ArrayList<String> cartContents;



    public UserInterface(Driver driver){
        this.driver = driver;
        undo = new Stack<>();
        redo = new Stack<>();
        cartContents = new ArrayList<>();
        buildUI();
        setVisible(true);
    }

    private void buildUI(){
        //Adding the main skeleton
        setName("Shopping List APP");
        setSize(500,500);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //adding the components

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        //menu bars
        JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_E);

        //action listeners for menu bars
        exit.addActionListener(e-> this.dispose());
        fileMenu.add(exit);

        setJMenuBar(menuBar);


        //shopping list
        shoppingList = new JPanel();
        shoppingList.setLayout(new FlowLayout());

        JLabel currentList = new JLabel("Items on your list:");
        currentCart = new JTextArea(30,40);
        currentCart.setEditable(false);

        shoppingList.add(currentList);
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
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        addButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addItem);
        buttonPanel.add(addButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(redoButton);

        add(shoppingList, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean addItemHandler(String item) {
        boolean contents = false;
        if(!driver.lookupList(item).equals("")){
            contents = true;
        }
        return contents;
    }

    private void addButtonHandler() {
        //Checks to see if the item is in the store
        String contents = "";
        if (addItemHandler(addItem.getText())) {
            if (undo.isEmpty()) {
                undoButton.setEnabled(true);
            }
            cartContents.add(addItem.getText());
            for (String item : cartContents){
                contents += item;
            }
            currentCart.setText(contents);
        } else {
            //prompts the user to see if they would like to add the item to the store
            addAlert(addItem.getText());
        }
    }

    private void redoButtonHandler() {
        String recentItem = redo.pop();
        undo.push(recentItem);
        cartContents.add(recentItem);
        if(redo.isEmpty()){
            redoButton.setEnabled(false);
        }
        currentCart.setText(cartContents.toString());
    }

    private void undoButtonHandler() {
        String lastItem = cartContents.get(cartContents.size()-1);
        cartContents.remove(cartContents.size()-1);
        redo.push(lastItem);
        redoButton.setEnabled(true);
        if (undo.isEmpty()){
            undoButton.setEnabled(false);
        }
        currentCart.setText(cartContents.toString());
    }

    public void addAlert(String item){
        int answer = JOptionPane.showConfirmDialog(null, "The item: " +  item + " isn't to be " +
                "found, would you like to add to the store", "Item not Found" ,JOptionPane
                .YES_NO_CANCEL_OPTION);
        if (answer == 0){
            //add an item to the store
            String isle = JOptionPane.showInputDialog(null, "What isle is the item located " +
                    "in?", "Isle number");
            driver.addItem(item, isle);
            System.out.println("Yes");
        } else if (answer == 1){
            //do nothing
            System.out.println("No");
        } else{
            //do nothing
            System.out.println("Cancel");
        }
    }
}
