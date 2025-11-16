# Warehouse Management System GUI

## Project Overview
This is a Warehouse Management System with a Graphical User Interface (GUI) implemented using Java Swing and the Finite State Machine (FSM) pattern. The system manages clients, products, wishlists, orders, and inventory for a warehouse operation.

## Features
- **User Roles**: Clerk, Manager, and Client interfaces
- **Client Management**: Add, view, and manage client accounts with credit/debit tracking
- **Product Catalog**: Add products, manage inventory, and track stock levels
- **Wishlist System**: Clients can create and manage wishlists
- **Order Processing**: Process orders with automatic waitlist management for out-of-stock items
- **Invoice Management**: Track client orders and balances
- **Shipment Management**: Receive shipments and fulfill waitlist orders

## GUI States
The application uses FSM pattern with the following states:
- **OpeningGUIState**: Login screen with role selection
- **ClerkMenuGUIState**: Clerk operations menu
- **ManagerMenuGUIState**: Manager operations menu  
- **ClientMenuGUIState**: Client operations menu
- **WishlistMenuGUIState**: Wishlist management for clients

## How to Compile and Run

### Prerequisites
- Java JDK 8 or higher
- Git (for cloning)

### Compilation
```bash
cd WarehouseFSMGUI
javac *.java
```

### Running the Application
```bash
java WarehouseGUIFSM
```

## Testing Sequences

Follow these steps to demonstrate all functionality:

### 1. Create 5 Clients as Clerk
1. Login as Clerk
2. Add 5 clients (C1-C5) using "Add Client" button
3. View all clients to verify $0.00 balances
4. Logout

### 2. Create 5 Products as Manager
1. Login as Manager
2. Add 5 products:
   - P1: Quantity 10, Price $1.00
   - P2: Quantity 20, Price $2.00
   - P3: Quantity 30, Price $3.00
   - P4: Quantity 40, Price $4.00
   - P5: Quantity 50, Price $5.00

### 3. Verify Products as Clerk
1. Become Clerk
2. Show Products to verify all products with correct quantities and prices

### 4. Client C1 Wishlist
1. Become Client C1
2. Add to wishlist: 5 each of P1, P3, P5
3. Display wishlist
4. Logout to Clerk

### 5. Client C2 Wishlist
1. Become Client C2
2. Add to wishlist: 7 each of P1, P2, P4
3. Show C2's wishlist
4. Logout to Clerk → Manager → Login

### 6. Client C3 Wishlist
1. Login as Client C3
2. Add to wishlist: 6 each of P1, P2, P5
3. Print C3's wishlist
4. Logout

### 7. Process Orders
1. Login as Client C2
2. Place order (buy everything available)
3. Logout
4. Login as Clerk
5. View all clients (check balances)
6. Become Client C3
7. Place order (buy everything available)
8. Logout to Clerk

### 8. Final Verification
1. Display all clients
2. Display clients with outstanding balance
3. Logout

## File Structure
- **GUI Classes**: OpeningGUIState, ClerkMenuGUIState, ManagerMenuGUIState, ClientMenuGUIState, WishlistMenuGUIState
- **Core Classes**: GUIContext, State, WarehouseGUIFSM
- **Business Logic**: Client, Product, ProductCatalog, Wishlist, OrderRecord, Invoice, Shipment
- **Data Management**: ClientDatabase, ClientIdServer
- **Test Files**: OrderTest, WishlistTest, TestScript

## Data Persistence
- Product catalog saved to `productcatalog.txt`
- Waitlist data saved to `waitlist.txt`
- Client data managed in memory with serialization support

## Author
Prashant Kandel
GitHub: https://github.com/prashantkandel55
