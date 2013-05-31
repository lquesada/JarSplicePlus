package org.ninjacave.jarsplice.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import org.ninjacave.jarsplice.core.ShellScriptSplicer;

public class ShellScriptPanel extends JPanel
  implements ActionListener
{
  JFileChooser fileChooser;
  JButton shellScriptButton;
  JarSpliceFrame jarSplice;
  ShellScriptSplicer shellScriptSplicer = new ShellScriptSplicer();

  public ShellScriptPanel(JarSpliceFrame jarSplice) {
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
        return filename.endsWith(".sh");
      }
      public String getDescription() {
        return "*.sh";
      }
    };
    this.fileChooser.setFileFilter(filter);

    setLayout(new BorderLayout(20, 20));

    TitledBorder border = BorderFactory.createTitledBorder("Create Linux ShellScript");
    border.setTitleJustification(2);
    setBorder(border);

    JPanel panel1 = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "This is an optional step and will create a Linux shellscript. This shellscript will contain all the jars and natives just like the executable jar. If there are native files then only the Linux native files (*.so) will be added to the shellscript." }));

    panel1.add(label);
    add(panel1, "First");

    JPanel panel2 = new JPanel();
    this.shellScriptButton = new JButton("Create Linux ShellScript");
    this.shellScriptButton.addActionListener(this);
    panel2.add(this.shellScriptButton);

    add(panel2, "Center");
  }

  public String getOutputFile(File file) {
    String outputFile = file.getAbsolutePath();

    if (!outputFile.endsWith(".sh")) {
      outputFile = outputFile + ".sh";
    }

    return outputFile;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.shellScriptButton)
    {
      this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
      int value = this.fileChooser.showSaveDialog(this);
      this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();

      if (value == 0) {
        String[] sources = this.jarSplice.getJarsList();
        String[] natives = this.jarSplice.getNativesList();
        String output = getOutputFile(this.fileChooser.getSelectedFile());
        String mainClass = this.jarSplice.getMainClass();
        String vmArgs = this.jarSplice.getVmArgs();
        try
        {
          this.shellScriptSplicer.createFatJar(sources, natives, output, mainClass, vmArgs);

          JOptionPane.showMessageDialog(this, 
            "ShellScript Successfully Created.", 
            "Success", -1);
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, 
            "ShellScript creation failed due to the following exception:\n" + ex.getMessage(), 
            "Failed", 0);
        }

        System.out.println("File Saved as " + output);
      }
    }
  }
}
