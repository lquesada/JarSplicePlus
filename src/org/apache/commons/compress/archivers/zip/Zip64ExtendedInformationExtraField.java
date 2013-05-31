/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.zip.ZipException;
/*     */ 
/*     */ public class Zip64ExtendedInformationExtraField
/*     */   implements ZipExtraField
/*     */ {
/*  78 */   static final ZipShort HEADER_ID = new ZipShort(1);
/*     */   private static final String LFH_MUST_HAVE_BOTH_SIZES_MSG = "Zip64 extended information must contain both size values in the local file header.";
/*     */   private ZipEightByteInteger size;
/*     */   private ZipEightByteInteger compressedSize;
/*     */   private ZipEightByteInteger relativeHeaderOffset;
/*     */   private ZipLong diskStart;
/*     */   private byte[] rawCentralDirectoryData;
/*     */ 
/*     */   public Zip64ExtendedInformationExtraField()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize)
/*     */   {
/* 115 */     this(size, compressedSize, null, null);
/*     */   }
/*     */ 
/*     */   public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize, ZipEightByteInteger relativeHeaderOffset, ZipLong diskStart)
/*     */   {
/* 130 */     this.size = size;
/* 131 */     this.compressedSize = compressedSize;
/* 132 */     this.relativeHeaderOffset = relativeHeaderOffset;
/* 133 */     this.diskStart = diskStart;
/*     */   }
/*     */ 
/*     */   public ZipShort getHeaderId()
/*     */   {
/* 138 */     return HEADER_ID;
/*     */   }
/*     */ 
/*     */   public ZipShort getLocalFileDataLength()
/*     */   {
/* 143 */     return new ZipShort(this.size != null ? 16 : 0);
/*     */   }
/*     */ 
/*     */   public ZipShort getCentralDirectoryLength()
/*     */   {
/* 148 */     return new ZipShort((this.size != null ? 8 : 0) + (
/* 149 */       this.compressedSize != null ? 8 : 0) + (
/* 150 */       this.relativeHeaderOffset != null ? 8 : 0) + (
/* 151 */       this.diskStart != null ? 4 : 0));
/*     */   }
/*     */ 
/*     */   public byte[] getLocalFileDataData()
/*     */   {
/* 156 */     if ((this.size != null) || (this.compressedSize != null)) {
/* 157 */       if ((this.size == null) || (this.compressedSize == null)) {
/* 158 */         throw new IllegalArgumentException("Zip64 extended information must contain both size values in the local file header.");
/*     */       }
/* 160 */       byte[] data = new byte[16];
/* 161 */       addSizes(data);
/* 162 */       return data;
/*     */     }
/* 164 */     return new byte[0];
/*     */   }
/*     */ 
/*     */   public byte[] getCentralDirectoryData()
/*     */   {
/* 169 */     byte[] data = new byte[getCentralDirectoryLength().getValue()];
/* 170 */     int off = addSizes(data);
/* 171 */     if (this.relativeHeaderOffset != null) {
/* 172 */       System.arraycopy(this.relativeHeaderOffset.getBytes(), 0, data, off, 8);
/* 173 */       off += 8;
/*     */     }
/* 175 */     if (this.diskStart != null) {
/* 176 */       System.arraycopy(this.diskStart.getBytes(), 0, data, off, 4);
/* 177 */       off += 4;
/*     */     }
/* 179 */     return data;
/*     */   }
/*     */ 
/*     */   public void parseFromLocalFileData(byte[] buffer, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 185 */     if (length == 0)
/*     */     {
/* 190 */       return;
/*     */     }
/* 192 */     if (length < 16) {
/* 193 */       throw new ZipException("Zip64 extended information must contain both size values in the local file header.");
/*     */     }
/* 195 */     this.size = new ZipEightByteInteger(buffer, offset);
/* 196 */     offset += 8;
/* 197 */     this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 198 */     offset += 8;
/* 199 */     int remaining = length - 16;
/* 200 */     if (remaining >= 8) {
/* 201 */       this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 202 */       offset += 8;
/* 203 */       remaining -= 8;
/*     */     }
/* 205 */     if (remaining >= 4) {
/* 206 */       this.diskStart = new ZipLong(buffer, offset);
/* 207 */       offset += 4;
/* 208 */       remaining -= 4;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*     */     throws ZipException
/*     */   {
/* 217 */     this.rawCentralDirectoryData = new byte[length];
/* 218 */     System.arraycopy(buffer, offset, this.rawCentralDirectoryData, 0, length);
/*     */ 
/* 226 */     if (length >= 28) {
/* 227 */       parseFromLocalFileData(buffer, offset, length);
/* 228 */     } else if (length == 24) {
/* 229 */       this.size = new ZipEightByteInteger(buffer, offset);
/* 230 */       offset += 8;
/* 231 */       this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 232 */       offset += 8;
/* 233 */       this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 234 */     } else if (length % 8 == 4) {
/* 235 */       this.diskStart = new ZipLong(buffer, offset + length - 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reparseCentralDirectoryData(boolean hasUncompressedSize, boolean hasCompressedSize, boolean hasRelativeHeaderOffset, boolean hasDiskStart)
/*     */     throws ZipException
/*     */   {
/* 253 */     if (this.rawCentralDirectoryData != null) {
/* 254 */       int expectedLength = (hasUncompressedSize ? 8 : 0) + (
/* 255 */         hasCompressedSize ? 8 : 0) + (
/* 256 */         hasRelativeHeaderOffset ? 8 : 0) + (
/* 257 */         hasDiskStart ? 4 : 0);
/* 258 */       if (this.rawCentralDirectoryData.length != expectedLength) {
/* 259 */         throw new ZipException("central directory zip64 extended information extra field's length doesn't match central directory data.  Expected length " + 
/* 263 */           expectedLength + " but is " + 
/* 264 */           this.rawCentralDirectoryData.length);
/*     */       }
/* 266 */       int offset = 0;
/* 267 */       if (hasUncompressedSize) {
/* 268 */         this.size = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 269 */         offset += 8;
/*     */       }
/* 271 */       if (hasCompressedSize) {
/* 272 */         this.compressedSize = new ZipEightByteInteger(this.rawCentralDirectoryData, 
/* 273 */           offset);
/* 274 */         offset += 8;
/*     */       }
/* 276 */       if (hasRelativeHeaderOffset) {
/* 277 */         this.relativeHeaderOffset = 
/* 278 */           new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 279 */         offset += 8;
/*     */       }
/* 281 */       if (hasDiskStart) {
/* 282 */         this.diskStart = new ZipLong(this.rawCentralDirectoryData, offset);
/* 283 */         offset += 4;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ZipEightByteInteger getSize()
/*     */   {
/* 292 */     return this.size;
/*     */   }
/*     */ 
/*     */   public void setSize(ZipEightByteInteger size)
/*     */   {
/* 299 */     this.size = size;
/*     */   }
/*     */ 
/*     */   public ZipEightByteInteger getCompressedSize()
/*     */   {
/* 306 */     return this.compressedSize;
/*     */   }
/*     */ 
/*     */   public void setCompressedSize(ZipEightByteInteger compressedSize)
/*     */   {
/* 313 */     this.compressedSize = compressedSize;
/*     */   }
/*     */ 
/*     */   public ZipEightByteInteger getRelativeHeaderOffset()
/*     */   {
/* 320 */     return this.relativeHeaderOffset;
/*     */   }
/*     */ 
/*     */   public void setRelativeHeaderOffset(ZipEightByteInteger rho)
/*     */   {
/* 327 */     this.relativeHeaderOffset = rho;
/*     */   }
/*     */ 
/*     */   public ZipLong getDiskStartNumber()
/*     */   {
/* 334 */     return this.diskStart;
/*     */   }
/*     */ 
/*     */   public void setDiskStartNumber(ZipLong ds)
/*     */   {
/* 341 */     this.diskStart = ds;
/*     */   }
/*     */ 
/*     */   private int addSizes(byte[] data) {
/* 345 */     int off = 0;
/* 346 */     if (this.size != null) {
/* 347 */       System.arraycopy(this.size.getBytes(), 0, data, 0, 8);
/* 348 */       off += 8;
/*     */     }
/* 350 */     if (this.compressedSize != null) {
/* 351 */       System.arraycopy(this.compressedSize.getBytes(), 0, data, off, 8);
/* 352 */       off += 8;
/*     */     }
/* 354 */     return off;
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField
 * JD-Core Version:    0.6.2
 */