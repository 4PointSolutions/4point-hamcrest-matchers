package com._4point.testing.matchers.jaxrs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@ExtendWith(MockitoExtension.class)
class ResponseMatchersTest {

	@Test
	void testIsStatus_passes(@Mock Response response) {
		Status expectedStatus = Status.OK;
		Mockito.when(response.getStatusInfo()).thenReturn(expectedStatus);
		assertThat(response, ResponseMatchers.isStatus(expectedStatus));
	}

	@Test
	void testIsStatus_fails(@Mock Response response) {
		Status actualStatus = Status.BAD_REQUEST;
		Status expectedStatus = Status.OK;
		Mockito.when(response.getStatusInfo()).thenReturn(actualStatus);
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(response, ResponseMatchers.isStatus(expectedStatus)));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(
				containsString("should be " + expectedStatus.toString()), 
				containsString("was " + actualStatus.toString()),
				containsString("Response HTTP status code")
				));
	}

	@Test
	void testHasMediaType_passes(@Mock Response response) {
		MediaType expectedMediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
		Mockito.when(response.getMediaType()).thenReturn(expectedMediaType);
		assertThat(response, ResponseMatchers.hasMediaType(expectedMediaType));
	}

	@Disabled("Doesn't work because MediaType.toString() delegates to an implentation.")
	@Test
	void testHasMediaType_fails(@Mock Response response) {
		MediaType actualMediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
		MediaType expectedMediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
		Mockito.when(response.getMediaType()).thenReturn(actualMediaType);
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(response, ResponseMatchers.hasMediaType(expectedMediaType)));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(
				containsString("should be " + expectedMediaType.toString()), 
				containsString("was " + actualMediaType.toString()),
				containsString("Response MediaType")
				));
	}

	@Test
	void testHasEntity_passes(@Mock Response response) {
		boolean hasEntity = true;
		Mockito.when(response.hasEntity()).thenReturn(hasEntity);
		assertThat(response, ResponseMatchers.hasEntity());
		
	}

	@Test
	void testHasEntity_fails(@Mock Response response) {
		boolean hasEntity = false;
		Mockito.when(response.hasEntity()).thenReturn(hasEntity);
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(response, ResponseMatchers.hasEntity()));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(
				containsString("should hava an entity"), 
				containsString("did not have an entity"),
				containsString("Response")
				));		
	}

	@Test
	void testDoesNotHaveEntity_passes(@Mock Response response) {
		boolean hasEntity = false;
		Mockito.when(response.hasEntity()).thenReturn(hasEntity);
		assertThat(response, ResponseMatchers.doesNotHaveEntity());
		
	}

	@Test
	void testDoesNotHaveEntity_fails(@Mock Response response) {
		boolean hasEntity = true;
		Mockito.when(response.hasEntity()).thenReturn(hasEntity);
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(response, ResponseMatchers.doesNotHaveEntity()));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(
				containsString("should not hava an entity"), 
				containsString("did have an entity"),
				containsString("Response")
				));		
	}

}
