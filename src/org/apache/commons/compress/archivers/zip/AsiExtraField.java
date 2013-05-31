/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ public class AsiExtraField
/*     */   implements ZipExtraField, UnixStat, Cloneable
/*     */ {
/*  55 */   private static final ZipShort HEADER_ID = new ZipShort(30062);
/*     */   private static final int WORD = 4;
/*  60 */   private int mode = 0;
/*     */ 
/*  64 */   private int uid = 0;
/*     */ 
/*  68 */   private int gid = 0;
/*     */ 
/*  74 */   private String link = "";
/*     */ 
/*  78 */   private boolean dirFlag = false;
/*     */ 
/*  83 */   private CRC32 crc = new CRC32();
/*     */ 
/*     */   public ZipShort getHeaderId()
/*     */   {
/*  94 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */   public ZipShort getLocalFileDataLength()
/*     */   {
/* 103 */     return new ZipShort(14 + 
/* 108 */       getLinkedFile().getBytes().length);
/*     */   }
/*     */ 
/*     */   public ZipShort getCentralDirectoryLength()
/*     */   {
/* 117 */     return getLocalFileDataLength();
/*     */   }
/*     */ 
/*     */   public byte[] getLocalFileDataData()
/*     */   {
/* 127 */     byte[] data = new byte[getLocalFileDataLength().getValue() - 4];
/* 128 */     System.arraycopy(ZipShort.getBytes(getMode()), 0, data, 0, 2);
/*     */ 
/* 130 */     byte[] linkArray = getLinkedFile().getBytes();
/*     */ 
/* 132 */     System.arraycopy(ZipLong.getBytes(linkArray.length), 
/* 133 */       0, data, 2, 4);
/*     */ 
/* 135 */     System.arraycopy(ZipShort.getBytes(getUserId()), 
/* 136 */       0, data, 6, 2);
/* 137 */     System.arraycopy(ZipShort.getBytes(getGroupId()), 
/* 138 */       0, data, 8, 2);
/*     */ 
/* 140 */     System.arraycopy(linkArray, 0, data, 10, linkArray.length);
/*     */ 
/* 143 */     this.crc.reset();
/* 144 */     this.crc.update(data);
/* 145 */     long checksum = this.crc.getValue();
/*     */ 
/* 147 */     byte[] result = new byte[data.length + 4];
/* 148 */     System.arraycopy(ZipLong.getBytes(checksum), 0, result, 0, 4);
/* 149 */     System.arraycopy(data, 0, result, 4, data.length);
/* 150 */     return result;
/*     */   }
/*     */ 
/*     */   public byte[] getCentralDirectoryData()
/*     */   {
/* 158 */     return getLocalFileDataData();
/*     */   }
/*     */ 
/*     */   public void setUserId(int uid)
/*     */   {
/* 166 */     this.uid = uid;
/*     */   }
/*     */ 
/*     */   public int getUserId()
/*     */   {
/* 174 */     return this.uid;
/*     */   }
/*     */ 
/*     */   public void setGroupId(int gid)
/*     */   {
/* 182 */     this.gid = gid;
/*     */   }
/*     */ 
/*     */   public int getGroupId()
/*     */   {
/* 190 */     return this.gid;
/*     */   }
/*     */ 
/*     */   public void setLinkedFile(String name)
/*     */   {
/* 200 */     this.link = name;
/* 201 */     this.mode = getMode(this.mode);
/*     */   }
/*     */ 
/*     */   public String getLinkedFile()
/*     */   {
/* 211 */     return this.link;
/*     */   }
/*     */ 
/*     */   public boolean isLink()
/*     */   {
/* 219 */     return getLinkedFile().length() != 0;
/*     */   }
/*     */ 
/*     */   public void setMode(int mode)
/*     */   {
/* 227 */     this.mode = getMode(mode);
/*     */   }
/*     */ 
/*     */   public int getMode()
/*     */   {
/* 235 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public void setDirectory(boolean dirFlag)
/*     */   {
/* 243 */     this.dirFlag = dirFlag;
/* 244 */     this.mode = getMode(this.mode);
/*     */   }
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/* 252 */     return (this.dirFlag) && (!isLink());
/*     */   }
/*     */ 
/*     */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 265 */     long givenChecksum = ZipLong.getValue(data, offset);
/* 266 */     byte[] tmp = new byte[length - 4];
/* 267 */     System.arraycopy(data, offset + 4, tmp, 0, length - 4);
/* 268 */     this.crc.reset();
/* 269 */     this.crc.update(tmp);
/* 270 */     long realChecksum = this.crc.getValue();
/* 271 */     if (givenChecksum != realChecksum) {
/* 272 */       throw new ZipException("bad CRC checksum " + 
/* 273 */         Long.toHexString(givenChecksum) + 
/* 274 */         " instead of " + 
/* 275 */         Long.toHexString(realChecksum));
/*     */     }
/*     */ 
/* 278 */     int newMode = ZipShort.getValue(tmp, 0);
/*     */ 
/* 280 */     byte[] linkArray = new byte[(int)ZipLong.getValue(tmp, 2)];
/* 281 */     this.uid = ZipShort.getValue(tmp, 6);
/* 282 */     this.gid = ZipShort.getValue(tmp, 8);
/*     */ 
/* 284 */     if (linkArray.length == 0) {
/* 285 */       this.link = "";
/*     */     } else {
/* 287 */       System.arraycopy(tmp, 10, linkArray, 0, linkArray.length);
/* 288 */       this.link = new String(linkArray);
/*     */     }
/*     */ 
/* 291 */     setDirectory((newMode & 0x4000) != 0);
/* 292 */     setMode(newMode);
/*     */   }
/*     */ 
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 302 */     parseFromLocalFileData(buffer, offset, length);
/*     */   }
/*     */ 
/*     */   protected int getMode(int mode)
/*     */   {
/* 311 */     int type = 32768;
/* 312 */     if (isLink())
/* 313 */       type = 40960;
/* 314 */     else if (isDirectory()) {
/* 315 */       type = 16384;
/*     */     }
/* 317 */     return type | mode & 0xFFF;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try {
/* 323 */       AsiExtraField cloned = (AsiExtraField)super.clone();
/* 324 */       cloned.crc = new CRC32();
/* 325 */       return cloned;
/*     */     }
/*     */     catch (CloneNotSupportedException cnfe) {
/* 328 */       throw new RuntimeException(cnfe);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.AsiExtraField
 * JD-Core Version:    0.6.2
 */