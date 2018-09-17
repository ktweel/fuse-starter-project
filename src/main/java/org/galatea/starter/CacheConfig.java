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
  public static final String STOCK_PRICE_CACHE = "stockPriceCache";

  @Bean
  public Cache cacheOne() {
    return new GuavaCache(STOCK_PRICE_CACHE, CacheBuilder.newBuilder()
    .expireAfterWrite(6, TimeUnit.HOURS).build());
  }

}
