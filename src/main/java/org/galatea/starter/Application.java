package org.galatea.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * This is the entry point for the application.
 *
 */
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@ComponentScan
public class Application {

  /**
   * Start up the spring context. java -Dcapsule.log=verbose -Dcapsule.mode=uat -jar
   * target/fuse-starter-java-0.0.1-SNAPSHOT-capsule.jar
   *
   * @param args command line args
   */
  public static void main(final String[] args) {
    log.info("Starting spring application {}", System.getProperty("application.name"));
    SpringApplication.run(Application.class, args);

  }
}
