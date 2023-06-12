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
 * Object representing an AEM HTML5 Form
 * 
 *
 */
public class HtmlForm {
	private final Document doc;

	private HtmlForm(Document doc) {
		this.doc = doc;
	}

	/**
	 * Gets the String value of the text inside the Title element.
	 * 
	 * @return form title
	 */
	public String getTitle() {
		Elements selectedElements = doc.getElementsByTag("title");
		assertNotNull(selectedElements);
		assertNotEquals(0, selectedElements.size());
		Element titleElement = selectedElements.first();
		assertNotNull(titleElement);
		return titleElement.text();
	}
	
	/**
	 * Static factory for HtmlForm object.  Creates an HtmlForm object.
	 * 
	 * Parses the provided html parameter and then returns an HtmlForm obkect that
	 * can be queried for information.
	 * 
	 * @param html
	 * 	bytes containing a valid HTML document
	 * @param baseUri
	 * 	the baseUri for the document
	 * @return the queriable HtmlForm object
	 * @throws IOException if there's an error while parsing the HTML bytes
	 */
	public static HtmlForm create(byte[] html, URI baseUri) throws IOException {
		return new HtmlForm(Jsoup.parse(new ByteArrayInputStream(html), StandardCharsets.UTF_8.toString(), baseUri.toString()));
	}

	/**
	 * Gets all the field Labels as Strings
	 * 
	 * @return List of field labels
	 */
	public List<String> getFieldLabels() {
		Elements elements = doc.select("div.guideFieldLabel > label");
		assertTrue(elements.size() > 0, "Couldn't find any field labels.");
		return elements.eachText();
	}
	
	/**
	 * Checks if a field label String is among the field labels for this form.
	 * 
	 * @param candidate
	 * 	string label that will be searched for
	 * @return true if the provided candidate is one of the field lanels
	 */
	public boolean hasFieldLabel(String candidate) {
		long matches = getFieldLabels().stream().filter(candidate::equals).count();
		return matches > 0;
	}
}