/*     */ package org.ninjacave.jarsplice.core;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.zip.ZipEntry;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
/*     */ 
/*     */ public class MacAppSplicer extends Splicer
/*     */ {
/*     */   private void addZipEntry(String input, ZipArchiveOutputStream os, String name, boolean executableFile)
/*     */     throws Exception
/*     */   {
/*  52 */     InputStream is = getResourceAsStream(input);
/*     */ 
/*  54 */     ZipArchiveEntry zae = new ZipArchiveEntry(name);
/*  55 */     if (executableFile) zae.setUnixMode(33261); else
/*  56 */       zae.setUnixMode(33188);
/*  57 */     os.putArchiveEntry(zae);
/*  58 */     copy(is, os);
/*  59 */     os.closeArchiveEntry();
/*     */ 
/*  61 */     is.close();
/*     */   }
/*     */ 
/*     */   private void addZipFolder(ZipArchiveOutputStream os, String folderName) throws Exception {
/*  65 */     ZipArchiveEntry zae = new ZipArchiveEntry(folderName);
/*  66 */     zae.setUnixMode(16877);
/*  67 */     os.putArchiveEntry(zae);
/*  68 */     os.closeArchiveEntry();
/*     */   }
/*     */ 
/*     */   private void addFileAsZipEntry(File inputFile, ZipArchiveOutputStream os, String name) throws Exception
/*     */   {
/*  73 */     InputStream is = new FileInputStream(inputFile);
/*     */ 
/*  75 */     ZipArchiveEntry zae = new ZipArchiveEntry(name);
/*  76 */     zae.setUnixMode(33188);
/*  77 */     os.putArchiveEntry(zae);
/*  78 */     copy(is, os);
/*  79 */     os.closeArchiveEntry();
/*     */ 
/*  81 */     is.close();
/*     */   }
/*     */ 
/*     */   public void createAppBundle(String[] jars, String[] natives, String output, String mainClass, String vmArgs, String bundleName, String icon) throws Exception {
/*  85 */     this.dirs.clear();
/*     */ 
/*  88 */     File tmpJarFile = new File(output + ".tmp");
/*  89 */     FileInputStream is = null;
/*     */ 
/*  92 */     FileOutputStream fos = new FileOutputStream(output);
/*     */ 
/*  94 */     ZipArchiveOutputStream os = null;
/*  95 */     PrintStream pos = null;
/*     */     try
/*     */     {
/*  98 */       os = new ZipArchiveOutputStream(fos);
/*     */ 
/* 100 */       String appName = bundleName + ".app/";
/*     */ 
/* 102 */       addZipFolder(os, appName);
/* 103 */       addZipFolder(os, appName + "Contents/");
/* 104 */       addZipFolder(os, appName + "Contents/MacOS/");
/* 105 */       addZipFolder(os, appName + "Contents/Resources/");
/* 106 */       addZipFolder(os, appName + "Contents/Resources/Java/");
/*     */ 
/* 109 */       addZipEntry("res/Contents/PkgInfo", os, appName + "Contents/PkgInfo", false);
/* 110 */       addZipEntry("res/Contents/MacOS/JavaApplicationStub", os, appName + "Contents/MacOS/JavaApplicationStub", true);
/* 111 */       addZipEntry("res/Contents/MacOS/mac_launch_fd.sh", os, appName + "Contents/MacOS/mac_launch_fd.sh", true);
/*     */ 
/* 113 */       File iconFile = null;
/*     */ 
/* 116 */       if (icon.length() != 0) {
/* 117 */         iconFile = new File(icon);
/*     */ 
/* 119 */         if ((!iconFile.exists()) || (!iconFile.isFile())) {
/* 120 */           throw new Exception("Icon file not found at: " + icon);
/*     */         }
/*     */ 
/* 123 */         addFileAsZipEntry(iconFile, os, appName + "Contents/Resources/" + iconFile.getName());
/*     */       }
/*     */ 
/* 127 */       createTmpJar(jars, natives, tmpJarFile, mainClass, vmArgs);
/* 128 */       addFileAsZipEntry(tmpJarFile, os, appName + "Contents/Resources/Java/app.jar");
/*     */ 
/* 132 */       ZipArchiveEntry zae = new ZipArchiveEntry(appName + "Contents/Info.plist");
/* 133 */       zae.setUnixMode(33188);
/* 134 */       os.putArchiveEntry(zae);
/*     */ 
/* 136 */       pos = new PrintStream(os);
/* 137 */       String iconFileName = iconFile != null ? iconFile.getName() : null;
/* 138 */       writePlistFile(pos, bundleName, iconFileName);
/* 139 */       pos.flush();
/*     */ 
/* 141 */       os.closeArchiveEntry();
/*     */     }
/*     */     finally {
/* 144 */       if (pos != null) pos.close();
/* 145 */       if (os != null) os.close();
/* 146 */       if (is != null) is.close();
/*     */ 
/* 148 */       tmpJarFile.delete();
/*     */     }
/*     */ 
/* 151 */     fos.close();
/*     */   }
/*     */ 
/*     */   private void createTmpJar(String[] jars, String[] natives, File tmpJarFile, String mainClass, String vmArgs) throws Exception {
/* 155 */     FileOutputStream fos = new FileOutputStream(tmpJarFile);
/*     */ 
/* 157 */     Manifest manifest = getManifest(mainClass, vmArgs);
/* 158 */     JarOutputStream jos = new JarOutputStream(fos, manifest);
/*     */     try
/*     */     {
/* 161 */       addFilesFromJars(jars, jos);
/* 162 */       addNativesToJar(natives, jos);
/* 163 */       addJarSpliceLauncher(jos);
/*     */     } finally {
/* 165 */       jos.close();
/* 166 */       fos.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected InputStream getResourceAsStream(String res) {
/* 171 */     return Thread.currentThread().getContextClassLoader().getResourceAsStream(res);
/*     */   }
/*     */ 
/*     */   protected void addNativesToJar(String[] natives, JarOutputStream out) throws Exception
/*     */   {
/* 176 */     for (int i = 0; i < natives.length; i++)
/*     */     {
/* 179 */       if ((natives[i].endsWith(".jnilib")) || (natives[i].endsWith(".dylib")))
/*     */       {
/* 181 */         InputStream in = new FileInputStream(natives[i]);
/*     */ 
/* 184 */         out.putNextEntry(new ZipEntry(getFileName(natives[i])));
/*     */ 
/* 186 */         while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
/* 187 */           out.write(this.buffer, 0, this.bufferSize);
/*     */         }
/*     */ 
/* 190 */         in.close();
/* 191 */         out.closeEntry();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 196 */   private long copy(InputStream input, OutputStream output) throws IOException { byte[] buffer = new byte[8024];
/* 197 */     int n = 0;
/* 198 */     long count = 0L;
/* 199 */     while (-1 != (n = input.read(buffer))) {
/* 200 */       output.write(buffer, 0, n);
/* 201 */       count += n;
/*     */     }
/* 203 */     return count;
/*     */   }
/*     */ 
/*     */   private void writePlistFile(PrintStream pos, String bundleName, String iconFile)
/*     */   {
/* 208 */     pos.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/* 209 */     pos.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
/* 210 */     pos.println("<plist version=\"1.0\">");
/* 211 */     pos.println("<dict>");
/*     */ 
/* 213 */     writeKeyString(pos, "CFBundleAllowMixedLocalizations", "true");
/* 214 */     writeKeyString(pos, "CFBundleDevelopmentRegion", "English");
/* 215 */     writeKeyString(pos, "CFBundleExecutable", "JavaApplicationStub");
/* 216 */     writeKeyString(pos, "CFBundleGetInfoString", bundleName + " 1.0.0");
/* 217 */     if (iconFile != null) writeKeyString(pos, "CFBundleIconFile", iconFile);
/* 218 */     writeKeyString(pos, "CFBundleInfoDictionaryVersion", "6.0");
/* 219 */     writeKeyString(pos, "CFBundleName", bundleName);
/* 220 */     writeKeyString(pos, "CFBundlePackageType", "APPL");
/* 221 */     writeKeyString(pos, "CFBundleShortVersionString", "1.0.0");
/* 222 */     writeKeyString(pos, "CFBundleSignature", "????");
/* 223 */     writeKeyString(pos, "CFBundleVersion", "10.2");
/*     */ 
/* 225 */     pos.println("<key>Java</key>");
/* 226 */     pos.println("<dict>");
/* 227 */     pos.println("<key>ClassPath</key>");
/* 228 */     pos.println("<array>");
/* 229 */     pos.println("<string>$JAVAROOT/app.jar</string>");
/* 230 */     pos.println("</array>");
/* 231 */     pos.println("<key>JVMVersion</key>");
/* 232 */     pos.println("<string>1.5+</string>");
/* 233 */     pos.println("<key>MainClass</key>");
/* 234 */     pos.println("<string>org.ninjacave.jarsplice.JarSpliceLauncher</string>");
/* 235 */     pos.println("<key>WorkingDirectory</key>");
/* 236 */     pos.println("<string>$APP_PACKAGE/Contents/Resources/Java</string>");
/* 237 */     pos.println("<key>Properties</key>");
/* 238 */     pos.println("<dict>");
/* 239 */     writeKeyString(pos, "apple.laf.useScreenMenuBar", "true");
/* 240 */     pos.println("</dict>");
/* 241 */     pos.println("</dict>");
/*     */ 
/* 244 */     pos.println("</dict>");
/* 245 */     pos.println("</plist>");
/*     */   }
/*     */ 
/*     */   private void writeKeyString(PrintStream pos, String key, String string) {
/* 249 */     pos.println("<key>" + key + "</key>");
/* 250 */     pos.println("<string>" + string + "</string>");
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.core.MacAppSplicer
 * JD-Core Version:    0.6.2
 */