/*    */ package org.ninjacave.jarsplice.gui;
/*    */ 
/*    */ import java.io.File;
/*    */ import javax.swing.JFrame;
/*    */ 
/*    */ public class JarSpliceFrame
/*    */ {
/* 42 */   IntroductionPanel introPanel = new IntroductionPanel();
/* 43 */   JarsPanel jarsPanel = new JarsPanel(this);
/* 44 */   NativesPanel nativesPanel = new NativesPanel(this);
/* 45 */   ClassPanel classPanel = new ClassPanel(this);
/* 46 */   CreatePanel createPanel = new CreatePanel(this);
/* 47 */   ShellScriptPanel shellScriptPanel = new ShellScriptPanel(this);
/* 48 */   MacAppPanel macAppPanel = new MacAppPanel(this);
/* 49 */   WinExePanel exePanel = new WinExePanel(this);
/*    */   public File lastDirectory;
/*    */ 
/*    */   public JarSpliceFrame()
/*    */   {
/* 64 */     JFrame frame = new JFrame("JarSplice - The Fat Jar Creator - version 0.40");
/*    */ 
/* 66 */     TabPane tabPane = new TabPane(this);
/*    */ 
/* 68 */     tabPane.addTab("INTRODUCTION", this.introPanel, true);
/* 69 */     tabPane.addTab("1) ADD JARS", this.jarsPanel, true);
/* 70 */     tabPane.addTab("2) ADD NATIVES", this.nativesPanel, true);
/* 71 */     tabPane.addTab("3) MAIN CLASS", this.classPanel, true);
/* 72 */     tabPane.addTab("4) CREATE FAT JAR", this.createPanel, true);
/*    */ 
/* 74 */     tabPane.addTab("EXTRA (LINUX .SH)", this.shellScriptPanel, false);
/* 75 */     tabPane.addTab("EXTRA (MAC .APP)", this.macAppPanel, false);
/* 76 */     tabPane.addTab("EXTRA (WINDOWS .EXE)", this.exePanel, false);
/*    */ 
/* 78 */     frame.add(tabPane, "Center");
/*    */ 
/* 80 */     frame.setSize(640, 480);
/* 81 */     frame.setVisible(true);
/* 82 */     frame.setDefaultCloseOperation(3);
/*    */   }
/*    */ 
/*    */   public String[] getJarsList() {
/* 86 */     return this.jarsPanel.getSelectedFiles();
/*    */   }
/*    */ 
/*    */   public String[] getNativesList() {
/* 90 */     return this.nativesPanel.getSelectedFiles();
/*    */   }
/*    */ 
/*    */   public String getMainClass() {
/* 94 */     return this.classPanel.getMainClass();
/*    */   }
/*    */ 
/*    */   public String getVmArgs() {
/* 98 */     return this.classPanel.getVmArgs();
/*    */   }
/*    */ }
