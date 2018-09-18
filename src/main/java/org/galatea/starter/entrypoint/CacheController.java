package org.galatea.starter.entrypoint;

import org.galatea.starter.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CacheController {

  @CacheEvict(value = CacheConfig.STOCK_PRICE_CACHE, allEntries = true)
  @RequestMapping("/clearCache")
  public ResponseEntity<String> clearCache() {
    return new ResponseEntity<String>("Cache Cleared", HttpStatus.OK);
  }

}
