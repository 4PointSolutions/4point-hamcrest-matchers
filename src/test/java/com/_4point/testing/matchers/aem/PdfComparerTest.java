package com._4point.testing.matchers.aem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.testing.matchers.aem.PdfTestConstants.SAMPLE_FILES_DIR;

import java.nio.file.Files;
import java.nio.file.Path;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfComparerTest {
	private static final String SAMPLE_FORM_NAME = "SampleForm.pdf";
	private static final String SAMPLE_ARTWORK_PDF_NAME = "SampleArtworkPdf.pdf";
	private static final Path SAMPLE_FORM = SAMPLE_FILES_DIR.resolve(SAMPLE_FORM_NAME);
	private static final Path SAMPLE_ARTWORK_PDF = SAMPLE_FILES_DIR.resolve(SAMPLE_ARTWORK_PDF_NAME);

	@TempDir
	Path actualResultsDir;
	
	private void testForPass(byte[] pdf, Matcher<byte[]> matcher) {
		assertThat(pdf, matcher);
	}
	
	private void testForFail(byte[] pdf, Matcher<byte[]> matcher, Matcher<String> msgMatcher) {
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(pdf, matcher));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, msgMatcher);
	}
	
	private PdfComparer underTest;
	
	@BeforeEach
	void setup() {
		underTest = new PdfComparer(SAMPLE_FILES_DIR, actualResultsDir);
	}
	
	@Test
	void testComparesEqualString_Pass() throws Exception {
		testForPass(Files.readAllBytes(SAMPLE_FORM), underTest.comparesEqual(SAMPLE_FORM_NAME));
	}

	@Test
	void testComparesEqualString_Fail() throws Exception {
		// TODO:  Add check that name of diff file is in message and the diff file is written.
		testForFail(Files.readAllBytes(SAMPLE_FORM), underTest.comparesEqual(SAMPLE_ARTWORK_PDF_NAME), containsString("Differences were found"));
	}

	@Disabled("Not Equals is not implemented yet.")
	@Test
	void testComparesNotEqualString_Pass() throws Exception {
		testForPass(Files.readAllBytes(SAMPLE_FORM), underTest.comparesEqual(SAMPLE_ARTWORK_PDF_NAME));
	}

	@Disabled("Not Equals is not implemented yet.")
	@Test
	void testComparesNotEqualString_Fail() throws Exception {
		testForFail(Files.readAllBytes(SAMPLE_FORM), underTest.comparesEqual(SAMPLE_FORM_NAME), containsString("Differences were found"));
	}

	@Disabled("Not Equals is not implemented yet.")
	@Test
	void testComparesEqualPathPath_Pass() throws Exception {
		testForPass(Files.readAllBytes(SAMPLE_FORM), underTest.comparesEqual(SAMPLE_FORM_NAME));
	}

	@Disabled("Not Equals is not implemented yet.")
	@Test
	void testComparesEqualPathPath_Fail() throws Exception {
		fail("Not yet implemented");
	}
	
	@Disabled("Not Equals is not implemented yet.")
	@Test
	void testComparesNotEqualPathPath_Pass() throws Exception {
		fail("Not yet implemented");
	}

	@Disabled("Not Equals is not implemented yet.")
	@Test
	void testComparesNotEqualPathPath_Fail() throws Exception {
		fail("Not yet implemented");
	}
}
