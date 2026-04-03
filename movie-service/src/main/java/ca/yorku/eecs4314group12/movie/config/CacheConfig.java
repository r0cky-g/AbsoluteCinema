package ca.yorku.eecs4314group12.movie.config;

import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {
	
	  @Bean
	  CacheManager cacheManager() {
		  CaffeineCacheManager manager = new CaffeineCacheManager("trending", "nowPlaying");
		  manager.setCaffeine(Caffeine.newBuilder()
				  .expireAfterWrite(1, TimeUnit.DAYS));
		  return manager;
	  }
}
