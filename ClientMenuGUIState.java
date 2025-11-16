import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class ClientMenuGUIState implements State {

    @Override
    public JPanel getPanel(GUIContext context) {
        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 10));
        int clientID = context.getClientID();
        Client client = context.getClientDatabase().search("C" + clientID);

        JLabel title = new JLabel("Client Menu: C" + clientID, JLabel.CENTER);
        panel.add(title);

        //Show client details
        JButton detailsBtn = new JButton("Show Client Details");
        detailsBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, client.toString()));
        panel.add(detailsBtn);

        //Show products
        JButton productsBtn = new JButton("Show Products");
        productsBtn.addActionListener(e -> showProducts(context));
        panel.add(productsBtn);

        //View invoices
        JButton viewInvoicesBtn = new JButton("View Invoices");
        viewInvoicesBtn.addActionListener(e -> viewInvoices(context, client));
        panel.add(viewInvoicesBtn);

        //Moved to wishlistGUI

        //Add to wishlist
        //JButton addToWishlistBtn = new JButton("Add to Wishlist");
        //addToWishlistBtn.addActionListener(e -> addToWishlist(context, client));
        //panel.add(addToWishlistBtn);

        //Display wishlist
        //JButton displayWishlistBtn = new JButton("Display Wishlist");
        //displayWishlistBtn.addActionListener(e -> displayWishlist(client));
        //panel.add(displayWishlistBtn);

        //wishlistGUI Operations
        JButton wishlistOpsBtn = new JButton("Wishlist Operations");
        wishlistOpsBtn.addActionListener(e -> context.setState(new WishlistMenuGUIState()));
        panel.add(wishlistOpsBtn);

        //Place order
        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.addActionListener(e -> placeOrder(context, client));
        panel.add(placeOrderBtn);

        

        //Logout
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> context.setState(new OpeningGUIState()));
        panel.add(logoutBtn);

        return panel;
    }


    private void showProducts(GUIContext context) {
    try {
        // Capture printed output
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);

        // Call the catalog method that requires PrintStream
        context.getProductCatalog().getAllProductInfo(ps);

        String productText = baos.toString();

        if (productText.isEmpty()) {
            productText = "No products found.";
        }

        // Display in a scrollable dialog
        JTextArea area = new JTextArea(productText);
        area.setEditable(false);

        JOptionPane.showMessageDialog(
                null,
                new JScrollPane(area),
                "Product Catalog",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error displaying products.");
    }
}


    private void viewInvoices(GUIContext context, Client client) {
        List<Invoice> invoices = context.getClientInvoices().get(client.getId());
        if (invoices == null || invoices.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No invoices.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        invoices.forEach(inv -> sb.append(inv).append("\n"));

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Invoices", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addToWishlist(GUIContext context, Client client) {
        List<Wishlist> wishlists = client.getWishlists();
        Wishlist wishlist;

        if (wishlists.isEmpty()) {
            wishlist = new Wishlist(1, Integer.parseInt(client.getId().substring(1)), new Date().toString());
            client.addWishlist(wishlist);
        } else {
            wishlist = wishlists.get(0);
        }

        String sku = JOptionPane.showInputDialog("Enter product SKU:");
        if (sku == null) return;

        Product product = context.getProductCatalog().getProductBySku(sku);
        if (product == null) {
            JOptionPane.showMessageDialog(null, "Product not found.");
            return;
        }

        String qtyStr = JOptionPane.showInputDialog("Enter quantity:");
        if (qtyStr == null) return;

        int qty = Integer.parseInt(qtyStr);
        wishlist.addItem(product, qty);

        JOptionPane.showMessageDialog(null, "Product added to wishlist.");
    }

    private void displayWishlist(Client client) {
        List<Wishlist> wishlists = client.getWishlists();
        if (wishlists.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No wishlists.");
            return;
        }

        Wishlist wishlist = wishlists.get(0);
        StringBuilder sb = new StringBuilder("Wishlist #" + wishlist.getWishlistID() + ":\n");

        for (WishlistItem item : wishlist.getItems()) {
            sb.append(item).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area), "Wishlist", JOptionPane.INFORMATION_MESSAGE);
    }

    private void placeOrder(GUIContext context, Client client) {
        List<Wishlist> wishlists = client.getWishlists();
        if (wishlists.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No wishlists.");
            return;
        }

        Wishlist wishlist = wishlists.get(0);
        List<WishlistItem> items = wishlist.getItems();
        List<WishlistItem> unfulfilled = new ArrayList<>();
        double totalCost = 0.0;

        for (WishlistItem item : items) {
            Product product = context.getProductCatalog().getProductBySku(item.getProductID());
            if (product == null) {
                unfulfilled.add(item);
                continue;
            }

            int availableQty = product.getQuantity();
            int requestedQty = item.getQuantity();

            if (availableQty >= requestedQty) {
                product.updateStock(availableQty - requestedQty);
                double cost = requestedQty * product.getDefaultPrice();
                totalCost += cost;
                client.addBalance(-cost);

                Invoice invoice = new Invoice(client.getId(), product.getSku(), requestedQty, product.getDefaultPrice());
                context.getClientInvoices().computeIfAbsent(client.getId(), k -> new ArrayList<>()).add(invoice);

            } else {
                context.getProductCatalog().addToWaitlist(product.getSku(), client.getId(),
                        requestedQty, product.getDefaultPrice());
                unfulfilled.add(item);
            }
        }

        wishlist.clear();
        for (WishlistItem item : unfulfilled) {
            Product product = context.getProductCatalog().getProductBySku(item.getProductID());
            if (product != null) wishlist.addItem(product, item.getQuantity());
        }

        JOptionPane.showMessageDialog(null,
                "Order processed.\nTotal cost: $" + String.format("%.2f", totalCost));
    }
}
