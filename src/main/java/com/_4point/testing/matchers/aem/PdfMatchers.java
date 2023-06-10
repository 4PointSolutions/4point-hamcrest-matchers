package com._4point.testing.matchers.aem;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

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
	
	public static TypeSafeDiagnosingMatcher<Pdf> hasXfa(boolean hasXfa) {
		return new HasXfa(hasXfa);
	}

	// Not implemented yet
//	public static TypeSafeDiagnosingMatcher<Pdf> hasRights() {
//		return null;
//	}
//
//	public static TypeSafeDiagnosingMatcher<Pdf> hasFonts(String fontName, String... fontNames) {
//		return null;
//	}
//
//	public static TypeSafeDiagnosingMatcher<Pdf> hasEmbeddedFonts(String fontName, String... fontNames) {
//		return null;
//	}
}
