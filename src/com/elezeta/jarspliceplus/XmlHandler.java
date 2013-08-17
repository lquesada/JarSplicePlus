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
            } else if (qName.equalsIgnoreCase("vmParam")) {
                jarSpliceParams.parameters(temp);
            } else if (qName.equalsIgnoreCase("mainClass")) {
                jarSpliceParams.mainClass(temp);
            } else if (qName.equalsIgnoreCase("outputJar")) {
                jarSpliceParams.output(temp);
            }
        }
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException {
        temp = new String(ch, start, length);
    }

}
