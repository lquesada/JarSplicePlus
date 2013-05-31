/*    */ package org.ninjacave.jarsplice.core;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.jar.JarOutputStream;
/*    */ import java.util.jar.Manifest;
/*    */ import java.util.zip.ZipEntry;
/*    */ 
/*    */ public class ShellScriptSplicer extends Splicer
/*    */ {
/* 49 */   String[] batchFile = { "#!/bin/sh", "FNAME=\"`readlink -f \"$0\"`\"", "java -jar \"$FNAME\"", "exit 0", "" };
/*    */ 
/*    */   public void createFatJar(String[] jars, String[] natives, String output, String mainClass, String vmArgs)
/*    */     throws Exception
/*    */   {
/* 56 */     this.dirs.clear();
/*    */ 
/* 59 */     FileOutputStream fos = new FileOutputStream(output);
/*    */ 
/* 61 */     PrintStream pos = new PrintStream(fos);
/*    */ 
/* 63 */     for (int i = 0; i < this.batchFile.length; i++) {
/* 64 */       pos.println(this.batchFile[i]);
/*    */     }
/*    */ 
/* 67 */     pos.flush();
/* 68 */     fos.flush();
/*    */ 
/* 70 */     Manifest manifest = getManifest(mainClass, vmArgs);
/* 71 */     JarOutputStream jos = new JarOutputStream(fos, manifest);
/*    */     try
/*    */     {
/* 74 */       addFilesFromJars(jars, jos);
/* 75 */       addNativesToJar(natives, jos);
/* 76 */       addJarSpliceLauncher(jos);
/*    */     } finally {
/* 78 */       jos.close();
/* 79 */       pos.close();
/* 80 */       fos.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void addNativesToJar(String[] natives, JarOutputStream out)
/*    */     throws Exception
/*    */   {
/* 87 */     for (int i = 0; i < natives.length; i++)
/*    */     {
/* 90 */       if (natives[i].endsWith(".so"))
/*    */       {
/* 92 */         InputStream in = new FileInputStream(natives[i]);
/*    */ 
/* 95 */         out.putNextEntry(new ZipEntry(getFileName(natives[i])));
/*    */ 
/* 97 */         while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
/* 98 */           out.write(this.buffer, 0, this.bufferSize);
/*    */         }
/*    */ 
/* 101 */         in.close();
/* 102 */         out.closeEntry();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.core.ShellScriptSplicer
 * JD-Core Version:    0.6.2
 */