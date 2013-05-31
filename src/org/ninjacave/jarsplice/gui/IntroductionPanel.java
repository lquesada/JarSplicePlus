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

	  //LABEL MODIFIED AS TO STATE THAT JARSPLICEPLUS IS AN EXTENSION TO JARSPLICE
      //THE NEW LABEL ALSO STATES HOW TO SEE THE COMMAND-LINE OPTIONS.
    
    label.setText(
      String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
      Integer.valueOf(300), 
      "JarSplicePlus is an extension to JarSplice.<br/>"+
      "In order to see the command-line options, run:<pre>$ java -jar JarSplicePlus.jar -h</pre><br/>"+
      "JarSplice is a fat executable jar creator. It aims to make deployment of Java applications with multiple jars and/or natives easy by creating just a single executable jar from them."+
      "<br/><br/>Its easy to use, just complete the following four steps:<br/><br/>1) Add Jars<br/><br/>2) Add Natives<br/><br/>3) Add Main Class<br/><br/>4) Create Fat Jar" }));

    add(label);
  }
}
