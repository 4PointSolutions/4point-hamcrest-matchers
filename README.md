# 4Point Hamcrest Matchers Library

This is a library of [Hamcrest](https://github.com/hamcrest/JavaHamcrest) "[Matchers](https://hamcrest.org/JavaHamcrest/javadoc/2.2/org/hamcrest/Matchers.html)" that test 
conditions commonly found in 4Point applications.

Currently it consists of a small number of classes:
* `ResponseMatchers` - Test Matchers for common conditions related to the [Jakarta RESTful Services](https://jakarta.ee/specifications/restful-ws/) a.k.a. JAX-RS ([Jersey](https://eclipse-ee4j.github.io/jersey/), [RESTEasy](https://resteasy.dev/), etc.) Response object.
* `ExceptionMatchers` - Test Matchers for testing java.lang.Exception objects.
* `Pdf` - Object that allows validation and verification of various properties of a PDF.
* `HtmlForm` - Object that allows validation and verification of various properties of an HTML form generated by AEM.

The Javadocs for these matchers can be found [here](https://4pointsolutions.github.io/4point-hamcrest-matchers/javadocs/0.0.1-SNAPSHOT/apidocs/).

To use this library, you need to include it as a test dependency in your project.

For Maven, the following entry in the pom.xml file is required:
```xml
	<dependency>
		<groupId>com._4point.testing</groupId>
		<artifactId>4point-hamcrest-matchers</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<scope>test</scope>
	</dependency>
```

The .jar file is stored in the project's GitHub package repository, so it may also may require that you
add that repo to your project:
```xml
	<repositories>
		<repository>
			<id>github</id>
			<url>https://maven.pkg.github.com/4PointSolutions/*</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
```


GitHub packages maven repositories require that the user authenticate (even for read access), 
so in order for this build to work, you need to have your personal GitHub credentials configured
in your local settings.xml file (found in you $HOME/.m2 directory).

Your settings.xml should look something like this:
```xml
	<?xml version="1.0"?>
	<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/SETTINGS/1.0.0">
		<servers>
			<server>
				<id>github</id>
				<username>Your GitHub Username goes here</username>
				<password>Your Personal Access Token goes here</password>
			</server>
		</servers>
	</settings>
```

