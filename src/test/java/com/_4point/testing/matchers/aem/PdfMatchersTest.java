package com._4point.testing.matchers.aem;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PdfMatchersTest {

	private static final String BAD_FONT_NAME = "BadFontName";
	private static final Path RESOURCES_DIR = Path.of("src","test","resources");
	private static final Path SAMPLE_FILES_DIR = RESOURCES_DIR.resolve("SampleFiles");
	
	private enum TestCase {
		SampleFormNonInteractive("SampleFormNonInteractive.pdf", false, false, false, false, List.of("MyriadPro-Regular"), List.of()),
		SampleForm("SampleForm.pdf", true, true, true, true, List.of("MyriadPro-Regular"), List.of("MyriadPro-Regular")),
		SampleArtworkPdf("SampleArtworkPdf.pdf", true, false, true, true, List.of(), List.of())
		;
		 
		private final Path filename;
		private final boolean isInteractive;
		private final boolean isDynamic;
		private final boolean isTagged;
		private final boolean hasXfa;
		private final List<String> fonts;
		private final List<String> embeddedFonts;

		private TestCase(String filenameStr, boolean isInteractive, boolean isDynamic, boolean isTagged, boolean hasXfa, List<String> fonts, List<String> embeddedFonts) {
			this.filename = SAMPLE_FILES_DIR.resolve(filenameStr);
			this.isInteractive = isInteractive;
			this.isDynamic = isDynamic;
			this.isTagged = isTagged;
			this.hasXfa = hasXfa;
			this.fonts = fonts;
			this.embeddedFonts = embeddedFonts;
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
				()->testForFail(pdf, PdfMatchers.hasXfa(!testCase.hasXfa), getXfaFailMsgMatcher(testCase.hasXfa)),
				()->testForPass(pdf, PdfMatchers.hasFonts(equalTo(testCase.fonts))),
				()->testForFail(pdf, PdfMatchers.hasFonts(hasItem(BAD_FONT_NAME)), allOf(containsString("font"), containsString(BAD_FONT_NAME))),
				()->testForPass(pdf, PdfMatchers.HasEmbeddedFonts(equalTo(testCase.embeddedFonts))),
				()->testForFail(pdf, PdfMatchers.HasEmbeddedFonts(hasItem(BAD_FONT_NAME)), allOf(containsString("embedded font"), containsString(BAD_FONT_NAME))),
				()->testForPass(pdf, PdfMatchers.hasExactlyTheseFonts(testCase.fonts.toArray(new String[0]))),
				()->testForFail(pdf, PdfMatchers.hasExactlyTheseFonts(BAD_FONT_NAME), allOf(containsString("font"), containsString(BAD_FONT_NAME))),
				()->testForPass(pdf, PdfMatchers.hasExactlyTheseEmbeddedFonts(testCase.embeddedFonts.toArray(new String[0]))),
				()->testForFail(pdf, PdfMatchers.hasExactlyTheseEmbeddedFonts(BAD_FONT_NAME), allOf(containsString("font"), containsString(BAD_FONT_NAME))),
				()->testForPass(pdf, PdfMatchers.hasAtLeastTheseFonts(testCase.fonts.toArray(new String[0]))),
				()->testForFail(pdf, PdfMatchers.hasAtLeastTheseFonts(BAD_FONT_NAME), allOf(containsString("font"), containsString(BAD_FONT_NAME))),
				()->testForPass(pdf, PdfMatchers.hasAtLeastTheseEmbeddedFonts(testCase.embeddedFonts.toArray(new String[0]))),
				()->testForFail(pdf, PdfMatchers.hasAtLeastTheseEmbeddedFonts(BAD_FONT_NAME), allOf(containsString("font"), containsString(BAD_FONT_NAME)))
				);
		
		System.out.println(testCase.toString() + " ccntains rights:" + pdf.getUsageRights());
		
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
}
