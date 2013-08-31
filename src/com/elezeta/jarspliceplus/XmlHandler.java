package com.elezeta.jarspliceplus;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * User: lachlan.krautz
 * Date: 18/08/13
 * Time: 12:03 AM
 */
public class XmlHandler extends DefaultHandler {

    private JarSpliceParams jarSpliceParams;
    private String temp;

    public XmlHandler (JarSpliceParams jarSpliceParams) {
        this.jarSpliceParams = jarSpliceParams;
        temp = null;
    }

    @Override
    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
        temp = null;
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException {
        if (temp != null) {
            if (qName.equalsIgnoreCase("inputJar")) {
                jarSpliceParams.inputJar(temp);
            } else if (qName.equalsIgnoreCase("inputNative")) {
                jarSpliceParams.inputNative(temp);
            } else if (qName.equalsIgnoreCase("nativesDir")) {
                jarSpliceParams.collectNativesInDir(temp);
            } else if (qName.equalsIgnoreCase("vmParam")) {
                jarSpliceParams.parameters(temp);
            } else if (qName.equalsIgnoreCase("mainClass")) {
                jarSpliceParams.mainClass(temp);
            } else if (qName.equalsIgnoreCase("outputJar")) {
                jarSpliceParams.output(temp);
            } else if (qName.equalsIgnoreCase("outputJarDir")) {
                jarSpliceParams.outputDir(temp);
            } else if (qName.equalsIgnoreCase("osxAppIcon")) {
                jarSpliceParams.osxAppIcon(temp);
            } else if (qName.equalsIgnoreCase("outputOsxApp")) {
                jarSpliceParams.outputOsxApp(temp);
            } else if (qName.equalsIgnoreCase("outputOsxAppDir")) {
                jarSpliceParams.outputOsxAppDir(temp);
            } else if (qName.equalsIgnoreCase("outputSh")) {
                jarSpliceParams.outputSh(temp);
            } else if (qName.equalsIgnoreCase("outputShDir")) {
                jarSpliceParams.outputShDir(temp);
            } else if (qName.equalsIgnoreCase("name")) {
                jarSpliceParams.name(temp);
            } else if (qName.equalsIgnoreCase("requiredPath")) {
                jarSpliceParams.requirePath(temp);
            }
        }
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException {
        temp = new String(ch, start, length);
    }

}
