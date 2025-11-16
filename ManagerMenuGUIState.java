import java.awt.*;
import javax.swing.*;

public class ManagerMenuGUIState implements State {

    @Override
    public JPanel getPanel(GUIContext context) {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Manager Menu", JLabel.CENTER);
        panel.add(title);

        //Add Product
        JButton addProductBtn = new JButton("Add Product");
        addProductBtn.addActionListener(e -> addProduct(context));
        panel.add(addProductBtn);

        //Display Waitlist
        JButton displayWaitlistBtn = new JButton("Display Waitlist");
        displayWaitlistBtn.addActionListener(e -> displayWaitlist(context));
        panel.add(displayWaitlistBtn);

        //Receive Shipment
        JButton receiveShipmentBtn = new JButton("Receive Shipment");
        receiveShipmentBtn.addActionListener(e -> receiveShipment(context));
        panel.add(receiveShipmentBtn);

        //Become Clerk
        JButton becomeClerkBtn = new JButton("Become Clerk");
        becomeClerkBtn.addActionListener(e -> {
            context.setClerkOrigin("Manager");  // track origin
            context.setState(new ClerkMenuGUIState());
        });
        panel.add(becomeClerkBtn);

        //Logout
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            context.resetClerkFlags();
            context.setState(new OpeningGUIState());
        });
        panel.add(logoutBtn);

        return panel;
    }

    private void addProduct(GUIContext context) {
        JTextField nameField = new JTextField();
        JTextField skuField = new JTextField();
        JTextField descField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField priceField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "SKU:", skuField,
                "Description:", descField,
                "Quantity:", qtyField,
                "Price:", priceField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String sku = skuField.getText();
                String desc = descField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());

                Product product = new Product(name, sku, desc, qty, price);
                context.getProductCatalog().addProduct(product);
                JOptionPane.showMessageDialog(null, "Product added!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid number format!");
            }
        }
    }

    private void displayWaitlist(GUIContext context) {
        String sku = JOptionPane.showInputDialog("Enter SKU to display waitlist:");
        if (sku != null) {
            context.getProductCatalog().displayWaitlist(sku);
        }
    }

    private void receiveShipment(GUIContext context) {
        JTextField skuField = new JTextField();
        JTextField qtyField = new JTextField();
        Object[] message = {"SKU:", skuField, "Quantity:", qtyField};

        int option = JOptionPane.showConfirmDialog(null, message, "Receive Shipment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String sku = skuField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                context.getProductCatalog().receiveShipment(sku, qty);
                JOptionPane.showMessageDialog(null, "Shipment received!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid number!");
            }
        }
    }
}

