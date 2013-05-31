package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ZipEncoding
{
  public abstract boolean canEncode(String paramString);

  public abstract ByteBuffer encode(String paramString)
    throws IOException;

  public abstract String decode(byte[] paramArrayOfByte)
    throws IOException;
}

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipEncoding
 * JD-Core Version:    0.6.2
 */