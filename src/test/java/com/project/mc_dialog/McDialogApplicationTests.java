package com.project.mc_dialog;

import com.project.mc_dialog.testContainer.PostgresContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class McDialogApplicationTests  extends PostgresContainer {

	@BeforeAll
	public static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	public static void afterAll() {
		postgres.stop();
	}

	@Test
	void contextLoads() {
	}

}
