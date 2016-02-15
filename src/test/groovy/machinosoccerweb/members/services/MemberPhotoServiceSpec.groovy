package machinosoccerweb.members.services

import spock.lang.Specification
import spock.lang.Unroll

class MemberPhotoServiceSpec extends Specification {
  @Unroll
  def "given contentType: #contentType and fileName: #fileName should use mediaType: #mediaType" ()
  {
    setup:
    def service = new MemberPhotoService(null, null)

    expect:
    mediaType == service.determineContentType(contentType, fileName)

    where:
    contentType    | fileName            || mediaType    | note
    "image/bmp"    | "*"                 || "image/bmp"  | "use the content type on upload"
    "image/png"    | "*"                 || "image/png"  | "use the content type on upload"
    "image/jpg"    | "*"                 || "image/jpg"  | "use the content type on upload"
    "image/jpeg"   | "*"                 || "image/jpg"  | "jpeg -> jpg"
    "notsupported" | "file.bmp"          || "image/bmp"  | "use file extension"
    "notsupported" | "file.foo"          || "image/foo"  | "use file extension"
    "image/bar"    | "file no extension" || "image/bar"  | "use unsupported content type on no ext"
  }
}