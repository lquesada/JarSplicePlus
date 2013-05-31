/*    */ package org.ninjacave.jarsplice.gui;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.CardLayout;
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.util.ArrayList;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JToggleButton;
/*    */ 
/*    */ public class TabPane extends JPanel
/*    */   implements ActionListener
/*    */ {
/* 18 */   ArrayList<JToggleButton> buttons = new ArrayList();
/*    */   JPanel topButtonPanel;
/*    */   JPanel bottomButtonPanel;
/*    */   JPanel cardPanel;
/* 22 */   CardLayout cards = new CardLayout();
/*    */ 
/*    */   public TabPane(JarSpliceFrame parent) {
/* 25 */     setLayout(new BorderLayout());
/*    */ 
/* 27 */     JPanel sideBarPanel = new JPanel(new BorderLayout());
/* 28 */     sideBarPanel.setBackground(Color.blue);
/*    */ 
/* 30 */     this.topButtonPanel = new JPanel(new GridLayout(0, 1));
/* 31 */     this.topButtonPanel.setPreferredSize(new Dimension(195, 240));
/* 32 */     this.topButtonPanel.setMaximumSize(new Dimension(195, 240));
/* 33 */     this.topButtonPanel.setMinimumSize(new Dimension(195, 240));
/*    */ 
/* 35 */     JPanel gapPanel = new JPanel(new BorderLayout());
/*    */ 
/* 37 */     this.bottomButtonPanel = new JPanel(new GridLayout(0, 1));
/* 38 */     this.bottomButtonPanel.setPreferredSize(new Dimension(195, 144));
/* 39 */     this.bottomButtonPanel.setMaximumSize(new Dimension(195, 144));
/* 40 */     this.bottomButtonPanel.setMinimumSize(new Dimension(195, 144));
/*    */ 
/* 42 */     sideBarPanel.add(this.topButtonPanel, "First");
/* 43 */     sideBarPanel.add(gapPanel, "Center");
/* 44 */     sideBarPanel.add(this.bottomButtonPanel, "Last");
/*    */ 
/* 46 */     add(sideBarPanel, "Before");
/*    */ 
/* 48 */     this.cardPanel = new JPanel(this.cards);
/* 49 */     add(this.cardPanel, "Center");
/*    */   }
/*    */ 
/*    */   public void addTab(String name, JPanel panel, boolean useTopButtonPanel) {
/* 53 */     addButton(new JToggleButton(name), useTopButtonPanel);
/* 54 */     this.cardPanel.add(panel, name);
/*    */   }
/*    */ 
/*    */   public void setTab(String name) {
/* 58 */     this.cards.show(this.cardPanel, name);
/*    */   }
/*    */ 
/*    */   private void addButton(JToggleButton button, boolean useTopButtonPanel) {
/* 62 */     button.setHorizontalAlignment(2);
/* 63 */     this.buttons.add(button);
/* 64 */     button.addActionListener(this);
/* 65 */     if (useTopButtonPanel) {
/* 66 */       this.topButtonPanel.add(button);
/*    */     }
/*    */     else {
/* 69 */       this.bottomButtonPanel.add(button);
/*    */     }
/*    */ 
/* 72 */     if (this.buttons.size() == 1)
/* 73 */       button.setSelected(true);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 79 */     for (int i = 0; i < this.buttons.size(); i++) {
/* 80 */       JToggleButton button = (JToggleButton)this.buttons.get(i);
/*    */ 
/* 82 */       if (e.getSource() == button) {
/* 83 */         button.setSelected(true);
/* 84 */         this.cards.show(this.cardPanel, button.getText());
/*    */       }
/*    */       else {
/* 87 */         button.setSelected(false);
/*    */       }
/*    */     }
/*    */   }
/*    */ }
