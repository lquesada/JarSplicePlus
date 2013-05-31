/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public final class ZipEightByteInteger
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
/*     */   private static final int BYTE_4 = 4;
/*     */   private static final long BYTE_4_MASK = 1095216660480L;
/*     */   private static final int BYTE_4_SHIFT = 32;
/*     */   private static final int BYTE_5 = 5;
/*     */   private static final long BYTE_5_MASK = 280375465082880L;
/*     */   private static final int BYTE_5_SHIFT = 40;
/*     */   private static final int BYTE_6 = 6;
/*     */   private static final long BYTE_6_MASK = 71776119061217280L;
/*     */   private static final int BYTE_6_SHIFT = 48;
/*     */   private static final int BYTE_7 = 7;
/*     */   private static final long BYTE_7_MASK = 9151314442816847872L;
/*     */   private static final int BYTE_7_SHIFT = 56;
/*     */   private static final int LEFTMOST_BIT_SHIFT = 63;
/*     */   private static final byte LEFTMOST_BIT = -128;
/*     */   private final BigInteger value;
/*  66 */   public static final ZipEightByteInteger ZERO = new ZipEightByteInteger(0L);
/*     */ 
/*     */   public ZipEightByteInteger(long value)
/*     */   {
/*  73 */     this(BigInteger.valueOf(value));
/*     */   }
/*     */ 
/*     */   public ZipEightByteInteger(BigInteger value)
/*     */   {
/*  81 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public ZipEightByteInteger(byte[] bytes)
/*     */   {
/*  89 */     this(bytes, 0);
/*     */   }
/*     */ 
/*     */   public ZipEightByteInteger(byte[] bytes, int offset)
/*     */   {
/*  98 */     this.value = getValue(bytes, offset);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/* 106 */     return getBytes(this.value);
/*     */   }
/*     */ 
/*     */   public long getLongValue()
/*     */   {
/* 114 */     return this.value.longValue();
/*     */   }
/*     */ 
/*     */   public BigInteger getValue()
/*     */   {
/* 122 */     return this.value;
/*     */   }
/*     */ 
/*     */   public static byte[] getBytes(long value)
/*     */   {
/* 131 */     return getBytes(BigInteger.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static byte[] getBytes(BigInteger value)
/*     */   {
/* 140 */     byte[] result = new byte[8];
/* 141 */     long val = value.longValue();
/* 142 */     result[0] = ((byte)(int)(val & 0xFF));
/* 143 */     result[1] = ((byte)(int)((val & 0xFF00) >> 8));
/* 144 */     result[2] = ((byte)(int)((val & 0xFF0000) >> 16));
/* 145 */     result[3] = ((byte)(int)((val & 0xFF000000) >> 24));
/* 146 */     result[4] = ((byte)(int)((val & 0x0) >> 32));
/* 147 */     result[5] = ((byte)(int)((val & 0x0) >> 40));
/* 148 */     result[6] = ((byte)(int)((val & 0x0) >> 48));
/* 149 */     result[7] = ((byte)(int)((val & 0x0) >> 56));
/* 150 */     if (value.testBit(63))
/*     */     {
/*     */       byte[] tmp125_122 = result; tmp125_122[7] = ((byte)(tmp125_122[7] | 0xFFFFFF80));
/*     */     }
/* 153 */     return result;
/*     */   }
/*     */ 
/*     */   public static long getLongValue(byte[] bytes, int offset)
/*     */   {
/* 164 */     return getValue(bytes, offset).longValue();
/*     */   }
/*     */ 
/*     */   public static BigInteger getValue(byte[] bytes, int offset)
/*     */   {
/* 175 */     long value = bytes[(offset + 7)] << 56 & 0x0;
/* 176 */     value += (bytes[(offset + 6)] << 48 & 0x0);
/* 177 */     value += (bytes[(offset + 5)] << 40 & 0x0);
/* 178 */     value += (bytes[(offset + 4)] << 32 & 0x0);
/* 179 */     value += (bytes[(offset + 3)] << 24 & 0xFF000000);
/* 180 */     value += (bytes[(offset + 2)] << 16 & 0xFF0000);
/* 181 */     value += (bytes[(offset + 1)] << 8 & 0xFF00);
/* 182 */     value += (bytes[offset] & 0xFF);
/* 183 */     BigInteger val = BigInteger.valueOf(value);
/* 184 */     return (bytes[(offset + 7)] & 0xFFFFFF80) == -128 ? 
/* 185 */       val.setBit(63) : val;
/*     */   }
/*     */ 
/*     */   public static long getLongValue(byte[] bytes)
/*     */   {
/* 194 */     return getLongValue(bytes, 0);
/*     */   }
/*     */ 
/*     */   public static BigInteger getValue(byte[] bytes)
/*     */   {
/* 203 */     return getValue(bytes, 0);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 213 */     if ((o == null) || (!(o instanceof ZipEightByteInteger))) {
/* 214 */       return false;
/*     */     }
/* 216 */     return this.value.equals(((ZipEightByteInteger)o).getValue());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 225 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 230 */     return "ZipEightByteInteger value: " + this.value;
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipEightByteInteger
 * JD-Core Version:    0.6.2
 */