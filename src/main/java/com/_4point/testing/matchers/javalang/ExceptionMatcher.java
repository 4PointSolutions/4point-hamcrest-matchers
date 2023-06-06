package com._4point.testing.matchers.javalang;

import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class ExceptionMatcher {

	public static Matcher<Exception> exceptionMsgContainsAll(String...expectedStrings) {
		@SuppressWarnings("unchecked")
		Matcher<String>[] containsList = Arrays.stream(expectedStrings).map(s->Matchers.containsString(s)).toArray(Matcher[]::new);
		return new ExceptionMsgContains(allOf(containsList));
	}

	private static class ExceptionMsgContains extends FeatureMatcher<Exception, String> {

		public ExceptionMsgContains(Matcher<String> subMatcher) {
			super(subMatcher, "Exception message", "Exception message");
		}

		@Override
		protected String featureValueOf(Exception actual) {
			String msg = actual.getMessage();
			assertNotNull(msg, "Exception message should not be null, but was.");
			return msg;
		}

	}
}
