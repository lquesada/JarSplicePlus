/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ public final class ZipLong
/*     */   implements Cloneable
/*     */ {
/*     */   private static final int BYTE_1 = 1;
/*     */   private static final int BYTE_1_MASK = 65280;
/*     */   private static final int BYTE_1_SHIFT = 8;
/*     */   private static final int BYTE_2 = 2;
/*     */   private static final int BYTE_2_MASK = 16711680;
/*     */   private static final int BYTE_2_SHIFT = 16;
/*     */   private static final int BYTE_3 = 3;
/*     */   private static final long BYTE_3_MASK = 4278190080L;
/*     */   private static final int BYTE_3_SHIFT = 24;
/*     */   private final long value;
/*  47 */   public static final ZipLong CFH_SIG = new ZipLong(33639248L);
/*     */ 
/*  50 */   public static final ZipLong LFH_SIG = new ZipLong(67324752L);
/*     */ 
/*  56 */   public static final ZipLong DD_SIG = new ZipLong(134695760L);
/*     */ 
/*  63 */   static final ZipLong ZIP64_MAGIC = new ZipLong(4294967295L);
/*     */ 
/*     */   public ZipLong(long value)
/*     */   {
/*  70 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public ZipLong(byte[] bytes)
/*     */   {
/*  78 */     this(bytes, 0);
/*     */   }
/*     */ 
/*     */   public ZipLong(byte[] bytes, int offset)
/*     */   {
/*  87 */     this.value = getValue(bytes, offset);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/*  95 */     return getBytes(this.value);
/*     */   }
/*     */ 
/*     */   public long getValue()
/*     */   {
/* 103 */     return this.value;
/*     */   }
/*     */ 
/*     */   public static byte[] getBytes(long value)
/*     */   {
/* 112 */     byte[] result = new byte[4];
/* 113 */     result[0] = ((byte)(int)(value & 0xFF));
/* 114 */     result[1] = ((byte)(int)((value & 0xFF00) >> 8));
/* 115 */     result[2] = ((byte)(int)((value & 0xFF0000) >> 16));
/* 116 */     result[3] = ((byte)(int)((value & 0xFF000000) >> 24));
/* 117 */     return result;
/*     */   }
/*     */ 
/*     */   public static long getValue(byte[] bytes, int offset)
/*     */   {
/* 127 */     long value = bytes[(offset + 3)] << 24 & 0xFF000000;
/* 128 */     value += (bytes[(offset + 2)] << 16 & 0xFF0000);
/* 129 */     value += (bytes[(offset + 1)] << 8 & 0xFF00);
/* 130 */     value += (bytes[offset] & 0xFF);
/* 131 */     return value;
/*     */   }
/*     */ 
/*     */   public static long getValue(byte[] bytes)
/*     */   {
/* 140 */     return getValue(bytes, 0);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 150 */     if ((o == null) || (!(o instanceof ZipLong))) {
/* 151 */       return false;
/*     */     }
/* 153 */     return this.value == ((ZipLong)o).getValue();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 162 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try {
/* 168 */       return super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cnfe) {
/* 171 */       throw new RuntimeException(cnfe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 177 */     return "ZipLong value: " + this.value;
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipLong
 * JD-Core Version:    0.6.2
 */