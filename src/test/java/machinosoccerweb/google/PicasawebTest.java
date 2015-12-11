package machinosoccerweb.google;

import java.util.Arrays;

import machinosoccerweb.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
public class PicasawebTest {
  private Picasaweb picasaweb = new Picasaweb();

  @Test
  public void testIsSupportedMediaType() {
    String[] validMediaTypes = {"image/bmp", "image/gif", "image/jpeg", "image/png", "image/jpg"};
    Arrays.asList(validMediaTypes).forEach(m -> assertTrue(picasaweb.isSupportedMediaType(m)));

    assertFalse(picasaweb.isSupportedMediaType(""));
    assertFalse(picasaweb.isSupportedMediaType("image/jpq"));
    assertFalse(picasaweb.isSupportedMediaType(null));
  }
}
