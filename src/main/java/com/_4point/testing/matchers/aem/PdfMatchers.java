package com._4point.testing.matchers.aem;

import static org.hamcrest.Matchers.*;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com._4point.testing.matchers.aem.Pdf.PdfException;

/**
 * Matchers to perform tests in PDFs
 *
 */
public class PdfMatchers {

	private static class IsInteractive extends TypeSafeDiagnosingMatcher<Pdf> {

		@Override
		public void describeTo(Description description) {
			description.appendText("should be interactive.");
		}

		@Override
		protected boolean matchesSafely(Pdf item, Description mismatchDescription) {
			boolean result = item.isInteractive();
			if (!result) {
				mismatchDescription.appendText("was non-interactive.");
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that validates that a PDF is interactive.
	 * 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> isInteractive() {
		return new IsInteractive();
	}

	private static class IsNonInteractive extends TypeSafeDiagnosingMatcher<Pdf> {

		@Override
		public void describeTo(Description description) {
			description.appendText("should be non-interactive.");
		}

		@Override
		protected boolean matchesSafely(Pdf item, Description mismatchDescription) {
			boolean result = !item.isInteractive();
			if (!result) {
				mismatchDescription.appendText("was interactive.");
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that validates that a PDF is non-interactive.
	 * 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> isNonInteractive() {
		return new IsNonInteractive();
	}
	
	private static class IsDynamic extends TypeSafeDiagnosingMatcher<Pdf> {

		@Override
		public void describeTo(Description description) {
			description.appendText("should be dynamic.");
		}

		@Override
		protected boolean matchesSafely(Pdf item, Description mismatchDescription) {
			boolean result = item.isDynamic();
			if (!result) {
				mismatchDescription.appendText("was static.");
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that validates that a PDF is a dynamic PDF.
	 * 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> isDynamic() {
		return new IsDynamic();
	}

	private static class IsStatic extends TypeSafeDiagnosingMatcher<Pdf> {

		@Override
		public void describeTo(Description description) {
			description.appendText("should be static.");
		}

		@Override
		protected boolean matchesSafely(Pdf item, Description mismatchDescription) {
			boolean result = !item.isDynamic();
			if (!result) {
				mismatchDescription.appendText("was dynamic.");
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that validates that a PDF is a static PDF.
	 * 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> isStatic() {
		return new IsStatic();
	}
	
	private static class IsTagged extends TypeSafeDiagnosingMatcher<Pdf> {

		@Override
		public void describeTo(Description description) {
			description.appendText("should be tagged.");
		}

		@Override
		protected boolean matchesSafely(Pdf item, Description mismatchDescription) {
			boolean result = item.isTagged();
			if (!result) {
				mismatchDescription.appendText("was not tagged.");
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that validates that a PDF is a tagged PDF.
	 * 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> isTagged() {
		return new IsTagged();
	}
	
	private static class HasXfa extends TypeSafeDiagnosingMatcher<Pdf> {
		private final boolean hasXfa;
		
		public HasXfa(boolean hasXfa) {
			this.hasXfa = hasXfa;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("should");
			description.appendText(hasXfa ? " " : " not ");
			description.appendText("be an XFA Pdf.");
		}

		@Override
		protected boolean matchesSafely(Pdf item, Description mismatchDescription) {
			boolean result = item.hasXfa() == hasXfa;
			if (!result) {
				mismatchDescription.appendText("was");
				mismatchDescription.appendText(!hasXfa ? " " : " not ");
				mismatchDescription.appendText("an XFA Pdf.");
			}
			return result;
		}
	}
	
	/**
	/**
	 * Creates a Matcher that tests whether a PDF is an XFA PDF.
	 * 
	 * @param hasXfa
	 * 	indicated whether the PDF should or should not contain XFA. 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> hasXfa(boolean hasXfa) {
		return new HasXfa(hasXfa);
	}

	private static class HasFonts extends FeatureMatcher<Pdf, Iterable<String>> {

		public HasFonts(Matcher<? super Iterable<String>> subMatcher) {
			super(subMatcher, "font", "font");
		}

		@Override
		protected Iterable<String> featureValueOf(Pdf actual) {
			try {
				return actual.allFonts();
			} catch (PdfException e) {
				throw new IllegalStateException("Error reading fonts from PDF.", e);
			}
		}
	}
	
	/**
	 * Creates a Matcher that tests the list of font names in the PDF.
	 * 
	 * @param matcher
	 * 	matcher that tests the list of fonts in the PDF.
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> hasFonts(Matcher<Iterable<? super String>> matcher) {
		return new HasFonts(matcher);
	}

	/**
	 * Creates a Matcher that matches a list of names against the list of fonts in the PDF.
	 * It must be an exact match.
	 * 
	 * @param fontNames
	 * 	the expected list of fonts in the PDF.
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> hasExactlyTheseFonts(String... fontNames) {
		return new HasFonts(containsInAnyOrder(fontNames));
	}
	
	/**
	 * Creates a Matcher that matches a list of names against the list of fonts in the PDF.
	 * It allows additional fonts to also be present.
	 * 
	 * @param fontNames
	 * 	the expected list of fonts in the PDF.
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> hasAtLeastTheseFonts(String... fontNames) {
		return new HasFonts(hasItems(fontNames));
	}
	
	private static class HasEmbeddedFonts extends FeatureMatcher<Pdf, Iterable<String>> {

		public HasEmbeddedFonts(Matcher<? super Iterable<String>> subMatcher) {
			super(subMatcher, "embedded font", "embedded font");
		}

		@Override
		protected Iterable<String> featureValueOf(Pdf actual) {
			try {
				return actual.embeddedFonts();
			} catch (PdfException e) {
				throw new IllegalStateException("Error reading embedded fonts from PDF.", e);
			}
		}
	}
	
	/**
	 * Creates a Matcher that tests the list of names of embedded fonts in the PDF.
	 * 
	 * @param matcher
	 * 	matcher that tests the list of embedded fonts in the PDF.
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> HasEmbeddedFonts(Matcher<Iterable<? super String>> matcher) {
		return new HasEmbeddedFonts(matcher);
	}

	/**
	 * Creates a Matcher that matches a list of names against the list of embedded fonts in the PDF.
	 * It must be an exact match.
	 * 
	 * @param fontNames
	 * 	the expected list of embedded fonts in the PDF.
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> hasExactlyTheseEmbeddedFonts(String... fontNames) {
		return new HasEmbeddedFonts(containsInAnyOrder(fontNames));
	}
	
	/**
	 * Creates a Matcher that matches a list of names against the list of embedded fonts in the PDF.
	 * It allows additional fonts to also be present.
	 * 
	 * @param fontNames
	 * 	the expected list of embedded fonts in the PDF.
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Pdf> hasAtLeastTheseEmbeddedFonts(String... fontNames) {
		return new HasEmbeddedFonts(hasItems(fontNames));
	}
	
	
	// Not implemented yet
//	public static TypeSafeDiagnosingMatcher<Pdf> hasRights() {
//		return null;
//	}
//
}
