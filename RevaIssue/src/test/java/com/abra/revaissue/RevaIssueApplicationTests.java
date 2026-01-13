package com.abra.revaissue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is your base test file.
 */
@SpringBootTest
class RevaIssueApplicationTests {

	@Test
	void contextLoads() {
		assertThat(2 + 2).isEqualTo(4);
	}
}
