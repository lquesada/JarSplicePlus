package org.apache.commons.compress.archivers;

import java.util.Date;

public abstract interface ArchiveEntry
{
  public static final long SIZE_UNKNOWN = -1L;

  public abstract String getName();

  public abstract long getSize();

  public abstract boolean isDirectory();

  public abstract Date getLastModifiedDate();
}

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveEntry
 * JD-Core Version:    0.6.2
 */