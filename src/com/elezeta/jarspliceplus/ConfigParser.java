package com.elezeta.jarspliceplus;

import java.io.File;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ninjacave.jarsplice.gui.JarSpliceFrame;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigParser {

	public static final String extension = ".xml";
	protected static final String el_root = "JarSplicePlusConfig";
	protected static final String el_jar = "Jar";
	protected static final String el_native = "Native";
	protected static final String at_path = "Path";
	protected static final String at_main = "MainClass";
	protected static final String at_vmArgs = "VM_Arguments";
	
	protected final JarSpliceFrame jarSpliceFrame;
	
	public ConfigParser(JarSpliceFrame frame) {
		this.jarSpliceFrame = frame;
	}

	public void saveXMLFile(File file) {
		try {
			// create document :
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			// define root elements :
			Document document = documentBuilder.newDocument();  
			Element rootElement = document.createElement(el_root);  
			document.appendChild(rootElement);

			// Save jars :
			String[] jarFiles = jarSpliceFrame.getJarsList();
			if(jarFiles != null) {
				int nbJars = jarFiles.length;
				for(int i=0; i<nbJars; i++) {
					Element jarEl = document.createElement(el_jar);
					rootElement.appendChild(jarEl);
					setElementAttribute(document, jarEl, at_path, jarFiles[i]);
				}
			}

			// Save natives :
			String[] nativeFiles = jarSpliceFrame.getNativesList();
			if(nativeFiles != null) {
				int nbNatives = nativeFiles.length;
				for(int i=0; i<nbNatives; i++) {
					Element nativeEl = document.createElement(el_native);
					rootElement.appendChild(nativeEl);
					setElementAttribute(document, nativeEl, at_path, nativeFiles[i]);
				}
			}

			// Save Main class and VM args :
			String mainClass = jarSpliceFrame.getMainClass();
			if(mainClass != null)
				setElementAttribute(document, rootElement, at_main, mainClass);
			String vmArgs = jarSpliceFrame.getVmArgs();
			if(vmArgs != null)
				setElementAttribute(document, rootElement, at_vmArgs, vmArgs);

			// creating and writing into xml file  
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(domSource, streamResult);
			System.out.println("Configuration file saved successfully !");
		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(jarSpliceFrame.getFrame(), "ERROR, could not save file !", "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void loadXMLFile(File file) {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);  
			doc.getDocumentElement().normalize();  
			Element rootEl = (Element) doc.getElementsByTagName(el_root).item(0);

			// Load Jars :
			NodeList jarList = rootEl.getElementsByTagName(el_jar);
			int nbJars = jarList.getLength();
			String[] jarPath = new String[nbJars];
			for(int i=0; i<nbJars; i++) {
				Element jarEl = (Element) jarList.item(i);
				jarPath[i] = jarEl.getAttribute(at_path);
			}
			jarSpliceFrame.setSelectedJars(jarPath);

			// Load Natives :
			NodeList nativeList = rootEl.getElementsByTagName(el_native);
			int nbNatives = nativeList.getLength();
			String[] natives = new String[nbNatives];
			for(int i=0; i<nbNatives; i++) {
				Element nativeEl = (Element) nativeList.item(i);
				natives[i] = nativeEl.getAttribute(at_path);
			}
			jarSpliceFrame.setSelectedNatives(natives);

			// Load Main class and VM args :
			if(rootEl.hasAttribute(at_main)) {
				String mainClass = rootEl.getAttribute(at_main);
				jarSpliceFrame.setMainClass(mainClass);
			}
			if(rootEl.hasAttribute(at_vmArgs)) {
				String vmArgs = rootEl.getAttribute(at_vmArgs);
				jarSpliceFrame.setVmArgs(vmArgs);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(jarSpliceFrame.getFrame(), "ERROR, could not read file !", "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void setElementAttribute(Document document, Element element, String attr_name, String attr_value) {
		Attr attr = document.createAttribute(attr_name);
		attr.setValue(attr_value);
		element.setAttributeNode(attr);
	}
}
