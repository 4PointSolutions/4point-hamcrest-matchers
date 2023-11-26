package com._4point.testing.matchers.javalang;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

	private static final String CAUSE_EXCEPTION_MSG = "Cause Exception";
	private static final Exception CAUSE_EXCEPTION = new IllegalStateException(CAUSE_EXCEPTION_MSG);
	private static final Exception TEST_EXCEPTION = new IllegalStateException("Test Exception", CAUSE_EXCEPTION);
	private static final Exception NON_CAUSE_EXCEPTION = new IllegalStateException("Non-cause Exception");

	@Test
	void testHasCause_Match() {
		assertThat(TEST_EXCEPTION,hasCause(CAUSE_EXCEPTION));
	}

	@Test
	void testHasCause_Mismatch() {
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(TEST_EXCEPTION,hasCause(NON_CAUSE_EXCEPTION)));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsString("an exception with cause"));		
	}
	
	@Test
	void testHasCause_NoCause() {
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(CAUSE_EXCEPTION,hasCause(CAUSE_EXCEPTION)));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsString("an exception with cause"));		
	}

	
	private enum ExceptionCauseMatchesScenario {
		ONE_EXCEPTION(
				hasCauseMatching(sameInstance(CAUSE_EXCEPTION)), 
				hasCauseMatching(sameInstance(NON_CAUSE_EXCEPTION))
				),
		TWO_EXCEPTIONS(
				hasCauseMatching(exceptionMsgContainsAll(CAUSE_EXCEPTION_MSG), sameInstance(CAUSE_EXCEPTION)),
				hasCauseMatching(exceptionMsgContainsAll(CAUSE_EXCEPTION_MSG), sameInstance(NON_CAUSE_EXCEPTION))
				),
		THREE_EXCEPTIONS(
				hasCauseMatching(sameInstance(CAUSE_EXCEPTION), exceptionMsgContainsAll(CAUSE_EXCEPTION_MSG), sameInstance(CAUSE_EXCEPTION)),
				hasCauseMatching(sameInstance(NON_CAUSE_EXCEPTION), exceptionMsgContainsAll(CAUSE_EXCEPTION_MSG), sameInstance(NON_CAUSE_EXCEPTION))
			),
		;
		
		private final Matcher<Throwable> passingTest;
		private final Matcher<Throwable> failingTest;

		private ExceptionCauseMatchesScenario(Matcher<Throwable> passingTest, Matcher<Throwable> failingTest) {
			this.passingTest = passingTest;
			this.failingTest = failingTest;
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testHasCauseMatching_Match(ExceptionCauseMatchesScenario scenario) {
		assertThat(TEST_EXCEPTION, scenario.passingTest);
	}

	@ParameterizedTest
	@EnumSource
	void testHasCauseMatching_Mismatch(ExceptionCauseMatchesScenario scenario) {
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(TEST_EXCEPTION, scenario.failingTest));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsString("an exception with cause"));		
	}

	@Test
	void testHasCauseMatching_NoCause() {
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(CAUSE_EXCEPTION, hasCauseMatching(sameInstance(CAUSE_EXCEPTION))));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsString("an exception with cause"));		
	}

}
