package com._4point.testing.matchers.jaxrs;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

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
public class ResponseMatcher {
	
	public static final String APPLICATION_PDF = "application/pdf";
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
	public static TypeSafeDiagnosingMatcher<Response> isStatus(Status expectedStatus) {
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
	public static TypeSafeDiagnosingMatcher<Response> hasMediaType(MediaType expectedMediaType) {
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
	public static TypeSafeDiagnosingMatcher<Response> hasEntity() {
		return new HasEntity(true); 
	}

	/**
	 * Creates a Matcher that validates that a Response does not have an entity..  
	 * 
	 * @return the matcher
	 */
	public static TypeSafeDiagnosingMatcher<Response> doesNotHaveEntity() {
		return new HasEntity(false); 
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
	 * a PDF.
	 * 
	 * * it ensures the ContentType header is application/pdf
	 * * it ensures the Status is OK
	 * * it ensures there is a body in the response
	 * * it reads the body into a PDF object which parses the body using a PDF parser.
	 * 
	 * @param response
	 *   the Response object to be validated
	 * @return
	 * @throws PdfException 
	 */
	public static Pdf expectingPdf(Response response) throws PdfException {
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE), hasEntity()));
		return Pdf.from(readEntityBytes(response));
	}

	// TODO: Implement this.
//	public static Pdf expectingHtmlForm(Response response) {
//		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE), hasEntity()));
//		
//	}
	
	private static String readEntityToString(Response result) {
		return new String(readEntityBytes(result), StandardCharsets.UTF_8);
	}
}