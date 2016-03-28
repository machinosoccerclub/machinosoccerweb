package machinosoccerweb.registration.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.members.models.Gender;
import machinosoccerweb.members.models.Grade;
import machinosoccerweb.members.models.Member;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import retrofit.RetrofitError;

@Slf4j
@Controller
public class UploadController {

  @Value("${machinosoccerweb.attendUrl}")
  private String attendUrl;

  @RequestMapping(value = "/completed", method = RequestMethod.GET)
  public String completed(Model model) {
    model.addAttribute("attendUrl", attendUrl);
    return "registration/completed";
  }

  @RequestMapping(value = "/upload", method = RequestMethod.GET)
  public String uploadForm(@ModelAttribute("memberPhoto") MemberPhotoForm form) {
    return "registration/upload";
  }

  @RequestMapping(value = "/upload2", method = RequestMethod.POST)
  public String upload(@RequestParam("name") String name,
                       @RequestParam("nameKana") String namekana,
                       @RequestParam("grade") String grade,
                       @RequestParam("gender") String gender,
                       @RequestParam("photo") MultipartFile file) throws IOException {
    if (!file.isEmpty()) {
//      String nameAndKana = normalizeName(name, namekana);
//      try (InputStream is = file.getInputStream()) {
//        picasaweb.uploadPhoto(
//            file.getOriginalFilename(),
//            grade + "・" + nameAndKana,
//            determineContentType(file),
//            new String[]{grade, gender, genderTags.get(grade)},
//            is);
//      } catch (RetrofitError error) {  // todo: move to exception handler
//        log.error("could not upload the photo." +
//                "filename:{}, givenContentType:{}, memberName:{}, status:{}, reason:{}",
//            file.getOriginalFilename(),
//            file.getContentType(),
//            Base64.getEncoder().encodeToString(nameAndKana.getBytes("utf8")),
//            error.getResponse() != null ? error.getResponse().getStatus() : "null response",
//            error.getResponse() != null ? error.getResponse().getReason() : "null response");
//        log.error("picasa service replies ...: {}",
//            error.getResponse() != null ? error.getResponse().getBody() : "null response");
//
//        // stack trace will be logged by exception handler
//        throw error;
//      }

      return "redirect:/completed";
    } else {
      log.error("empty file posted ... name:{}, kana:{}, grade:{}, gender:{}",
          name, namekana, grade, gender);

      return "redirect:/confirm-error";
    }
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public String upload(@Validated @ModelAttribute("memberPhoto") MemberPhotoForm form,
                       BindingResult result) {
    if(result.hasErrors()) {
      return "registration/upload";
    }
    Member member = new Member();
    BeanUtils.copyProperties(form, member);

    log.debug("upload requested with member:", member);

    return "redirect:/completed";
  }


  protected String determineContentType(MultipartFile file) {
//    if (picasaweb.isSupportedMediaType(file.getContentType())) {
//      return file.getContentType();
//    } else {
//      String mediaType = "image/" + getFileExt(file.getOriginalFilename());
//      if (!picasaweb.isSupportedMediaType(mediaType)) {
//        log.warn("we are trying to upload the photo to the GooglePicasa" +
//            " with unsupported media type ... : {}", mediaType);
//      }
//      return mediaType;
//    }
    return "";
  }

  private String normalizeName(String name, String kana) {
    return (name.trim() + "（" + kana.trim() + "）").replace(' ', '　');
  }

  private String getFileExt(String filename) {
    int i = filename.indexOf(".");
    return i >= 0 ? filename.substring(i + 1) : "";
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

  @Data
  private static class MemberPhotoForm {
    @NotBlank
    private String name;
    @NotBlank
    private String namekana;

    @NotBlank
    private Grade grade;
    @NotBlank
    private Gender gender;

    @NotBlank
    private MultipartFile file;
  }
}