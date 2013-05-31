package org.ninjacave.jarsplice.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class IntroductionPanel extends JPanel
{
  public IntroductionPanel()
  {
    TitledBorder border = BorderFactory.createTitledBorder("Introduction");
    border.setTitleJustification(2);
    setBorder(border);

    JLabel label = new JLabel();
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "JarSplice is a fat executable jar creator. It aims to make deployment of Java applications with multiple jars and/or natives easy by creating just a single executable jar from them. <br/><br/>Its easy to use, just complete the following four steps:<br/><br/>1) Add Jars<br/><br/>2) Add Natives<br/><br/>3) Add Main Class<br/><br/>4) Create Fat Jar" }));

    add(label);
  }
}
