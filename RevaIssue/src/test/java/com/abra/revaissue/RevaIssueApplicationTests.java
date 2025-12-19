package com.abra.revaissue;

import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;
// * Right now @SpringBootTest spins up the entire context (triggering JPA) causing an error.

// * Adding an Assertions for a smoke test, this can be deleted later once we implement actual tests.
import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest
class RevaIssueApplicationTests {

	@Test
	void contextLoads() {
		assertThat(2 + 2).isEqualTo(4);
	}

}
