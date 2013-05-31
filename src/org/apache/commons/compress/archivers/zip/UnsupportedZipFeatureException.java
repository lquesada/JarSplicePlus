/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.util.zip.ZipException;
/*    */ 
/*    */ public class UnsupportedZipFeatureException extends ZipException
/*    */ {
/*    */   private final Feature reason;
/*    */   private final ZipArchiveEntry entry;
/*    */   private static final long serialVersionUID = 4430521921766595597L;
/*    */ 
/*    */   public UnsupportedZipFeatureException(Feature reason, ZipArchiveEntry entry)
/*    */   {
/* 42 */     super("unsupported feature " + reason + " used in entry " + 
/* 42 */       entry.getName());
/* 43 */     this.reason = reason;
/* 44 */     this.entry = entry;
/*    */   }
/*    */ 
/*    */   public Feature getFeature()
/*    */   {
/* 51 */     return this.reason;
/*    */   }
/*    */ 
/*    */   public ZipArchiveEntry getEntry()
/*    */   {
/* 58 */     return this.entry;
/*    */   }
/*    */ 
/*    */   public static class Feature
/*    */   {
/* 69 */     public static final Feature ENCRYPTION = new Feature("encryption");
/*    */ 
/* 73 */     public static final Feature METHOD = new Feature("compression method");
/*    */ 
/* 77 */     public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");
/*    */     private final String name;
/*    */ 
/*    */     private Feature(String name)
/*    */     {
/* 82 */       this.name = name;
/*    */     }
/*    */ 
/*    */     public String toString()
/*    */     {
/* 87 */       return this.name;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException
 * JD-Core Version:    0.6.2
 */