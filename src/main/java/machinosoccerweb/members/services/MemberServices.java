package machinosoccerweb.members.services;

import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.infra.google.GooglePicasa;
import machinosoccerweb.members.models.GooglePicasaPhotoEntry;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.MemberPhoto;
import machinosoccerweb.members.repositories.MemberPhotoRepository;
import machinosoccerweb.members.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberServices {
  private final MemberRepository memberRepository;
  private final MemberPhotoRepository memberPhotoRepository;
  private final GooglePicasa googlePicasa;

  @Autowired
  public MemberServices(MemberRepository memberRepository, MemberPhotoRepository memberPhotoRepository, GooglePicasa googlePicasa) {
    this.memberRepository = memberRepository;
    this.memberPhotoRepository = memberPhotoRepository;
    this.googlePicasa = googlePicasa;
  }

  public Member saveNewMember(Member member, InputStream photoImage, String originalFilename,
                           String originalContentType) {
    GooglePicasaPhotoEntry entry =
        googlePicasa.uploadPhoto(originalFilename, createDescription(member), createTags(member),
            photoImage, determineContentType(originalContentType, originalFilename));
    Member savedMember = memberRepository.save(member);
    MemberPhoto photo = new MemberPhoto(savedMember.getId(), entry);
    memberPhotoRepository.save(photo);

    return savedMember;
  }

  private String createDescription(Member member) {
    return String.format("%s・%s（%s）", member.getGrade().getLabel(),
        member.getName(), member.getNamekana());
  }

  private String[] createTags(Member member) {
    return new String[]{member.getGender().getLabel(), member.getGrade().getLabel()};
  }

  private String determineContentType(String originalContentType, String originalFilename) {
    Supplier<String> mediaTypeFromFileExt = () ->
        createMediaTypeFromFilename(originalFilename, originalContentType);

    return GooglePicasa.normalizeMediaType(originalContentType)
        .orElseGet(mediaTypeFromFileExt);  }

  private String createMediaTypeFromFilename(String originalFilename, String originalContentType) {
    Function<String, String> warnLog = (String mediaTypeToUse) -> {
      log.warn("we are attempting upload the file to picasa:`{}` using mediaType: `{}`," +
              " because we got unsupported content type from browser: `{}`",
          originalFilename, mediaTypeToUse, originalContentType);
      return mediaTypeToUse;
    };

    int indexOfExt = originalFilename.indexOf(".");
    if (indexOfExt >= 0) {
      String contentTypeByFileext = "image/" +
          originalFilename.substring(indexOfExt + 1).toLowerCase();
      return googlePicasa.normalizeMediaType(contentTypeByFileext)
          .orElseGet(() -> warnLog.apply(contentTypeByFileext));
    } else {
      return warnLog.apply(originalContentType);
    }
  }
}
