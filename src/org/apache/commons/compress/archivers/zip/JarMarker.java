/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ public final class JarMarker
/*     */   implements ZipExtraField
/*     */ {
/*  29 */   private static final ZipShort ID = new ZipShort(51966);
/*  30 */   private static final ZipShort NULL = new ZipShort(0);
/*  31 */   private static final byte[] NO_BYTES = new byte[0];
/*  32 */   private static final JarMarker DEFAULT = new JarMarker();
/*     */ 
/*     */   public static JarMarker getInstance()
/*     */   {
/*  44 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public ZipShort getHeaderId()
/*     */   {
/*  52 */     return ID;
/*     */   }
/*     */ 
/*     */   public ZipShort getLocalFileDataLength()
/*     */   {
/*  61 */     return NULL;
/*     */   }
/*     */ 
/*     */   public ZipShort getCentralDirectoryLength()
/*     */   {
/*  70 */     return NULL;
/*     */   }
/*     */ 
/*     */   public byte[] getLocalFileDataData()
/*     */   {
/*  79 */     return NO_BYTES;
/*     */   }
/*     */ 
/*     */   public byte[] getCentralDirectoryData()
/*     */   {
/*  88 */     return NO_BYTES;
/*     */   }
/*     */ 
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 101 */     if (length != 0)
/* 102 */       throw new ZipException("JarMarker doesn't expect any data");
/*     */   }
/*     */ 
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 113 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.JarMarker
 * JD-Core Version:    0.6.2
 */