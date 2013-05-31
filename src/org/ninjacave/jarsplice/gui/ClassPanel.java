package org.ninjacave.jarsplice.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ClassPanel extends JPanel
  implements ActionListener
{
  JarSpliceFrame jarSplice;
  JTextField classTextField;
  JTextField vmTextField;
  JPanel vmArgBox;
  JButton optionsButton = new JButton("Show Options");

  public ClassPanel(JarSpliceFrame jarSplice) {
    this.jarSplice = jarSplice;

    TitledBorder border = BorderFactory.createTitledBorder("Set Main Class");
    border.setTitleJustification(2);
    setBorder(border);

    setLayout(new BorderLayout(5, 5));

    add(getMainClassBox(), "First");

    this.vmArgBox = getVmArgBox();
    add(this.vmArgBox, "Center");

    this.vmArgBox.setVisible(false);

    this.optionsButton.addActionListener(this);
    this.optionsButton.setPreferredSize(new Dimension(150, 30));
  }

  protected JPanel getMainClassBox() {
    JPanel mainClassBox = new JPanel();
    mainClassBox.setLayout(new BorderLayout(5, 5));
    TitledBorder border1 = BorderFactory.createTitledBorder("Enter Main Class");
    mainClassBox.setBorder(border1);

    JPanel centerPanel1 = new JPanel();
    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "Enter your applications main class file below, complete with any packages that it maybe in.<br><br>e.g. mypackage.someotherpackage.MainClass<br> " }));

    centerPanel1.add(label);
    mainClassBox.add(centerPanel1, "First");

    JPanel centerPanel2 = new JPanel();
    this.classTextField = new JTextField();
    this.classTextField.setPreferredSize(new Dimension(400, 30));
    this.classTextField.setMinimumSize(new Dimension(400, 30));
    this.classTextField.setMaximumSize(new Dimension(400, 30));
    this.classTextField.setText("");
    centerPanel2.add(this.classTextField);

    mainClassBox.add(centerPanel2, "Center");

    JPanel centerPanel3 = new JPanel();
    centerPanel3.setLayout(new FlowLayout(2));
    centerPanel3.add(this.optionsButton);

    mainClassBox.add(centerPanel3, "Last");

    return mainClassBox;
  }

  protected JPanel getVmArgBox() {
    JPanel vmArgBox = new JPanel();
    vmArgBox.setLayout(new BorderLayout(5, 5));
    TitledBorder border2 = BorderFactory.createTitledBorder("Set VM Arguments");
    vmArgBox.setBorder(border2);

    JPanel centerPanel3 = new JPanel();
    JLabel label2 = new JLabel();
    label2.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "Enter any java VM arguments that you would like to start the java virtual machine with. Leave blank if you are unsure whether you need to enter something here.<br><br>e.g. -Xms128m -Xmx512m<br> " }));

    centerPanel3.add(label2);
    vmArgBox.add(centerPanel3, "First");

    JPanel centerVmArgPanel = new JPanel();
    centerVmArgPanel.setLayout(new BorderLayout(5, 5));

    JPanel centerPanel4 = new JPanel();
    this.vmTextField = new JTextField();
    this.vmTextField.setPreferredSize(new Dimension(400, 30));
    this.vmTextField.setMinimumSize(new Dimension(400, 30));
    this.vmTextField.setMaximumSize(new Dimension(400, 30));
    this.vmTextField.setText("");
    centerPanel4.add(this.vmTextField);

    centerVmArgPanel.add(centerPanel4, "First");

    JPanel centerPanel5 = new JPanel();
    JLabel label3 = new JLabel();
    label3.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "Note: Do not use the -cp and -Djava.library.path VM arguments as JarSplice uses them internally and adds them automatically.<br> " }));

    centerPanel5.add(label3);
    centerVmArgPanel.add(centerPanel5, "Center");

    vmArgBox.add(centerVmArgPanel, "Center");

    return vmArgBox;
  }

  public String getMainClass() {
    return this.classTextField.getText();
  }

  public String getVmArgs() {
    return this.vmTextField.getText();
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.optionsButton)
      if (this.vmArgBox.isVisible()) {
        this.vmArgBox.setVisible(false);
        this.optionsButton.setText("Show Options");
      }
      else {
        this.vmArgBox.setVisible(true);
        this.optionsButton.setText("Hide Options");
      }
  }
}
