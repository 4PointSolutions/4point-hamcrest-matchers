package com._4point.testing.matchers.aem;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PdfMatchersTest {

	private static final Path RESOURCES_DIR = Path.of("src","test","resources");
	private static final Path SAMPLE_FILES_DIR = RESOURCES_DIR.resolve("SampleFiles");
	
	private enum TestCase {
		SampleFormNonInteractive("SampleFormNonInteractive.pdf", false, false, false, false),
		SampleForm("SampleForm.pdf", true, true, true, true)
		;
		
		private final Path filename;
		private final boolean isInteractive;
		private final boolean isDynamic;
		private final boolean isTagged;
		private final boolean hasXfa;

		private TestCase(String filenameStr, boolean isInteractive, boolean isDynamic, boolean isTagged, boolean hasXfa) {
			this.filename = SAMPLE_FILES_DIR.resolve(filenameStr);
			this.isInteractive = isInteractive;
			this.isDynamic = isDynamic;
			this.isTagged = isTagged;
			this.hasXfa = hasXfa;
		}
	}

	@ParameterizedTest
	@EnumSource
	void testPdf(TestCase testCase) throws Exception {
		Pdf pdf = Pdf.from(testCase.filename);
		assertAll(
				()->performTest(pdf, PdfMatchers.isInteractive(), allOf(containsString("should be interactive"), containsString("was non-interactive")), testCase.isInteractive),
				()->performTest(pdf, PdfMatchers.isNonInteractive(), allOf(containsString("should be non-interactive"), containsString("was interactive")), !testCase.isInteractive),
				()->performTest(pdf, PdfMatchers.isDynamic(), allOf(containsString("should be dynamic"), containsString("was static")), testCase.isDynamic),
				()->performTest(pdf, PdfMatchers.isStatic(), allOf(containsString("should be static"), containsString("was dynamic")), !testCase.isDynamic),
				()->performTest(pdf, PdfMatchers.isTagged(), allOf(containsString("should be tagged"), containsString("was not tagged")), testCase.isTagged),
				()->testForPass(pdf, PdfMatchers.hasXfa(testCase.hasXfa)),
				()->testForFail(pdf, PdfMatchers.hasXfa(!testCase.hasXfa), getXfaFailMsgMatcher(testCase.hasXfa))
				);
	}
	
	void performTest(Pdf pdf, Matcher<Pdf> matcher, Matcher<String> msgMatcher, boolean shouldPass) {
		if (shouldPass) {
			testForPass(pdf, matcher);
		} else {
			testForFail(pdf, matcher, msgMatcher);
		}
	}
	
	void testForPass(Pdf pdf, Matcher<Pdf> matcher) {
		assertThat(pdf, matcher);
	}
	
	void testForFail(Pdf pdf, Matcher<Pdf> matcher, Matcher<String> msgMatcher) {
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(pdf, matcher));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, msgMatcher);
	}
	
	private Matcher<String> getXfaFailMsgMatcher(boolean hasXfa) {
		// if the PDF has XFA, then the error message would be the opposite (i.e. it should not be an XFA PDF)
		return hasXfa ? allOf(containsString("should not be an XFA Pdf"), containsString("was an XFA Pdf"))
				   	  : allOf(containsString("should be an XFA Pdf"), containsString("was not an XFA Pdf"));
	}

	// Not implemented yet
	@Disabled
	@Test
	void testHasRights() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void testHasFonts() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void testHasEmbeddedFonts() {
		fail("Not yet implemented");
	}

}
