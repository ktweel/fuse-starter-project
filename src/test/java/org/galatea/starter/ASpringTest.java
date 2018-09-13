package org.galatea.starter;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

public class ASpringTest {
  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  public static String readData(final String fileName) throws IOException {
    return IOUtils.toString(ASpringTest.class.getClassLoader().getResourceAsStream(fileName))
        .trim();
  }

}
