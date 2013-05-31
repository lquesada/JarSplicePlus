/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ class Simple8BitZipEncoding
/*     */   implements ZipEncoding
/*     */ {
/*     */   private final char[] highChars;
/*     */   private final List<Simple8BitChar> reverseMapping;
/*     */ 
/*     */   public Simple8BitZipEncoding(char[] highChars)
/*     */   {
/* 105 */     this.highChars = ((char[])highChars.clone());
/* 106 */     List temp = 
/* 107 */       new ArrayList(this.highChars.length);
/*     */ 
/* 109 */     byte code = 127;
/*     */ 
/* 111 */     for (int i = 0; i < this.highChars.length; i++) {
/* 112 */       temp.add(new Simple8BitChar(code = (byte)(code + 1), this.highChars[i]));
/*     */     }
/*     */ 
/* 115 */     Collections.sort(temp);
/* 116 */     this.reverseMapping = Collections.unmodifiableList(temp);
/*     */   }
/*     */ 
/*     */   public char decodeByte(byte b)
/*     */   {
/* 127 */     if (b >= 0) {
/* 128 */       return (char)b;
/*     */     }
/*     */ 
/* 132 */     return this.highChars[(128 + b)];
/*     */   }
/*     */ 
/*     */   public boolean canEncodeChar(char c)
/*     */   {
/* 141 */     if ((c >= 0) && (c < '')) {
/* 142 */       return true;
/*     */     }
/*     */ 
/* 145 */     Simple8BitChar r = encodeHighChar(c);
/* 146 */     return r != null;
/*     */   }
/*     */ 
/*     */   public boolean pushEncodedChar(ByteBuffer bb, char c)
/*     */   {
/* 160 */     if ((c >= 0) && (c < '')) {
/* 161 */       bb.put((byte)c);
/* 162 */       return true;
/*     */     }
/*     */ 
/* 165 */     Simple8BitChar r = encodeHighChar(c);
/* 166 */     if (r == null) {
/* 167 */       return false;
/*     */     }
/* 169 */     bb.put(r.code);
/* 170 */     return true;
/*     */   }
/*     */ 
/*     */   private Simple8BitChar encodeHighChar(char c)
/*     */   {
/* 182 */     int i0 = 0;
/* 183 */     int i1 = this.reverseMapping.size();
/*     */ 
/* 185 */     while (i1 > i0)
/*     */     {
/* 187 */       int i = i0 + (i1 - i0) / 2;
/*     */ 
/* 189 */       Simple8BitChar m = (Simple8BitChar)this.reverseMapping.get(i);
/*     */ 
/* 191 */       if (m.unicode == c) {
/* 192 */         return m;
/*     */       }
/*     */ 
/* 195 */       if (m.unicode < c)
/* 196 */         i0 = i + 1;
/*     */       else {
/* 198 */         i1 = i;
/*     */       }
/*     */     }
/*     */ 
/* 202 */     if (i0 >= this.reverseMapping.size()) {
/* 203 */       return null;
/*     */     }
/*     */ 
/* 206 */     Simple8BitChar r = (Simple8BitChar)this.reverseMapping.get(i0);
/*     */ 
/* 208 */     if (r.unicode != c) {
/* 209 */       return null;
/*     */     }
/*     */ 
/* 212 */     return r;
/*     */   }
/*     */ 
/*     */   public boolean canEncode(String name)
/*     */   {
/* 221 */     for (int i = 0; i < name.length(); i++)
/*     */     {
/* 223 */       char c = name.charAt(i);
/*     */ 
/* 225 */       if (!canEncodeChar(c)) {
/* 226 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 230 */     return true;
/*     */   }
/*     */ 
/*     */   public ByteBuffer encode(String name)
/*     */   {
/* 238 */     ByteBuffer out = ByteBuffer.allocate(name.length() + 
/* 239 */       6 + (name.length() + 1) / 2);
/*     */ 
/* 241 */     for (int i = 0; i < name.length(); i++)
/*     */     {
/* 243 */       char c = name.charAt(i);
/*     */ 
/* 245 */       if (out.remaining() < 6) {
/* 246 */         out = ZipEncodingHelper.growBuffer(out, out.position() + 6);
/*     */       }
/*     */ 
/* 249 */       if (!pushEncodedChar(out, c))
/*     */       {
/* 251 */         ZipEncodingHelper.appendSurrogate(out, c);
/*     */       }
/*     */     }
/*     */ 
/* 255 */     out.limit(out.position());
/* 256 */     out.rewind();
/* 257 */     return out;
/*     */   }
/*     */ 
/*     */   public String decode(byte[] data)
/*     */     throws IOException
/*     */   {
/* 265 */     char[] ret = new char[data.length];
/*     */ 
/* 267 */     for (int i = 0; i < data.length; i++) {
/* 268 */       ret[i] = decodeByte(data[i]);
/*     */     }
/*     */ 
/* 271 */     return new String(ret);
/*     */   }
/*     */ 
/*     */   private static final class Simple8BitChar
/*     */     implements Comparable<Simple8BitChar>
/*     */   {
/*     */     public final char unicode;
/*     */     public final byte code;
/*     */ 
/*     */     Simple8BitChar(byte code, char unicode)
/*     */     {
/*  58 */       this.code = code;
/*  59 */       this.unicode = unicode;
/*     */     }
/*     */ 
/*     */     public int compareTo(Simple8BitChar a) {
/*  63 */       return this.unicode - a.unicode;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  68 */       return "0x" + Integer.toHexString(0xFFFF & this.unicode) + 
/*  69 */         "->0x" + Integer.toHexString(0xFF & this.code);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/*  74 */       if ((o instanceof Simple8BitChar)) {
/*  75 */         Simple8BitChar other = (Simple8BitChar)o;
/*  76 */         return (this.unicode == other.unicode) && (this.code == other.code);
/*     */       }
/*  78 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  83 */       return this.unicode;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.Simple8BitZipEncoding
 * JD-Core Version:    0.6.2
 */