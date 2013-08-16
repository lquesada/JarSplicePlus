package com.elezeta.jarspliceplus;

import java.util.ArrayList;
import java.util.List;

import org.ninjacave.jarsplice.core.Splicer;
import org.ninjacave.jarsplice.gui.JarSpliceFrame;

public class JarSplicePlusLauncher {

	public enum Target {
		NONE,
		INPUTJARS,
		INPUTNATIVES,
		MAINCLASS,
		PARAMETERS,
		OUTPUT
	}

	public static void main(String args[]) {
		if (args.length == 0) {
		    JarSpliceFrame gui = new JarSpliceFrame();
		} else if (args.length == 1
                   && args[0].equals("-h")) {
            help();
            System.exit(0);
        } else {
			Splicer spl = new Splicer();

			List<String> inputJars    = new ArrayList<String>();
			List<String> inputNatives = new ArrayList<String>();
			String mainClass  = null;
			String parameters = null;
			String output     = null;

			Target current = Target.NONE;

			// Parse and check parameters

            for (String arg : args) {
                if (arg.equals("-i"))
                    current = Target.INPUTJARS;
                else if (arg.equals("-n"))
                    current = Target.INPUTNATIVES;
                else if (arg.equals("-m"))
                    current = Target.MAINCLASS;
                else if (arg.equals("-p"))
                    current = Target.PARAMETERS;
                else if (arg.equals("-o"))
                    current = Target.OUTPUT;
                else {
                    switch (current) {
                        case NONE:
                            error("Invalid parameters.");
                            break;
                        case INPUTJARS:
                            System.out.println("Input JAR files: " + arg);
                            inputJars.add(arg);
                            break;
                        case INPUTNATIVES:
                            System.out.println("Input native files: " + arg);
                            inputNatives.add(arg);
                            break;
                        case MAINCLASS:
                            if (mainClass != null) {
                                error("Multiple declaration of main class.");
                            } else {
                                mainClass = arg;
                                System.out.println("Main class: " + arg);
                            }
                            current = Target.NONE;
                            break;
                        case PARAMETERS:
                            if (parameters != null) {
                                parameters = parameters + " " + arg;
                                System.out.println("JVM Parameters: " + parameters);
                            } else {
                                parameters = arg;
                                System.out.println("JVM Parameters: " + parameters);
                            }
                            break;
                        case OUTPUT:
                            if (output != null) {
                                error("Multiple declaration of output JAR file.");
                            } else {
                                output = arg;
                                System.out.println("Output JAR file: " + arg);
                            }
                            current = Target.NONE;
                            break;
                    }
                }
            }

			if (inputJars.size() == 0) {
				error("No input JAR files.");
			}

			if (mainClass == null) {
				error("No main class.");
			}

			if (output == null) {
				error("No output JAR file.");
			}

			if (parameters == null) {
				parameters = "";
			}

			// Invoke JarSplice

			try {
				spl.createFatJar(inputJars.toArray(new String[0]),inputNatives.toArray(new String[0]),output,mainClass,parameters);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("");
				System.out.println("Error while building output JAR file.");
				System.exit(1);
			}

			System.out.println("Output JAR file "+output+" built successfully.");
			System.exit(0);
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

