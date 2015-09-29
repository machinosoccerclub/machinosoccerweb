package machinosoccerweb.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import retrofit.mime.TypedOutput;

public class TypedOutputStream implements TypedOutput {
  private String fileName;
  private long length;
  private String mimeType;
  private InputStream is;

  public TypedOutputStream(String fileName, long length, String mimeType, InputStream is) {
    this.fileName = fileName;
    this.length = length;
    this.mimeType = mimeType;
    this.is = is;
  }

  public String fileName() {
    return this.fileName;
  }

  public long length() {
    return this.length;
  }

  public String mimeType() {
    return this.mimeType;
  }

  public void writeTo(OutputStream os) throws IOException {
    IOUtils.copy(this.is, os);
  }
}
