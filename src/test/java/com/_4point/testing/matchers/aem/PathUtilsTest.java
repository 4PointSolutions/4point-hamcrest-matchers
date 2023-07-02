package com._4point.testing.matchers.aem;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PathUtilsTest {

	@ParameterizedTest
	@CsvSource(textBlock="""
			foo.bar, 			foo
			parent/foo.bar, 	parent/foo
			foo, 				foo
			parent/foo, 		parent/foo
			foo., 				foo
			parent/foo., 		parent/foo
			.,					'' 
			parent/., 			parent
			""")
	void testRemoveExtension(Path input, Path expectedResult) {
		assertEquals(expectedResult, PathUtils.removeExtension(input));
	}

	@ParameterizedTest
	@CsvSource(textBlock="""
			foo.bar, 			bar
			parent/foo.bar, 	bar
			foo, 				''
			parent/foo, 		''
			foo., 				''
			parent/foo., 		''
			.,					'' 
			parent/., 			''
			foo.bar.bat, 		bat
			parent/foo.bar.bat, bat
			""")
	void testGetExtension(Path input, String expectedResult) {
		assertEquals(expectedResult, PathUtils.getExtension(input));
	}

	@ParameterizedTest
	@CsvSource(textBlock="""
			foo.bar, 				foo_new.bar
			parent/foo.bar, 		parent/foo_new.bar
			foo_old.bar, 			foo_new.bar
			parent/foo_old.bar, 	parent/foo_new.bar
			foo_old, 				foo_new
			parent/foo_old, 		parent/foo_new
			foo__old., 				foo__new
			parent/foo_old., 		parent/foo_new
			.,						_new 
			parent/., 				parent/_new
			""")
	void testReplaceQualifier(Path input, Path expectedResult) {
		assertEquals(expectedResult, PathUtils.replaceQualifier(input, "new"));
	}

}
