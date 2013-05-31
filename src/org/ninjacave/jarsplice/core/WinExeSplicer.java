package org.ninjacave.jarsplice.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class WinExeSplicer extends Splicer
{
  String stubFile = "res/stub.exe";

  public void createFatJar(String[] jars, String[] natives, String output, String mainClass, String vmArgs) throws Exception {
    this.dirs.clear();

    FileOutputStream fos = new FileOutputStream(output);

    InputStream is = getResourceAsStream(this.stubFile);
    try
    {
      int read = 0;
      byte[] bytes = new byte[8024];

      while ((read = is.read(bytes)) != -1)
        fos.write(bytes, 0, read);
    }
    finally
    {
      is.close();
    }

    fos.flush();

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

  protected InputStream getResourceAsStream(String res)
  {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(res);
  }

  protected void addNativesToJar(String[] natives, JarOutputStream out) throws Exception
  {
    for (int i = 0; i < natives.length; i++)
    {
      if (natives[i].endsWith(".dll"))
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
}
