import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // Create and set up the main window
        Fileexplorer fileExplorer = new Fileexplorer();

        // Set the behavior for the close button
        fileExplorer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Display the window
        fileExplorer.setVisible(true);
    }
}
