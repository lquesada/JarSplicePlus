/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ public final class GeneralPurposeBit
/*     */ {
/*     */   private static final int ENCRYPTION_FLAG = 1;
/*     */   private static final int DATA_DESCRIPTOR_FLAG = 8;
/*     */   private static final int STRONG_ENCRYPTION_FLAG = 64;
/*     */   public static final int UFT8_NAMES_FLAG = 2048;
/*  52 */   private boolean languageEncodingFlag = false;
/*  53 */   private boolean dataDescriptorFlag = false;
/*  54 */   private boolean encryptionFlag = false;
/*  55 */   private boolean strongEncryptionFlag = false;
/*     */ 
/*     */   public boolean usesUTF8ForNames()
/*     */   {
/*  64 */     return this.languageEncodingFlag;
/*     */   }
/*     */ 
/*     */   public void useUTF8ForNames(boolean b)
/*     */   {
/*  71 */     this.languageEncodingFlag = b;
/*     */   }
/*     */ 
/*     */   public boolean usesDataDescriptor()
/*     */   {
/*  79 */     return this.dataDescriptorFlag;
/*     */   }
/*     */ 
/*     */   public void useDataDescriptor(boolean b)
/*     */   {
/*  87 */     this.dataDescriptorFlag = b;
/*     */   }
/*     */ 
/*     */   public boolean usesEncryption()
/*     */   {
/*  94 */     return this.encryptionFlag;
/*     */   }
/*     */ 
/*     */   public void useEncryption(boolean b)
/*     */   {
/* 101 */     this.encryptionFlag = b;
/*     */   }
/*     */ 
/*     */   public boolean usesStrongEncryption()
/*     */   {
/* 108 */     return (this.encryptionFlag) && (this.strongEncryptionFlag);
/*     */   }
/*     */ 
/*     */   public void useStrongEncryption(boolean b)
/*     */   {
/* 115 */     this.strongEncryptionFlag = b;
/* 116 */     if (b)
/* 117 */       useEncryption(true);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 125 */     return 
/* 126 */       ZipShort.getBytes((this.dataDescriptorFlag ? 8 : 0) | (
/* 128 */       this.languageEncodingFlag ? 2048 : 0) | (
/* 130 */       this.encryptionFlag ? 1 : 0) | (
/* 132 */       this.strongEncryptionFlag ? 64 : 0));
/*     */   }
/*     */ 
/*     */   public static GeneralPurposeBit parse(byte[] data, int offset)
/*     */   {
/* 142 */     int generalPurposeFlag = ZipShort.getValue(data, offset);
/* 143 */     GeneralPurposeBit b = new GeneralPurposeBit();
/* 144 */     b.useDataDescriptor((generalPurposeFlag & 0x8) != 0);
/* 145 */     b.useUTF8ForNames((generalPurposeFlag & 0x800) != 0);
/* 146 */     b.useStrongEncryption((generalPurposeFlag & 0x40) != 0);
/*     */ 
/* 148 */     b.useEncryption((generalPurposeFlag & 0x1) != 0);
/* 149 */     return b;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 154 */     return 3 * (7 * (13 * (17 * (this.encryptionFlag ? 1 : 0) + (
/* 155 */       this.strongEncryptionFlag ? 1 : 0)) + (
/* 156 */       this.languageEncodingFlag ? 1 : 0)) + (
/* 157 */       this.dataDescriptorFlag ? 1 : 0));
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 162 */     if (!(o instanceof GeneralPurposeBit)) {
/* 163 */       return false;
/*     */     }
/* 165 */     GeneralPurposeBit g = (GeneralPurposeBit)o;
/*     */ 
/* 169 */     return (g.encryptionFlag == this.encryptionFlag) && 
/* 167 */       (g.strongEncryptionFlag == this.strongEncryptionFlag) && 
/* 168 */       (g.languageEncodingFlag == this.languageEncodingFlag) && 
/* 169 */       (g.dataDescriptorFlag == this.dataDescriptorFlag);
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.GeneralPurposeBit
 * JD-Core Version:    0.6.2
 */