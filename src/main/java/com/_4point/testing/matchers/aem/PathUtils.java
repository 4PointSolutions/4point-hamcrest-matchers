package com._4point.testing.matchers.aem;

import java.nio.file.Path;

/*package*/ class PathUtils {
	private static final char EXTENSION_CHAR = '.';
	private static final char QUALIFIER_CHAR = '_';

	/**
	 * Removes the extnsion from a file.
	 * 
	 * @param file
	 * 	file from which the extension will get removed 
	 * @return the file without an extension
	 */
	/*package*/ static Path removeExtension(Path file) {
		String newFilename = truncateFrom(file.getFileName().toString(), EXTENSION_CHAR);
		Path resolve = replaceFilename(file.getParent(), newFilename);
		return resolve;
	}

	private static String truncateFrom(String filename, char target) {
		int extensionIndex = filename.lastIndexOf(target);
		return extensionIndex < 0 ? filename : filename.substring(0, extensionIndex);
	}
	
	private static Path replaceFilename(Path parent, String newFilename) {
		return parent != null ? parent.resolve(newFilename) : Path.of(newFilename);
	}
	
	/**
	 * Gets the extension of a file.  It returns an empty string if there is no extension on the filename.
	 * 
	 * @param file
	 * 	file from which the extension will get retrieved 
	 * @return the extension of the file (empty string if the filename has no extension)
	 */
	/*package*/ static String getExtension(Path file) {
		String filename = file.getFileName().toString();
		int extensionIndex = filename.lastIndexOf(EXTENSION_CHAR);
		return extensionIndex < 0 ? "" : filename.substring(extensionIndex + 1);
	}
	
	/**
	 * A qualifier is a string that occurs at the end of a filename after an _ character but before the extension.
	 * For example, foo_result.pdf - qualifier is "_result"
	 * 
	 * It replaces the qualifier while keeping the original extension
	 * 
	 * @param file
	 * 	original filename
	 * @param newQualifier
	 * 	new qualifier to replace old one.
	 * @return updated filename
	 */
	/*package*/ static Path replaceQualifier(Path file, String newQualifier) {
		String extension = getExtension(file);
		String newFilename = removeExtension(file.getFileName()).toString();
		if (newFilename.indexOf(QUALIFIER_CHAR) > 0) {
			newFilename = truncateFrom(newFilename, QUALIFIER_CHAR);
		}
		return replaceFilename(file.getParent(), newFilename + QUALIFIER_CHAR + newQualifier + (extension.isEmpty() ? "" : EXTENSION_CHAR + extension));
	}
}
