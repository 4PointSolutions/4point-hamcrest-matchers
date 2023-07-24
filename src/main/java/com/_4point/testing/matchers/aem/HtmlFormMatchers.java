package com._4point.testing.matchers.aem;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matchers for testing an AEM HTML5 Form. 
 *
 */
public class HtmlFormMatchers {
	// Prevent instantiation of this class
	private HtmlFormMatchers() {
	}

	private static class HasTitle extends TypeSafeDiagnosingMatcher<HtmlForm> {

		private final String expectedTitle;
		
		private HasTitle(String expectedTitle) {
			this.expectedTitle = expectedTitle;
		}

		@Override
		protected boolean matchesSafely(HtmlForm item, Description mismatchDescription) {
			boolean result = expectedTitle.equals(item.getTitle());
			if (!result) {
				mismatchDescription.appendText("was '")
								   .appendText(item.getTitle())
								   .appendText("'.");
			}
			return result;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Page title should be '")
			   			.appendText(expectedTitle)
			   			.appendText("'");
		}
	}
	
	/**
	 * Creates a Matcher that validates that an HTML Form's title.  
	 * 
	 * @param expectedTitle
	 *   the expected value for the title
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<HtmlForm> hasTitle(String expectedTitle) {
		return new HasTitle(expectedTitle);
	}
	
	private static class ContainsFieldLabel extends TypeSafeDiagnosingMatcher<HtmlForm> {

		private final String expectedFieldLabel;
		
		private ContainsFieldLabel(String expectedFieldLabel) {
			this.expectedFieldLabel = expectedFieldLabel;
		}

		@Override
		protected boolean matchesSafely(HtmlForm item, Description mismatchDescription) {
			List<String> fieldLabels = item.getFieldLabels();
			boolean result = fieldLabels.stream().filter(expectedFieldLabel::equals).count() > 0;
			if (!result) {
				mismatchDescription.appendText("field labels were ")
								   .appendValueList("['", "','", "']", fieldLabels);
								   ;
			}
			return result;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Page should have a field label '")
			   			.appendText(expectedFieldLabel)
			   			.appendText("'");
		}
	}
	
	/**
	 * Creates a Matcher that validates that an HTML Form has a input field with a specific field label.  
	 * 
	 * @param expectedFieldLabel
	 * 	field label to match on
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<HtmlForm> containsFieldLabel(String expectedFieldLabel) {
		return new ContainsFieldLabel(expectedFieldLabel);
	}
}
