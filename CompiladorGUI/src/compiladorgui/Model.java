package compiladorgui;



import javafx.stage.Window;

/*
 * Holds everything the UI needs to work.
 * The actual data is loaded via the DAO objects.
 */
public class Model {

    /*
     * Initialize the model. This will load the groups from the database, as
     * well as the reminders in the first group (if there is one).
     * If something goes wrong here, the exception is queued and the group
     * and/or reminder list starts out empty.
     */
   
    /*
     * A reference to the main window. This can come in handy when working with
     * dialog windows.
     */
    private Window mainWindow;

    public Window getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(Window mainWindow) {
        this.mainWindow = mainWindow;
    }
   
    
    private Model() { }
    
    private static final Model instance = new Model();
    
    public static Model getInstance() {
        return instance;
    }
}
