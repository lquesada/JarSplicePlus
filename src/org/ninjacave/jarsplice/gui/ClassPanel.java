/*     */ package org.ninjacave.jarsplice.gui;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.border.TitledBorder;
/*     */ 
/*     */ public class ClassPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   JarSpliceFrame jarSplice;
/*     */   JTextField classTextField;
/*     */   JTextField vmTextField;
/*     */   JPanel vmArgBox;
/*  57 */   JButton optionsButton = new JButton("Show Options");
/*     */ 
/*     */   public ClassPanel(JarSpliceFrame jarSplice) {
/*  60 */     this.jarSplice = jarSplice;
/*     */ 
/*  62 */     TitledBorder border = BorderFactory.createTitledBorder("Set Main Class");
/*  63 */     border.setTitleJustification(2);
/*  64 */     setBorder(border);
/*     */ 
/*  66 */     setLayout(new BorderLayout(5, 5));
/*     */ 
/*  68 */     add(getMainClassBox(), "First");
/*     */ 
/*  70 */     this.vmArgBox = getVmArgBox();
/*  71 */     add(this.vmArgBox, "Center");
/*     */ 
/*  73 */     this.vmArgBox.setVisible(false);
/*     */ 
/*  75 */     this.optionsButton.addActionListener(this);
/*  76 */     this.optionsButton.setPreferredSize(new Dimension(150, 30));
/*     */   }
/*     */ 
/*     */   protected JPanel getMainClassBox() {
/*  80 */     JPanel mainClassBox = new JPanel();
/*  81 */     mainClassBox.setLayout(new BorderLayout(5, 5));
/*  82 */     TitledBorder border1 = BorderFactory.createTitledBorder("Enter Main Class");
/*  83 */     mainClassBox.setBorder(border1);
/*     */ 
/*  85 */     JPanel centerPanel1 = new JPanel();
/*  86 */     JLabel label = new JLabel();
/*  87 */     label.setText(
/*  88 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/*  89 */       Integer.valueOf(300), 
/*  90 */       "Enter your applications main class file below, complete with any packages that it maybe in.<br><br>e.g. mypackage.someotherpackage.MainClass<br> " }));
/*     */ 
/*  97 */     centerPanel1.add(label);
/*  98 */     mainClassBox.add(centerPanel1, "First");
/*     */ 
/* 100 */     JPanel centerPanel2 = new JPanel();
/* 101 */     this.classTextField = new JTextField();
/* 102 */     this.classTextField.setPreferredSize(new Dimension(400, 30));
/* 103 */     this.classTextField.setMinimumSize(new Dimension(400, 30));
/* 104 */     this.classTextField.setMaximumSize(new Dimension(400, 30));
/* 105 */     this.classTextField.setText("");
/* 106 */     centerPanel2.add(this.classTextField);
/*     */ 
/* 108 */     mainClassBox.add(centerPanel2, "Center");
/*     */ 
/* 110 */     JPanel centerPanel3 = new JPanel();
/* 111 */     centerPanel3.setLayout(new FlowLayout(2));
/* 112 */     centerPanel3.add(this.optionsButton);
/*     */ 
/* 114 */     mainClassBox.add(centerPanel3, "Last");
/*     */ 
/* 116 */     return mainClassBox;
/*     */   }
/*     */ 
/*     */   protected JPanel getVmArgBox() {
/* 120 */     JPanel vmArgBox = new JPanel();
/* 121 */     vmArgBox.setLayout(new BorderLayout(5, 5));
/* 122 */     TitledBorder border2 = BorderFactory.createTitledBorder("Set VM Arguments");
/* 123 */     vmArgBox.setBorder(border2);
/*     */ 
/* 125 */     JPanel centerPanel3 = new JPanel();
/* 126 */     JLabel label2 = new JLabel();
/* 127 */     label2.setText(
/* 128 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 129 */       Integer.valueOf(300), 
/* 130 */       "Enter any java VM arguments that you would like to start the java virtual machine with. Leave blank if you are unsure whether you need to enter something here.<br><br>e.g. -Xms128m -Xmx512m<br> " }));
/*     */ 
/* 138 */     centerPanel3.add(label2);
/* 139 */     vmArgBox.add(centerPanel3, "First");
/*     */ 
/* 141 */     JPanel centerVmArgPanel = new JPanel();
/* 142 */     centerVmArgPanel.setLayout(new BorderLayout(5, 5));
/*     */ 
/* 144 */     JPanel centerPanel4 = new JPanel();
/* 145 */     this.vmTextField = new JTextField();
/* 146 */     this.vmTextField.setPreferredSize(new Dimension(400, 30));
/* 147 */     this.vmTextField.setMinimumSize(new Dimension(400, 30));
/* 148 */     this.vmTextField.setMaximumSize(new Dimension(400, 30));
/* 149 */     this.vmTextField.setText("");
/* 150 */     centerPanel4.add(this.vmTextField);
/*     */ 
/* 152 */     centerVmArgPanel.add(centerPanel4, "First");
/*     */ 
/* 154 */     JPanel centerPanel5 = new JPanel();
/* 155 */     JLabel label3 = new JLabel();
/* 156 */     label3.setText(
/* 157 */       String.format("<html><div style=\"width:%dpx;\">%s</div><html>", new Object[] { 
/* 158 */       Integer.valueOf(300), 
/* 159 */       "Note: Do not use the -cp and -Djava.library.path VM arguments as JarSplice uses them internally and adds them automatically.<br> " }));
/*     */ 
/* 163 */     centerPanel5.add(label3);
/* 164 */     centerVmArgPanel.add(centerPanel5, "Center");
/*     */ 
/* 166 */     vmArgBox.add(centerVmArgPanel, "Center");
/*     */ 
/* 168 */     return vmArgBox;
/*     */   }
/*     */ 
/*     */   public String getMainClass() {
/* 172 */     return this.classTextField.getText();
/*     */   }
/*     */ 
/*     */   public String getVmArgs() {
/* 176 */     return this.vmTextField.getText();
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 180 */     if (e.getSource() == this.optionsButton)
/* 181 */       if (this.vmArgBox.isVisible()) {
/* 182 */         this.vmArgBox.setVisible(false);
/* 183 */         this.optionsButton.setText("Show Options");
/*     */       }
/*     */       else {
/* 186 */         this.vmArgBox.setVisible(true);
/* 187 */         this.optionsButton.setText("Hide Options");
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/elezeta/Descargas/jarsplice-0.40.jar
 * Qualified Name:     org.ninjacave.jarsplice.gui.ClassPanel
 * JD-Core Version:    0.6.2
 */