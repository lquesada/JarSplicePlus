/*     */ package org.ninjacave.jarsplice.gui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ 
/*     */ public class JarsPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   JarSpliceFrame jarSplice;
/*     */   JFileChooser fileChooser;
/*     */   JList list;
/*  54 */   DefaultListModel listModel = new DefaultListModel();
/*     */   JButton addButton;
/*     */   JButton removeButton;
/*     */   File[] selectedFiles;
/*     */ 
/*     */   public JarsPanel(JarSpliceFrame jarSplice)
/*     */   {
/*  61 */     this.jarSplice = jarSplice;
/*     */ 
/*  63 */     this.fileChooser = new JFileChooser();
/*  64 */     this.fileChooser.setMultiSelectionEnabled(true);
/*  65 */     this.fileChooser.setAcceptAllFileFilterUsed(false);
/*     */ 
/*  67 */     FileFilter filter = new FileFilter() {
/*     */       public boolean accept(File file) {
/*  69 */         if (file.isDirectory()) return true;
/*  70 */         String filename = file.getName();
/*  71 */         return (filename.endsWith(".jar")) || (filename.endsWith(".zip"));
/*     */       }
/*     */       public String getDescription() {
/*  74 */         return "*.jar, *.zip";
/*     */       }
/*     */     };
/*  78 */     this.fileChooser.setFileFilter(filter);
/*     */ 
/*  81 */     setLayout(new BorderLayout(5, 5));
/*     */ 
/*  83 */     this.list = new JList(this.listModel);
/*  84 */     add(this.list, "Center");
/*     */ 
/*  86 */     TitledBorder border = BorderFactory.createTitledBorder("Add Jars");
/*  87 */     border.setTitleJustification(2);
/*  88 */     setBorder(border);
/*     */ 
/*  90 */     JPanel buttonPanel = new JPanel();
/*     */ 
/*  92 */     this.addButton = new JButton("Add Jar(s)");
/*  93 */     this.addButton.addActionListener(this);
/*  94 */     buttonPanel.add(this.addButton);
/*     */ 
/*  96 */     this.removeButton = new JButton("Remove Jar(s)");
/*  97 */     this.removeButton.addActionListener(this);
/*  98 */     buttonPanel.add(this.removeButton);
/*     */ 
/* 100 */     add(buttonPanel, "Last");
/*     */   }
/*     */ 
/*     */   public String[] getSelectedFiles() {
/* 104 */     if (this.selectedFiles == null) return new String[0];
/*     */ 
/* 106 */     String[] files = new String[this.listModel.getSize()];
/*     */ 
/* 108 */     for (int i = 0; i < files.length; i++) {
/* 109 */       files[i] = ((String)this.listModel.get(i));
/*     */     }
/*     */ 
/* 113 */     return files;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 117 */     if (e.getSource() == this.addButton)
/*     */     {
/* 119 */       this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 120 */       int value = this.fileChooser.showDialog(this, "Add");
/* 121 */       this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
/*     */ 
/* 123 */       if (value == 0) {
/* 124 */         this.selectedFiles = this.fileChooser.getSelectedFiles();
/*     */ 
/* 126 */         for (int i = 0; i < this.selectedFiles.length; i++) {
/* 127 */           this.listModel.removeElement(this.selectedFiles[i].getAbsolutePath());
/* 128 */           this.listModel.addElement(this.selectedFiles[i].getAbsolutePath());
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 133 */     else if (e.getSource() == this.removeButton) {
/* 134 */       Object[] selectedItems = this.list.getSelectedValues();
/* 135 */       for (int i = 0; i < selectedItems.length; i++)
/* 136 */         this.listModel.removeElement(selectedItems[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.JarsPanel
 * JD-Core Version:    0.6.2
 */