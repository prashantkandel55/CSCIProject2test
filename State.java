import javax.swing.*;

public interface State {
    JPanel getPanel(GUIContext context);
}
