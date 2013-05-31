/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class ZipEncodingHelper
/*     */ {
/*  68 */   private static final Map<String, SimpleEncodingHolder> simpleEncodings = new HashMap();
/*     */ 
/* 164 */   private static final byte[] HEX_DIGITS = { 
/* 165 */     48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 
/* 166 */     66, 67, 68, 69, 70 };
/*     */   static final String UTF8 = "UTF8";
/*     */   private static final String UTF_DASH_8 = "UTF-8";
/* 201 */   static final ZipEncoding UTF8_ZIP_ENCODING = new FallbackZipEncoding("UTF8");
/*     */ 
/*     */   static
/*     */   {
/*  70 */     char[] cp437_high_chars = 
/*  71 */       { 'Ç', 'ü', 'é', 'â', 'ä', 'à', 
/*  72 */       'å', 'ç', 'ê', 'ë', 'è', 'ï', 
/*  73 */       'î', 'ì', 'Ä', 'Å', 'É', 'æ', 
/*  74 */       'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 
/*  75 */       'ÿ', 'Ö', 'Ü', '¢', '£', '¥', 
/*  76 */       '₧', 'ƒ', 'á', 'í', 'ó', 'ú', 
/*  77 */       'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', 
/*  78 */       '¬', '½', '¼', '¡', '«', '»', 
/*  79 */       '░', '▒', '▓', '│', '┤', '╡', 
/*  80 */       '╢', '╖', '╕', '╣', '║', '╗', 
/*  81 */       '╝', '╜', '╛', '┐', '└', '┴', 
/*  82 */       '┬', '├', '─', '┼', '╞', '╟', 
/*  83 */       '╚', '╔', '╩', '╦', '╠', '═', 
/*  84 */       '╬', '╧', '╨', '╤', '╥', '╙', 
/*  85 */       '╘', '╒', '╓', '╫', '╪', '┘', 
/*  86 */       '┌', '█', '▄', '▌', '▐', '▀', 
/*  87 */       'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 
/*  88 */       'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', 
/*  89 */       '∞', 'φ', 'ε', '∩', '≡', '±', 
/*  90 */       '≥', '≤', '⌠', '⌡', '÷', '≈', 
/*  91 */       '°', '∙', '·', '√', 'ⁿ', '²', 
/*  92 */       '■', ' ' };
/*     */ 
/*  94 */     SimpleEncodingHolder cp437 = new SimpleEncodingHolder(cp437_high_chars);
/*     */ 
/*  96 */     simpleEncodings.put("CP437", cp437);
/*  97 */     simpleEncodings.put("Cp437", cp437);
/*  98 */     simpleEncodings.put("cp437", cp437);
/*  99 */     simpleEncodings.put("IBM437", cp437);
/* 100 */     simpleEncodings.put("ibm437", cp437);
/*     */ 
/* 102 */     char[] cp850_high_chars = 
/* 103 */       { 'Ç', 'ü', 'é', 'â', 'ä', 'à', 
/* 104 */       'å', 'ç', 'ê', 'ë', 'è', 'ï', 
/* 105 */       'î', 'ì', 'Ä', 'Å', 'É', 'æ', 
/* 106 */       'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 
/* 107 */       'ÿ', 'Ö', 'Ü', 'ø', '£', 'Ø', 
/* 108 */       '×', 'ƒ', 'á', 'í', 'ó', 'ú', 
/* 109 */       'ñ', 'Ñ', 'ª', 'º', '¿', '®', 
/* 110 */       '¬', '½', '¼', '¡', '«', '»', 
/* 111 */       '░', '▒', '▓', '│', '┤', 'Á', 
/* 112 */       'Â', 'À', '©', '╣', '║', '╗', 
/* 113 */       '╝', '¢', '¥', '┐', '└', '┴', 
/* 114 */       '┬', '├', '─', '┼', 'ã', 'Ã', 
/* 115 */       '╚', '╔', '╩', '╦', '╠', '═', 
/* 116 */       '╬', '¤', 'ð', 'Ð', 'Ê', 'Ë', 
/* 117 */       'È', 'ı', 'Í', 'Î', 'Ï', '┘', 
/* 118 */       '┌', '█', '▄', '¦', 'Ì', '▀', 
/* 119 */       'Ó', 'ß', 'Ô', 'Ò', 'õ', 'Õ', 
/* 120 */       'µ', 'þ', 'Þ', 'Ú', 'Û', 'Ù', 
/* 121 */       'ý', 'Ý', '¯', '´', '­', '±', 
/* 122 */       '‗', '¾', '¶', '§', '÷', '¸', 
/* 123 */       '°', '¨', '·', '¹', '³', '²', 
/* 124 */       '■', ' ' };
/*     */ 
/* 126 */     SimpleEncodingHolder cp850 = new SimpleEncodingHolder(cp850_high_chars);
/*     */ 
/* 128 */     simpleEncodings.put("CP850", cp850);
/* 129 */     simpleEncodings.put("Cp850", cp850);
/* 130 */     simpleEncodings.put("cp850", cp850);
/* 131 */     simpleEncodings.put("IBM850", cp850);
/* 132 */     simpleEncodings.put("ibm850", cp850);
/*     */   }
/*     */ 
/*     */   static ByteBuffer growBuffer(ByteBuffer b, int newCapacity)
/*     */   {
/* 148 */     b.limit(b.position());
/* 149 */     b.rewind();
/*     */ 
/* 151 */     int c2 = b.capacity() * 2;
/* 152 */     ByteBuffer on = ByteBuffer.allocate(c2 < newCapacity ? newCapacity : c2);
/*     */ 
/* 154 */     on.put(b);
/* 155 */     return on;
/*     */   }
/*     */ 
/*     */   static void appendSurrogate(ByteBuffer bb, char c)
/*     */   {
/* 178 */     bb.put((byte)37);
/* 179 */     bb.put((byte)85);
/*     */ 
/* 181 */     bb.put(HEX_DIGITS[(c >> '\f' & 0xF)]);
/* 182 */     bb.put(HEX_DIGITS[(c >> '\b' & 0xF)]);
/* 183 */     bb.put(HEX_DIGITS[(c >> '\004' & 0xF)]);
/* 184 */     bb.put(HEX_DIGITS[(c & 0xF)]);
/*     */   }
/*     */ 
/*     */   public static ZipEncoding getZipEncoding(String name)
/*     */   {
/* 213 */     if (isUTF8(name)) {
/* 214 */       return UTF8_ZIP_ENCODING;
/*     */     }
/*     */ 
/* 217 */     if (name == null) {
/* 218 */       return new FallbackZipEncoding();
/*     */     }
/*     */ 
/* 221 */     SimpleEncodingHolder h = (SimpleEncodingHolder)simpleEncodings.get(name);
/*     */ 
/* 223 */     if (h != null) {
/* 224 */       return h.getEncoding();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 229 */       Charset cs = Charset.forName(name);
/* 230 */       return new NioZipEncoding(cs);
/*     */     } catch (UnsupportedCharsetException e) {
/*     */     }
/* 233 */     return new FallbackZipEncoding(name);
/*     */   }
/*     */ 
/*     */   static boolean isUTF8(String encoding)
/*     */   {
/* 242 */     if (encoding == null)
/*     */     {
/* 244 */       encoding = System.getProperty("file.encoding");
/*     */     }
/*     */ 
/* 247 */     return ("UTF8".equalsIgnoreCase(encoding)) || 
/* 247 */       ("UTF-8".equalsIgnoreCase(encoding));
/*     */   }
/*     */ 
/*     */   private static class SimpleEncodingHolder
/*     */   {
/*     */     private final char[] highChars;
/*     */     private Simple8BitZipEncoding encoding;
/*     */ 
/*     */     SimpleEncodingHolder(char[] highChars)
/*     */     {
/*  50 */       this.highChars = highChars;
/*     */     }
/*     */ 
/*     */     public synchronized Simple8BitZipEncoding getEncoding()
/*     */     {
/*  58 */       if (this.encoding == null) {
/*  59 */         this.encoding = new Simple8BitZipEncoding(this.highChars);
/*     */       }
/*  61 */       return this.encoding;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipEncodingHelper
 * JD-Core Version:    0.6.2
 */