package SevenZip;

import SevenZip.Compression.LZMA.Decoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LzmaAlone {
	@SuppressWarnings("resource")
	public static void decompress(File in, File out) throws Exception {
	File inFile = in;
    File outFile = out;

	BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(inFile));
	BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outFile));

    int propertiesSize = 5;
    byte[] properties = new byte[propertiesSize];
    if (inStream.read(properties, 0, propertiesSize) != propertiesSize) {
      throw new Exception("input .lzma file is too short");
    }
    Decoder decoder = new Decoder();
    if (!decoder.SetDecoderProperties(properties)) {
      throw new Exception("Incorrect stream properties");
    }
    long outSize = 0L;
    for (int i = 0; i < 8; i++) {
      int v = inStream.read();
      if (v < 0) {
        throw new Exception("Can't read stream size");
      }
      outSize |= v << 8 * i;
    }
    if (!decoder.Code(inStream, outStream, outSize)) {
      throw new Exception("Error in data stream");
    }

    outStream.flush();
    outStream.close();
    inStream.close();
  }
}