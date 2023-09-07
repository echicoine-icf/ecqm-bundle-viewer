package main;

import javax.swing.*;

import ui.JSONTransferHandler;
import util.FileOpenHandler;

import java.awt.*;

public class JSONFileViewer {
    // Declare tabbedPane as a class-level variable
    public static JTabbedPane tabbedPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("eCQM Bundle Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);

            // Set system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            tabbedPane = new JTabbedPane();

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenuItem openMenuItem = new JMenuItem("Open");

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
