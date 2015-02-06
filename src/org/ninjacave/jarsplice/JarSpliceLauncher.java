package org.ninjacave.jarsplice;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public class JarSpliceLauncher
{
  public JarSpliceLauncher()
    throws Exception
  {
    File file = getCodeSourceLocation();

    String nativeDirectory = getNativeDirectory();

    String mainClass = getMainClass(file);
    String vmArgs = getVmArgs(file);
    try
    {
      extractNatives(file, nativeDirectory);

      ArrayList arguments = new ArrayList();

      String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
      arguments.add(javaPath);

      StringTokenizer vmArgsToken = new StringTokenizer(vmArgs, " ");
      int count = vmArgsToken.countTokens();
      for (int i = 0; i < count; i++) {
        arguments.add(vmArgsToken.nextToken());
      }
      arguments.add("-cp");
      arguments.add(file.getAbsoluteFile().toString());
      arguments.add("-Djava.library.path=" + nativeDirectory);
      arguments.add(mainClass);

      ProcessBuilder processBuilder = new ProcessBuilder(arguments);
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      writeConsoleOutput(process);

      process.waitFor();
    }
    finally {
      deleteNativeDirectory(nativeDirectory);
    }
  }

  public void writeConsoleOutput(Process process) throws Exception {
    InputStream is = process.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    while ((line = br.readLine()) != null)
    {
      System.out.println(line);
    }
  }

  public void extractNatives(File file, String nativeDirectory) throws Exception
  {
    JarFile jarFile = new JarFile(file, false);
    Enumeration entities = jarFile.entries();

    while (entities.hasMoreElements()) {
      JarEntry entry = (JarEntry)entities.nextElement();

      if ((!entry.isDirectory()) && (entry.getName().indexOf('/') == -1))
      {
        if (isNativeFile(entry.getName()))
        {
          InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
          OutputStream out = new FileOutputStream(nativeDirectory + File.separator + entry.getName());

          byte[] buffer = new byte[65536];
          int bufferSize;
          while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1)
          {
            out.write(buffer, 0, bufferSize);
          }

          in.close();
          out.close();
        }
      }
    }
    jarFile.close();
  }

  public boolean isNativeFile(String entryName) {
    String osName = System.getProperty("os.name");
    String name = entryName.toLowerCase();

    if (osName.startsWith("Win")) {
      if (name.endsWith(".dll"))
        return true;
    }
    else if (osName.startsWith("Linux")) {
      if (name.endsWith(".so"))
        return true;
    }
    else if (((osName.startsWith("Mac")) || (osName.startsWith("Darwin"))) && (
      (name.endsWith(".jnilib")) || (name.endsWith(".dylib")))) {
      return true;
    }

    return false;
  }

  public String getNativeDirectory() {
    String nativeDir = System.getProperty("deployment.user.cachedir");

    if ((nativeDir == null) || (System.getProperty("os.name").startsWith("Win"))) {
      nativeDir = System.getProperty("java.io.tmpdir");
    }
    if(!nativeDir.endsWith(File.separator))
        nativeDir+=File.separator;
    nativeDir += "natives" + new Random().nextInt();

    File dir = new File(nativeDir);

    if (!dir.exists()) {
      dir.mkdirs();
    }
    return nativeDir;
  }

  public void deleteNativeDirectory(String directoryName) {
    File directory = new File(directoryName);

    File[] files = directory.listFiles();
    for (File file : files) {
      file.delete();
    }

    directory.delete();
  }

  public String getMainClass(File file) throws Exception {
    JarFile jarFile = new JarFile(file);
    Manifest manifest = jarFile.getManifest();
    Attributes attribute = manifest.getMainAttributes();

    return attribute.getValue("Launcher-Main-Class");
  }

  public String getVmArgs(File file) throws Exception {
    JarFile jarFile = new JarFile(file);
    Manifest manifest = jarFile.getManifest();
    Attributes attribute = manifest.getMainAttributes();

    return attribute.getValue("Launcher-VM-Args");
  }

  public File getCodeSourceLocation()
  {
    try
    {
      return new File(JarSpliceLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    new JarSpliceLauncher();
  }
}
