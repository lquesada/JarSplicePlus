package com.elezeta.jarspliceplus;

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
    private String       mainClass;
    private String       parameters;
    private String       output;

    public JarSpliceParams () {
        inputJars    = new ArrayList<String>();
        inputNatives = new ArrayList<String>();
        mainClass    = null;
        parameters   = null;
        output       = null;
    }

    public void inputJar (String inputJar) {
        inputJars.add(inputJar);
        System.out.println("Input jar file: " + inputJar);
    }

    public List<String> getInputJars () {
        return inputJars;
    }

    public boolean hasInputJars () {
        return !inputJars.isEmpty();
    }

    public void inputNative (String inputNative) {
        inputNatives.add(inputNative);
        System.out.println("Native file: " + inputNative);
    }

    public List<String> getInputNatives () {
        return inputNatives;
    }

    public boolean hasInputNatives () {
        return !inputNatives.isEmpty();
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

    public void output (String output) {
        if (this.output != null) {
            JarSplicePlusLauncher.error("Multiple declaration of output JAR file.");
        } else {
            this.output = output;
            System.out.println("Output JAR file: " + output);
        }
    }

    public String getOutput () {
        return output;
    }

    public boolean hasOutput () {
        return output != null;
    }

}
