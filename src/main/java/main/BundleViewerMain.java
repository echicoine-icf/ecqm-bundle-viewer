package main;

import javax.swing.*;

import persist.Constants;
import ui.CloseableTabbedPane;
import ui.JSONTransferHandler;
import util.FileOpenHandler;

import java.awt.*;

public class BundleViewerMain {
    // Declare tabbedPane as a class-level variable
    public static CloseableTabbedPane tabbedPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(Constants.TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);

            // Set system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            tabbedPane = new CloseableTabbedPane();

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu(Constants.MENU_FILE);
            JMenuItem openMenuItem = new JMenuItem(Constants.MENU_FILE_OPEN);

            openMenuItem.addActionListener(new FileOpenHandler());

            fileMenu.add(openMenuItem);
            menuBar.add(fileMenu);

            frame.setJMenuBar(menuBar);
            frame.add(tabbedPane, BorderLayout.CENTER);

            // Enable drag-and-drop functionality for the frame
            frame.setTransferHandler(new JSONTransferHandler());

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
