/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ public abstract class AbstractUnicodeExtraField
/*     */   implements ZipExtraField
/*     */ {
/*     */   private long nameCRC32;
/*     */   private byte[] unicodeName;
/*     */   private byte[] data;
/*     */ 
/*     */   protected AbstractUnicodeExtraField()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len)
/*     */   {
/*  50 */     CRC32 crc32 = new CRC32();
/*  51 */     crc32.update(bytes, off, len);
/*  52 */     this.nameCRC32 = crc32.getValue();
/*     */     try
/*     */     {
/*  55 */       this.unicodeName = text.getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  57 */       throw new RuntimeException("FATAL: UTF-8 encoding not supported.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected AbstractUnicodeExtraField(String text, byte[] bytes)
/*     */   {
/*  70 */     this(text, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   private void assembleData() {
/*  74 */     if (this.unicodeName == null) {
/*  75 */       return;
/*     */     }
/*     */ 
/*  78 */     this.data = new byte[5 + this.unicodeName.length];
/*     */ 
/*  80 */     this.data[0] = 1;
/*  81 */     System.arraycopy(ZipLong.getBytes(this.nameCRC32), 0, this.data, 1, 4);
/*  82 */     System.arraycopy(this.unicodeName, 0, this.data, 5, this.unicodeName.length);
/*     */   }
/*     */ 
/*     */   public long getNameCRC32()
/*     */   {
/*  90 */     return this.nameCRC32;
/*     */   }
/*     */ 
/*     */   public void setNameCRC32(long nameCRC32)
/*     */   {
/*  98 */     this.nameCRC32 = nameCRC32;
/*  99 */     this.data = null;
/*     */   }
/*     */ 
/*     */   public byte[] getUnicodeName()
/*     */   {
/* 106 */     byte[] b = (byte[])null;
/* 107 */     if (this.unicodeName != null) {
/* 108 */       b = new byte[this.unicodeName.length];
/* 109 */       System.arraycopy(this.unicodeName, 0, b, 0, b.length);
/*     */     }
/* 111 */     return b;
/*     */   }
/*     */ 
/*     */   public void setUnicodeName(byte[] unicodeName)
/*     */   {
/* 118 */     if (unicodeName != null) {
/* 119 */       this.unicodeName = new byte[unicodeName.length];
/* 120 */       System.arraycopy(unicodeName, 0, this.unicodeName, 0, 
/* 121 */         unicodeName.length);
/*     */     } else {
/* 123 */       this.unicodeName = null;
/*     */     }
/* 125 */     this.data = null;
/*     */   }
/*     */ 
/*     */   public byte[] getCentralDirectoryData()
/*     */   {
/* 130 */     if (this.data == null) {
/* 131 */       assembleData();
/*     */     }
/* 133 */     byte[] b = (byte[])null;
/* 134 */     if (this.data != null) {
/* 135 */       b = new byte[this.data.length];
/* 136 */       System.arraycopy(this.data, 0, b, 0, b.length);
/*     */     }
/* 138 */     return b;
/*     */   }
/*     */ 
/*     */   public ZipShort getCentralDirectoryLength()
/*     */   {
/* 143 */     if (this.data == null) {
/* 144 */       assembleData();
/*     */     }
/* 146 */     return new ZipShort(this.data.length);
/*     */   }
/*     */ 
/*     */   public byte[] getLocalFileDataData()
/*     */   {
/* 151 */     return getCentralDirectoryData();
/*     */   }
/*     */ 
/*     */   public ZipShort getLocalFileDataLength()
/*     */   {
/* 156 */     return getCentralDirectoryLength();
/*     */   }
/*     */ 
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 163 */     if (length < 5) {
/* 164 */       throw new ZipException("UniCode path extra data must have at least 5 bytes.");
/*     */     }
/*     */ 
/* 167 */     int version = buffer[offset];
/*     */ 
/* 169 */     if (version != 1) {
/* 170 */       throw new ZipException("Unsupported version [" + version + 
/* 171 */         "] for UniCode path extra data.");
/*     */     }
/*     */ 
/* 174 */     this.nameCRC32 = ZipLong.getValue(buffer, offset + 1);
/* 175 */     this.unicodeName = new byte[length - 5];
/* 176 */     System.arraycopy(buffer, offset + 5, this.unicodeName, 0, length - 5);
/* 177 */     this.data = null;
/*     */   }
/*     */ 
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 187 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.AbstractUnicodeExtraField
 * JD-Core Version:    0.6.2
 */