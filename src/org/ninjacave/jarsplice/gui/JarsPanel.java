package org.ninjacave.jarsplice.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;

public class JarsPanel extends JPanel
  implements ActionListener
{
  JarSpliceFrame jarSplice;
  JFileChooser fileChooser;
  JList list;
  DefaultListModel listModel = new DefaultListModel();
  JButton addButton;
  JButton removeButton;
  File[] selectedFiles;

  public JarsPanel(JarSpliceFrame jarSplice)
  {
    this.jarSplice = jarSplice;

    this.fileChooser = new JFileChooser();
    this.fileChooser.setMultiSelectionEnabled(true);
    this.fileChooser.setAcceptAllFileFilterUsed(false);

    FileFilter filter = new FileFilter() {
      public boolean accept(File file) {
        if (file.isDirectory()) return true;
        String filename = file.getName();
        return (filename.endsWith(".jar")) || (filename.endsWith(".zip"));
      }
      public String getDescription() {
        return "*.jar, *.zip";
      }
    };
    this.fileChooser.setFileFilter(filter);

    setLayout(new BorderLayout(5, 5));

    this.list = new JList(this.listModel);
    add(this.list, "Center");

    TitledBorder border = BorderFactory.createTitledBorder("Add Jars");
    border.setTitleJustification(2);
    setBorder(border);

    JPanel buttonPanel = new JPanel();

    this.addButton = new JButton("Add Jar(s)");
    this.addButton.addActionListener(this);
    buttonPanel.add(this.addButton);

    this.removeButton = new JButton("Remove Jar(s)");
    this.removeButton.addActionListener(this);
    buttonPanel.add(this.removeButton);

    add(buttonPanel, "Last");
  }

  public String[] getSelectedFiles() {
    if (this.selectedFiles == null) return new String[0];

    String[] files = new String[this.listModel.getSize()];

    for (int i = 0; i < files.length; i++) {
      files[i] = ((String)this.listModel.get(i));
    }

    return files;
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.addButton)
    {
      this.fileChooser.setCurrentDirectory(this.jarSplice.lastJarsDirectory);
      int value = this.fileChooser.showDialog(this, "Add");
      this.jarSplice.lastJarsDirectory = this.fileChooser.getCurrentDirectory();

      if (value == 0) {
        this.selectedFiles = this.fileChooser.getSelectedFiles();

        for (int i = 0; i < this.selectedFiles.length; i++) {
          this.listModel.removeElement(this.selectedFiles[i].getAbsolutePath());
          this.listModel.addElement(this.selectedFiles[i].getAbsolutePath());
        }
      }

    }
    else if (e.getSource() == this.removeButton) {
      Object[] selectedItems = this.list.getSelectedValues();
      for (int i = 0; i < selectedItems.length; i++)
        this.listModel.removeElement(selectedItems[i]);
    }
  }
}
