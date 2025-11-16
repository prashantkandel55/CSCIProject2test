import java.util.*;

public class OrderTest {
    public static void main(String[] args) {
        // create wishlist
        Wishlist wishlist = new Wishlist(1, 290, "2025-10-19");
        List<WishlistItem> itemsToAdd = new ArrayList<>();
        itemsToAdd.add(new WishlistItem("100", 3)); // product 100 (stock 2)
        itemsToAdd.add(new WishlistItem("101", 1)); // product 101 (stock 5)
        // Add items to wishlist using addItem method
        for (WishlistItem item : itemsToAdd) {
            // Create a dummy product for testing
            Product product = new Product("Test Product", item.getProductID(), "Test", 10, 10.0);
            wishlist.addItem(product, item.getQuantity());
        }

        // create fake product stocks
        Map<Integer, Integer> stockMap = new HashMap<>();
        stockMap.put(100, 2);  
        stockMap.put(101, 5); 

        // Create OrderRecord and Waitlist
        OrderRecord order = new OrderRecord(1, 290);
        Waitlist waitlist = new Waitlist();

        // processOrder logic
        for (WishlistItem item : wishlist.getItems()) {
            String productID = item.getProductID();
            int qtyRequested = item.getQuantity();
            int stock = stockMap.getOrDefault(Integer.parseInt(productID), 0);

            if (stock >= qtyRequested) {
                order.addOrderedItem(item, 10.0); 
                stockMap.put(Integer.parseInt(productID), stock - qtyRequested);
            } else if (stock > 0) {
                order.addOrderedItem(new WishlistItem(999, 1, productID, stock), 10.0);
                waitlist.addToWaitlist(new WishlistItem(1000, 1, productID, qtyRequested - stock));
                stockMap.put(Integer.parseInt(productID), 0);
            } else {
                waitlist.addToWaitlist(item);
            }
        }

        // Display results
        order.displayOrder();
        waitlist.displayWaitlist();
    }
}
