import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import javax.swing.*;

public class ClerkMenuGUIState implements State {

    private GUIContext context;

    @Override
    public JPanel getPanel(GUIContext context) {
        this.context = context;

        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("=== Clerk Menu ===", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        JButton addClientBtn = new JButton("Add Client");
        JButton showProductsBtn = new JButton("Show Products");
        JButton viewClientsBtn = new JButton("View Clients");
        JButton viewClientsWithBalanceBtn = new JButton("View Clients with Balance");
        JButton recordPaymentBtn = new JButton("Record Payment");
        JButton becomeClientBtn = new JButton("Become Client");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(addClientBtn);
        buttonPanel.add(showProductsBtn);
        buttonPanel.add(viewClientsBtn);
        buttonPanel.add(viewClientsWithBalanceBtn);
        buttonPanel.add(recordPaymentBtn);
        buttonPanel.add(becomeClientBtn);
        buttonPanel.add(logoutBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        addClientBtn.addActionListener(e -> showAddClientDialog());
        showProductsBtn.addActionListener(e -> showProductCatalog());
        viewClientsBtn.addActionListener(e -> showAllClientsDialog());
        viewClientsWithBalanceBtn.addActionListener(e -> showClientsWithBalance());
        recordPaymentBtn.addActionListener(e -> recordPayment());
        becomeClientBtn.addActionListener(e -> becomeClient());
        logoutBtn.addActionListener(e -> handleLogout());

        return panel;
    }

    // ==========================================================
    // GUI IMPLEMENTATIONS OF TEXT-BASED CLERK ACTIONS
    // ==========================================================

    /** 1. Add client */
    private void showAddClientDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
                "ID (number):", idField,
                "Name:", nameField,
                "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Client", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = "C" + idField.getText().trim();
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Name and Email cannot be empty!");
                    return;
                }

                Client client = new Client(id, name, email);
                ClientDatabase.instance().addClient(client);
                JOptionPane.showMessageDialog(null, "Client added: " + client.getId());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error adding client: " + ex.getMessage());
            }
        }
    }

    /** 2. Show products (PrintStream → String → GUI) */
    private void showProductCatalog() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);

            context.getProductCatalog().getAllProductInfo(ps);

            JTextArea area = new JTextArea(baos.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(null, new JScrollPane(area), "Product Catalog", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error showing products: " + ex.getMessage());
        }
    }

    /** 3. Show all clients */
    private void showAllClientsDialog() {
        StringBuilder sb = new StringBuilder();
        Iterator<Client> it = ClientDatabase.instance().getClients();

        while (it.hasNext()) {
            Client c = it.next();
            sb.append(c).append("\n");
        }

        JOptionPane.showMessageDialog(null,
                sb.length() > 0 ? sb.toString() : "No clients found.");
    }

    /** 4. Show clients with non-zero balance */
    private void showClientsWithBalance() {
        StringBuilder sb = new StringBuilder();
        Iterator<Client> it = ClientDatabase.instance().getClients();

        while (it.hasNext()) {
            Client c = it.next();
            if (c.getBalance() != 0.0) {
                sb.append(c).append("\n");
            }
        }

        JOptionPane.showMessageDialog(null,
                sb.length() > 0 ? sb.toString() : "No clients with outstanding balances.");
    }

    /** 5. Record payment */
    private void recordPayment() {
        String clientId = JOptionPane.showInputDialog("Enter Client ID:");
        if (clientId == null) return;

        Client client = context.getClientDatabase().search(clientId.trim());
        if (client == null) {
            JOptionPane.showMessageDialog(null, "Client not found.");
            return;
        }

        String amountStr = JOptionPane.showInputDialog("Payment amount:");
        if (amountStr == null) return;

        try {
            double amount = Double.parseDouble(amountStr);
            client.addBalance(-amount);  // payment reduces balance
            JOptionPane.showMessageDialog(null, "New balance: $" + client.getBalance());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount.");
        }
    }

    /** 6. Become Client */
    private void becomeClient() {
        String raw = JOptionPane.showInputDialog("Enter Client ID (e.g., C5):");
        if (raw == null) return;

        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid Client ID.");
            return;
        }

        int id = Integer.parseInt(digits);
        context.setClientID(id);
        context.setStartedFromClerk(true);

        context.setState(new ClientMenuGUIState());
    }

    /** 7. Logout (origin-aware like console version) */
    private void handleLogout() {
        String origin = context.getClerkOrigin();

        if ("Manager".equals(origin)) {
            context.setState(new ManagerMenuGUIState());
        } else {
            context.setState(new OpeningGUIState());
        }
    }
}
