package com.abra.revaissue;

/**
 * package => what folder/namespace does this class live in?
 * com.abra.revaissue => is the package name (and matches folder structure -> "RevaIssue/src/main/java/com/abra/revaissue/RevaIssueApplication.java")
 * Why it matters: helps organize code + avoids name collisions (two classes can both be named Main.)
 */

import org.springframework.boot.SpringApplication;
/**
 * import => being this class into scope so I can use it without typing the full path/
 * SpringApplication => a Spring Boot helper class that starts the app. 
*/
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * imports the annotation type @SpringBootApplication
 * Needed so Java knows what @SpringBootApplication means
 */
@SpringBootApplication
/**
 * Annotation => metadata that tells Spring Boot:
 * - This is a Spring Boot app
 * - Turn on auto-config
 * - Scan this package and subpackages for Spring components like @Controller, @Service, @Repository, etc.
 * 
 * UNDER THE HOOD
 * It's a bundle of:
 * - @Configuration => means this class can define beans
 * - @EnableAutoConfiguration => Spring Boot guesses sensible defaults
 * - @ComponentScan => scan this package + subpackages for components
 */
public class RevaIssueApplication {

	public static void main(String[] args) {
		/**
		 * public => other classes/methods can access this method -> JVM (and anything else can call it)
		 * static => means JVM can call it without having to create an object first. 
		 * void => method returns nothing.
		 * String[] args => command line arguments passed when you run the program
		 * 	- ex: java RevaIssueApplication --server.port=8081
		 * 	- Spring Boot can also interpret some args as properties
		 */

		SpringApplication.run(RevaIssueApplication.class, args);
		/**
		 * This is the "turn the key" line.
		 * 
		 * SpringApplication.run(...):
		 * 	- Creates the Spring application context aka (Spring’s “container”)
		 * 	- Starts auto-configuration
		 * 	- Starts embedded server if needed (Tomcat by default for web apps)
		 * 	- Runs component scanning to find your beans/controllers/etc.
		 * 
		 * RevaIssueApplication.class:
		 * 	- This passes the Class object so Spring knows what your “primary” application class is (where to start scanning/config).
		 * 
		 * args:
		 * 	- Passes through any command-line arguments so Spring can use them.
		 */
	}

	/**
	 * Bean -> an object managed by the Spring container (ApplicationContext). I.e., using the 
	 * ApplicationContext -> 
	 * Component => a bean discovered via component scanning (usually through annotations).
	 * 	- Spring will auto-detect and create beans for classes annotated with things like:
	 * 		- @Component (generic)
	 * 		- @Controller / @RestController (web layer)
	 * 		- @Service (business logic)
	 * 		- @Repository (data access)
	 * 		- All of these are basically “special flavors” of @Component.
	 */

}
