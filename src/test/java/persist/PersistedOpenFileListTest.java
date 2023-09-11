package persist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

public class PersistedOpenFileListTest {

	@Test
	public void testAddFileToList() {
		// Test adding a file to the list
		int tabIndex = PersistedOpenFileList.addFileToList(new File("/path/to/file1.txt"));
		assertEquals(0, tabIndex); // Expected tab index for the first file

		// Test adding the same file again
		int tabIndex2 = PersistedOpenFileList.addFileToList(new File("/path/to/file1.txt"));
		assertEquals(0, tabIndex2); // Should return the same tab index

		// Test adding a different file
		int tabIndex3 = PersistedOpenFileList.addFileToList(new File("/path/to/file2.txt"));
		assertEquals(-1, tabIndex3); // Expected tab index for the second file
	}

	@Test
	public void testRemoveFromMap() {
		// Add a file to the list
		PersistedOpenFileList.addFileToList(new File("/path/to/file1.txt"));
		
		//add it again, this time get it's index:
		int tabIndex = PersistedOpenFileList.addFileToList(new File("/path/to/file1.txt"));
		assertEquals(0, tabIndex);
		
		// Remove the file from the list
		PersistedOpenFileList.removeFromMap(tabIndex);

		// Attempt to add again, this time index should remain 0:
		int notFoundIndex = PersistedOpenFileList.addFileToList(new File("/path/to/file1.txt"));
		assertEquals(-1, notFoundIndex); // Should not find the removed file and assign a new index
		
 
	}
}
