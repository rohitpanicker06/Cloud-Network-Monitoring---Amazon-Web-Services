package POC;
//Class for Compressing payload.
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
enum  GZipCompressor {
  INSTANCE;
  public static byte[] compress(byte[] source) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(source.length);
    try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
      gzip.write(source);
    }
    return baos.toByteArray();
  }
}