/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ public final class ZipShort
/*     */   implements Cloneable
/*     */ {
/*     */   private static final int BYTE_1_MASK = 65280;
/*     */   private static final int BYTE_1_SHIFT = 8;
/*     */   private final int value;
/*     */ 
/*     */   public ZipShort(int value)
/*     */   {
/*  38 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public ZipShort(byte[] bytes)
/*     */   {
/*  46 */     this(bytes, 0);
/*     */   }
/*     */ 
/*     */   public ZipShort(byte[] bytes, int offset)
/*     */   {
/*  55 */     this.value = getValue(bytes, offset);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/*  63 */     byte[] result = new byte[2];
/*  64 */     result[0] = ((byte)(this.value & 0xFF));
/*  65 */     result[1] = ((byte)((this.value & 0xFF00) >> 8));
/*  66 */     return result;
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/*  74 */     return this.value;
/*     */   }
/*     */ 
/*     */   public static byte[] getBytes(int value)
/*     */   {
/*  83 */     byte[] result = new byte[2];
/*  84 */     result[0] = ((byte)(value & 0xFF));
/*  85 */     result[1] = ((byte)((value & 0xFF00) >> 8));
/*  86 */     return result;
/*     */   }
/*     */ 
/*     */   public static int getValue(byte[] bytes, int offset)
/*     */   {
/*  96 */     int value = bytes[(offset + 1)] << 8 & 0xFF00;
/*  97 */     value += (bytes[offset] & 0xFF);
/*  98 */     return value;
/*     */   }
/*     */ 
/*     */   public static int getValue(byte[] bytes)
/*     */   {
/* 107 */     return getValue(bytes, 0);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 117 */     if ((o == null) || (!(o instanceof ZipShort))) {
/* 118 */       return false;
/*     */     }
/* 120 */     return this.value == ((ZipShort)o).getValue();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 129 */     return this.value;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try {
/* 135 */       return super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cnfe) {
/* 138 */       throw new RuntimeException(cnfe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 144 */     return "ZipShort value: " + this.value;
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipShort
 * JD-Core Version:    0.6.2
 */