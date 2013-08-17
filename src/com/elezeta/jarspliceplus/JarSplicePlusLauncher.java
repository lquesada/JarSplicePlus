package com.elezeta.jarspliceplus;

import java.io.File;
import java.io.IOException;

import org.ninjacave.jarsplice.core.Splicer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class JarSplicePlusLauncher {

	public enum Target {
		NONE,
		INPUTJARS,
		INPUTNATIVES,
		MAINCLASS,
		PARAMETERS,
		OUTPUT,
        XML
	}

    private JarSpliceParams jarSpliceParams;
    private Splicer spl;
    private Target  current;

    public JarSplicePlusLauncher (String[] args) {
        jarSpliceParams = new JarSpliceParams();
        spl             = new Splicer();
        current         = Target.NONE;
        parseInput(args);
    }

    public static void main(String args[]) {
        new JarSplicePlusLauncher(args);
    }

    private void handleXmlParam (String fileName) {
        System.out.println("Path: " + fileName);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new File(fileName), new XmlHandler(jarSpliceParams));
        } catch (ParserConfigurationException e) {
            System.out.println("bad sax config");
        } catch (SAXException e) {
            System.out.println("sax is busted");
        } catch (IOException e) {
            System.out.println("io problem");
        }
    }

    private String getFilePath (String fileName) {
        String path = new File(fileName).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    private void parseParams (String[] args) {
        // Parse and check parameters
        for (String arg : args) {
            if (arg.equals("-x")) {
                current = Target.XML;
            } else if (arg.equals("-i")) {
                current = Target.INPUTJARS;
            } else if (arg.equals("-n")) {
                current = Target.INPUTNATIVES;
            } else if (arg.equals("-m")) {
                current = Target.MAINCLASS;
            } else if (arg.equals("-p")) {
                current = Target.PARAMETERS;
            } else if (arg.equals("-o")) {
                current = Target.OUTPUT;
            } else {
                performTargetAction(arg);
            }
        }
    }

    private void verifyInput () {
        if (!jarSpliceParams.hasInputJars()) {
            error("No input JAR files.");
        }

        if (!jarSpliceParams.hasMainClass()) {
            error("No main class.");
        }

        if (!jarSpliceParams.hasOutput()) {
            error("No output JAR file.");
        }

    }

    private void invokeJarSplice () {
        try {
            spl.createFatJar(
                    jarSpliceParams.getInputJars().toArray(new String[0]),
                    jarSpliceParams.getInputNatives().toArray(new String[0]),
                    jarSpliceParams.getOutput(),
                    jarSpliceParams.getMainClass(),
                    jarSpliceParams.getParameters()
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("");
            System.out.println("Error while building output JAR file.");
            System.exit(1);
        }
        System.out.println("Output JAR file " + jarSpliceParams.getOutput() + " built successfully.");
        System.exit(0);
    }

    private void parseInput (String[] args) {
        if (args.length == 1
                && args[0].equals("-h")) {
            help();
            System.exit(0);
        } else {
            parseParams(args);
            verifyInput();
            invokeJarSplice();
        }
    }

    private void performTargetAction (String arg) {
        switch (current) {
            case NONE:
                error("Invalid parameters.");
                break;
            case XML:
                System.out.println("Parsing config from xml file: " + arg);
                handleXmlParam(arg);
                break;
            case INPUTJARS:
                System.out.println("Input JAR files: " + arg);
                jarSpliceParams.inputJar(arg);
                break;
            case INPUTNATIVES:
                System.out.println("Input native files: " + arg);
                jarSpliceParams.inputNative(arg);
                break;
            case MAINCLASS:
                jarSpliceParams.mainClass(arg);
                current = Target.NONE;
                break;
            case PARAMETERS:
                jarSpliceParams.parameters(arg);
                break;
            case OUTPUT:
                jarSpliceParams.output(arg);
                current = Target.NONE;
                break;
        }
    }

	public static void error(String text) {
		System.out.println("Error: "+text);
		System.out.println("");
		help();
		System.exit(1);
	}

	public static void help() {
		System.out.println("JarSplicePlus - An Extension to JarSplice");
		System.out.println("Copyright (c) 2013, Luis Quesada - https://github.com/lquesada");
		System.out.println("");
		System.out.println("Call without arguments to open the GUI.");
		System.out.println("");
		System.out.println("Usage: java -jar JarSplicePlus.jar -i <input JAR file list>");
		System.out.println("                                 [-n <input native file list>]");
		System.out.println("                                  -m <main class>");
		System.out.println("                                 [-p <JVM parameters>]");
		System.out.println("                                 [-o <output JAR file>]");
        System.out.println("                                 [-x <xml config file>]");
		System.out.println("");
		System.out.println("Example: Bundling a complete application.");
		System.out.println("");
		System.out.println("Application JAR file:");
		System.out.println("");
		System.out.println(" - MyApplication.jar");
		System.out.println("");
		System.out.println("Main class:");
		System.out.println("");
		System.out.println(" - org.myapplication.mainClass");
		System.out.println("");
		System.out.println("JVM parameters:");
		System.out.println("");
		System.out.println(" -Xms256m -Xmx512m");
		System.out.println("");
		System.out.println("JAR libraries:");
		System.out.println("");
		System.out.println(" - lib/lwjgl-2.9.0/jar/lwjgl.jar");
		System.out.println(" - lib/lwjgl-2.9.0/jar/lwjgl_util.jar");
		System.out.println(" - lib/lwjgl-2.9.0/jar/jinput.jar");
		System.out.println(" - lib/slick-util/slick-util.jar");
		System.out.println("");
		System.out.println("Native library files for distinct operating systems:");
		System.out.println("");
		System.out.println("- lib/lwjgl-2.9.0/native/linux/libjinput-linux64.so");
		System.out.println("- lib/lwjgl-2.9.0/native/linux/libjinput-linux.so");
		System.out.println("- lib/lwjgl-2.9.0/native/linux/liblwjgl64.so");
		System.out.println("- lib/lwjgl-2.9.0/native/linux/liblwjgl.so");
		System.out.println("- lib/lwjgl-2.9.0/native/macosx/libjinput-osx.jnilib");
		System.out.println("- lib/lwjgl-2.9.0/native/macosx/liblwjgl.jnilib");
		System.out.println("- lib/lwjgl-2.9.0/native/windows/jinput-dx8_64.dll");
		System.out.println("- lib/lwjgl-2.9.0/native/windows/jinput-dx8.dll");
		System.out.println("- lib/lwjgl-2.9.0/native/windows/jinput-raw_64.dll");
		System.out.println("- lib/lwjgl-2.9.0/native/windows/jinput-raw.dll");
		System.out.println("- lib/lwjgl-2.9.0/native/windows/lwjgl64.dll");
		System.out.println("- lib/lwjgl-2.9.0/native/windows/lwjgl.dll");
		System.out.println("");
		System.out.println("Output JAR file:");
		System.out.println("");
		System.out.println(" - MyBundledApplication.jar");
		System.out.println("");
		System.out.println("Bundle them together issuing:");
		System.out.println("");
		System.out.println("java -jar JarSplicePlus.jar \\");
		System.out.println("                        -i MyApplication.jar \\");
		System.out.println("                           lib/lwjgl-2.9.0/jar/lwjgl.jar \\");
		System.out.println("                           lib/lwjgl-2.9.0/jar/lwjgl_util.jar \\");
		System.out.println("                           lib/lwjgl-2.9.0/jar/jinput.jar \\");
		System.out.println("                           lib/slick-util/*.jar \\");
		System.out.println("                        -n lib/lwjgl-2.9.0/native/linux/libjinput-linux64.so \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/linux/libjinput-linux.so \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/linux/liblwjgl64.so \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/linux/liblwjgl.so \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/macosx/libjinput-osx.jnilib \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/macosx/liblwjgl.jnilib \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/windows/jinput-dx8_64.dll \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/windows/jinput-dx8.dll \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/windows/jinput-raw_64.dll \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/windows/jinput-raw.dll \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/windows/lwjgl64.dll \\");
		System.out.println("                           lib/lwjgl-2.9.0/native/windows/lwjgl.dll \\");
		System.out.println("                        -m org.myapplication.mainClass \\");
		System.out.println("                        -p -Xms256m -Xmx512m \\");
		System.out.println("                        -o MyBundledApplication.jar");
		System.out.println("");
	}
}

