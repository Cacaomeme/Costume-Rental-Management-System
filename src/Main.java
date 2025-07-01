import gui.LoginFrame;
import gui.Costume; 
import gui.CostumeDataManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;

public class Main {
	public static List<Costume> allCostumes;
	
    public static void main(String[] args) {
    	CostumeDataManager dataManager = new CostumeDataManager();
        allCostumes = dataManager.loadCostumes();

        System.out.println("Loaded " + allCostumes.size() + " costumes.");
        
        // Set system look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch the application on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}