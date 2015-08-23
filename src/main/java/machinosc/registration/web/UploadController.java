package machinosc.registration.web;

import machinosc.services.google.PicasaAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Controller
@EnableAutoConfiguration
public class UploadController {
  @Autowired
  private PicasaAlbumService picasaAlbumService;

  @RequestMapping(value = "/upload", method = RequestMethod.GET)
  public String uploadForm() {
      return "/upload";
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public String upload(@RequestParam("name") String name,
                       @RequestParam("nameKana") String namekana,
                       @RequestParam("grade") String grade,
                       @RequestParam("gender") String gender,
                       @RequestParam("photo") MultipartFile file) throws IOException {
    if(!file.isEmpty()) {
      String nameAndKana = normalizeName(name, namekana);
      try(InputStream is = file.getInputStream()) {
          picasaAlbumService.uploadPhoto(
            file.getOriginalFilename(),
            nameAndKana,
            file.getContentType(),
            new String[]{grade, gender},
            is);
      }
      return "/conf";
    } else {
      return "/conferror";
    }
  }

  private String normalizeName(String name, String kana) {
    return (name.trim() + "（" + kana.trim() + "）").replace(' ', '　');
  }
}