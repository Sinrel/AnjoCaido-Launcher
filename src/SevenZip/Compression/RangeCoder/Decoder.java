package SevenZip.Compression.RangeCoder;

import java.io.IOException;
import java.io.InputStream;

public class Decoder
{
  static final int kTopMask = -16777216;
  static final int kNumBitModelTotalBits = 11;
  static final int kBitModelTotal = 2048;
  static final int kNumMoveBits = 5;
  int Range;
  int Code;
  InputStream Stream;

  public final void SetStream(InputStream stream)
  {
    Stream = stream;
  }

  public final void ReleaseStream()
  {
    Stream = null;
  }

  public final void Init() throws IOException
  {
    Code = 0;
    Range = -1;
    for (int i = 0; i < 5; i++)
      Code = (Code << 8 | Stream.read());
  }

  public final int DecodeDirectBits(int numTotalBits) throws IOException
  {
    int result = 0;
    for (int i = numTotalBits; i != 0; i--)
    {
      Range >>>= 1;
      int t = Code - Range >>> 31;
      Code -= (Range & t - 1);
      result = result << 1 | 1 - t;

      if ((Range & 0xFF000000) != 0)
        continue;
      Code = (Code << 8 | Stream.read());
      Range <<= 8;
    }

    return result;
  }

  public int DecodeBit(short[] probs, int index) throws IOException
  {
    int prob = probs[index];
    int newBound = (Range >>> 11) * prob;
    if ((Code ^ 0x80000000) < (newBound ^ 0x80000000))
    {
      Range = newBound;
      probs[index] = (short)(prob + (2048 - prob >>> 5));
      if ((Range & 0xFF000000) == 0)
      {
        Code = (Code << 8 | Stream.read());
        Range <<= 8;
      }
      return 0;
    }

    Range -= newBound;
    Code -= newBound;
    probs[index] = (short)(prob - (prob >>> 5));
    if ((Range & 0xFF000000) == 0)
    {
      Code = (Code << 8 | Stream.read());
      Range <<= 8;
    }
    return 1;
  }

  public static void InitBitModels(short[] probs)
  {
    for (int i = 0; i < probs.length; i++)
      probs[i] = 1024;
  }
}