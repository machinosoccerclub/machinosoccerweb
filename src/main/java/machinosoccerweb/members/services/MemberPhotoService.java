package machinosoccerweb.members.services;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import machinosoccerweb.infra.google.GooglePicasa;
import machinosoccerweb.members.models.GooglePicasaPhotoEntry;
import machinosoccerweb.members.models.Member;
import machinosoccerweb.members.models.MemberPhoto;
import machinosoccerweb.members.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberPhotoService {
  private final MemberRepository memberRepository;

  private final GooglePicasa googlePicasa;

  @Autowired
  public MemberPhotoService(MemberRepository memberRepository, GooglePicasa googlePicasa) {
    this.memberRepository = memberRepository;
    this.googlePicasa = googlePicasa;
  }

  public Optional<Member> findMember(String memberNo) {
    return Optional.ofNullable(memberRepository.findOne(memberNo));
  }

  public Member updatePhoto(Member member, InputStream is, String originalFilename,
                            String uploadedContentType) {
    GooglePicasaPhotoEntry entry = Optional.ofNullable(member.getPhoto())
        .map(p ->          // update and replace the photo existing already
            googlePicasa.updatePhoto(p, originalFilename, is,
                determineContentType(uploadedContentType, originalFilename))
        )
        .orElseGet(           // upload the photo newly
            () -> googlePicasa.uploadPhoto(
                originalFilename,
                createSummary(member),
                createTags(member),
                is,
                determineContentType(uploadedContentType, originalFilename))
        );

    MemberPhoto photo = new MemberPhoto(member.getMemberNo(), entry);
    member.setPhoto(photo);

    return memberRepository.save(member);
  }

  private String[] createTags(Member member) {
    return new String[]{member.getGender().getLabel(), member.getGrade().getLabel()};
  }

  private String createSummary(Member member) {
    return String.format("%s %s（%s %s）", member.getFamilyName(), member.getGivenName(),
        member.getFamilyNameKana(), member.getGivenNameKana());
  }

  private String determineContentType(String uploadedContentType, String originalFilename) {
    Supplier<String> mediaTypeFromFileExt = () ->
        createMediaTypeFromFilename(originalFilename, uploadedContentType);

    return googlePicasa.normalizeMediaType(uploadedContentType)
        .orElseGet(mediaTypeFromFileExt);
  }

  private String createMediaTypeFromFilename(String originalFilename, String uploadedContentType) {
    Function<String, String> warnLog = (String mediaTypeToUse) -> {
      log.warn("we are attempting upload the file to picasa:`{}` using mediaType: `{}`," +
              "use got from browser: `{}`",
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
}
