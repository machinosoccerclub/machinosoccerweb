package machinosoccerweb.registration.web;

import java.io.IOException;
import java.io.InputStream;

import machinosoccerweb.google.Picasaweb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@EnableAutoConfiguration
public class UploadController {
  private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

  @Autowired
  private Picasaweb picasaweb;

  @RequestMapping(value = "/completed", method = RequestMethod.GET)
  public String completed() {
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
            file.getContentType(),
            new String[]{grade, gender},
            is);
      }
      return "redirect:/completed";
    } else {
      logger.error("empty file posted ... name:{}, kana:{}, grade:{}, gender:{}",
          name, namekana, grade, gender);

      return "redirect:/confirm-error";
    }
  }

  private String normalizeName(String name, String kana) {
    return (name.trim() + "（" + kana.trim() + "）").replace(' ', '　');
  }
}