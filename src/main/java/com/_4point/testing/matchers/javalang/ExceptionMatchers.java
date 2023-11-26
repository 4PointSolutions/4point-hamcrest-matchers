package com._4point.testing.matchers.javalang;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.stream.Stream;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Matchers used for testing `java.lang.Exception`s (and its subclasses).
 *
 *	Based on https://www.planetgeek.ch/2012/03/07/create-your-own-matcher/
 */
public class ExceptionMatchers {

	// Prevent instantiation of this class
	private ExceptionMatchers() {
	}

	/**
	 * Matcher that validates that an exception's message contains all the expected strings.
	 * 
	 * At least one string must be specified.
	 * 
	 * @param firstExpectedString
	 * 	string that is expected to be in the message
	 * @param expectedStrings
	 * 	additional strings that are expected to be in the message
	 * @return the matcher
	 */
	public static Matcher<Throwable> exceptionMsgContainsAll(String firstExpectedString, String...expectedStrings) {
		if (expectedStrings.length == 0) {
			// Just one matcher, so use it directly (should produce a better error message)
			return new ExceptionMsgContains(Matchers.containsString(firstExpectedString));
		}
		@SuppressWarnings("unchecked")
		Matcher<String>[] containsList = Stream.concat( Stream.of(firstExpectedString), Arrays.stream(expectedStrings))
											   .map(s->Matchers.containsString(s))
											   .toArray(Matcher[]::new);
		return new ExceptionMsgContains(allOf(containsList));
	}

	private static class ExceptionMsgContains extends FeatureMatcher<Throwable, String> {

		public ExceptionMsgContains(Matcher<String> subMatcher) {
			super(subMatcher, "Exception message", "Exception message");
		}

		@Override
		protected String featureValueOf(Throwable actual) {
			String msg = actual.getMessage();
			assertNotNull(msg, "Exception message was null.");
			return msg;
		}

	}
	
	/**
	 * Matcher that validates the cause of an exception is the cause exception provided.
	 * 
	 * The provided cause exception must be the same instance as cause in the exception being tested.
	 * 
	 * @param throwable
	 * 	the expected cause exception
	 * @return the matcher
	 */
	public static Matcher<Throwable> hasCause(Throwable throwable) {
		return new HasCause(throwable);
	}

	private static class HasCause extends FeatureMatcher<Throwable, Throwable> {

		public HasCause(Throwable throwable) {
			super(sameInstance(throwable), "an exception with cause", "cause");
		}

		@Override
		protected Throwable featureValueOf(Throwable actual) {
			return actual.getCause();
		}
	}
	
	@SafeVarargs
	public static Matcher<Throwable> hasCauseMatching(Matcher<Throwable> matcher, Matcher<Throwable>...matchers) {
		if (matchers.length == 0) {
			// Just one matcher, so use it directly (should produce a better error message)
			return new HasCauseMatching(matcher);
		}
		@SuppressWarnings("unchecked")
		Matcher<Throwable>[] allMatchers = Stream.concat(Stream.of(matcher), Arrays.stream(matchers))
												 .toArray(Matcher[]::new);
		return new HasCauseMatching(allOf(allMatchers));
	}	

	private static class HasCauseMatching extends FeatureMatcher<Throwable, Throwable> {

		public HasCauseMatching(Matcher<Throwable> subMatcher) {
			super(subMatcher,"an exception with cause", "cause");
		}

		@Override
		protected Throwable featureValueOf(Throwable actual) {
			return actual.getCause();
		}
		
	}

}
