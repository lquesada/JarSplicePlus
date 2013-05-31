/*     */ package org.ninjacave.jarsplice.gui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ import org.ninjacave.jarsplice.core.ShellScriptSplicer;
/*     */ 
/*     */ public class ShellScriptPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   JFileChooser fileChooser;
/*     */   JButton shellScriptButton;
/*     */   JarSpliceFrame jarSplice;
/*  58 */   ShellScriptSplicer shellScriptSplicer = new ShellScriptSplicer();
/*     */ 
/*     */   public ShellScriptPanel(JarSpliceFrame jarSplice) {
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
/*  89 */         return filename.endsWith(".sh");
/*     */       }
/*     */       public String getDescription() {
/*  92 */         return "*.sh";
/*     */       }
/*     */     };
/*  96 */     this.fileChooser.setFileFilter(filter);
/*     */ 
/*  98 */     setLayout(new BorderLayout(20, 20));
/*     */ 
/* 100 */     TitledBorder border = BorderFactory.createTitledBorder("Create Linux ShellScript");
/* 101 */     border.setTitleJustification(2);
/* 102 */     setBorder(border);
/*     */ 
/* 104 */     JPanel panel1 = new JPanel();
/* 105 */     JLabel label = new JLabel();
/* 106 */     label.setText(
/* 107 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 108 */       Integer.valueOf(300), 
/* 109 */       "This is an optional step and will create a Linux shellscript. This shellscript will contain all the jars and natives just like the executable jar. If there are native files then only the Linux native files (*.so) will be added to the shellscript." }));
/*     */ 
/* 114 */     panel1.add(label);
/* 115 */     add(panel1, "First");
/*     */ 
/* 117 */     JPanel panel2 = new JPanel();
/* 118 */     this.shellScriptButton = new JButton("Create Linux ShellScript");
/* 119 */     this.shellScriptButton.addActionListener(this);
/* 120 */     panel2.add(this.shellScriptButton);
/*     */ 
/* 122 */     add(panel2, "Center");
/*     */   }
/*     */ 
/*     */   public String getOutputFile(File file) {
/* 126 */     String outputFile = file.getAbsolutePath();
/*     */ 
/* 128 */     if (!outputFile.endsWith(".sh")) {
/* 129 */       outputFile = outputFile + ".sh";
/*     */     }
/*     */ 
/* 132 */     return outputFile;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 137 */     if (e.getSource() == this.shellScriptButton)
/*     */     {
/* 139 */       this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 140 */       int value = this.fileChooser.showSaveDialog(this);
/* 141 */       this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
/*     */ 
/* 143 */       if (value == 0) {
/* 144 */         String[] sources = this.jarSplice.getJarsList();
/* 145 */         String[] natives = this.jarSplice.getNativesList();
/* 146 */         String output = getOutputFile(this.fileChooser.getSelectedFile());
/* 147 */         String mainClass = this.jarSplice.getMainClass();
/* 148 */         String vmArgs = this.jarSplice.getVmArgs();
/*     */         try
/*     */         {
/* 151 */           this.shellScriptSplicer.createFatJar(sources, natives, output, mainClass, vmArgs);
/*     */ 
/* 153 */           JOptionPane.showMessageDialog(this, 
/* 154 */             "ShellScript Successfully Created.", 
/* 155 */             "Success", -1);
/*     */         }
/*     */         catch (Exception ex) {
/* 158 */           ex.printStackTrace();
/* 159 */           JOptionPane.showMessageDialog(this, 
/* 160 */             "ShellScript creation failed due to the following exception:\n" + ex.getMessage(), 
/* 161 */             "Failed", 0);
/*     */         }
/*     */ 
/* 164 */         System.out.println("File Saved as " + output);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.ShellScriptPanel
 * JD-Core Version:    0.6.2
 */