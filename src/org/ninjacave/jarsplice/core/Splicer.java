/*     */ package org.ninjacave.jarsplice.core;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.ninjacave.jarsplice.JarSplice;
/*     */ 
/*     */ public class Splicer
/*     */ {
/*  50 */   ArrayList<String> dirs = new ArrayList();
/*     */   int bufferSize;
/*  53 */   byte[] buffer = new byte[4096];
/*     */ 
/*     */   public void createFatJar(String[] jars, String[] natives, String output, String mainClass, String vmArgs) throws Exception {
/*  56 */     this.dirs.clear();
/*     */ 
/*  58 */     Manifest manifest = getManifest(mainClass, vmArgs);
/*     */ 
/*  61 */     FileOutputStream fos = new FileOutputStream(output);
/*  62 */     JarOutputStream jos = new JarOutputStream(fos, manifest);
/*     */     try
/*     */     {
/*  65 */       addFilesFromJars(jars, jos);
/*  66 */       addNativesToJar(natives, jos);
/*  67 */       addJarSpliceLauncher(jos);
/*     */     }
/*     */     finally {
/*  70 */       jos.close();
/*  71 */       fos.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Manifest getManifest(String mainClass, String vmArgs) {
/*  76 */     Manifest manifest = new Manifest();
/*  77 */     Attributes attribute = manifest.getMainAttributes();
/*  78 */     attribute.putValue("Manifest-Version", "1.0");
/*  79 */     attribute.putValue("Main-Class", "org.ninjacave.jarsplice.JarSpliceLauncher");
/*  80 */     attribute.putValue("Launcher-Main-Class", mainClass);
/*  81 */     attribute.putValue("Launcher-VM-Args", vmArgs);
/*     */ 
/*  83 */     return manifest;
/*     */   }
/*     */ 
/*     */   protected void addFilesFromJars(String[] jars, JarOutputStream out) throws Exception
/*     */   {
/*  88 */     for (int i = 0; i < jars.length; i++)
/*     */     {
/*  91 */       ZipFile jarFile = new ZipFile(jars[i]);
/*     */ 
/*  94 */       Enumeration entities = jarFile.entries();
/*     */ 
/*  96 */       while (entities.hasMoreElements()) {
/*  97 */         ZipEntry entry = (ZipEntry)entities.nextElement();
/*     */ 
/*  99 */         if (entry.isDirectory()) {
/* 100 */           if (!this.dirs.contains(entry.getName()))
/*     */           {
/* 103 */             this.dirs.add(entry.getName());
/*     */           }
/*     */         }
/* 106 */         else if (!entry.getName().toLowerCase().startsWith("meta-inf"))
/*     */         {
/* 111 */           if (!entry.getName().toLowerCase().contains("JarSpliceLauncher"))
/*     */           {
/* 115 */             InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
/*     */ 
/* 118 */             out.putNextEntry(new ZipEntry(entry.getName()));
/*     */ 
/* 120 */             while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
/* 121 */               out.write(this.buffer, 0, this.bufferSize);
/*     */             }
/*     */ 
/* 124 */             in.close();
/* 125 */             out.closeEntry();
/*     */           }
/*     */         }
/*     */       }
/* 128 */       jarFile.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addNativesToJar(String[] natives, JarOutputStream out) throws Exception
/*     */   {
/* 134 */     for (int i = 0; i < natives.length; i++) {
/* 135 */       InputStream in = new FileInputStream(natives[i]);
/*     */ 
/* 138 */       out.putNextEntry(new ZipEntry(getFileName(natives[i])));
/*     */ 
/* 140 */       while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
/* 141 */         out.write(this.buffer, 0, this.bufferSize);
/*     */       }
/*     */ 
/* 144 */       in.close();
/* 145 */       out.closeEntry();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addJarSpliceLauncher(JarOutputStream out) throws Exception
/*     */   {
/* 151 */     InputStream in = JarSplice.class.getResourceAsStream("JarSpliceLauncher.class");
/*     */ 
/* 153 */     out.putNextEntry(new ZipEntry("org/ninjacave/jarsplice/JarSpliceLauncher.class"));
/* 154 */     while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
/* 155 */       out.write(this.buffer, 0, this.bufferSize);
/*     */     }
/* 157 */     in.close();
/* 158 */     out.closeEntry();
/*     */   }
/*     */ 
/*     */   protected String getFileName(String ref) {
/* 162 */     ref = ref.replace('\\', '/');
/* 163 */     return ref.substring(ref.lastIndexOf('/') + 1);
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.core.Splicer
 * JD-Core Version:    0.6.2
 */