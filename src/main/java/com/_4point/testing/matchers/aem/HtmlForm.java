package com._4point.testing.matchers.aem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Matchers for testing an AEM HTML5 Form. 
 * 
 *
 */
public class HtmlForm {
	private final Document doc;

	private HtmlForm(Document doc) {
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
	
	public static HtmlForm create(byte[] html, URI baseUri) throws IOException {
		return new HtmlForm(Jsoup.parse(new ByteArrayInputStream(html), StandardCharsets.UTF_8.toString(), baseUri.toString()));
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
}