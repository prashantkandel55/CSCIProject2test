public class WishlistItem {

    private int wishlistItemID;   // optional (your code uses different constructors)
    private int wishlistID;       // optional
    private String productID;     // SKU as String
    private int quantity;

    // Full constructor (for tests that expect wishlistItemID & wishlistID)
    public WishlistItem(int wishlistItemID, int wishlistID, String productID, int quantity) {
        this.wishlistItemID = wishlistItemID;
        this.wishlistID = wishlistID;
        this.productID = productID;
        this.quantity = quantity;
    }

    // Simpler constructor (for your FSM Wishlist operations)
    public WishlistItem(String productID, int quantity) {
        this.wishlistItemID = 0;
        this.wishlistID = 0;
        this.productID = productID;
        this.quantity = quantity;
    }

    public int getWishlistItemID() {
        return wishlistItemID;
    }

    public int getWishlistID() {
        return wishlistID;
    }

    public String getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    // This is what WishlistMenuState needs
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Old method renamed to maintain compatibility
    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    @Override
    public String toString() {
        return "ProductID: " + productID + ", Quantity: " + quantity;
    }
}
