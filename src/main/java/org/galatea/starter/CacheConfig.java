package org.galatea.starter;

import com.google.common.cache.CacheBuilder;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
  public static final String CACHE_ONE = "cacheOne";

  @Bean
  public Cache cacheOne() {
    return new GuavaCache(CACHE_ONE, CacheBuilder.newBuilder()
    .expireAfterWrite(6, TimeUnit.HOURS).build());
  }

}
