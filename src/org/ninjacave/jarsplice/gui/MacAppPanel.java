package org.ninjacave.jarsplice.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.ninjacave.jarsplice.core.MacAppSplicer;

public class MacAppPanel extends JPanel
  implements ActionListener
{
  JFileChooser fileChooser;
  JFileChooser iconChooser;
  JButton macAppButton;
  JButton iconButton;
  JarSpliceFrame jarSplice;
  MacAppSplicer macAppSplicer = new MacAppSplicer();
  JTextField nameTextField;
  JTextField iconTextField;

  public MacAppPanel(JarSpliceFrame jarSplice)
  {
    this.jarSplice = jarSplice;

    UIManager.put("FileChooser.readOnly", Boolean.TRUE);

    this.fileChooser = getFileChooser();
    this.iconChooser = getIconChooser();

    setLayout(new BorderLayout(5, 20));

    TitledBorder border = BorderFactory.createTitledBorder("Create OS X APP Bundle");
    border.setTitleJustification(2);
    setBorder(border);

    add(createDescriptionPanel(), "First");

    JPanel panel1 = new JPanel(new BorderLayout());
    panel1.add(createNamePanel(), "First");
    add(panel1, "Center");

    JPanel panel2 = new JPanel(new BorderLayout());
    panel2.add(createIconPanel(), "First");
    panel1.add(panel2, "Center");

    JPanel panel3 = new JPanel();
    panel3.add(new JLabel(), "First");
    panel3.add(createButtonPanel(), "Center");
    panel2.add(panel3, "Center");
  }

  private JPanel createDescriptionPanel()
  {
    JPanel descriptionPanel = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "This is an optional step and will create an OS X APP Bundle. If there are native files then only the Mac native files (*.jnilib and *.dylib) will be added to the APP Bundle." }));

    descriptionPanel.add(label);

    return descriptionPanel;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel();
    this.macAppButton = new JButton("Create OS X APP Bundle");
    this.macAppButton.addActionListener(this);
    buttonPanel.add(this.macAppButton);

    return buttonPanel;
  }

  public JPanel createNamePanel()
  {
    JPanel selectPanel = new JPanel();
    selectPanel.setLayout(new FlowLayout(1, 0, 0));

    JPanel pathPanel = new JPanel();
    this.nameTextField = new JTextField("");
    this.nameTextField.setDocument(new JTextFieldLimit(32));
    this.nameTextField.setPreferredSize(new Dimension(380, 30));
    this.nameTextField.setMinimumSize(new Dimension(380, 30));
    this.nameTextField.setMaximumSize(new Dimension(380, 30));
    pathPanel.add(this.nameTextField);

    selectPanel.add(pathPanel);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(5, 20));
    TitledBorder border1 = BorderFactory.createTitledBorder("Set APP Bundle Name");
    panel.setBorder(border1);

    JPanel descriptionPanel = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "Set the name of the APP Bundle." }));

    descriptionPanel.add(label);

    panel.add(selectPanel, "Center");
    panel.add(descriptionPanel, "First");
    panel.add(new JLabel(), "Last");

    return panel;
  }

  public JPanel createIconPanel()
  {
    JPanel selectPanel = new JPanel();

    selectPanel.setLayout(new FlowLayout(1, 0, 0));

    JPanel pathPanel = new JPanel();
    this.iconTextField = new JTextField("");
    this.iconTextField.setPreferredSize(new Dimension(280, 30));
    this.iconTextField.setMinimumSize(new Dimension(280, 30));
    this.iconTextField.setMaximumSize(new Dimension(280, 30));
    pathPanel.add(this.iconTextField);

    JPanel buttonPanel = new JPanel();
    this.iconButton = new JButton("Select Icon");
    this.iconButton.addActionListener(this);
    buttonPanel.add(this.iconButton);

    selectPanel.add(pathPanel);
    selectPanel.add(buttonPanel);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(5, 20));
    TitledBorder border1 = BorderFactory.createTitledBorder("Set APP Bundle Icon");
    panel.setBorder(border1);

    JPanel descriptionPanel = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "Select the icon the app bundle will use. This should be in the Apple Icon Image format (*.icns)." }));

    descriptionPanel.add(label);

    panel.add(selectPanel, "Center");
    panel.add(descriptionPanel, "First");
    panel.add(new JLabel(), "Last");

    return panel;
  }

  public String getOutputFile(File file) {
    String outputFile = file.getAbsolutePath();

    if (!outputFile.endsWith(".zip")) {
      outputFile = outputFile + ".zip";
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
        return filename.endsWith(".zip");
      }
      public String getDescription() {
        return "*.zip";
      }
    };
    this.fileChooser.setFileFilter(filter);

    return this.fileChooser;
  }

  private JFileChooser getIconChooser() {
    this.iconChooser = new JFileChooser();

    this.iconChooser.setAcceptAllFileFilterUsed(false);

    FileFilter filter = new FileFilter() {
      public boolean accept(File file) {
        if (file.isDirectory()) return true;
        String filename = file.getName();
        return filename.endsWith(".icns");
      }
      public String getDescription() {
        return "*.icns";
      }
    };
    this.iconChooser.setFileFilter(filter);

    return this.iconChooser;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.macAppButton)
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
          this.macAppSplicer.createAppBundle(sources, natives, output, mainClass, vmArgs, this.nameTextField.getText(), this.iconTextField.getText());

          JOptionPane.showMessageDialog(this, 
            "APP Bundle Successfully Created.", 
            "Success", -1);
        }
        catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, 
            "APP Bundle creation failed due to the following exception:\n" + ex.getMessage(), 
            "Failed", 0);
        }

        System.out.println("File Saved as " + output);
      }
    }
    else if (e.getSource() == this.iconButton) {
      this.iconChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
      int value = this.iconChooser.showDialog(this, "Add");
      this.jarSplice.lastDirectory = this.iconChooser.getCurrentDirectory();

      if (value == 0) {
        File iconFile = this.iconChooser.getSelectedFile();
        try {
          this.iconTextField.setText(iconFile.getCanonicalPath());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  public class JTextFieldLimit extends PlainDocument
  {
    private int limit;

    JTextFieldLimit(int limit) {
      this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
    {
      if (str == null) {
        return;
      }
      if (getLength() + str.length() <= this.limit)
        super.insertString(offset, str, attr);
    }
  }
}
