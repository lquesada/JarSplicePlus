package com.elezeta.jarspliceplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: lachlan.krautz
 * Date: 17/08/13
 * Time: 5:49 PM
 */
public class JarSpliceParams {

    private List<String> inputJars;
    private List<String> inputNatives;
    private List<String> requiredPaths;
    private String       mainClass;
    private String       parameters;
    private String       output;
    private String       outputDir;
    private String       osxAppIcon;
    private String       outputOsxApp;
    private String       outputOsxAppDir;
    private String       outputSh;
    private String       outputShDir;
    private String       name;

    public JarSpliceParams () {
        inputJars       = new ArrayList<String>();
        inputNatives    = new ArrayList<String>();
        requiredPaths   = new ArrayList<String>();
        mainClass       = null;
        parameters      = null;
        output          = null;
        outputDir       = null;
        osxAppIcon      = null;
        outputOsxApp    = null;
        outputOsxAppDir = null;
        outputSh        = null;
        outputShDir     = null;
        name            = null;
    }

    public void outputSh (String outputSh) {
        requirePath(new File(outputSh).getParent());
        this.outputSh = outputSh;
    }

    public void outputShDir (String outputShDir) {
        requirePath(outputShDir);
        this.outputShDir = outputShDir;
    }

    public void requirePath (String path) {
        requiredPaths.add(path);
    }

    public void name (String name) {
        this.name = name;
    }

    public void osxAppIcon (String osxAppIcon) {
        this.osxAppIcon = osxAppIcon;
    }

    public void outputOsxApp (String outputOsxApp) {
        requirePath(new File(outputOsxApp).getParent());
        this.outputOsxApp = outputOsxApp;
    }

    public void outputOsxAppDir (String outputOsxAppDir) {
        requirePath(outputOsxAppDir);
        this.outputOsxAppDir = outputOsxAppDir;
    }

    public boolean osxAppRequested () {
        return osxAppIcon != null
                && (outputOsxApp != null
                    || outputOsxAppDir != null)
                && name != null;
    }

    public void inputJar (String inputJar) {
        requirePath(new File(inputJar).getParent());
        inputJars.add(inputJar);
        System.out.println("Input jar file: " + inputJar);
    }

    public List<String> getInputJars () {
        return inputJars;
    }

    public boolean hasInputJars () {
        return !inputJars.isEmpty();
    }

    public void collectNativesInDir (String path) {
        File file = new File(path);
        for (String nativeFileName: file.list()) {
            inputNative(path + File.separator + nativeFileName);
        }
    }

    public void inputNative (String inputNative) {
        inputNatives.add(inputNative);
        System.out.println("Native file: " + inputNative);
    }

    public List<String> getInputNatives () {
        return inputNatives;
    }

    public void mainClass (String mainClass) {
        if (this.mainClass != null) {
            JarSplicePlusLauncher.error("Multiple declaration of main class.");
        } else {
            this.mainClass = mainClass;
            System.out.println("Main class: " + mainClass);
        }
    }

    public String getMainClass () {
        return mainClass;
    }

    public boolean hasMainClass () {
        return mainClass != null;
    }

    public void parameters (String parameters) {
        if (this.parameters != null) {
            this.parameters += " " + parameters;
        } else {
            this.parameters = parameters;
        }
        System.out.println("JVM Parameter: " + parameters);
    }

    public String getParameters () {
        return hasParameters()? parameters: "";
    }

    public boolean hasParameters () {
        return parameters != null;
    }

    public void outputDir (String outputDir) {
        requirePath(outputDir);
        this.outputDir = outputDir;
    }

    public void output (String output) {
        if (this.output != null) {
            JarSplicePlusLauncher.error("Multiple declaration of output JAR file.");
        } else {
            requirePath(new File(output).getParent());
            this.output = output;
            System.out.println("Output JAR file: " + output);
        }
    }

    public String getOutput () {
        String output;
        if (outputDir != null
            && name != null) {
            output = outputDir + File.separator + name + ".jar";
        } else {
            output = this.output;
        }
        return output;
    }

    public String getOsxAppIcon () {
        return osxAppIcon;
    }

    public String getName () {
        return name;
    }

    public boolean hasOutput () {
        return output != null || (outputDir != null && name != null);
    }

    public String getOutputOsxApp () {
        String outputOsxApp;
        if (outputOsxAppDir != null
                && name != null) {
            outputOsxApp = outputOsxAppDir + File.separator + name + ".zip";
        } else {
            outputOsxApp = this.outputOsxApp;
        }
        return outputOsxApp;
    }

    public String getOutputSh () {
        String outputSh;
        if (outputShDir != null
                && name != null) {
            outputSh = outputShDir + File.separator + name + ".sh";
        } else {
            outputSh = this.outputSh;
        }
        return outputSh;
    }

    public boolean shRequested () {
        return (outputSh != null
                || outputShDir != null)
                && name != null;
    }

    public List<String> getRequiredPaths () {
        return requiredPaths;
    }

}
