package com._4point.testing.matchers.javalang;

import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.stream.Stream;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class ExceptionMatchers {

	public static Matcher<Exception> exceptionMsgContainsAll(String firstExpectedString, String...expectedStrings) {
		@SuppressWarnings("unchecked")
		Matcher<String>[] containsList = Stream.concat( Stream.of(firstExpectedString), Arrays.stream(expectedStrings))
											   .map(s->Matchers.containsString(s))
											   .toArray(Matcher[]::new);
		return new ExceptionMsgContains(allOf(containsList));
	}

	private static class ExceptionMsgContains extends FeatureMatcher<Exception, String> {

		public ExceptionMsgContains(Matcher<String> subMatcher) {
			super(subMatcher, "Exception message", "Exception message");
		}

		@Override
		protected String featureValueOf(Exception actual) {
			String msg = actual.getMessage();
			assertNotNull(msg, "Exception message was null.");
			return msg;
		}

	}
}
