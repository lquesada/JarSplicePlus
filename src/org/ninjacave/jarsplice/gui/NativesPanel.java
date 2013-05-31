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
/*     */ public class NativesPanel extends JPanel
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
/*     */   public NativesPanel(JarSpliceFrame jarSplice)
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
/*     */ 
/*  74 */         return (filename.endsWith(".dll")) || 
/*  72 */           (filename.endsWith(".so")) || 
/*  73 */           (filename.endsWith(".jnilib")) || 
/*  74 */           (filename.endsWith(".dylib"));
/*     */       }
/*     */       public String getDescription() {
/*  77 */         return "*.dll, *.so, *.jnilib, *.dylib";
/*     */       }
/*     */     };
/*  81 */     this.fileChooser.setFileFilter(filter);
/*     */ 
/*  83 */     setLayout(new BorderLayout(5, 5));
/*     */ 
/*  85 */     this.list = new JList(this.listModel);
/*  86 */     add(this.list, "Center");
/*     */ 
/*  88 */     TitledBorder border = BorderFactory.createTitledBorder("Add Natives");
/*  89 */     border.setTitleJustification(2);
/*  90 */     setBorder(border);
/*     */ 
/*  92 */     JPanel buttonPanel = new JPanel();
/*     */ 
/*  94 */     this.addButton = new JButton("Add Native(s)");
/*  95 */     this.addButton.addActionListener(this);
/*  96 */     buttonPanel.add(this.addButton);
/*     */ 
/*  98 */     this.removeButton = new JButton("Remove Native(s)");
/*  99 */     this.removeButton.addActionListener(this);
/* 100 */     buttonPanel.add(this.removeButton);
/*     */ 
/* 102 */     add(buttonPanel, "Last");
/*     */   }
/*     */ 
/*     */   public String[] getSelectedFiles() {
/* 106 */     if (this.selectedFiles == null) return new String[0];
/*     */ 
/* 108 */     String[] files = new String[this.listModel.getSize()];
/*     */ 
/* 110 */     for (int i = 0; i < files.length; i++) {
/* 111 */       files[i] = ((String)this.listModel.get(i));
/*     */     }
/*     */ 
/* 115 */     return files;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 119 */     if (e.getSource() == this.addButton)
/*     */     {
/* 121 */       this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 122 */       int value = this.fileChooser.showDialog(this, "Add");
/* 123 */       this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
/*     */ 
/* 125 */       if (value == 0) {
/* 126 */         this.selectedFiles = this.fileChooser.getSelectedFiles();
/*     */ 
/* 128 */         for (int i = 0; i < this.selectedFiles.length; i++) {
/* 129 */           this.listModel.removeElement(this.selectedFiles[i].getAbsolutePath());
/* 130 */           this.listModel.addElement(this.selectedFiles[i].getAbsolutePath());
/*     */         }
/*     */       }
/*     */     }
/* 134 */     else if (e.getSource() == this.removeButton) {
/* 135 */       Object[] selectedItems = this.list.getSelectedValues();
/* 136 */       for (int i = 0; i < selectedItems.length; i++)
/* 137 */         this.listModel.removeElement(selectedItems[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.NativesPanel
 * JD-Core Version:    0.6.2
 */