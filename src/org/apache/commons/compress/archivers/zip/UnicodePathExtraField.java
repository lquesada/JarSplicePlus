/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ public class UnicodePathExtraField extends AbstractUnicodeExtraField
/*    */ {
/* 40 */   public static final ZipShort UPATH_ID = new ZipShort(28789);
/*    */ 
/*    */   public UnicodePathExtraField()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnicodePathExtraField(String text, byte[] bytes, int off, int len)
/*    */   {
/* 56 */     super(text, bytes, off, len);
/*    */   }
/*    */ 
/*    */   public UnicodePathExtraField(String name, byte[] bytes)
/*    */   {
/* 67 */     super(name, bytes);
/*    */   }
/*    */ 
/*    */   public ZipShort getHeaderId()
/*    */   {
/* 72 */     return UPATH_ID;
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnicodePathExtraField
 * JD-Core Version:    0.6.2
 */