package machinosoccerweb.registration.web;

import java.util.Arrays;

import machinosoccerweb.google.Picasaweb;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.springframework.web.multipart.MultipartFile;

public class UploadControllerTest {
  @Test
  public void testDetermineContentType() {
    UploadController controller = new UploadController(new Picasaweb());

    // supported content types
    Arrays.asList("image/jpeg", "image/jpg", "image/bmp", "image/png", "image/gif")
        .stream()
        .map(ct ->
          {MultipartFile f = mock(MultipartFile.class);
            when(f.getContentType()).thenReturn(ct);
            return f;})
        .forEach(f -> assertEquals(f.getContentType(), controller.determineContentType(f)));

    // unsupported content types
    String unsupportedContentType = "text/xml";
    MultipartFile stubFile = mock(MultipartFile.class);
    when(stubFile.getContentType()).thenReturn(unsupportedContentType);
    when(stubFile.getOriginalFilename()).thenReturn("filename.jpeg");

    assertEquals("image/jpeg", controller.determineContentType(stubFile));

    // unsupported content types with unsupported file extension
    stubFile = mock(MultipartFile.class);
    when(stubFile.getContentType()).thenReturn(unsupportedContentType);
    when(stubFile.getOriginalFilename()).thenReturn("filename.xml");

    assertEquals("image/xml", controller.determineContentType(stubFile));

    // unsupported content types with no file extension
    stubFile = mock(MultipartFile.class);
    when(stubFile.getContentType()).thenReturn(unsupportedContentType);
    when(stubFile.getOriginalFilename()).thenReturn("filename");

    assertEquals("image/", controller.determineContentType(stubFile));

    // unsupported content types with filename ending dot(.)
    stubFile = mock(MultipartFile.class);
    when(stubFile.getContentType()).thenReturn(unsupportedContentType);
    when(stubFile.getOriginalFilename()).thenReturn("filename.");

    assertEquals("image/", controller.determineContentType(stubFile));

    // unsupported content types with filename starting dot(.)
    stubFile = mock(MultipartFile.class);
    when(stubFile.getContentType()).thenReturn(unsupportedContentType);
    when(stubFile.getOriginalFilename()).thenReturn(".filename");

    assertEquals("image/filename", controller.determineContentType(stubFile));

    // unsupported content types with empty filename
    stubFile = mock(MultipartFile.class);
    when(stubFile.getContentType()).thenReturn(unsupportedContentType);
    when(stubFile.getOriginalFilename()).thenReturn("");

    assertEquals("image/", controller.determineContentType(stubFile));

    // todo: rewrite in spock ...
  }
}
