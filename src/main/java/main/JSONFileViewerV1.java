package main;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONFileViewerV1 {
    // Declare tabbedPane as a class-level variable
    private static JTabbedPane tabbedPane;

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

            openMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
                    fileChooser.setFileFilter(filter);

                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        openJsonFile(fileChooser.getSelectedFile());
                    }
                }
            });

            fileMenu.add(openMenuItem);
            menuBar.add(fileMenu);

            frame.setJMenuBar(menuBar);
            frame.add(tabbedPane, BorderLayout.CENTER);

            // Enable drag-and-drop functionality for the frame
            frame.setTransferHandler(new FileTransferHandler());

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Validate JSON content using JSONTokener
    private static boolean isValidJSON(String jsonContent) {
        try {
            new JSONTokener(jsonContent).nextValue();
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    // Custom TransferHandler to handle file drops
    static class FileTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable transferable = support.getTransferable();
            try {
                @SuppressWarnings("unchecked")
                java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                // Handle each dropped file
                for (File file : files) {
                    if (file.getName().toLowerCase().endsWith(".json")) {
                        openJsonFile(file);
                    }
                }
                return true;
            } catch (IOException | UnsupportedFlavorException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static void openJsonFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            reader.close();

            // Validate JSON content
            if (!isValidJSON(fileContent.toString())) {
                JOptionPane.showMessageDialog(null, "Invalid JSON content.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextArea textArea = new JTextArea(20, 60);
            textArea.setEditable(false);
            textArea.setText(fileContent.toString());

            JScrollPane scrollPane = new JScrollPane(textArea);

            // Parse the JSON content as a JSONObject
            JSONObject jsonObject = new JSONObject(fileContent.toString());

            // Check if the top-level object has an "entry" array and "resourceType" is "Bundle"
            if (jsonObject.has("entry") && jsonObject.has("resourceType") && jsonObject.getString("resourceType").equals("Bundle")) {
                JSONArray jsonArray = jsonObject.getJSONArray("entry");
                DefaultListModel<String> resourceListModel = new DefaultListModel<>();

                // Add the top-level JSON as the "Full JSON File" entry
                resourceListModel.addElement("Full JSON File");

                // Iterate through the "entry" array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject entryObject = jsonArray.getJSONObject(i);
                    if (entryObject.has("resource") && entryObject.getJSONObject("resource").has("title")) {
                        // Use the "title" field if it exists
                        resourceListModel.addElement(entryObject.getJSONObject("resource").getString("title"));
                    } else {
                        // Use a default title
                        resourceListModel.addElement("Resource " + (i + 1));
                    }
                }

                JList<String> resourceList = new JList<>(resourceListModel);
                resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // Add a ListSelectionListener to handle list item clicks
                resourceList.addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        if (!event.getValueIsAdjusting()) {
                            int selectedIndex = resourceList.getSelectedIndex();
                            if (selectedIndex == 0) {
                                // Display the full JSON file
                                textArea.setText(fileContent.toString());
                            } else {
                                // Display the JSON snippet for the selected resource
                                if (jsonObject.has("entry")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("entry");
                                    JSONObject selectedResource = jsonArray.getJSONObject(selectedIndex - 1);
                                    String snippet = selectedResource.toString(4); // 4-space indentation
                                    textArea.setText(snippet);
                                }
                            }
                            textArea.setCaretPosition(0); // Scroll to the top
                        }
                    }
                });

                // Create a split pane to display list and text
                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resourceList), scrollPane);
                splitPane.setResizeWeight(0.2);

                // Add the new tab with the split pane
                tabbedPane.addTab(file.getName(), splitPane);

                // Set focus to the newly created tab
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

            } else {
                JOptionPane.showMessageDialog(null, "The selected file is not a valid Bundle JSON.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle file read error
        }
    }
}
