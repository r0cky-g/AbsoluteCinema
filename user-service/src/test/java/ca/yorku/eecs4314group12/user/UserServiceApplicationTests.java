package ca.yorku.eecs4314group12.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(UserServiceTestMailConfig.class)
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
