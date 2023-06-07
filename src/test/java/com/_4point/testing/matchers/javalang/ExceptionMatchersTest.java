package com._4point.testing.matchers.javalang;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.exceptionMsgContainsAll;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExceptionMatchersTest {

	@Test
	void testExceptionMsgContainsAll_OneString_Match() {
		String expectedMsg = "Test Exception Message";
		var testException = new IllegalStateException(expectedMsg);
		
		assertThat(testException, exceptionMsgContainsAll(expectedMsg));
	}

	@Test
	void testExceptionMsgContainsAll_OneString_Mismatch() {
		String expectedMsg = "Test Exception Message";
		String mismatchString = " foo";
		var testException = new IllegalStateException(expectedMsg);
		
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(testException, exceptionMsgContainsAll(expectedMsg + mismatchString)));

		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString(expectedMsg), containsString(mismatchString)));
	}

	@Test
	void testExceptionMsgContainsAll_TwoStrings_Match() {
		String expectedMsg1 = "Test Exception";
		String expectedMsg2 = "Message";
		var testException = new IllegalStateException(expectedMsg1 + " " + expectedMsg2);
		
		assertThat(testException, exceptionMsgContainsAll(expectedMsg1, expectedMsg2));
	}

	@Test
	void testExceptionMsgContainsAll_TwoStrings_Mismatch() {
		String expectedMsg1 = "Test Exception";
		String expectedMsg2 = "Message";
		String mismatchString = " foo";
		var testException = new IllegalStateException(expectedMsg1 + " " + expectedMsg2);
		
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(testException, exceptionMsgContainsAll(expectedMsg1, expectedMsg2, mismatchString)));

		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString(expectedMsg1), containsString(expectedMsg2), containsString(mismatchString)));
	}

	@Test
	void testExceptionMsgContainsAll_ThreeStrings_Match() {
		String expectedMsg1 = "Test";
		String expectedMsg2 = "Exception";
		String expectedMsg3 = "Message";
		var testException = new IllegalStateException(expectedMsg1 + " " + expectedMsg2 + " - " + expectedMsg3);
		
		assertThat(testException, exceptionMsgContainsAll(expectedMsg1, expectedMsg2, expectedMsg3));
	}

	@Test
	void testExceptionMsgContainsAll_ThreeStrings_Mismatch() {
		String expectedMsg1 = "Test";
		String expectedMsg2 = "Exception";
		String expectedMsg3 = "Message";
		String mismatchString = " foo";
		var testException = new IllegalStateException(expectedMsg1 + " " + expectedMsg2 + " - " + expectedMsg3);
		
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(testException, exceptionMsgContainsAll(expectedMsg1, expectedMsg2, expectedMsg3, mismatchString)));

		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString(expectedMsg1), containsString(expectedMsg2), containsString(expectedMsg3), containsString(mismatchString)));
	}

	@Test
	void testExceptionMsgContainsAll_NoMessage() {
		var testException = new NullPointerException();
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(testException, exceptionMsgContainsAll("something")));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsString("Exception message was null."));
	}


}
