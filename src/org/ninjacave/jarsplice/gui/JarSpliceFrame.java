package org.ninjacave.jarsplice.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.elezeta.jarspliceplus.ConfigParser;

public class JarSpliceFrame
{
	final JFrame frame;
	IntroductionPanel introPanel = new IntroductionPanel();
	JarsPanel jarsPanel = new JarsPanel(this);
	NativesPanel nativesPanel = new NativesPanel(this);
	ClassPanel classPanel = new ClassPanel(this);
	CreatePanel createPanel = new CreatePanel(this);
	ShellScriptPanel shellScriptPanel = new ShellScriptPanel(this);
	MacAppPanel macAppPanel = new MacAppPanel(this);
	WinExePanel exePanel = new WinExePanel(this);
	public File lastDirectory;

	public JarSpliceFrame()
	{
		// TITLE MODIFIED AS TO STATE THAT JARSPLICEPLUS IS AN EXTENSION TO JARSPLICE :
		frame = new JFrame("JarSplicePlus - An Extension to JarSplice");

		TabPane tabPane = new TabPane(this);

		tabPane.addTab("INTRODUCTION", this.introPanel, true);
		tabPane.addTab("1) ADD JARS", this.jarsPanel, true);
		tabPane.addTab("2) ADD NATIVES", this.nativesPanel, true);
		tabPane.addTab("3) MAIN CLASS", this.classPanel, true);
		tabPane.addTab("4) CREATE FAT JAR", this.createPanel, true);

		tabPane.addTab("EXTRA (LINUX .SH)", this.shellScriptPanel, false);
		tabPane.addTab("EXTRA (MAC .APP)", this.macAppPanel, false);
		tabPane.addTab("EXTRA (WINDOWS .EXE)", this.exePanel, false);

		frame.add(tabPane, "Center");

		// Menu :
		createMenu();

		frame.setSize(680, 490);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null); // center frame on screen
		frame.setDefaultCloseOperation(3);
	}

	public JFrame getFrame() {
		return frame;
	}

	protected void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem loadItem = new JMenuItem("Load configuration");
		JMenuItem saveItem = new JMenuItem("Save configuration");
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);

		loadItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadConfig();
			}
		});

		saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
	}

	public String[] getJarsList() {
		return this.jarsPanel.getSelectedFiles();
	}

	public String[] getNativesList() {
		return this.nativesPanel.getSelectedFiles();
	}

	public String getMainClass() {
		return this.classPanel.getMainClass();
	}

	public String getVmArgs() {
		return this.classPanel.getVmArgs();
	}

	public void setMainClass(String mainClass) {
		this.classPanel.setMainClass(mainClass);
	}

	public void setVmArgs(String vmArgs) {
		this.classPanel.setVmArgs(vmArgs);
	}

	public void setSelectedJars(String[] files) {
		jarsPanel.setSelectedFiles(files);
	}

	public void setSelectedNatives(String[] files) {
		nativesPanel.setSelectedFiles(files);
	}

	protected void loadConfig() {
		JFileChooser chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(lastDirectory);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("Select the file to load");
		chooser.addChoosableFileFilter(getConfigFileFilter());

		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if(selectedFile.exists()) {
				new ConfigParser(this).loadXMLFile(selectedFile);
			}
		}

		// save directory :
		lastDirectory = chooser.getCurrentDirectory();
	}

	protected void saveConfig() {
		JFileChooser chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(lastDirectory);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("Select the file to save");
		chooser.setSelectedFile(new File("JarSpliceConfig" + ConfigParser.extension));
		chooser.addChoosableFileFilter(getConfigFileFilter());

		if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if(selectedFile.exists()) {
				int option = JOptionPane.showConfirmDialog(frame, "File already exists, overwrite it ?", "", JOptionPane.YES_NO_OPTION );
				if(option != JOptionPane.YES_OPTION)
					return;
				if(!selectedFile.delete()) {
					JOptionPane.showMessageDialog(frame, "ERROR, could not overwrite file !", "", JOptionPane.ERROR_MESSAGE);
				}
			}

			// add extension if needed : 
			if(selectedFile.getName().toLowerCase().endsWith(ConfigParser.extension) == false)
				selectedFile = new File(selectedFile.getAbsolutePath() + ConfigParser.extension);

			new ConfigParser(this).saveXMLFile(selectedFile);
		}

		// save directory :
		lastDirectory = chooser.getCurrentDirectory();
	}

	protected FileFilter getConfigFileFilter() {
		return new FileFilter() {
			@Override
			public String getDescription() {
				return ConfigParser.extension;
			}

			@Override
			public boolean accept(File file) {
				return file != null && file.getName().toLowerCase().endsWith(ConfigParser.extension);
			}
		};
	}
}
