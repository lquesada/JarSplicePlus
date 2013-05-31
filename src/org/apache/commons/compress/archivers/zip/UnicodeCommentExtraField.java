/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ public class UnicodeCommentExtraField extends AbstractUnicodeExtraField
/*    */ {
/* 40 */   public static final ZipShort UCOM_ID = new ZipShort(25461);
/*    */ 
/*    */   public UnicodeCommentExtraField()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnicodeCommentExtraField(String text, byte[] bytes, int off, int len)
/*    */   {
/* 57 */     super(text, bytes, off, len);
/*    */   }
/*    */ 
/*    */   public UnicodeCommentExtraField(String comment, byte[] bytes)
/*    */   {
/* 68 */     super(comment, bytes);
/*    */   }
/*    */ 
/*    */   public ZipShort getHeaderId()
/*    */   {
/* 73 */     return UCOM_ID;
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField
 * JD-Core Version:    0.6.2
 */