/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ public class UnrecognizedExtraField
/*     */   implements ZipExtraField
/*     */ {
/*     */   private ZipShort headerId;
/*     */   private byte[] localData;
/*     */   private byte[] centralData;
/*     */ 
/*     */   public void setHeaderId(ZipShort headerId)
/*     */   {
/*  41 */     this.headerId = headerId;
/*     */   }
/*     */ 
/*     */   public ZipShort getHeaderId()
/*     */   {
/*  49 */     return this.headerId;
/*     */   }
/*     */ 
/*     */   public void setLocalFileDataData(byte[] data)
/*     */   {
/*  64 */     this.localData = ZipUtil.copy(data);
/*     */   }
/*     */ 
/*     */   public ZipShort getLocalFileDataLength()
/*     */   {
/*  72 */     return new ZipShort(this.localData.length);
/*     */   }
/*     */ 
/*     */   public byte[] getLocalFileDataData()
/*     */   {
/*  80 */     return ZipUtil.copy(this.localData);
/*     */   }
/*     */ 
/*     */   public void setCentralDirectoryData(byte[] data)
/*     */   {
/*  94 */     this.centralData = ZipUtil.copy(data);
/*     */   }
/*     */ 
/*     */   public ZipShort getCentralDirectoryLength()
/*     */   {
/* 103 */     if (this.centralData != null) {
/* 104 */       return new ZipShort(this.centralData.length);
/*     */     }
/* 106 */     return getLocalFileDataLength();
/*     */   }
/*     */ 
/*     */   public byte[] getCentralDirectoryData()
/*     */   {
/* 114 */     if (this.centralData != null) {
/* 115 */       return ZipUtil.copy(this.centralData);
/*     */     }
/* 117 */     return getLocalFileDataData();
/*     */   }
/*     */ 
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*     */   {
/* 127 */     byte[] tmp = new byte[length];
/* 128 */     System.arraycopy(data, offset, tmp, 0, length);
/* 129 */     setLocalFileDataData(tmp);
/*     */   }
/*     */ 
/*     */   public void parseFromCentralDirectoryData(byte[] data, int offset, int length)
/*     */   {
/* 140 */     byte[] tmp = new byte[length];
/* 141 */     System.arraycopy(data, offset, tmp, 0, length);
/* 142 */     setCentralDirectoryData(tmp);
/* 143 */     if (this.localData == null)
/* 144 */       setLocalFileDataData(tmp);
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnrecognizedExtraField
 * JD-Core Version:    0.6.2
 */