package machinosoccerweb.registration.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.infra.google.GooglePicasa;
import machinosoccerweb.members.models.Gender;
import machinosoccerweb.members.models.Grade;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import retrofit.RetrofitError;

@Slf4j
@Controller
public class UploadController {
  private final String attendUrl;

  private final GooglePicasa googlePicasa;

  @Autowired
  public UploadController(@Value("${machinosoccerweb.attendUrl}") String attendUrl,
                          GooglePicasa googlePicasa) {
    this.attendUrl = attendUrl;
    this.googlePicasa = googlePicasa;
  }

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
        googlePicasa.uploadPhoto(file.getOriginalFilename(),
            grade + "・" + nameAndKana,
            new String[]{grade, gender, genderTags.get(grade)},
            is,
            determineMediaType(file.getContentType(), file.getOriginalFilename()));
      }

      return "redirect:/completed";
    } else {
      log.error("empty file posted ... name:{}, kana:{}, grade:{}, gender:{}",
          name, namekana, grade, gender);

      return "redirect:/confirm-error";
    }
  }

  private String determineMediaType(String uploadedContentType, String originalFilename) {
    Supplier<String> mediaTypeFromFileExt = () ->
        createMediaTypeFromFilename(originalFilename, uploadedContentType);

    return GooglePicasa.normalizeMediaType(uploadedContentType)
        .orElseGet(mediaTypeFromFileExt);
  }

  private String createMediaTypeFromFilename(String originalFilename, String uploadedContentType) {
    Function<String, String> warnLog = (String mediaTypeToUse) -> {
      log.warn("we are attempting upload the file to picasa:`{}` using mediaType: `{}`," +
              " because we got unsupported content type from browser: `{}`",
          originalFilename, mediaTypeToUse, uploadedContentType);
      return mediaTypeToUse;
    };

    int indexOfExt = originalFilename.indexOf(".");
    if (indexOfExt >= 0) {
      String contentTypeByFileext = "image/" +
          originalFilename.substring(indexOfExt + 1).toLowerCase();
      return googlePicasa.normalizeMediaType(contentTypeByFileext)
          .orElseGet(() -> warnLog.apply(contentTypeByFileext));
    } else {
      return warnLog.apply(uploadedContentType);
    }
  }

  private String normalizeName(String name, String kana) {
    return (name.trim() + "（" + kana.trim() + "）").replace(' ', '　');
  }

  private static final Map<String, String> genderTags = new LinkedHashMap<String, String>() {
    {
      put("年少", "MachisakaPreSchoolerG1");
      put("年中", "MachisakaPreSchoolerG2");
      put("年長", "MachisakaPreSchoolerG3");
      put("小１", "MachisakaFC-U7");
      put("小２", "MachisakaFC-U8");
      put("小３", "MachisakaFC-U9");
      put("小４", "MachisakaFC-U10");
      put("小５", "MachisakaFC-U11");
      put("小６", "MachisakaFC-U12");
    }
  };
}