/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.util.zip.ZipException;
/*    */ 
/*    */ public class Zip64RequiredException extends ZipException
/*    */ {
/*    */   private static final long serialVersionUID = 20110809L;
/*    */   static final String ARCHIVE_TOO_BIG_MESSAGE = "archive's size exceeds the limit of 4GByte.";
/*    */   static final String TOO_MANY_ENTRIES_MESSAGE = "archive contains more than 65535 entries.";
/*    */ 
/*    */   static String getEntryTooBigMessage(ZipArchiveEntry ze)
/*    */   {
/* 37 */     return ze.getName() + "'s size exceeds the limit of 4GByte.";
/*    */   }
/*    */ 
/*    */   public Zip64RequiredException(String reason)
/*    */   {
/* 47 */     super(reason);
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.Zip64RequiredException
 * JD-Core Version:    0.6.2
 */