/*    */ package org.ninjacave.jarsplice.gui;
/*    */ 
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.border.TitledBorder;
/*    */ 
/*    */ public class IntroductionPanel extends JPanel
/*    */ {
/*    */   public IntroductionPanel()
/*    */   {
/* 46 */     TitledBorder border = BorderFactory.createTitledBorder("Introduction");
/* 47 */     border.setTitleJustification(2);
/* 48 */     setBorder(border);
/*    */ 
/* 50 */     JLabel label = new JLabel();
/* 51 */     label.setText(
/* 52 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 53 */       Integer.valueOf(300), 
/* 54 */       "JarSplice is a fat executable jar creator. It aims to make deployment of Java applications with multiple jars and/or natives easy by creating just a single executable jar from them. <br/><br/>Its easy to use, just complete the following four steps:<br/><br/>1) Add Jars<br/><br/>2) Add Natives<br/><br/>3) Add Main Class<br/><br/>4) Create Fat Jar" }));
/*    */ 
/* 65 */     add(label);
/*    */   }
/*    */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.IntroductionPanel
 * JD-Core Version:    0.6.2
 */