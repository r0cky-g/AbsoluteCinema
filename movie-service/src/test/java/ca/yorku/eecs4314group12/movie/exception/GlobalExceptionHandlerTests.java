package ca.yorku.eecs4314group12.movie.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {

    private MockMvc mockMvc;
	
	@RestController
    static class FakeController {
        @GetMapping("/test/movie-not-found")
        public void throwMovieNotFound() {
            throw new MovieNotFoundException(550);
        }
    }
	
	 @BeforeEach
	 void setUp() {
		 mockMvc = MockMvcBuilders
				 .standaloneSetup(new FakeController())
				 .setControllerAdvice(new GlobalExceptionHandler())
				 .build();
	 }
	
	@Test
    void test_1_movieNotFound() throws Exception {
		mockMvc.perform(get("/test/movie-not-found"))
		.andExpect(status().isNotFound())
		.andExpect(content().string("Movie not found with id: 550"));
	}
}
