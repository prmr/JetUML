/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.AWTKeyStroke;
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

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.PointNode;

/**
 *  A tool bar that contains node and edge prototype icons.
 *  Exactly one icon is selected at any time.
 */
@SuppressWarnings("serial")
public class ToolBar extends JPanel
{
	private static final int BUTTON_SIZE = 25;
	private static final int OFFSET = 4;
	
	private ButtonGroup aGroup;
	private ArrayList<GraphElement> aTools;
	private JPopupMenu aPopupMenu = new JPopupMenu();
	private ActionListener aPopupListener;

	/**
     * Constructs a tool bar with no icons.
     * @param pGraph The graph associated with this toolbar.
	 */
	public ToolBar(Graph pGraph)
	{
		aGroup = new ButtonGroup();
		aTools = new ArrayList<>();

		Icon icon = new Icon()
		{
			public int getIconHeight() 
			{ return BUTTON_SIZE; }
            
			public int getIconWidth() 
			{ return BUTTON_SIZE; }
            
			public void paintIcon(Component pComponent, Graphics pGraphics, int pX, int pY)
            {
				Graphics2D g2 = (Graphics2D)pGraphics;
				GraphPanel.drawGrabber(g2, pX + OFFSET, pY + OFFSET);
				GraphPanel.drawGrabber(g2, pX + OFFSET, pY + BUTTON_SIZE - OFFSET);
				GraphPanel.drawGrabber(g2, pX + BUTTON_SIZE - OFFSET, pY + OFFSET);
				GraphPanel.drawGrabber(g2, pX + BUTTON_SIZE - OFFSET, pY + BUTTON_SIZE - OFFSET);
            }
		};
		final JToggleButton button = new JToggleButton(icon);
		ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		String tip = editorResources.getString("grabber.tooltip");
		button.setToolTipText(tip);
		aGroup.add(button);      
		add(button);
		button.setSelected(true);
		aTools.add(null);
      
		JMenuItem item = new JMenuItem(tip, icon);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				button.setSelected(true);
				if(aPopupListener != null)
				{
					aPopupListener.actionPerformed(pEvent);
				}
			}
		});
		aPopupMenu.add(item);
      
		ResourceBundle graphResources = ResourceBundle.getBundle(pGraph.getClass().getName() + "Strings");

		Node[] nodeTypes = pGraph.getNodePrototypes();
		for(int i = 0; i < nodeTypes.length; i++)
		{
			tip = graphResources.getString("node" + (i + 1) + ".tooltip");
			add(nodeTypes[i], tip);
		}
		Edge[] edgeTypes = pGraph.getEdgePrototypes();
		for(int i = 0; i < edgeTypes.length; i++)
		{
			tip = graphResources.getString("edge" + (i + 1) + ".tooltip");
			add(edgeTypes[i], tip);
		}
         
		// free up ctrl TAB for cycling windows
		Set<AWTKeyStroke> oldKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> newKeys = new HashSet<>();
		newKeys.addAll(oldKeys);
		newKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newKeys);
		oldKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		newKeys = new HashSet<>();
		newKeys.addAll(oldKeys);
		newKeys.remove(KeyStroke.getKeyStroke("ctrl shift TAB"));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newKeys);          
	}

	/**
     * Gets the node or edge prototype that is associated with
     * the currently selected button.
     * @return a Node or Edge prototype
	 */
	public Object getSelectedTool()
	{
		for(int i = 0; i < aTools.size(); i++)
		{
			JToggleButton button = (JToggleButton)getComponent(i);
			if (button.isSelected())
			{
				return aTools.get(i);
			}
		}
		return null;
	}

	/**
     * Adds a node to the tool bar.
     * @param pNode the node to add
     * @param pTip the tool tip
	 */
	public void add(final Node pNode, String pTip)
	{
		Icon icon = new Icon()
		{
            public int getIconHeight() 
            { return BUTTON_SIZE; }
            
            public int getIconWidth() 
            { return BUTTON_SIZE; }
            
            public void paintIcon(Component pComponent, Graphics pGraphic, int pX, int pY)
            {
            	double width = pNode.getBounds().getWidth();
            	double height = pNode.getBounds().getHeight();
               	Graphics2D g2 = (Graphics2D)pGraphic;
               	double scaleX = (BUTTON_SIZE - OFFSET)/ width;
               	double scaleY = (BUTTON_SIZE - OFFSET)/ height;
               	double scale = Math.min(scaleX, scaleY);

               	AffineTransform oldTransform = g2.getTransform();
               	g2.translate(pX, pY);
               	g2.scale(scale, scale);
               	g2.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
               	g2.setColor(Color.black);
               	pNode.draw(g2);
               	g2.setTransform(oldTransform);
            }
		};

		final JToggleButton button = new JToggleButton(icon);
		button.setToolTipText(pTip);
		aGroup.add(button);      
		add(button);
		aTools.add(pNode);
      
		JMenuItem item = new JMenuItem(pTip, icon);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
            {
				button.setSelected(true);
				if(aPopupListener != null)
				{
					aPopupListener.actionPerformed(pEvent);
				}
            }
		});
		aPopupMenu.add(item);
	}

	/**
	 * Show the popup menu corresponding to this toolbar.
	 * @param pPanel The panel associated with this menu.
	 * @param pPoint The point where to show the menu.
	 * @param pListener The listener for the menu selection.
	 */
	public void showPopup(GraphPanel pPanel, Point2D pPoint, ActionListener pListener) 
	{
		aPopupListener = pListener;
		aPopupMenu.show(pPanel, (int) pPoint.getX(), (int) pPoint.getY());
	}
   
	/**
     * Adds an edge to the tool bar.
     * @param pEdge the edge to add
     * @param pTip the tool tip
	 */
	public void add(final Edge pEdge, String pTip)
	{
		Icon icon = new Icon()
         {
            public int getIconHeight() 
            { return BUTTON_SIZE; }
            
            public int getIconWidth() 
            { return BUTTON_SIZE; }
            
            public void paintIcon(Component pComponent, Graphics pGraphics, int pX, int pY)
            {
            	Graphics2D g2 = (Graphics2D)pGraphics;
            	PointNode p = new PointNode();
            	p.translate(OFFSET, OFFSET);
            	PointNode q = new PointNode();
            	q.translate(BUTTON_SIZE - OFFSET, BUTTON_SIZE - OFFSET);
            	pEdge.connect(p, q);
               
            	Rectangle2D bounds = new Rectangle2D.Double();
            	bounds.add(p.getBounds());
            	bounds.add(q.getBounds());
            	bounds.add(pEdge.getBounds(g2));
               
            	double width = bounds.getWidth();
            	double height = bounds.getHeight();
            	double scaleX = (BUTTON_SIZE - OFFSET)/ width;
            	double scaleY = (BUTTON_SIZE - OFFSET)/ height;
            	double scale = Math.min(scaleX, scaleY);

            	AffineTransform oldTransform = g2.getTransform();
            	g2.translate(pX, pY);
            	g2.scale(scale, scale);
            	g2.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
                              
            	g2.setColor(Color.black);
            	pEdge.draw(g2);
            	g2.setTransform(oldTransform);
            }
         };
         final JToggleButton button = new JToggleButton(icon);               
         button.setToolTipText(pTip);
         aGroup.add(button);
         add(button);      
         aTools.add(pEdge);

         JMenuItem item = new JMenuItem(pTip, icon);
         item.addActionListener(new ActionListener()
         {
        	 public void actionPerformed(ActionEvent pEvent)
        	 {
        		 button.setSelected(true);
        		 if(aPopupListener != null)
        		 {
        			 aPopupListener.actionPerformed(pEvent);
        		 }
        	 }
         });
         aPopupMenu.add(item);
	}
}
