/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
 
/**
 * Component to be used as tabComponent.
 * Contains a JLabel to show the text and 
 * a JButton to close the tab it belongs to 
 */
@SuppressWarnings("serial")
public class ButtonTabComponent extends JPanel 
{
	private static final int TAB_SIZE = 17;
    private static final int LABEL_BORDER = 5;
    private static final int DELTA = 6;
    private static final MouseListener BUTTON_MOUSE_LISTENER = new MouseAdapter() 
    {
  	   public void mouseEntered(MouseEvent pEvent) 
         {
             Component component = pEvent.getComponent();
             if (component instanceof AbstractButton) 
             {
                 AbstractButton button = (AbstractButton) component;
                 button.setBorderPainted(true);
             }
         }

         public void mouseExited(MouseEvent pEvent) 
         {
             Component component = pEvent.getComponent();
             if (component instanceof AbstractButton) 
             {
                 AbstractButton button = (AbstractButton) component;
                 button.setBorderPainted(false);
             }
         }
      };
	private final EditorFrame aEditorFrame;
	private final JInternalFrame aJInternalFrame;
    private final JTabbedPane aPane;
   
 
    /**
     * @param pEditorFrame The EditorFrame that houses the JTabbedView.
     * @param pJInternalFrame The JInternalFrame that maps to this Button.
     * @param pJTabbedPane The JTabbedPane where this button will be placed.
     */
    public ButtonTabComponent(EditorFrame pEditorFrame, JInternalFrame pJInternalFrame, final JTabbedPane pJTabbedPane) 
    {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pEditorFrame == null) 
        {
            throw new NullPointerException("EditorFrame is null");
        }
        aEditorFrame = pEditorFrame;
        if (pJInternalFrame == null) 
        {
            throw new NullPointerException("GraphFrame is null");
        }
        aJInternalFrame = pJInternalFrame;
        if (pJTabbedPane == null) 
        {
            throw new NullPointerException("TabbedPane is null");
        }
        aPane = pJTabbedPane;
        setOpaque(false);
         
        //make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() 
        {
            public String getText() 
            {
                int i = aPane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) 
                {
                    return aPane.getTitleAt(i);
                }
                return null;
            }
        };
         
        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, LABEL_BORDER));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
 
    private class TabButton extends JButton implements ActionListener 
    {
        TabButton() 
        {
            int size = TAB_SIZE;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(BUTTON_MOUSE_LISTENER);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }
 
        public void actionPerformed(ActionEvent pEvent) 
        {
            int i = aPane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) 
            {
                aEditorFrame.close(aJInternalFrame);
            }
        }
 
        //we don't want to update UI for this button
        public void updateUI() 
        {
        }
 
        //paint the cross
        protected void paintComponent(Graphics pGraphics) 
        {
            super.paintComponent(pGraphics);
            Graphics2D g2 = (Graphics2D) pGraphics.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) 
            {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) 
            {
                g2.setColor(Color.MAGENTA);
            }
            int delta = DELTA;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
}