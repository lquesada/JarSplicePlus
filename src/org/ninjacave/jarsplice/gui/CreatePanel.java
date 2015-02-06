package org.ninjacave.jarsplice.gui;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import org.ninjacave.jarsplice.core.*;

public class CreatePanel extends JPanel
  implements ActionListener
{
  JFileChooser fileChooser;
  JButton createButton;
  JarSpliceFrame jarSplice;
  Splicer splicer = new Splicer();

  public CreatePanel(JarSpliceFrame jarSplice) {
    this.jarSplice = jarSplice;

    this.fileChooser = new JFileChooser() {
      public void approveSelection() {
        File f = getSelectedFile();
        if ((f.exists()) && (getDialogType() == 1)) {
          int result =
            JOptionPane.showConfirmDialog(
            this, "The file already exists. Do you want to overwrite it?",
            "Confirm Replace", 0);
          switch (result) {
          case 0:
            super.approveSelection();
            return;
          case 1:
            return;
          case 2:
            return;
          }
        }
        super.approveSelection();
      }
    };
    this.fileChooser.setAcceptAllFileFilterUsed(false);

    FileFilter filter = new FileFilter() {
      public boolean accept(File file) {
        if (file.isDirectory()) return true;
        String filename = file.getName();
        return filename.endsWith(".jar");
      }
      public String getDescription() {
        return "*.jar";
      }
    };
    this.fileChooser.setFileFilter(filter);

    TitledBorder border = BorderFactory.createTitledBorder("Create Fat Jar");
    border.setTitleJustification(2);
    setBorder(border);

    JPanel buttonPanel = new JPanel();

    this.createButton = new JButton("Create Fat Jar");
    this.createButton.addActionListener(this);
    buttonPanel.add(this.createButton);

    add(buttonPanel);
  }

  public String getOutputFile(File file) {
    String outputFile = file.getAbsolutePath();

    if (!outputFile.endsWith(".jar")) {
      outputFile = outputFile + ".jar";
    }

    return outputFile;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.createButton) {
      this.fileChooser.setCurrentDirectory(this.jarSplice.lastExportDirectory);
      int value = this.fileChooser.showSaveDialog(this);
      this.jarSplice.lastExportDirectory = this.fileChooser.getCurrentDirectory();

      if (value == 0)
      {
        String[] jars = this.jarSplice.getJarsList();
        String[] natives = this.jarSplice.getNativesList();
        String output = getOutputFile(this.fileChooser.getSelectedFile());
        String mainClass = this.jarSplice.getMainClass();
        String vmArgs = this.jarSplice.getVmArgs();
        try
        {
          this.splicer.createFatJar(jars, natives, output, mainClass, vmArgs);

          JOptionPane.showMessageDialog(this,
            "Fat Jar Successfully Created.",
            "Success", -1);
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this,
            "Jar creation failed due to the following exception:\n" + ex.getMessage(),
            "Failed", 0);
        }

        System.out.println("File Saved as " + output);
      }
    }
  }
}
