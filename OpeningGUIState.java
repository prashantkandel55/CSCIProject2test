import java.awt.*;
import javax.swing.*;

public class OpeningGUIState implements State {

    @Override
    public JPanel getPanel(GUIContext context) {

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel label = new JLabel("Warehouse Login System", JLabel.CENTER);
        panel.add(label);

        //CLIENT LOGIN
        JButton clientBtn = new JButton("Client Login");
        clientBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter Client ID (e.g., C1):");

            if (input == null) return;
            input = input.trim();

            if (!input.matches("C\\d+")) {
                JOptionPane.showMessageDialog(panel, "Invalid Client ID format.");
                return;
            }

            Client client = ClientDatabase.instance().search(input);
            if (client == null) {
                JOptionPane.showMessageDialog(panel, "Client not found.");
                return;
            }

            int id = Integer.parseInt(input.substring(1));

            context.setClientID(id);
            context.setStartedFromClerk(false); 
            context.setState(new ClientMenuGUIState());
        });
        panel.add(clientBtn);

        //CLERK LOGIN
        JButton clerkBtn = new JButton("Clerk Login");
        clerkBtn.addActionListener(e -> {
            context.setClerkOrigin("Opening");
            context.setState(new ClerkMenuGUIState());
        });
        panel.add(clerkBtn);

        //MANAGER LOGIN
        JButton managerBtn = new JButton("Manager Login");
        managerBtn.addActionListener(e -> {
            context.setState(new ManagerMenuGUIState());
        });
        panel.add(managerBtn);


        //EXIT PROGRAM
        JButton exitBtn = new JButton("Exit Program");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

        return panel;
    }
}
