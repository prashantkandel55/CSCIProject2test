public class WarehouseGUIFSM {
    public static void main(String[] args) {
        GUIContext context = new GUIContext();
        context.setState(new OpeningGUIState());
        context.start();
    }
}
