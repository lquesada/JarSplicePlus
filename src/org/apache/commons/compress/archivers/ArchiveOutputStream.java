/*     */ package org.apache.commons.compress.archivers;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public abstract class ArchiveOutputStream extends OutputStream
/*     */ {
/*  52 */   private final byte[] oneByte = new byte[1];
/*     */   static final int BYTE_MASK = 255;
/*  56 */   private long bytesWritten = 0L;
/*     */ 
/*     */   public abstract void putArchiveEntry(ArchiveEntry paramArchiveEntry)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract void closeArchiveEntry()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract void finish()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract ArchiveEntry createArchiveEntry(File paramFile, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public void write(int b)
/*     */     throws IOException
/*     */   {
/* 112 */     this.oneByte[0] = ((byte)(b & 0xFF));
/* 113 */     write(this.oneByte, 0, 1);
/*     */   }
/*     */ 
/*     */   protected void count(int written)
/*     */   {
/* 123 */     count(written);
/*     */   }
/*     */ 
/*     */   protected void count(long written)
/*     */   {
/* 134 */     if (written != -1L)
/* 135 */       this.bytesWritten += written;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int getCount()
/*     */   {
/* 147 */     return (int)this.bytesWritten;
/*     */   }
/*     */ 
/*     */   public long getBytesWritten()
/*     */   {
/* 156 */     return this.bytesWritten;
/*     */   }
/*     */ 
/*     */   public boolean canWriteEntryData(ArchiveEntry ae)
/*     */   {
/* 169 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveOutputStream
 * JD-Core Version:    0.6.2
 */