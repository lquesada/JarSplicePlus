/*     */ package org.ninjacave.jarsplice.gui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
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
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.PlainDocument;
/*     */ import org.ninjacave.jarsplice.core.MacAppSplicer;
/*     */ 
/*     */ public class MacAppPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   JFileChooser fileChooser;
/*     */   JFileChooser iconChooser;
/*     */   JButton macAppButton;
/*     */   JButton iconButton;
/*     */   JarSpliceFrame jarSplice;
/*  66 */   MacAppSplicer macAppSplicer = new MacAppSplicer();
/*     */   JTextField nameTextField;
/*     */   JTextField iconTextField;
/*     */ 
/*     */   public MacAppPanel(JarSpliceFrame jarSplice)
/*     */   {
/*  71 */     this.jarSplice = jarSplice;
/*     */ 
/*  74 */     UIManager.put("FileChooser.readOnly", Boolean.TRUE);
/*     */ 
/*  76 */     this.fileChooser = getFileChooser();
/*  77 */     this.iconChooser = getIconChooser();
/*     */ 
/*  79 */     setLayout(new BorderLayout(5, 20));
/*     */ 
/*  81 */     TitledBorder border = BorderFactory.createTitledBorder("Create OS X APP Bundle");
/*  82 */     border.setTitleJustification(2);
/*  83 */     setBorder(border);
/*     */ 
/*  85 */     add(createDescriptionPanel(), "First");
/*     */ 
/*  87 */     JPanel panel1 = new JPanel(new BorderLayout());
/*  88 */     panel1.add(createNamePanel(), "First");
/*  89 */     add(panel1, "Center");
/*     */ 
/*  91 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  92 */     panel2.add(createIconPanel(), "First");
/*  93 */     panel1.add(panel2, "Center");
/*     */ 
/*  95 */     JPanel panel3 = new JPanel();
/*  96 */     panel3.add(new JLabel(), "First");
/*  97 */     panel3.add(createButtonPanel(), "Center");
/*  98 */     panel2.add(panel3, "Center");
/*     */   }
/*     */ 
/*     */   private JPanel createDescriptionPanel()
/*     */   {
/* 106 */     JPanel descriptionPanel = new JPanel();
/* 107 */     JLabel label = new JLabel();
/* 108 */     label.setText(
/* 109 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 110 */       Integer.valueOf(300), 
/* 111 */       "This is an optional step and will create an OS X APP Bundle. If there are native files then only the Mac native files (*.jnilib and *.dylib) will be added to the APP Bundle." }));
/*     */ 
/* 115 */     descriptionPanel.add(label);
/*     */ 
/* 125 */     return descriptionPanel;
/*     */   }
/*     */ 
/*     */   private JPanel createButtonPanel() {
/* 129 */     JPanel buttonPanel = new JPanel();
/* 130 */     this.macAppButton = new JButton("Create OS X APP Bundle");
/* 131 */     this.macAppButton.addActionListener(this);
/* 132 */     buttonPanel.add(this.macAppButton);
/*     */ 
/* 134 */     return buttonPanel;
/*     */   }
/*     */ 
/*     */   public JPanel createNamePanel()
/*     */   {
/* 139 */     JPanel selectPanel = new JPanel();
/* 140 */     selectPanel.setLayout(new FlowLayout(1, 0, 0));
/*     */ 
/* 142 */     JPanel pathPanel = new JPanel();
/* 143 */     this.nameTextField = new JTextField("");
/* 144 */     this.nameTextField.setDocument(new JTextFieldLimit(32));
/* 145 */     this.nameTextField.setPreferredSize(new Dimension(380, 30));
/* 146 */     this.nameTextField.setMinimumSize(new Dimension(380, 30));
/* 147 */     this.nameTextField.setMaximumSize(new Dimension(380, 30));
/* 148 */     pathPanel.add(this.nameTextField);
/*     */ 
/* 150 */     selectPanel.add(pathPanel);
/*     */ 
/* 153 */     JPanel panel = new JPanel();
/* 154 */     panel.setLayout(new BorderLayout(5, 20));
/* 155 */     TitledBorder border1 = BorderFactory.createTitledBorder("Set APP Bundle Name");
/* 156 */     panel.setBorder(border1);
/*     */ 
/* 158 */     JPanel descriptionPanel = new JPanel();
/* 159 */     JLabel label = new JLabel();
/* 160 */     label.setText(
/* 161 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 162 */       Integer.valueOf(300), 
/* 163 */       "Set the name of the APP Bundle." }));
/*     */ 
/* 165 */     descriptionPanel.add(label);
/*     */ 
/* 167 */     panel.add(selectPanel, "Center");
/* 168 */     panel.add(descriptionPanel, "First");
/* 169 */     panel.add(new JLabel(), "Last");
/*     */ 
/* 171 */     return panel;
/*     */   }
/*     */ 
/*     */   public JPanel createIconPanel()
/*     */   {
/* 176 */     JPanel selectPanel = new JPanel();
/*     */ 
/* 178 */     selectPanel.setLayout(new FlowLayout(1, 0, 0));
/*     */ 
/* 180 */     JPanel pathPanel = new JPanel();
/* 181 */     this.iconTextField = new JTextField("");
/* 182 */     this.iconTextField.setPreferredSize(new Dimension(280, 30));
/* 183 */     this.iconTextField.setMinimumSize(new Dimension(280, 30));
/* 184 */     this.iconTextField.setMaximumSize(new Dimension(280, 30));
/* 185 */     pathPanel.add(this.iconTextField);
/*     */ 
/* 187 */     JPanel buttonPanel = new JPanel();
/* 188 */     this.iconButton = new JButton("Select Icon");
/* 189 */     this.iconButton.addActionListener(this);
/* 190 */     buttonPanel.add(this.iconButton);
/*     */ 
/* 192 */     selectPanel.add(pathPanel);
/* 193 */     selectPanel.add(buttonPanel);
/*     */ 
/* 195 */     JPanel panel = new JPanel();
/* 196 */     panel.setLayout(new BorderLayout(5, 20));
/* 197 */     TitledBorder border1 = BorderFactory.createTitledBorder("Set APP Bundle Icon");
/* 198 */     panel.setBorder(border1);
/*     */ 
/* 200 */     JPanel descriptionPanel = new JPanel();
/* 201 */     JLabel label = new JLabel();
/* 202 */     label.setText(
/* 203 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 204 */       Integer.valueOf(300), 
/* 205 */       "Select the icon the app bundle will use. This should be in the Apple Icon Image format (*.icns)." }));
/*     */ 
/* 208 */     descriptionPanel.add(label);
/*     */ 
/* 210 */     panel.add(selectPanel, "Center");
/* 211 */     panel.add(descriptionPanel, "First");
/* 212 */     panel.add(new JLabel(), "Last");
/*     */ 
/* 214 */     return panel;
/*     */   }
/*     */ 
/*     */   public String getOutputFile(File file) {
/* 218 */     String outputFile = file.getAbsolutePath();
/*     */ 
/* 220 */     if (!outputFile.endsWith(".zip")) {
/* 221 */       outputFile = outputFile + ".zip";
/*     */     }
/*     */ 
/* 224 */     return outputFile;
/*     */   }
/*     */ 
/*     */   private JFileChooser getFileChooser() {
/* 228 */     this.fileChooser = new JFileChooser() {
/*     */       public void approveSelection() {
/* 230 */         File f = getSelectedFile();
/* 231 */         if ((f.exists()) && (getDialogType() == 1)) {
/* 232 */           int result = 
/* 233 */             JOptionPane.showConfirmDialog(
/* 234 */             this, "The file already exists. Do you want to overwrite it?", 
/* 235 */             "Confirm Replace", 0);
/* 236 */           switch (result) {
/*     */           case 0:
/* 238 */             super.approveSelection();
/* 239 */             return;
/*     */           case 1:
/* 241 */             return;
/*     */           case 2:
/* 243 */             return;
/*     */           }
/*     */         }
/* 246 */         super.approveSelection();
/*     */       }
/*     */     };
/* 248 */     this.fileChooser.setAcceptAllFileFilterUsed(false);
/*     */ 
/* 250 */     FileFilter filter = new FileFilter() {
/*     */       public boolean accept(File file) {
/* 252 */         if (file.isDirectory()) return true;
/* 253 */         String filename = file.getName();
/* 254 */         return filename.endsWith(".zip");
/*     */       }
/*     */       public String getDescription() {
/* 257 */         return "*.zip";
/*     */       }
/*     */     };
/* 261 */     this.fileChooser.setFileFilter(filter);
/*     */ 
/* 263 */     return this.fileChooser;
/*     */   }
/*     */ 
/*     */   private JFileChooser getIconChooser() {
/* 267 */     this.iconChooser = new JFileChooser();
/*     */ 
/* 269 */     this.iconChooser.setAcceptAllFileFilterUsed(false);
/*     */ 
/* 271 */     FileFilter filter = new FileFilter() {
/*     */       public boolean accept(File file) {
/* 273 */         if (file.isDirectory()) return true;
/* 274 */         String filename = file.getName();
/* 275 */         return filename.endsWith(".icns");
/*     */       }
/*     */       public String getDescription() {
/* 278 */         return "*.icns";
/*     */       }
/*     */     };
/* 282 */     this.iconChooser.setFileFilter(filter);
/*     */ 
/* 284 */     return this.iconChooser;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 289 */     if (e.getSource() == this.macAppButton)
/*     */     {
/* 291 */       this.fileChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 292 */       int value = this.fileChooser.showSaveDialog(this);
/* 293 */       this.jarSplice.lastDirectory = this.fileChooser.getCurrentDirectory();
/*     */ 
/* 295 */       if (value == 0) {
/* 296 */         String[] sources = this.jarSplice.getJarsList();
/* 297 */         String[] natives = this.jarSplice.getNativesList();
/* 298 */         String output = getOutputFile(this.fileChooser.getSelectedFile());
/* 299 */         String mainClass = this.jarSplice.getMainClass();
/* 300 */         String vmArgs = this.jarSplice.getVmArgs();
/*     */         try
/*     */         {
/* 303 */           this.macAppSplicer.createAppBundle(sources, natives, output, mainClass, vmArgs, this.nameTextField.getText(), this.iconTextField.getText());
/*     */ 
/* 305 */           JOptionPane.showMessageDialog(this, 
/* 306 */             "APP Bundle Successfully Created.", 
/* 307 */             "Success", -1);
/*     */         }
/*     */         catch (Exception ex) {
/* 310 */           ex.printStackTrace();
/* 311 */           JOptionPane.showMessageDialog(this, 
/* 312 */             "APP Bundle creation failed due to the following exception:\n" + ex.getMessage(), 
/* 313 */             "Failed", 0);
/*     */         }
/*     */ 
/* 316 */         System.out.println("File Saved as " + output);
/*     */       }
/*     */     }
/* 319 */     else if (e.getSource() == this.iconButton) {
/* 320 */       this.iconChooser.setCurrentDirectory(this.jarSplice.lastDirectory);
/* 321 */       int value = this.iconChooser.showDialog(this, "Add");
/* 322 */       this.jarSplice.lastDirectory = this.iconChooser.getCurrentDirectory();
/*     */ 
/* 324 */       if (value == 0) {
/* 325 */         File iconFile = this.iconChooser.getSelectedFile();
/*     */         try {
/* 327 */           this.iconTextField.setText(iconFile.getCanonicalPath());
/*     */         } catch (IOException e1) {
/* 329 */           e1.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public class JTextFieldLimit extends PlainDocument
/*     */   {
/*     */     private int limit;
/*     */ 
/*     */     JTextFieldLimit(int limit) {
/* 340 */       this.limit = limit;
/*     */     }
/*     */ 
/*     */     public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
/*     */     {
/* 345 */       if (str == null) {
/* 346 */         return;
/*     */       }
/* 348 */       if (getLength() + str.length() <= this.limit)
/* 349 */         super.insertString(offset, str, attr);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.MacAppPanel
 * JD-Core Version:    0.6.2
 */