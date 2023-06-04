package com._4point.testing.matchers.aem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlFormDocument {
	private final Document doc;

	private HtmlFormDocument(Document doc) {
		super();
		this.doc = doc;
	}

	public String getTitle() {
		Elements selectedElements = doc.getElementsByTag("title");
		assertNotNull(selectedElements);
		assertNotEquals(0, selectedElements.size());
		Element titleElement = selectedElements.first();
		assertNotNull(titleElement);
		return titleElement.text();
	}
	
	public static HtmlFormDocument create(byte[] html, URI baseUri) throws IOException {
		return new HtmlFormDocument(Jsoup.parse(new ByteArrayInputStream(html), StandardCharsets.UTF_8.toString(), baseUri.toString()));
	}

	public List<String> getFieldLabels() {
		Elements elements = doc.select("div.guideFieldLabel > label");
		assertTrue(elements.size() > 0, "Couldn't find any field labels.");
		return elements.eachText();
	}
	
	public boolean hasFieldLabel(String candidate) {
		long matches = getFieldLabels().stream().filter(candidate::equals).count();
		return matches > 0;
	}
	
	private static class HasTitle extends TypeSafeDiagnosingMatcher<HtmlFormDocument> {

		private final String expectedTitle;
		
		private HasTitle(String expectedTitle) {
			super();
			this.expectedTitle = expectedTitle;
		}

		@Override
		protected boolean matchesSafely(HtmlFormDocument item, Description mismatchDescription) {
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
	
	public static TypeSafeDiagnosingMatcher<HtmlFormDocument> hasTitle(String expectedTitle) {
		return new HasTitle(expectedTitle);
	}
	
	private static class ContainsFieldLabel extends TypeSafeDiagnosingMatcher<HtmlFormDocument> {

		private final String expectedFieldLabel;
		
		private ContainsFieldLabel(String expectedFieldLabel) {
			super();
			this.expectedFieldLabel = expectedFieldLabel;
		}

		@Override
		protected boolean matchesSafely(HtmlFormDocument item, Description mismatchDescription) {
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
	
	public static TypeSafeDiagnosingMatcher<HtmlFormDocument> containsFieldLabel(String expectedFieldLabel) {
		return new ContainsFieldLabel(expectedFieldLabel);
	}
	
	
}