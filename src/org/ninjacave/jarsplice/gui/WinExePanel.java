/*     */ package org.ninjacave.jarsplice.gui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
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
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ import org.ninjacave.jarsplice.core.WinExeSplicer;
/*     */ 
/*     */ public class WinExePanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   JFileChooser fileChooser;
/*     */   JButton winExeButton;
/*     */   JButton iconButton;
/*     */   JarSpliceFrame jarSplice;
/*  62 */   WinExeSplicer winExeSplicer = new WinExeSplicer();
/*     */ 
/*     */   public WinExePanel(JarSpliceFrame jarSplice) {
/*  65 */     this.jarSplice = jarSplice;
/*     */ 
/*  68 */     UIManager.put("FileChooser.readOnly", Boolean.TRUE);
/*     */ 
/*  70 */     this.fileChooser = getFileChooser();
/*     */ 
/*  72 */     setLayout(new BorderLayout(5, 20));
/*     */ 
/*  74 */     TitledBorder border = BorderFactory.createTitledBorder("Create EXE file for Windows");
/*  75 */     border.setTitleJustification(2);
/*  76 */     setBorder(border);
/*     */ 
/*  78 */     add(createAppPanel(), "First");
/*     */ 
/*  80 */     add(createButtonPanel(), "Center");
/*     */   }
/*     */ 
/*     */   private JPanel createAppPanel() {
/*  84 */     JPanel descriptionPanel = new JPanel();
/*  85 */     JLabel label = new JLabel();
/*  86 */     label.setText(
/*  87 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/*  88 */       Integer.valueOf(300), 
/*  89 */       "This is an optional step and will create a Windows EXE File. " }));
/*     */ 
/*  91 */     descriptionPanel.add(label);
/*     */ 
/*  93 */     JPanel panel = new JPanel();
/*  94 */     panel.setLayout(new BorderLayout(5, 20));
/*     */ 
/*  98 */     panel.add(descriptionPanel, "First");
/*     */ 
/* 101 */     return panel;
/*     */   }
/*     */ 
/*     */   private JPanel createButtonPanel() {
/* 105 */     JPanel buttonPanel = new JPanel();
/* 106 */     this.winExeButton = new JButton("Create Windows EXE file");
/* 107 */     this.winExeButton.addActionListener(this);
/* 108 */     buttonPanel.add(this.winExeButton);
/*     */ 
/* 110 */     return buttonPanel;
/*     */   }
/*     */ 
/*     */   public JPanel createIconPanel()
/*     */   {
/* 115 */     JPanel selectPanel = new JPanel();
/*     */ 
/* 117 */     selectPanel.setLayout(new FlowLayout(1, 0, 0));
/*     */ 
/* 119 */     JPanel pathPanel = new JPanel();
/* 120 */     JTextField textField = new JTextField("image.png");
/* 121 */     textField.setPreferredSize(new Dimension(300, 30));
/* 122 */     textField.setMinimumSize(new Dimension(300, 30));
/* 123 */     textField.setMaximumSize(new Dimension(300, 30));
/*     */ 
/* 126 */     pathPanel.add(textField);
/*     */ 
/* 128 */     JPanel buttonPanel = new JPanel();
/* 129 */     this.iconButton = new JButton("Select Icon");
/* 130 */     this.iconButton.addActionListener(this);
/* 131 */     buttonPanel.add(this.iconButton);
/*     */ 
/* 133 */     selectPanel.add(pathPanel);
/* 134 */     selectPanel.add(buttonPanel);
/*     */ 
/* 136 */     JPanel panel = new JPanel();
/* 137 */     panel.setLayout(new BorderLayout(5, 20));
/* 138 */     TitledBorder border1 = BorderFactory.createTitledBorder("Set Exe Icon");
/* 139 */     panel.setBorder(border1);
/*     */ 
/* 141 */     JPanel descriptionPanel = new JPanel();
/* 142 */     JLabel label = new JLabel();
/* 143 */     label.setText(
/* 144 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 145 */       Integer.valueOf(300), 
/* 146 */       "Select the icon the exe will use. This should be in the*.png file format." }));
/*     */ 
/* 149 */     descriptionPanel.add(label);
/*     */ 
/* 151 */     panel.add(selectPanel, "First");
/* 152 */     panel.add(descriptionPanel, "Center");
/*     */ 
/* 154 */     return panel;
/*     */   }
/*     */ 
/*     */   public String getOutputFile(File file) {
/* 158 */     String outputFile = file.getAbsolutePath();
/*     */ 
/* 160 */     if (!outputFile.endsWith(".exe")) {
/* 161 */       outputFile = outputFile + ".exe";
/*     */     }
/*     */ 
/* 164 */     return outputFile;
/*     */   }
/*     */ 
/*     */   private JFileChooser getFileChooser() {
/* 168 */     this.fileChooser = new JFileChooser() {
/*     */       public void approveSelection() {
/* 170 */         File f = getSelectedFile();
/* 171 */         if ((f.exists()) && (getDialogType() == 1)) {
/* 172 */           int result = 
/* 173 */             JOptionPane.showConfirmDialog(
/* 174 */             this, "The file already exists. Do you want to overwrite it?", 
/* 175 */             "Confirm Replace", 0);
/* 176 */           switch (result) {
/*     */           case 0:
/* 178 */             super.approveSelection();
/* 179 */             return;
/*     */           case 1:
/* 181 */             return;
/*     */           case 2:
/* 183 */             return;
/*     */           }
/*     */         }
/* 186 */         super.approveSelection();
/*     */       }
/*     */     };
/* 188 */     this.fileChooser.setAcceptAllFileFilterUsed(false);
/*     */ 
/* 190 */     FileFilter filter = new FileFilter() {
/*     */       public boolean accept(File file) {
/* 192 */         if (file.isDirectory()) return true;
/* 193 */         String filename = file.getName();
/* 194 */         return filename.endsWith(".exe");
/*     */       }
/*     */       public String getDescription() {
/* 197 */         return "*.exe";
/*     */       }
/*     */     };
/* 201 */     this.fileChooser.setFileFilter(filter);
/*     */ 
/* 203 */     return this.fileChooser;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 208 */     if (e.getSource() == this.winExeButton)
/*     */     {
/* 210 */       this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 211 */       int value = this.fileChooser.showSaveDialog(this);
/* 212 */       this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
/*     */ 
/* 214 */       if (value == 0) {
/* 215 */         String[] sources = this.jarSplice.getJarsList();
/* 216 */         String[] natives = this.jarSplice.getNativesList();
/* 217 */         String output = getOutputFile(this.fileChooser.getSelectedFile());
/* 218 */         String mainClass = this.jarSplice.getMainClass();
/* 219 */         String vmArgs = this.jarSplice.getVmArgs();
/*     */         try
/*     */         {
/* 222 */           this.winExeSplicer.createFatJar(sources, natives, output, mainClass, vmArgs);
/*     */ 
/* 224 */           JOptionPane.showMessageDialog(this, 
/* 225 */             "EXE Successfully Created.", 
/* 226 */             "Success", -1);
/*     */         }
/*     */         catch (Exception ex) {
/* 229 */           ex.printStackTrace();
/* 230 */           JOptionPane.showMessageDialog(this, 
/* 231 */             "EXE creation failed due to the following exception:\n" + ex.getMessage(), 
/* 232 */             "Failed", 0);
/*     */         }
/*     */ 
/* 235 */         System.out.println("File Saved as " + output);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.WinExePanel
 * JD-Core Version:    0.6.2
 */