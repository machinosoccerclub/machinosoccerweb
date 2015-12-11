package machinosoccerweb.registration.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import machinosoccerweb.google.Picasaweb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import retrofit.RetrofitError;

@Controller
@EnableAutoConfiguration
public class UploadController {
  private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

  @Autowired
  public UploadController(Picasaweb picasaweb) {
    this.picasaweb = picasaweb;
  }

  @Value("${machinosoccerweb.attendUrl}")
  private String attendUrl;

  private Picasaweb picasaweb;

  @RequestMapping(value = "/completed", method = RequestMethod.GET)
  public String completed(Model model) {
    model.addAttribute("attendUrl", attendUrl);
    return "registration/completed";
  }

  @RequestMapping(value = "/upload", method = RequestMethod.GET)
  public String uploadForm() {
    return "registration/upload";
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public String upload(@RequestParam("name") String name,
                       @RequestParam("nameKana") String namekana,
                       @RequestParam("grade") String grade,
                       @RequestParam("gender") String gender,
                       @RequestParam("photo") MultipartFile file) throws IOException {
    if (!file.isEmpty()) {
      String nameAndKana = normalizeName(name, namekana);
      try (InputStream is = file.getInputStream()) {
        picasaweb.uploadPhoto(
            file.getOriginalFilename(),
            nameAndKana,
            determineContentType(file),
            new String[]{grade, gender},
            is);
      } catch(RetrofitError error) {  // todo: move to exception handler
        logger.error("could not upload the photo." +
                "filename:{}, givenContentType:{}, memberName:{}, status:{}, reason:{}",
            file.getOriginalFilename(),
            file.getContentType(),
            Base64.getEncoder().encodeToString(nameAndKana.getBytes("utf8")),
            error.getResponse() != null ? error.getResponse().getStatus() : "null response",
            error.getResponse() != null ? error.getResponse().getReason() : "null response");
        logger.error("picasa service replies ...: {}",
            error.getResponse() != null ? error.getResponse().getBody() : "null response");

        // stack trace will be logged by exception handler
        throw error;
      }

      return "redirect:/completed";
    } else {
      logger.error("empty file posted ... name:{}, kana:{}, grade:{}, gender:{}",
          name, namekana, grade, gender);

      return "redirect:/confirm-error";
    }
  }

  protected String determineContentType(MultipartFile file) {
    if(picasaweb.isSupportedMediaType(file.getContentType())) {
      return file.getContentType();
    } else {
      String mediaType = "image/" + getFileExt(file.getOriginalFilename());
      if(!picasaweb.isSupportedMediaType(mediaType)) {
        logger.warn("we are trying to upload the photo to the GooglePicasa" +
            " with unsupported media type ... : {}", mediaType);
      }
      return mediaType;
    }
  }

  private String normalizeName(String name, String kana) {
    return (name.trim() + "（" + kana.trim() + "）").replace(' ', '　');
  }

  private String getFileExt(String filename) {
    int i = filename.indexOf(".");
    return i >= 0 ? filename.substring(i+1) : "";
  }
}