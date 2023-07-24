package com._4point.testing.matchers.aem;

import static com._4point.testing.matchers.aem.PathUtils.replaceQualifier;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PdfComparator;

/**
 * PdfComparer is used to compare to PDFs for visual results (sort of a "digital lightbox").
 * 
 * NOTE: Class is still under development - api and behaviour may change.
 * 
 */
public class PdfComparer {

	private final Path goldResultsDir;
	private final Path actualResultsDir;
	
	/**
	 * Constructs a PdfComparer
	 * 
	 * @param goldResultsDir
	 *  directory where the expected (gold) pdf result files will be.  This cannot be null and the directory
	 *  must already exist.
	 * @param actualResultsDir
	 * 	directory where the actual results will be written along with diff comparison results.  This directory
	 *  will be created if it does not already exist.  This parameter can be null if no actual lresults are to
	 *  be retained. 
	 */
	public PdfComparer(Path goldResultsDir, Path actualResultsDir) {
		if (goldResultsDir != null && !Files.exists(goldResultsDir)){
			throw new IllegalArgumentException("Expected (Gold) Results Directory does not exist.");
		}
		if (goldResultsDir != null && !Files.isDirectory(goldResultsDir)){
			throw new IllegalArgumentException("Expected (Gold) Results Path does not point to a directory.");
		}
		this.goldResultsDir = goldResultsDir;
		this.actualResultsDir = actualResultsDir;
	}

	/**
	 * PDF Comparison helper routines.
	 * 
	 */
	private void compare(Path goldFile, byte[] fileUnderTest) {
		compare(goldFile, fileUnderTest, null);
	}

	private void compare(Path goldFile, byte[] fileUnderTest, Path exclusionFile) {
		if (goldResultsDir!= null &&  !goldFile.isAbsolute()) {
			goldFile = goldResultsDir.resolve(goldFile);
		}
		if (exclusionFile != null && !exclusionFile.isAbsolute()) {
			exclusionFile = goldResultsDir.resolve(exclusionFile);
		}
		try (var goldIs = Files.newInputStream(goldFile);
			 var exclIs = exclusionFile == null ? null : Files.newInputStream(exclusionFile)
			) {
			compare(goldIs, fileUnderTest, goldFile, exclIs);
		} catch (IOException e) {
			throw new IllegalStateException("I/O error while comparing Pdfs", e);
		}

	}

	private void compare(InputStream goldFile, byte[] fileUnderTestBytes, Path filenameUnderTest, InputStream exclusions) throws IOException {
		ByteArrayInputStream fileUnderTestStream = new ByteArrayInputStream(fileUnderTestBytes);
//		LOG.info("Testing file '" + filenameUnderTest.getFileName().toString() + "'.");
		final CompareResult result = exclusions == null ? new PdfComparator(goldFile, fileUnderTestStream).compare()
				: new PdfComparator(goldFile, fileUnderTestStream).withIgnore(exclusions).compare();
		Path diffFilename = replaceQualifier(filenameUnderTest, "diff");
		Path diffPath = actualResultsDir.resolve(diffFilename.getFileName());
		if (result.isNotEqual()) {
			try {
				Files.createDirectories(actualResultsDir);
			} catch (IOException e) {
				// eat it, we don't care.
			}
			result.writeTo(diffPath.toString());
			fileUnderTestStream.reset();
			Files.copy(fileUnderTestStream, actualResultsDir.resolve(replaceQualifier(filenameUnderTest, "result").getFileName()));
		}
		assertTrue(result.isEqual(), "Differences were found, Diff written to " + diffPath.toString());
	}
	
	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * If the PDFs do not match, then the byte array is written to the actual results directory and a PDF
	 * of the differences is also written to the actual results directory.
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesEqual(Path expectedResultFile) {
		return new TypeSafeDiagnosingMatcher<byte[]>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("pdfs should match ");;
			}

			@Override
			protected boolean matchesSafely(byte[] item, Description mismatchDescription) {
				compare(expectedResultFile, item);
				return true;
			}
		};
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * If the PDFs do not match, then the byte array is written to the actual results directory and a PDF
	 * of the differences is also written to the actual results directory.
	 * 
	 * This is a convenience function that converts the parameter to a Path and then calls comparesEqual(Path).
	 * 
	 * @param expectedResultFile
	 * 	name of expected result file.
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesEqual(String expectedResultFile) {
		return comparesEqual(Path.of(expectedResultFile));
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * Nothing is written to the actual results directory regardless of the outcome of the match.
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesNotEqual(Path expectedResultFile) {
		throw new UnsupportedOperationException("Not implented yet.");
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * Nothing is written to the actual results directory regardless of the outcome of the match.
	 * 
	 * This is a convenience function that converts the parameter to a Path and then calls comparesNotEqual(Path).
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesNotEqual(String expectedResultFile) {
		return comparesNotEqual(Path.of(expectedResultFile));
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * If the PDFs do not match, then the byte array is written to the actual results directory and a PDF
	 * of the differences is also written to the actual results directory.
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @param exclusionsFile
	 *  path to the exclusions file
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesEqual(Path expectedResultFile, Path exclusionsFile) {
		return null;
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * If the PDFs do not match, then the byte array is written to the actual results directory and a PDF
	 * of the differences is also written to the actual results directory.
	 * 
	 * This is a convenience function that converts the parameters to Path objects and then calls comparesEqual(Path, Path).
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @param exclusionsFile
	 *  path to the exclusions file
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesEqual(String expectedResultFile, String exclusionsFile) {
		return comparesEqual(Path.of(expectedResultFile), Path.of(exclusionsFile));
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * Nothing is written to the actual results directory regardless of the outcome of the match.
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @param exclusionsFile
	 *  path to the exclusions file
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesNotEqual(Path expectedResultFile, Path exclusionsFile) {
		throw new UnsupportedOperationException("Not implented yet.");
	}

	/**
	 * Creates a matcher that compares a PDF in a byte array to a expected "gold" result file.
	 * 
	 * If the expected "gold" result file path is relative, then it is relative to the "gold" directory.
	 * 
	 * Nothing is written to the actual results directory regardless of the outcome of the match.
	 * 
	 * This is a convenience function that converts the parameters to Path objects and then calls comparesNotEqual(Path, Path).
	 * 
	 * @param expectedResultFile
	 * 	path to expected result file.
	 * @param exclusionsFile
	 *  path to the exclusions file
	 * @return the Matcher
	 */
	public Matcher<byte[]> comparesNotEqual(String expectedResultFile, String exclusionsFile) {
		return comparesNotEqual(Path.of(expectedResultFile), Path.of(exclusionsFile));
	}
}
