import java.awt.*;
import javax.swing.*;

public class WishlistMenuGUIState implements State {

    @Override
    public JPanel getPanel(GUIContext context) {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        int clientID = context.getClientID();
        Client client = context.getClientDatabase().search("C" + clientID);

        if (client == null) {
            JOptionPane.showMessageDialog(null, "Client not found.");
            context.setState(new OpeningGUIState());
            return new JPanel();
        }

        JLabel title = new JLabel("Wishlist: C" + clientID, JLabel.CENTER);
        panel.add(title);

        JButton viewBtn = new JButton("View Wishlist");
        viewBtn.addActionListener(e -> viewWishlist(client));
        panel.add(viewBtn);

        JButton addBtn = new JButton("Add Item");
        addBtn.addActionListener(e -> addItem(context, client));
        panel.add(addBtn);

        JButton modifyBtn = new JButton("Modify Item Quantity");
        modifyBtn.addActionListener(e -> modifyItem(context, client));
        panel.add(modifyBtn);

        JButton removeBtn = new JButton("Remove Item");
        removeBtn.addActionListener(e -> removeItem(client));
        panel.add(removeBtn);

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.addActionListener(e -> placeOrder(context, client));
        panel.add(placeOrderBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> context.setState(new ClientMenuGUIState()));
        panel.add(backBtn);

        return panel;
    }

    private Wishlist getOrCreateWishlist(Client client) {
        java.util.List<Wishlist> wishlists = client.getWishlists();
        if (wishlists.isEmpty()) {
            Wishlist wl = new Wishlist(1, Integer.parseInt(client.getId().substring(1)), new java.util.Date().toString());
            client.addWishlist(wl);
            return wl;
        }
        return wishlists.get(0);
    }

    private void viewWishlist(Client client) {
        Wishlist wl = getOrCreateWishlist(client);
        if (wl.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Wishlist empty.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (WishlistItem item : wl.getItems()) sb.append(item).append("\n");
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Wishlist", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addItem(GUIContext context, Client client) {
        JTextField skuField = new JTextField();
        JTextField qtyField = new JTextField();
        Object[] message = {"Product SKU:", skuField, "Quantity:", qtyField};
        int option = JOptionPane.showConfirmDialog(null, message, "Add Item", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Product product = context.getProductCatalog().getProductBySku(skuField.getText());
            if (product == null) { JOptionPane.showMessageDialog(null, "Product not found."); return; }
            try {
                int qty = Integer.parseInt(qtyField.getText());
                getOrCreateWishlist(client).addItem(product, qty);
                JOptionPane.showMessageDialog(null, "Item added.");
            } catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "Invalid quantity."); }
        }
    }

    private void modifyItem(GUIContext context, Client client) {
        Wishlist wl = getOrCreateWishlist(client);
        if (wl.getItems().isEmpty()) { JOptionPane.showMessageDialog(null, "Wishlist empty."); return; }

        JTextField skuField = new JTextField();
        JTextField qtyField = new JTextField();
        Object[] message = {"Product SKU:", skuField, "New Quantity:", qtyField};
        int option = JOptionPane.showConfirmDialog(null, message, "Modify Item", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            for (WishlistItem item : wl.getItems()) {
                if (item.getProductID().equals(skuField.getText())) {
                    try {
                        item.setQuantity(Integer.parseInt(qtyField.getText()));
                        JOptionPane.showMessageDialog(null, "Quantity updated.");
                        return;
                    } catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "Invalid quantity."); return; }
                }
            }
            JOptionPane.showMessageDialog(null, "Item not found.");
        }
    }

    private void removeItem(Client client) {
        Wishlist wl = getOrCreateWishlist(client);
        if (wl.getItems().isEmpty()) { JOptionPane.showMessageDialog(null, "Wishlist empty."); return; }

        String sku = JOptionPane.showInputDialog("Enter product SKU to remove:");
        if (sku != null) {
            if (wl.removeItem(sku)) JOptionPane.showMessageDialog(null, "Item removed.");
            else JOptionPane.showMessageDialog(null, "Item not found.");
        }
    }

    private void placeOrder(GUIContext context, Client client) {
        Wishlist wl = getOrCreateWishlist(client);
        if (wl.getItems().isEmpty()) { JOptionPane.showMessageDialog(null, "Wishlist empty."); return; }

        java.util.List<WishlistItem> items = wl.getItems();
        java.util.List<WishlistItem> unfulfilled = new java.util.ArrayList<>();
        double totalCost = 0.0;

        for (WishlistItem item : items) {
            Product product = context.getProductCatalog().getProductBySku(item.getProductID());
            if (product == null) { unfulfilled.add(item); continue; }

            int needed = item.getQuantity();
            int available = product.getQuantity();

            if (available >= needed) {
                product.updateStock(available - needed);
                double cost = needed * product.getDefaultPrice();
                totalCost += cost;
                client.addBalance(-cost);

                Invoice invoice = new Invoice(client.getId(), product.getSku(), needed, product.getDefaultPrice());
                context.getClientInvoices().computeIfAbsent(client.getId(), k -> new java.util.ArrayList<>()).add(invoice);

                JOptionPane.showMessageDialog(null, "Order fulfilled: " + needed + " of " + product.getSku());
            } else {
                JOptionPane.showMessageDialog(null, "Insufficient stock for " + product.getSku() + ". Added to waitlist.");
                context.getProductCatalog().addToWaitlist(product.getSku(), client.getId(), needed, product.getDefaultPrice());
                unfulfilled.add(item);
            }
        }

        wl.clear();
        for (WishlistItem item : unfulfilled) {
            Product product = context.getProductCatalog().getProductBySku(item.getProductID());
            if (product != null) wl.addItem(product, item.getQuantity());
        }

        if (totalCost > 0) JOptionPane.showMessageDialog(null, "Total order cost: $" + String.format("%.2f", totalCost));
        JOptionPane.showMessageDialog(null, "Order processing complete.");
    }
}
