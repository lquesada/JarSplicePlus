package org.ninjacave.jarsplice.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class TabPane extends JPanel
  implements ActionListener
{
  ArrayList<JToggleButton> buttons = new ArrayList();
  JPanel topButtonPanel;
  JPanel bottomButtonPanel;
  JPanel cardPanel;
  CardLayout cards = new CardLayout();

  public TabPane(JarSpliceFrame parent) {
    setLayout(new BorderLayout());

    JPanel sideBarPanel = new JPanel(new BorderLayout());
    sideBarPanel.setBackground(Color.blue);

    this.topButtonPanel = new JPanel(new GridLayout(0, 1));
    this.topButtonPanel.setPreferredSize(new Dimension(195, 240));
    this.topButtonPanel.setMaximumSize(new Dimension(195, 240));
    this.topButtonPanel.setMinimumSize(new Dimension(195, 240));

    JPanel gapPanel = new JPanel(new BorderLayout());

    this.bottomButtonPanel = new JPanel(new GridLayout(0, 1));
    this.bottomButtonPanel.setPreferredSize(new Dimension(195, 144));
    this.bottomButtonPanel.setMaximumSize(new Dimension(195, 144));
    this.bottomButtonPanel.setMinimumSize(new Dimension(195, 144));

    sideBarPanel.add(this.topButtonPanel, "First");
    sideBarPanel.add(gapPanel, "Center");
    sideBarPanel.add(this.bottomButtonPanel, "Last");

    add(sideBarPanel, "Before");

    this.cardPanel = new JPanel(this.cards);
    add(this.cardPanel, "Center");
  }

  public void addTab(String name, JPanel panel, boolean useTopButtonPanel) {
    addButton(new JToggleButton(name), useTopButtonPanel);
    this.cardPanel.add(panel, name);
  }

  public void setTab(String name) {
    this.cards.show(this.cardPanel, name);
  }

  private void addButton(JToggleButton button, boolean useTopButtonPanel) {
    button.setHorizontalAlignment(2);
    this.buttons.add(button);
    button.addActionListener(this);
    if (useTopButtonPanel) {
      this.topButtonPanel.add(button);
    }
    else {
      this.bottomButtonPanel.add(button);
    }

    if (this.buttons.size() == 1)
      button.setSelected(true);
  }

  public void actionPerformed(ActionEvent e)
  {
    for (int i = 0; i < this.buttons.size(); i++) {
      JToggleButton button = (JToggleButton)this.buttons.get(i);

      if (e.getSource() == button) {
        button.setSelected(true);
        this.cards.show(this.cardPanel, button.getText());
      }
      else {
        button.setSelected(false);
      }
    }
  }
}
