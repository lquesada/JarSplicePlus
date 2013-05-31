/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ 
/*     */ class NioZipEncoding
/*     */   implements ZipEncoding
/*     */ {
/*     */   private final Charset charset;
/*     */ 
/*     */   public NioZipEncoding(Charset charset)
/*     */   {
/*  51 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   public boolean canEncode(String name)
/*     */   {
/*  59 */     CharsetEncoder enc = this.charset.newEncoder();
/*  60 */     enc.onMalformedInput(CodingErrorAction.REPORT);
/*  61 */     enc.onUnmappableCharacter(CodingErrorAction.REPORT);
/*     */ 
/*  63 */     return enc.canEncode(name);
/*     */   }
/*     */ 
/*     */   public ByteBuffer encode(String name)
/*     */   {
/*  71 */     CharsetEncoder enc = this.charset.newEncoder();
/*     */ 
/*  73 */     enc.onMalformedInput(CodingErrorAction.REPORT);
/*  74 */     enc.onUnmappableCharacter(CodingErrorAction.REPORT);
/*     */ 
/*  76 */     CharBuffer cb = CharBuffer.wrap(name);
/*  77 */     ByteBuffer out = ByteBuffer.allocate(name.length() + 
/*  78 */       (name.length() + 1) / 2);
/*     */ 
/*  80 */     while (cb.remaining() > 0) {
/*  81 */       CoderResult res = enc.encode(cb, out, true);
/*     */ 
/*  83 */       if ((res.isUnmappable()) || (res.isMalformed()))
/*     */       {
/*  87 */         if (res.length() * 6 > out.remaining()) {
/*  88 */           out = ZipEncodingHelper.growBuffer(out, out.position() + 
/*  89 */             res.length() * 6);
/*     */         }
/*     */ 
/*  92 */         for (int i = 0; i < res.length(); i++) {
/*  93 */           ZipEncodingHelper.appendSurrogate(out, cb.get());
/*     */         }
/*     */       }
/*  96 */       else if (res.isOverflow())
/*     */       {
/*  98 */         out = ZipEncodingHelper.growBuffer(out, 0);
/*     */       }
/* 100 */       else if (res.isUnderflow())
/*     */       {
/* 102 */         enc.flush(out);
/* 103 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 108 */     out.limit(out.position());
/* 109 */     out.rewind();
/* 110 */     return out;
/*     */   }
/*     */ 
/*     */   public String decode(byte[] data)
/*     */     throws IOException
/*     */   {
/* 118 */     return this.charset.newDecoder()
/* 119 */       .onMalformedInput(CodingErrorAction.REPORT)
/* 120 */       .onUnmappableCharacter(CodingErrorAction.REPORT)
/* 121 */       .decode(ByteBuffer.wrap(data)).toString();
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.NioZipEncoding
 * JD-Core Version:    0.6.2
 */