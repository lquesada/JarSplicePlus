package org.ninjacave.jarsplice.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class MacAppSplicer extends Splicer
{
  private void addZipEntry(String input, ZipArchiveOutputStream os, String name, boolean executableFile)
    throws Exception
  {
    InputStream is = getResourceAsStream(input);

    ZipArchiveEntry zae = new ZipArchiveEntry(name);
    if (executableFile) zae.setUnixMode(33261); else
      zae.setUnixMode(33188);
    os.putArchiveEntry(zae);
    copy(is, os);
    os.closeArchiveEntry();

    is.close();
  }

  private void addZipFolder(ZipArchiveOutputStream os, String folderName) throws Exception {
    ZipArchiveEntry zae = new ZipArchiveEntry(folderName);
    zae.setUnixMode(16877);
    os.putArchiveEntry(zae);
    os.closeArchiveEntry();
  }

  private void addFileAsZipEntry(File inputFile, ZipArchiveOutputStream os, String name) throws Exception
  {
    InputStream is = new FileInputStream(inputFile);

    ZipArchiveEntry zae = new ZipArchiveEntry(name);
    zae.setUnixMode(33188);
    os.putArchiveEntry(zae);
    copy(is, os);
    os.closeArchiveEntry();

    is.close();
  }

  public void createAppBundle(String[] jars, String[] natives, String output, String mainClass, String vmArgs, String bundleName, String icon) throws Exception {
    this.dirs.clear();

    File tmpJarFile = new File(output + ".tmp");
    FileInputStream is = null;

    FileOutputStream fos = new FileOutputStream(output);

    ZipArchiveOutputStream os = null;
    PrintStream pos = null;
    try
    {
      os = new ZipArchiveOutputStream(fos);

      String appName = bundleName + ".app/";

      addZipFolder(os, appName);
      addZipFolder(os, appName + "Contents/");
      addZipFolder(os, appName + "Contents/MacOS/");
      addZipFolder(os, appName + "Contents/Resources/");
      addZipFolder(os, appName + "Contents/Resources/Java/");

      addZipEntry("res/Contents/PkgInfo", os, appName + "Contents/PkgInfo", false);
      addZipEntry("res/Contents/MacOS/JavaApplicationStub", os, appName + "Contents/MacOS/JavaApplicationStub", true);
      addZipEntry("res/Contents/MacOS/mac_launch_fd.sh", os, appName + "Contents/MacOS/mac_launch_fd.sh", true);

      File iconFile = null;

      if (icon.length() != 0) {
        iconFile = new File(icon);

        if ((!iconFile.exists()) || (!iconFile.isFile())) {
          throw new Exception("Icon file not found at: " + icon);
        }

        addFileAsZipEntry(iconFile, os, appName + "Contents/Resources/" + iconFile.getName());
      }

      createTmpJar(jars, natives, tmpJarFile, mainClass, vmArgs);
      addFileAsZipEntry(tmpJarFile, os, appName + "Contents/Resources/Java/app.jar");

      ZipArchiveEntry zae = new ZipArchiveEntry(appName + "Contents/Info.plist");
      zae.setUnixMode(33188);
      os.putArchiveEntry(zae);

      pos = new PrintStream(os);
      String iconFileName = iconFile != null ? iconFile.getName() : null;
      writePlistFile(pos, bundleName, iconFileName);
      pos.flush();

      os.closeArchiveEntry();
    }
    finally {
      if (pos != null) pos.close();
      if (os != null) os.close();
      if (is != null) is.close();

      tmpJarFile.delete();
    }

    fos.close();
  }

  private void createTmpJar(String[] jars, String[] natives, File tmpJarFile, String mainClass, String vmArgs) throws Exception {
    FileOutputStream fos = new FileOutputStream(tmpJarFile);

    Manifest manifest = getManifest(mainClass, vmArgs);
    JarOutputStream jos = new JarOutputStream(fos, manifest);
    try
    {
      addFilesFromJars(jars, jos);
      addNativesToJar(natives, jos);
      addJarSpliceLauncher(jos);
    } finally {
      jos.close();
      fos.close();
    }
  }

  protected InputStream getResourceAsStream(String res) {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(res);
  }

  protected void addNativesToJar(String[] natives, JarOutputStream out) throws Exception
  {
    for (int i = 0; i < natives.length; i++)
    {
      if ((natives[i].endsWith(".jnilib")) || (natives[i].endsWith(".dylib")))
      {
        InputStream in = new FileInputStream(natives[i]);

        out.putNextEntry(new ZipEntry(getFileName(natives[i])));

        while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1) {
          out.write(this.buffer, 0, this.bufferSize);
        }

        in.close();
        out.closeEntry();
      }
    }
  }

  private long copy(InputStream input, OutputStream output) throws IOException { byte[] buffer = new byte[8024];
    int n = 0;
    long count = 0L;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  private void writePlistFile(PrintStream pos, String bundleName, String iconFile)
  {
    pos.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    pos.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
    pos.println("<plist version=\"1.0\">");
    pos.println("<dict>");

    writeKeyString(pos, "CFBundleAllowMixedLocalizations", "true");
    writeKeyString(pos, "CFBundleDevelopmentRegion", "English");
    writeKeyString(pos, "CFBundleExecutable", "JavaApplicationStub");
    writeKeyString(pos, "CFBundleGetInfoString", bundleName + " 1.0.0");
    if (iconFile != null) writeKeyString(pos, "CFBundleIconFile", iconFile);
    writeKeyString(pos, "CFBundleInfoDictionaryVersion", "6.0");
    writeKeyString(pos, "CFBundleName", bundleName);
    writeKeyString(pos, "CFBundlePackageType", "APPL");
    writeKeyString(pos, "CFBundleShortVersionString", "1.0.0");
    writeKeyString(pos, "CFBundleSignature", "????");
    writeKeyString(pos, "CFBundleVersion", "10.2");

    pos.println("<key>Java</key>");
    pos.println("<dict>");
    pos.println("<key>ClassPath</key>");
    pos.println("<array>");
    pos.println("<string>$JAVAROOT/app.jar</string>");
    pos.println("</array>");
    pos.println("<key>JVMVersion</key>");
    pos.println("<string>1.5+</string>");
    pos.println("<key>MainClass</key>");
    pos.println("<string>org.ninjacave.jarsplice.JarSpliceLauncher</string>");
    pos.println("<key>WorkingDirectory</key>");
    pos.println("<string>$APP_PACKAGE/Contents/Resources/Java</string>");
    pos.println("<key>Properties</key>");
    pos.println("<dict>");
    writeKeyString(pos, "apple.laf.useScreenMenuBar", "true");
    pos.println("</dict>");
    pos.println("</dict>");

    pos.println("</dict>");
    pos.println("</plist>");
  }

  private void writeKeyString(PrintStream pos, String key, String string) {
    pos.println("<key>" + key + "</key>");
    pos.println("<string>" + string + "</string>");
  }
}
