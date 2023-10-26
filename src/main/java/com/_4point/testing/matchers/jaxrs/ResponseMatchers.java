package com._4point.testing.matchers.jaxrs;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com._4point.testing.matchers.aem.HtmlForm;
import com._4point.testing.matchers.aem.Pdf;
import com._4point.testing.matchers.aem.Pdf.PdfException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;

/**
 * Response Matchers for testing an JAX-RS HTTP Response object. 
 * 
 *	Based on https://www.planetgeek.ch/2012/03/07/create-your-own-matcher/
 */
public class ResponseMatchers {
	
	// Prevent instantiation of this class
	private ResponseMatchers() {
	}

	/**
	 * Content-type string for "application/pdf"
	 */
	public static final String APPLICATION_PDF = "application/pdf";
	/**
	 * JAX-RS MedisType object for "application/pdf"
	 */
	public static final MediaType APPLICATION_PDF_TYPE = new MediaType("application", "pdf");

	private static class IsStatus extends TypeSafeDiagnosingMatcher<Response> {
		
		private final Response.Status expected;
		
		private IsStatus(Status expectedStatus) {
			this.expected = Objects.requireNonNull(expectedStatus);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Response HTTP status code should be %s (%d)".formatted(expected.toString(), expected.getStatusCode()));
		}

		@Override
		protected boolean matchesSafely(Response item, Description mismatchDescription) {
			StatusType actual = item.getStatusInfo();
			boolean result = actual.equals(expected);
			if (!result) {
				mismatchDescription.appendText("Response HTTP status code was %s (%d)".formatted(actual.toString(), actual.getStatusCode()));
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that compares a Response object's status to the one provided.  
	 * 
	 * @param expectedStatus
	 * 	the expected Status 
	 * @return The matcher
	 */
	public static Matcher<Response> isStatus(Status expectedStatus) {
		return new IsStatus(expectedStatus);
	}

	private static class HasMediaType extends TypeSafeDiagnosingMatcher<Response> {
		
		private final MediaType expected;
		
		public HasMediaType(MediaType expectedMediaType) {
			this.expected = Objects.requireNonNull(expectedMediaType);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Response MediaType should be '%s'".formatted(expected.toString()));
		}

		@Override
		protected boolean matchesSafely(Response item, Description mismatchDescription) {
			MediaType actual = item.getMediaType();
			boolean result = expected.isCompatible(actual);
			if (!result) {
				mismatchDescription.appendText("Response MediaType was '%s'.".formatted(actual == null ? "null" : actual.toString()));
			}
			return result;
		}
	}
	
	/**
	 * Creates a Matcher that compares a Response object's MediaType to the one provided.  
	 * 
	 * @param expectedMediaType
	 * 	the expected MediaType
	 * @return the matcher
	 */
	public static Matcher<Response> hasMediaType(MediaType expectedMediaType) {
		return new HasMediaType(expectedMediaType); 
	}

	private static class HasEntity extends TypeSafeDiagnosingMatcher<Response> {
		private final boolean expected;
		
		public HasEntity(boolean expected) {
			this.expected = expected;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Response should " + (expected ? "" : "not ") + "hava an entity.");
		}

		@Override
		protected boolean matchesSafely(Response item, Description mismatchDescription) {
			boolean result = item.hasEntity();
			if (result != expected) {
				mismatchDescription.appendText("Response did " + (result ? "" : "not ") + "have an entity.");
			}
			return result == expected;
		}
	}
	
	/**
	 * Creates a Matcher that validates that a Response has an entity.
	 * 
	 * @return the matcher
	 */
	public static Matcher<Response> hasEntity() {
		return new HasEntity(true); 
	}

	/**
	 * Creates a Matcher that validates that a Response does not have an entity..  
	 * 
	 * @return the matcher
	 */
	public static Matcher<Response> doesNotHaveEntity() {
		return new HasEntity(false); 
	}
	
	
	/**
	 * Creates a matcher that allows someone to validate the bytes in the response entity.
	 * The matcher fails if the Response has no entity to match against.
	 * 
	 * @param byteMatcher
	 *   a matcher that validates the bytes in the response enrity
	 * @return the matcher
	 */
	public static Matcher<Response> hasEntityMatching(Matcher<byte[]> byteMatcher) {
		return new FeatureMatcher<Response, byte[]>(byteMatcher, "Response entity", "Response entity") {
			private byte[] entityBytes = null;
			@Override
			protected byte[] featureValueOf(Response actual) {
				// This method gets called twice for failures, but we can only read the entity once, so
				// I need to cache it for the second call.
				if (entityBytes == null) {
					// If this is the first time into this, read the entity.
					assertThat(actual, hasEntity());
					entityBytes = readEntityBytes(actual);
				}
				return entityBytes;
			}
		};
	}

	/**
	 * Creates a matcher that allows someone to validate the response entity as a Srting.
	 * The matcher fails if the Response has no entity to match against.
	 * 
	 * @param charSet
	 * 	 the character set the incoming data should be in
	 * @param stringMatcher
	 *   a matcher that validates the response entity as a String
	 * @return the matcher
	 */
	public static Matcher<Response> hasStringEntityMatching(Charset charset, Matcher<String> stringMatcher) {
		return new FeatureMatcher<Response, String>(stringMatcher, "Response entity", "Response entity") {
			private String entityString = null;
			@Override
			protected String featureValueOf(Response actual) {
				// This method gets called twice for failures, but we can only read the entity once, so
				// I need to cache it for the second call.
				if (entityString == null) {
					// If this is the first time into this, read the entity.
					assertThat(actual, hasEntity());
					entityString = readEntityToString(actual, charset);
				}
				return entityString;
			}
		};
	}

	 /**
	 * Creates a matcher that allows someone to validate the response entity as a Srting.
	 * The matcher fails if the Response has no entity to match against.
	 * 
	 * Current assumes that the incoming data is in UTF-9.
	 * 
	 * @param stringMatcher
	 *   a matcher that validates the response entity as a String
	 * @return the matcher
	 */
	public static Matcher<Response> hasStringEntityMatching(Matcher<String> stringMatcher) {
		return hasStringEntityMatching(StandardCharsets.UTF_8, stringMatcher);
	}

	/**
	 * Creates a matcher that compares the Response bytes with a provided bute array to see if they match.
	 * 
	 * @param bytes
	 *   a byte array to be matched against
	 * @return the matcher
	 */
	public static Matcher<Response> hasEntityEqualTo(byte[] bytes) {
		return hasEntityMatching(is(bytes));
	}

	private static byte[] readEntityBytes(Response result) {
		try {
			return ((InputStream) result.getEntity()).readAllBytes();
		} catch (IOException e) {
			throw new IllegalStateException("Exception while reading response stream.", e);
		}
	}

	/**
	 * Performs a series of checks on the Response object to validate that it is a response containing
	 * a PDF and then returns a Pdf object for further validation.
	 * 
	 * * it ensures the ContentType header is application/pdf
	 * * it ensures the Status is OK
	 * * it ensures there is a body in the response
	 * * it reads the body into a PDF object which parses the body using a PDF parser.
	 * 
	 * @param response
	 *   the Response object to be validated
	 * @return the valid Pdf object
	 * @throws PdfException thrown if there are any I/O or parsing errors when validating the Pdf 
	 */
	public static Pdf expectingPdf(Response response) throws PdfException {
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE), hasEntity()));
		return Pdf.from(readEntityBytes(response));
	}

	/**
	 * Performs a series of checks on the Response object to validate that it is a response containing
	 * HTML and then returns an HtmlForm object for further validation.
	 * 
	 * * it ensures the ContentType header is test/html
	 * * it ensures the Status is OK
	 * * it ensures there is a body in the response
	 * * it reads the body into a HtmlForm object which parses the body using an Html parser.
	 * 
	 * @param response
	 *   the Response object to be validated
	 * @param baseUri
	 * @return the valid HtmlForm object
	 * @throws IllegalStateException thrown if there are any I/O or parsing errors when validating the Html 
	 */
	public static HtmlForm expectingHtmlForm(Response response, URI baseUri) {
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE), hasEntity()));
		try {
			return HtmlForm.create(readEntityBytes(response), baseUri);
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading Html from response entiry.", e);
		}
	}
	
	private static String readEntityToString(Response result, Charset charset) {
		return new String(readEntityBytes(result), charset);
	}
}