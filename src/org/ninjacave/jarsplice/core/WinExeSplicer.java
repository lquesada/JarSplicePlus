/*    */ package org.ninjacave.jarsplice.core;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.InputStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.jar.JarOutputStream;
/*    */ import java.util.jar.Manifest;
/*    */ import java.util.zip.ZipEntry;
/*    */ 
/*    */ public class WinExeSplicer extends Splicer
/*    */ {
/* 44 */   String stubFile = "res/stub.exe";
/*    */ 
/*    */   public void createFatJar(String[] jars, String[] natives, String output, String mainClass, String vmArgs) throws Exception {
/* 47 */     this.dirs.clear();
/*    */ 
/* 50 */     FileOutputStream fos = new FileOutputStream(output);
/*    */ 
/* 53 */     InputStream is = getResourceAsStream(this.stubFile);
/*    */     try
/*    */     {
/* 56 */       int read = 0;
/* 57 */       byte[] bytes = new byte[8024];
/*    */ 
/* 59 */       while ((read = is.read(bytes)) != -1)
/* 60 */         fos.write(bytes, 0, read);
/*    */     }
/*    */     finally
/*    */     {
/* 64 */       is.close();
/*    */     }
/*    */ 
/* 67 */     fos.flush();
/*    */ 
/* 69 */     Manifest manifest = getManifest(mainClass, vmArgs);
/* 70 */     JarOutputStream jos = new JarOutputStream(fos, manifest);
/*    */     try
/*    */     {
/* 73 */       addFilesFromJars(jars, jos);
/* 74 */       addNativesToJar(natives, jos);
/* 75 */       addJarSpliceLauncher(jos);
/*    */     } finally {
/* 77 */       jos.close();
/* 78 */       fos.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   protected InputStream getResourceAsStream(String res)
/*    */   {
/* 84 */     return Thread.currentThread().getContextClassLoader().getResourceAsStream(res);
/*    */   }
/*    */ 
/*    */   protected void addNativesToJar(String[] natives, JarOutputStream out) throws Exception
/*    */   {
/* 89 */     for (int i = 0; i < natives.length; i++)
/*    */     {
/* 92 */       if (natives[i].endsWith(".dll"))
/*    */       {
/* 94 */         InputStream in = new FileInputStream(natives[i]);
/*    */ 
/* 97 */         out.putNextEntry(new ZipEntry(getFileName(natives[i])));
/*    */ 
/* 99 */         while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
/* 100 */           out.write(this.buffer, 0, this.bufferSize);
/*    */         }
/*    */ 
/* 103 */         in.close();
/* 104 */         out.closeEntry();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.core.WinExeSplicer
 * JD-Core Version:    0.6.2
 */