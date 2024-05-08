package com._4point.testing.matchers.aem;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;

/**
 * Custom Hamcrest Matcher for comparing images.
 */
public class BufferedImageMatcher extends TypeSafeDiagnosingMatcher<BufferedImage> {

	private final BufferedImage expectedImage;
	private final Consumer<BufferedImage> resultImageWriter;
	
	private BufferedImageMatcher(BufferedImage expectedImage) {
		this(expectedImage, null);
	}

	private BufferedImageMatcher(BufferedImage expectedImage, Consumer<BufferedImage> resultImageWriter) {
		this.expectedImage = expectedImage;
		this.resultImageWriter = resultImageWriter;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should match exactly.");
		
	}

	@Override
	protected boolean matchesSafely(BufferedImage actualImage, Description mismatchDescription) {
		ImageComparisonResult result = new ImageComparison(expectedImage, actualImage)
					.setAllowingPercentOfDifferentPixels(0.0001165)	// Create an allowance of a few pixels to allow for font differences.
					.compareImages();
		return switch(result.getImageComparisonState()) {
			case MATCH -> true;
			case MISMATCH -> processMismatch(result, mismatchDescription, "image does not match.");
			case SIZE_MISMATCH -> processMismatch(result, mismatchDescription, "size does not match.");
			};
	}
	
	private boolean processMismatch(ImageComparisonResult result, Description mismatchDescription, String cause) {
		mismatchDescription.appendText(cause);
		if (resultImageWriter != null) {
			resultImageWriter.accept(result.getResult());
		}
		return false;
	}
	
	/**
	 * @param expected the expected image
	 * @return a matcher that compares another image to the expected image
	 */
	public static Matcher<BufferedImage> isSameAs(BufferedImage expected) {
		return new BufferedImageMatcher(expected);
	}

	/**
	 * @param expected the expected image
	 * @param resultImageConsumer a Consumer to save the comparison resuit if the match fails
	 * @return a matcher that compares another image to the expected image and calls the Consumer if they don't match 
	 */
	public static Matcher<BufferedImage> isSameAs(BufferedImage expected, Consumer<BufferedImage> resultImageConsumer) {
		return new BufferedImageMatcher(expected, resultImageConsumer);
	}
	
	private static Matcher<Path> createMatcher(Path expectedImagePath, Function<BufferedImage, Matcher<BufferedImage>> matchFn) {
		BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(expectedImagePath.toString());
		return new FeatureMatcher<Path, BufferedImage>(matchFn.apply(expectedImage), "", "") {

			@Override
			protected BufferedImage featureValueOf(Path actualImagePath) {
				return ImageComparisonUtil.readImageFromResources(actualImagePath.toString());
			}
			
		};
	}

	/**
	 * @param expected the location of the expected image 
	 * @return a matcher that compares another image file to the expected image file
	 */
	public static Matcher<Path> isSameAs(Path expected) {
		return createMatcher(expected, BufferedImageMatcher::isSameAs);
	}

	/**
	 * @param expected the location of the expected image 
	 * @param result the location where the result image will be stored if the image does not match the expected image
	 * @return a matcher that compares another image file to the expected image file and writes out the result if they don't match 
	 */
	public static Matcher<Path> isSameAs(Path expected, Path result) {
		return createMatcher(expected, i->isSameAs(i, r->ImageComparisonUtil.saveImage(result.toFile(), r)));
	}

	
	private static Matcher<byte[]> createMatcher(byte[] expectedImageBytes, Function<BufferedImage, Matcher<BufferedImage>> matchFn) {
		BufferedImage expectedImage = bufferedImageFromByteArray(expectedImageBytes);
		return new FeatureMatcher<byte[], BufferedImage>(matchFn.apply(expectedImage), "", "") {

			@Override
			protected BufferedImage featureValueOf(byte[] actual) {
				return bufferedImageFromByteArray(actual);
			}

		};
	}

	/**
	 * @param expected the expected image
	 * @return a matcher that compares another image to the expected image
	 */
	public static Matcher<byte[]> isSameAs(byte[] expected) {
		return createMatcher(expected, BufferedImageMatcher::isSameAs);
	}

	/**
	 * @param expected the expected image
	 * @param resultImageConsumer a Consumer to save the comparison resuit if the match fails
	 * @return a matcher that compares another image's bytes to the expected image's bytes and calls the Consumer if they don't match 
	 */
	public static Matcher<byte[]> isSameAs(byte[] expected, Consumer<byte[]> resultImageConsumer) {
		return  createMatcher(expected, i->isSameAs(i, r->resultImageConsumer.accept(byteArrayFromBufferedImage(r))));
	}

	private static BufferedImage bufferedImageFromByteArray(byte[] ba) {
		try (ByteArrayInputStream is = new ByteArrayInputStream(ba)) {
			return ImageIO.read(is);
		} catch (IOException e) {
			throw new UncheckedIOException("Error while transferring bytes into BufferedImage", e);
		}
	}
	
	private static byte[] byteArrayFromBufferedImage(BufferedImage bi) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ImageIO.write(bi, "png", os);
			return os.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException("Error while transferring bytes into BufferedImage", e);
		}
	}

}