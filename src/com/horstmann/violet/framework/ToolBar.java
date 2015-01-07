/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import com.horstmann.violet.PointNode;


/**
   A tool bar that contains node and edge prototype icons.
   Exactly one icon is selected at any time.
*/
public class ToolBar extends JPanel
{
   /**
      Constructs a tool bar with no icons.
   */
   public ToolBar(Graph graph)
   {
      group = new ButtonGroup();
      tools = new ArrayList();

      Icon icon = new       
         Icon()
         {
            public int getIconHeight() { return BUTTON_SIZE; }
            public int getIconWidth() { return BUTTON_SIZE; }
            public void paintIcon(Component c, Graphics g,
               int x, int y)
            {
               Graphics2D g2 = (Graphics2D)g;
               GraphPanel.drawGrabber(g2, x + OFFSET, y + OFFSET);
               GraphPanel.drawGrabber(g2, x + OFFSET, y + BUTTON_SIZE - OFFSET);
               GraphPanel.drawGrabber(g2, x + BUTTON_SIZE - OFFSET, y + OFFSET);
               GraphPanel.drawGrabber(g2, x + BUTTON_SIZE - OFFSET, y + BUTTON_SIZE - OFFSET);
            }
         };
      final JToggleButton button = new JToggleButton(icon);
      ResourceBundle editorResources = 
         ResourceBundle.getBundle("com.horstmann.violet.framework.EditorStrings");
      String tip = editorResources.getString("grabber.tooltip");
      button.setToolTipText(tip);
      group.add(button);      
      add(button);
      button.setSelected(true);
      tools.add(null);
      
      JMenuItem item = new JMenuItem(tip, icon);
      item.addActionListener(new
            ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  button.setSelected(true);
                  if (popupListener != null)
                     popupListener.actionPerformed(event);
               }
            });
      popup.add(item);
      
      
      ResourceBundle graphResources = 
         ResourceBundle.getBundle(graph.getClass().getName() + "Strings");

      Node[] nodeTypes = graph.getNodePrototypes();
      for (int i = 0; i < nodeTypes.length; i++)
      {
         tip = graphResources.getString("node" + (i + 1) + ".tooltip");
         add(nodeTypes[i], tip);
      }
      Edge[] edgeTypes = graph.getEdgePrototypes();
      for (int i = 0; i < edgeTypes.length; i++)
      {
         tip = graphResources.getString("edge" + (i + 1) + ".tooltip");
         add(edgeTypes[i], tip);
      }
         
      // free up ctrl TAB for cycling windows
      Set oldKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
      HashSet newKeys = new HashSet();
      newKeys.addAll(oldKeys);
      newKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
      setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newKeys);
      oldKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
      newKeys = new HashSet();
      newKeys.addAll(oldKeys);
      newKeys.remove(KeyStroke.getKeyStroke("ctrl shift TAB"));
      setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newKeys);          
   }

   /**
      Gets the node or edge prototype that is associated with
      the currently selected button
      @return a Node or Edge prototype
   */
   public Object getSelectedTool()
   {
      for (int i = 0; i < tools.size(); i++)
      {
         JToggleButton button = (JToggleButton)getComponent(i);
         if (button.isSelected()) return tools.get(i);
      }
      return null;
   }

   /**
      Adds a node to the tool bar.
      @param n the node to add
      @param tip the tool tip
   */
   public void add(final Node n, String tip)
   {
      Icon icon = new
         Icon()
         {
            public int getIconHeight() { return BUTTON_SIZE; }
            public int getIconWidth() { return BUTTON_SIZE; }
            public void paintIcon(Component c, Graphics g,
                  int x, int y)
            {
               double width = n.getBounds().getWidth();
               double height = n.getBounds().getHeight();
               Graphics2D g2 = (Graphics2D)g;
               double scaleX = (BUTTON_SIZE - OFFSET)/ width;
               double scaleY = (BUTTON_SIZE - OFFSET)/ height;
               double scale = Math.min(scaleX, scaleY);

               AffineTransform oldTransform = g2.getTransform();
               g2.translate(x, y);
               g2.scale(scale, scale);
               g2.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
               g2.setColor(Color.black);
               n.draw(g2);
               g2.setTransform(oldTransform);
            }
         };

      final JToggleButton button = new JToggleButton(icon);
      button.setToolTipText(tip);
      group.add(button);      
      add(button);
      tools.add(n);
      
      JMenuItem item = new JMenuItem(tip, icon);
      item.addActionListener(new
            ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  button.setSelected(true);
                  if (popupListener != null)
                     popupListener.actionPerformed(event);
               }
            });
      popup.add(item);
   }

   public void showPopup(final GraphPanel panel, final Point2D p, final ActionListener listener) 
   {
      /*
      JPopupMenu popup = new JPopupMenu();
      Enumeration buttons = group.getElements();
      int i = 0;
      while (buttons.hasMoreElements())
      {
         final Object tool = tools.get(i);
         i++;
         final JToggleButton button = (JToggleButton) buttons.nextElement();
         JMenuItem item = new JMenuItem(button.getToolTipText(), button.getIcon());
         item.addActionListener(new
               ActionListener()
               {
                  public void actionPerformed(ActionEvent event)
                  {
                     button.setSelected(true);
                     listener.actionPerformed(event);
                  }
               });
         popup.add(item);
      }
      */
      popupListener = listener;
      popup.show(panel, (int) p.getX(), (int) p.getY());
   }
   
   /**
      Adds an edge to the tool bar.
      @param n the node to add
      @param tip the tool tip
   */
   public void add(final Edge e, String tip)
   {
      Icon icon = new      
         Icon()
         {
            public int getIconHeight() { return BUTTON_SIZE; }
            public int getIconWidth() { return BUTTON_SIZE; }
            public void paintIcon(Component c, Graphics g,
               int x, int y)
            {
               Graphics2D g2 = (Graphics2D)g;
               PointNode p = new PointNode();
               p.translate(OFFSET, OFFSET);
               PointNode q = new PointNode();
               q.translate(BUTTON_SIZE - OFFSET, BUTTON_SIZE - OFFSET);
               e.connect(p, q);
               
               Rectangle2D bounds = new Rectangle2D.Double();
               bounds.add(p.getBounds());
               bounds.add(q.getBounds());
               bounds.add(e.getBounds(g2));
               
               double width = bounds.getWidth();
               double height = bounds.getHeight();
               double scaleX = (BUTTON_SIZE - OFFSET)/ width;
               double scaleY = (BUTTON_SIZE - OFFSET)/ height;
               double scale = Math.min(scaleX, scaleY);

               AffineTransform oldTransform = g2.getTransform();
               g2.translate(x, y);
               g2.scale(scale, scale);
               g2.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
                              
               g2.setColor(Color.black);
               e.draw(g2);
               g2.setTransform(oldTransform);
            }
         };
      final JToggleButton button = new JToggleButton(icon);               
      button.setToolTipText(tip);
      group.add(button);
      add(button);      
      tools.add(e);

      JMenuItem item = new JMenuItem(tip, icon);
      item.addActionListener(new
            ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  button.setSelected(true);
                  if (popupListener != null)
                     popupListener.actionPerformed(event);
               }
            });
      popup.add(item);
   }
 
   private ButtonGroup group;
   private ArrayList tools;
   private JPopupMenu popup = new JPopupMenu();
   private ActionListener popupListener;

   private static final int BUTTON_SIZE = 25;
   private static final int OFFSET = 4;
}
