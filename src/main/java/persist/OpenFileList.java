package persist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a hashmap of open files and their tab index
 * 
 * @author echic
 *
 */
public class OpenFileList {
	private static List<String> tabLocation = new ArrayList<>();
	/**
	 * when opening file in new tab, check for existing entry, return index if
	 * already open
	 * 
	 * @param fullPath
	 * @param tabIndex
	 * @return
	 */
	public static Integer addFileToList(File file) {
		String fullPath = file.getAbsolutePath();
		Integer i = tabLocation.indexOf(fullPath);
		System.out.println(i);
		if (i != -1 ) {
			return i;
		}
		tabLocation.add(fullPath);
		return -1;
	}

	public static void removeFromMap(Integer tabIndex) {
		int tabIn = tabIndex;
		tabLocation.remove(tabIn);
	}
}
