package org.ninjacave.jarsplice.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import org.ninjacave.jarsplice.core.*;

public class WinExePanel extends JPanel
  implements ActionListener
{
  JFileChooser fileChooser;
  JButton winExeButton;
  JButton iconButton;
  JarSpliceFrame jarSplice;
  WinExeSplicer winExeSplicer = new WinExeSplicer();

  public WinExePanel(JarSpliceFrame jarSplice) {
    this.jarSplice = jarSplice;

    UIManager.put("FileChooser.readOnly", Boolean.TRUE);

    this.fileChooser = getFileChooser();

    setLayout(new BorderLayout(5, 20));

    TitledBorder border = BorderFactory.createTitledBorder("Create EXE file for Windows");
    border.setTitleJustification(2);
    setBorder(border);

    add(createAppPanel(), "First");

    add(createButtonPanel(), "Center");
  }

  private JPanel createAppPanel() {
    JPanel descriptionPanel = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] {
      Integer.valueOf(300),
      "This is an optional step and will create a Windows EXE File. " }));

    descriptionPanel.add(label);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(5, 20));

    panel.add(descriptionPanel, "First");

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel();
    this.winExeButton = new JButton("Create Windows EXE file");
    this.winExeButton.addActionListener(this);
    buttonPanel.add(this.winExeButton);

    return buttonPanel;
  }

  public JPanel createIconPanel()
  {
    JPanel selectPanel = new JPanel();

    selectPanel.setLayout(new FlowLayout(1, 0, 0));

    JPanel pathPanel = new JPanel();
    JTextField textField = new JTextField("image.png");
    textField.setPreferredSize(new Dimension(300, 30));
    textField.setMinimumSize(new Dimension(300, 30));
    textField.setMaximumSize(new Dimension(300, 30));

    pathPanel.add(textField);

    JPanel buttonPanel = new JPanel();
    this.iconButton = new JButton("Select Icon");
    this.iconButton.addActionListener(this);
    buttonPanel.add(this.iconButton);

    selectPanel.add(pathPanel);
    selectPanel.add(buttonPanel);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(5, 20));
    TitledBorder border1 = BorderFactory.createTitledBorder("Set Exe Icon");
    panel.setBorder(border1);

    JPanel descriptionPanel = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] {
      Integer.valueOf(300),
      "Select the icon the exe will use. This should be in the*.png file format." }));

    descriptionPanel.add(label);

    panel.add(selectPanel, "First");
    panel.add(descriptionPanel, "Center");

    return panel;
  }

  public String getOutputFile(File file) {
    String outputFile = file.getAbsolutePath();

    if (!outputFile.endsWith(".exe")) {
      outputFile = outputFile + ".exe";
    }

    return outputFile;
  }

  private JFileChooser getFileChooser() {
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
        return filename.endsWith(".exe");
      }
      public String getDescription() {
        return "*.exe";
      }
    };
    this.fileChooser.setFileFilter(filter);

    return this.fileChooser;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.winExeButton)
    {
      this.fileChooser.setCurrentDirectory(this.jarSplice.lastExportDirectory);
      int value = this.fileChooser.showSaveDialog(this);
      this.jarSplice.lastExportDirectory = this.fileChooser.getCurrentDirectory();

      if (value == 0) {
        String[] sources = this.jarSplice.getJarsList();
        String[] natives = this.jarSplice.getNativesList();
        String output = getOutputFile(this.fileChooser.getSelectedFile());
        String mainClass = this.jarSplice.getMainClass();
        String vmArgs = this.jarSplice.getVmArgs();
        try
        {
          this.winExeSplicer.createFatJar(sources, natives, output, mainClass, vmArgs);

          JOptionPane.showMessageDialog(this,
            "EXE Successfully Created.",
            "Success", -1);
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this,
            "EXE creation failed due to the following exception:\n" + ex.getMessage(),
            "Failed", 0);
        }

        System.out.println("File Saved as " + output);
      }
    }
  }
}
