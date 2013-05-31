/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ class FallbackZipEncoding
/*    */   implements ZipEncoding
/*    */ {
/*    */   private final String charset;
/*    */ 
/*    */   public FallbackZipEncoding()
/*    */   {
/* 51 */     this.charset = null;
/*    */   }
/*    */ 
/*    */   public FallbackZipEncoding(String charset)
/*    */   {
/* 61 */     this.charset = charset;
/*    */   }
/*    */ 
/*    */   public boolean canEncode(String name)
/*    */   {
/* 69 */     return true;
/*    */   }
/*    */ 
/*    */   public ByteBuffer encode(String name)
/*    */     throws IOException
/*    */   {
/* 77 */     if (this.charset == null) {
/* 78 */       return ByteBuffer.wrap(name.getBytes());
/*    */     }
/* 80 */     return ByteBuffer.wrap(name.getBytes(this.charset));
/*    */   }
/*    */ 
/*    */   public String decode(byte[] data)
/*    */     throws IOException
/*    */   {
/* 89 */     if (this.charset == null) {
/* 90 */       return new String(data);
/*    */     }
/* 92 */     return new String(data, this.charset);
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.FallbackZipEncoding
 * JD-Core Version:    0.6.2
 */