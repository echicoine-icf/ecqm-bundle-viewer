package ui;


import javax.swing.*;

import util.FileOpenService;

import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**Simple class for allowing drag and drop of files over the main pane to open them
 * @author echic
 *
 */
public class BundleTransferHandler extends TransferHandler {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
            List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

            // Handle each dropped file
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".json")) {
                    FileOpenService.openJsonFile(file);
                }
            }
            return true;
        } catch (IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
            return false;
        }
    }
}

