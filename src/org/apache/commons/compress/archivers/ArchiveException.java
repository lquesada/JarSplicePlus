/*    */ package org.apache.commons.compress.archivers;
/*    */ 
/*    */ public class ArchiveException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 2772690708123267100L;
/*    */ 
/*    */   public ArchiveException(String message)
/*    */   {
/* 37 */     super(message);
/*    */   }
/*    */ 
/*    */   public ArchiveException(String message, Exception cause)
/*    */   {
/* 49 */     super(message);
/* 50 */     initCause(cause);
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveException
 * JD-Core Version:    0.6.2
 */