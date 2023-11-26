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

	private static final String EXPECTED_PARTIAL_MSG_1 = "Test";
	private static final String EXPECTED_PARTIAL_MSG_2 = "Exception";
	private static final String EXPECTED_PARTIAL_MSG_3 = "Message";
	private static final String EXPECTED_MSG = EXPECTED_PARTIAL_MSG_1 + " " + EXPECTED_PARTIAL_MSG_2 + " " + EXPECTED_PARTIAL_MSG_3;
	private static final String MISMATCH_STRING = " foo";
	
	private enum ExceptionMsgContainsAllScenario {
		ONE_STRING(exceptionMsgContainsAll(EXPECTED_MSG), exceptionMsgContainsAll(EXPECTED_MSG + MISMATCH_STRING)),
		TWO_STRINGS(exceptionMsgContainsAll(EXPECTED_PARTIAL_MSG_1, EXPECTED_PARTIAL_MSG_2), exceptionMsgContainsAll(EXPECTED_PARTIAL_MSG_1, MISMATCH_STRING)),
		THREE_STRINGS(exceptionMsgContainsAll(EXPECTED_PARTIAL_MSG_1, EXPECTED_PARTIAL_MSG_2, EXPECTED_PARTIAL_MSG_3), exceptionMsgContainsAll(MISMATCH_STRING, EXPECTED_PARTIAL_MSG_2, EXPECTED_PARTIAL_MSG_3)),
		;
	
		private final Matcher<Throwable> passingTest;
		private final Matcher<Throwable> failingTest;

		private ExceptionMsgContainsAllScenario(Matcher<Throwable> matcher, Matcher<Throwable> matcher2) {
			this.passingTest = matcher;
			this.failingTest = matcher2;
		}
	}

	@ParameterizedTest
	@EnumSource
	void testExceptionMsgContainsAll_Match(ExceptionMsgContainsAllScenario scenario) {
		var testException = new IllegalStateException(EXPECTED_MSG);
		
		assertThat(testException, scenario.passingTest);
	}

	@ParameterizedTest
	@EnumSource
	void testExceptionMsgContainsAll_Mismatch(ExceptionMsgContainsAllScenario scenario) {
		var testException = new IllegalStateException(EXPECTED_MSG);
		
		AssertionError ex = assertThrows(AssertionError.class, ()->assertThat(testException, scenario.failingTest));

		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString(EXPECTED_PARTIAL_MSG_1), containsString(EXPECTED_PARTIAL_MSG_2), containsString(EXPECTED_PARTIAL_MSG_3), containsString(MISMATCH_STRING)));
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
