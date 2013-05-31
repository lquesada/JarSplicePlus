/*     */ package org.ninjacave.jarsplice.gui;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ import org.ninjacave.jarsplice.core.Splicer;
/*     */ 
/*     */ public class CreatePanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   JFileChooser fileChooser;
/*     */   JButton createButton;
/*     */   JarSpliceFrame jarSplice;
/*  58 */   Splicer splicer = new Splicer();
/*     */ 
/*     */   public CreatePanel(JarSpliceFrame jarSplice) {
/*  61 */     this.jarSplice = jarSplice;
/*     */ 
/*  63 */     this.fileChooser = new JFileChooser() {
/*     */       public void approveSelection() {
/*  65 */         File f = getSelectedFile();
/*  66 */         if ((f.exists()) && (getDialogType() == 1)) {
/*  67 */           int result = 
/*  68 */             JOptionPane.showConfirmDialog(
/*  69 */             this, "The file already exists. Do you want to overwrite it?", 
/*  70 */             "Confirm Replace", 0);
/*  71 */           switch (result) {
/*     */           case 0:
/*  73 */             super.approveSelection();
/*  74 */             return;
/*     */           case 1:
/*  76 */             return;
/*     */           case 2:
/*  78 */             return;
/*     */           }
/*     */         }
/*  81 */         super.approveSelection();
/*     */       }
/*     */     };
/*  83 */     this.fileChooser.setAcceptAllFileFilterUsed(false);
/*     */ 
/*  85 */     FileFilter filter = new FileFilter() {
/*     */       public boolean accept(File file) {
/*  87 */         if (file.isDirectory()) return true;
/*  88 */         String filename = file.getName();
/*  89 */         return filename.endsWith(".jar");
/*     */       }
/*     */       public String getDescription() {
/*  92 */         return "*.jar";
/*     */       }
/*     */     };
/*  96 */     this.fileChooser.setFileFilter(filter);
/*     */ 
/*  98 */     TitledBorder border = BorderFactory.createTitledBorder("Create Fat Jar");
/*  99 */     border.setTitleJustification(2);
/* 100 */     setBorder(border);
/*     */ 
/* 102 */     JPanel buttonPanel = new JPanel();
/*     */ 
/* 104 */     this.createButton = new JButton("Create Fat Jar");
/* 105 */     this.createButton.addActionListener(this);
/* 106 */     buttonPanel.add(this.createButton);
/*     */ 
/* 108 */     add(buttonPanel);
/*     */   }
/*     */ 
/*     */   public String getOutputFile(File file) {
/* 112 */     String outputFile = file.getAbsolutePath();
/*     */ 
/* 114 */     if (!outputFile.endsWith(".jar")) {
/* 115 */       outputFile = outputFile + ".jar";
/*     */     }
/*     */ 
/* 118 */     return outputFile;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 123 */     if (e.getSource() == this.createButton) {
/* 124 */       this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 125 */       int value = this.fileChooser.showSaveDialog(this);
/* 126 */       this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
/*     */ 
/* 128 */       if (value == 0)
/*     */       {
/* 130 */         String[] jars = this.jarSplice.getJarsList();
/* 131 */         String[] natives = this.jarSplice.getNativesList();
/* 132 */         String output = getOutputFile(this.fileChooser.getSelectedFile());
/* 133 */         String mainClass = this.jarSplice.getMainClass();
/* 134 */         String vmArgs = this.jarSplice.getVmArgs();
/*     */         try
/*     */         {
/* 137 */           this.splicer.createFatJar(jars, natives, output, mainClass, vmArgs);
/*     */ 
/* 139 */           JOptionPane.showMessageDialog(this, 
/* 140 */             "Fat Jar Successfully Created.", 
/* 141 */             "Success", -1);
/*     */         }
/*     */         catch (Exception ex) {
/* 144 */           ex.printStackTrace();
/* 145 */           JOptionPane.showMessageDialog(this, 
/* 146 */             "Jar creation failed due to the following exception:\n" + ex.getMessage(), 
/* 147 */             "Failed", 0);
/*     */         }
/*     */ 
/* 150 */         System.out.println("File Saved as " + output);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.CreatePanel
 * JD-Core Version:    0.6.2
 */