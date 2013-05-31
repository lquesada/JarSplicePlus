package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public abstract interface ZipExtraField
{
  public abstract ZipShort getHeaderId();

  public abstract ZipShort getLocalFileDataLength();

  public abstract ZipShort getCentralDirectoryLength();

  public abstract byte[] getLocalFileDataData();

  public abstract byte[] getCentralDirectoryData();

  public abstract void parseFromLocalFileData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException;

  public abstract void parseFromCentralDirectoryData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException;
}

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipExtraField
 * JD-Core Version:    0.6.2
 */