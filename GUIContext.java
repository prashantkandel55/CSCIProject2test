import java.awt.*;
import javax.swing.*;

public class GUIContext {
    private JFrame frame;
    private JPanel currentPanel;
    private State currentState;

    // Shared data
    private int clientID;
    private ProductCatalog productCatalog;
    private ClientDatabase clientDatabase;
    private java.util.Map<String, java.util.List<Invoice>> clientInvoices;


    private boolean startedFromClerk = false;   
    private String clerkOrigin = "";           

    public GUIContext() {
        frame = new JFrame("Warehouse FSM");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        productCatalog = new ProductCatalog();
        clientDatabase = ClientDatabase.instance();
        clientInvoices = new java.util.HashMap<>();
    }

    public void setState(State state) {
        if (currentPanel != null)
            frame.remove(currentPanel);
        
        currentState = state;
        currentPanel = state.getPanel(this);
        frame.add(currentPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public JFrame getFrame() { return frame; }
    public ProductCatalog getProductCatalog() { return productCatalog; }
    public ClientDatabase getClientDatabase() { return clientDatabase; }

    public int getClientID() { return clientID; }
    public void setClientID(int clientID) { this.clientID = clientID; }

    public java.util.Map<String, java.util.List<Invoice>> getClientInvoices() { 
        return clientInvoices; 
    }

    public void setStartedFromClerk(boolean value) {
        this.startedFromClerk = value;
    }

    public boolean isStartedFromClerk() {
        return startedFromClerk;
    }

    public void setClerkOrigin(String origin) {
        this.clerkOrigin = origin;
    }

    public String getClerkOrigin() {
        return clerkOrigin;
    }

    public void resetClerkFlags() {
        startedFromClerk = false;
        clerkOrigin = "";
    }

    public void start() { frame.setVisible(true); }
}

