/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ public final class UnparseableExtraFieldData
/*     */   implements ZipExtraField
/*     */ {
/*  33 */   private static final ZipShort HEADER_ID = new ZipShort(44225);
/*     */   private byte[] localFileData;
/*     */   private byte[] centralDirectoryData;
/*     */ 
/*     */   public ZipShort getHeaderId()
/*     */   {
/*  44 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */   public ZipShort getLocalFileDataLength()
/*     */   {
/*  53 */     return new ZipShort(this.localFileData == null ? 0 : this.localFileData.length);
/*     */   }
/*     */ 
/*     */   public ZipShort getCentralDirectoryLength()
/*     */   {
/*  62 */     return this.centralDirectoryData == null ? 
/*  63 */       getLocalFileDataLength() : 
/*  64 */       new ZipShort(this.centralDirectoryData.length);
/*     */   }
/*     */ 
/*     */   public byte[] getLocalFileDataData()
/*     */   {
/*  73 */     return ZipUtil.copy(this.localFileData);
/*     */   }
/*     */ 
/*     */   public byte[] getCentralDirectoryData()
/*     */   {
/*  82 */     return this.centralDirectoryData == null ? 
/*  83 */       getLocalFileDataData() : ZipUtil.copy(this.centralDirectoryData);
/*     */   }
/*     */ 
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length)
/*     */   {
/*  94 */     this.localFileData = new byte[length];
/*  95 */     System.arraycopy(buffer, offset, this.localFileData, 0, length);
/*     */   }
/*     */ 
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*     */   {
/* 107 */     this.centralDirectoryData = new byte[length];
/* 108 */     System.arraycopy(buffer, offset, this.centralDirectoryData, 0, length);
/* 109 */     if (this.localFileData == null)
/* 110 */       parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnparseableExtraFieldData
 * JD-Core Version:    0.6.2
 */