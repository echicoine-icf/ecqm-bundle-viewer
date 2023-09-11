package main;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import persist.Constants;
import ui.BundleTransferHandler;
import ui.CloseableTabbedPane;
import util.FileOpenHandler;

public class BundleViewerMain {
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

			// Create a JPanel to hold the tabbedPane and message label
			JPanel mainPanel = new JPanel(new BorderLayout());

			tabbedPane = new CloseableTabbedPane();
			mainPanel.add(tabbedPane, BorderLayout.CENTER);

			// Enable drag-and-drop functionality for the mainPanel
			mainPanel.setTransferHandler(new BundleTransferHandler());

			// Create a JLabel for the message
			JLabel messageLabel = new JLabel("Drag and drop Bundle JSON files here to open them");
			messageLabel.setFont(new Font("Arial", Font.PLAIN, 9));
			messageLabel.setHorizontalAlignment(JLabel.CENTER);

			mainPanel.add(messageLabel, BorderLayout.NORTH);

			frame.setJMenuBar(createMenuBar());
			frame.add(mainPanel, BorderLayout.CENTER);

			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

	// Create the menu bar (extracted to a separate method for readability)
	private static JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(Constants.MENU_FILE);
		JMenuItem openMenuItem = new JMenuItem(Constants.MENU_FILE_OPEN);

		openMenuItem.addActionListener(new FileOpenHandler());

		fileMenu.add(openMenuItem);
		menuBar.add(fileMenu);

		return menuBar;
	}
}
