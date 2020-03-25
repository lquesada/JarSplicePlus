package org.ninjacave.jarsplice.core;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import org.ninjacave.jarsplice.*;

public class Splicer
{

    ArrayList<String> dirs = new ArrayList();
    int bufferSize;
    byte[] buffer = new byte[4096];

    public void createFatJar(String[] jars, String[] natives, String output, String mainClass, String vmArgs) throws Exception
    {
        this.dirs.clear();
        services.clear();

        Manifest manifest = getManifest(mainClass, vmArgs);

        FileOutputStream fos = new FileOutputStream(output);
        JarOutputStream jos = new JarOutputStream(fos, manifest);
        try
        {
            addFilesFromJars(jars, jos);
            addNativesToJar(natives, jos);
            addJarSpliceLauncher(jos);
            processCachedServices(jos);
        }
        finally
        {
            jos.close();
            fos.close();
        }
    }

    protected Manifest getManifest(String mainClass, String vmArgs)
    {
        Manifest manifest = new Manifest();
        Attributes attribute = manifest.getMainAttributes();
        attribute.putValue("Manifest-Version", "1.0");
        attribute.putValue("Main-Class", "org.ninjacave.jarsplice.JarSpliceLauncher");
        attribute.putValue("Launcher-Main-Class", mainClass);
        attribute.putValue("Launcher-VM-Args", vmArgs);

        return manifest;
    }

    HashMap<String, String> services = new HashMap<>();

    private void cacheService(String filename, InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                out.append(line).append("\n");
            }
        }
        catch (IOException ex)
        {
        }

        if (services.containsKey(filename))
        {
            out.append(services.get(filename));
            services.replace(filename, out.toString());
        }
        else
        {
            services.put(filename, out.toString());
        }
    }

    private void processCachedServices(JarOutputStream out) throws IOException
    {
        for (String key : services.keySet())
        {
            System.out.print(key);
            out.putNextEntry(new ZipEntry(key));
            out.write(services.get(key).getBytes());
            out.closeEntry();
        }
    }

    protected void addFilesFromJars(String[] jars, JarOutputStream out) throws Exception
    {
        for (int i = 0; i < jars.length; i++)
        {
            ZipFile jarFile = new ZipFile(jars[i]);

            Enumeration entities = jarFile.entries();

            while (entities.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry)entities.nextElement();
                boolean isService = entry.getName().toLowerCase().contains("meta-inf/services/");

                if (entry.isDirectory())
                {
                    if (!this.dirs.contains(entry.getName()))
                    {
                        this.dirs.add(entry.getName());
                    }
                }
                else if ((!entry.getName().toLowerCase().startsWith("meta-inf") || isService))
                {
                    if (isService)
                    {
                        InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
                        cacheService(entry.getName(), jarFile.getInputStream(jarFile.getEntry(entry.getName())));
                        in.close();
                    }
                    else if (!entry.getName().toLowerCase().contains("JarSpliceLauncher"))
                    {
                        InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));

                        out.putNextEntry(new ZipEntry(entry.getName()));
                        while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1)
                        {
                            out.write(this.buffer, 0, this.bufferSize);
                        }

                        in.close();
                        out.closeEntry();
                    }
                }
            }
            jarFile.close();
        }
    }

    protected void addNativesToJar(String[] natives, JarOutputStream out) throws Exception
    {
        for (int i = 0; i < natives.length; i++)
        {
            InputStream in = new FileInputStream(natives[i]);

            out.putNextEntry(new ZipEntry(getFileName(natives[i])));

            while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1)
            {
                out.write(this.buffer, 0, this.bufferSize);
            }

            in.close();
            out.closeEntry();
        }
    }

    protected void addJarSpliceLauncher(JarOutputStream out) throws Exception
    {
        InputStream in = JarSplice.class.getResourceAsStream("JarSpliceLauncher.class");

        out.putNextEntry(new ZipEntry("org/ninjacave/jarsplice/JarSpliceLauncher.class"));
        while ((this.bufferSize = in.read(this.buffer, 0, this.buffer.length)) != -1)
        {
            out.write(this.buffer, 0, this.bufferSize);
        }
        in.close();
        out.closeEntry();
    }

    protected String getFileName(String ref)
    {
        ref = ref.replace('\\', '/');
        return ref.substring(ref.lastIndexOf('/') + 1);
    }
}
