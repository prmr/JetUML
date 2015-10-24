/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
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
public class ToolBar2 extends JPanel
{
	private static final int BUTTON_SIZE = 25;
	private static final int OFFSET = 3;
	private ButtonGroup aGroup = new ButtonGroup();
	private ButtonGroup aGroupEx = new ButtonGroup();
	private ArrayList<JToggleButton> aButtons = new ArrayList<>();
	private ArrayList<JToggleButton> aButtonsEx = new ArrayList<>();
	private JPanel aToolPanel = new JPanel(new GridLayout(0, 1));
	private JPanel aToolPanelEx = new JPanel(new GridLayout(0, 1));
	private ArrayList<GraphElement> aTools = new ArrayList<>();
	private JPopupMenu aPopupMenu = new JPopupMenu();
	private ActionListener aPopupListener;

	/**
     * Constructs the tool bar.
     * @param pGraph The graph associated with this tool bar.
	 */
	public ToolBar2(Graph pGraph)
	{
		setLayout(new BorderLayout());
		
		createSelectionTool();
      
		ResourceBundle graphResources = ResourceBundle.getBundle(pGraph.getClass().getName() + "Strings");

		Node[] nodeTypes = pGraph.getNodePrototypes();
		for(int i = 0; i < nodeTypes.length; i++)
		{
			String tip = graphResources.getString("node" + (i + 1) + ".tooltip");
			add(nodeTypes[i], tip);
		}
		Edge[] edgeTypes = pGraph.getEdgePrototypes();
		for(int i = 0; i < edgeTypes.length; i++)
		{
			String tip = graphResources.getString("edge" + (i + 1) + ".tooltip");
			add(aToolPanel, edgeTypes[i], tip);
		}
		
		insertSpace();

		addCopyToClipboard();
		
		createExpandButton();
		
		freeCtrlTab();
		
		add(aToolPanel, BorderLayout.NORTH);
	}
	
	private static Icon createSelectionIcon()
	{
		return new Icon()
		{
			public int getIconHeight() 
			{ return BUTTON_SIZE; }
            
			public int getIconWidth() 
			{ return BUTTON_SIZE; }
            
			public void paintIcon(Component pComponent, Graphics pGraphics, int pX, int pY)
            {
				int offset = OFFSET+3;
				Graphics2D g2 = (Graphics2D)pGraphics;
				GraphPanel.drawGrabber(g2, pX + offset, pY + offset);
				GraphPanel.drawGrabber(g2, pX + offset, pY + BUTTON_SIZE - offset);
				GraphPanel.drawGrabber(g2, pX + BUTTON_SIZE - offset, pY + offset);
				GraphPanel.drawGrabber(g2, pX + BUTTON_SIZE - offset, pY + BUTTON_SIZE - offset);
            }
		};
	}

	/*
	 * Create the selection tool button and pop-up menu item.
	 * @param pPanel The panel to add the tool to
	 */
	private void createSelectionTool()
	{
		Icon icon = createSelectionIcon();
		ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		String tip = editorResources.getString("grabber.tooltip");
		final JToggleButton button = new JToggleButton(icon);
		button.setToolTipText(tip);
		aGroup.add(button);
		aButtons.add(button);
		aToolPanel.add(button);
		button.setSelected(true);
		aTools.add(null);
		
		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		final JToggleButton buttonEx = new JToggleButton(icon);
		buttonEx.setToolTipText(tip);
		aGroupEx.add(buttonEx);
		aButtonsEx.add(buttonEx);
		linePanel.add(buttonEx);
		JLabel label = new JLabel("Selection Tool");
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		linePanel.add(label);
		aToolPanelEx.add(linePanel);
		buttonEx.setSelected(true);
      
		JMenuItem item = new JMenuItem(tip, icon);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				button.setSelected(true);
				buttonEx.setSelected(true);
				if(aPopupListener != null)
				{
					aPopupListener.actionPerformed(pEvent);
				}
			}
		});
		aPopupMenu.add(item);
	}
	
	/*
	 * Insert space equivalent to one button.
	 */
	private void insertSpace()
	{
		aToolPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		aToolPanelEx.add(Box.createRigidArea(new Dimension(0, 15)));
	}
	
	/*
	 * Free up ctrl TAB for cycling windows
	 */
	private void freeCtrlTab()
	{
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
			JToggleButton button = aButtons.get(i);
			if (button.isSelected())
			{
				return aTools.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Overrides the currently selected tool to be the grabber tool instead.
	 */
	public void setToolToBeSelect()
	{
		for(int i = 0; i < aTools.size(); i++)
		{
			JToggleButton button = aButtons.get(i);
			if (button.isSelected())
			{
				button.setSelected(false);
			}
		}
		aButtons.get(0).setSelected(true);
	}

	public void addCopyToClipboard()
	{
		final JButton button = new JButton(new ImageIcon(getClass().getClassLoader().getResource(ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings").getString("toolbar.copyToClipBoard"))));
		button.setToolTipText("Tool tip");
		aToolPanel.add(button);
		
		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		final JButton buttonEx = new JButton(new ImageIcon(getClass().getClassLoader().getResource(ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings").getString("toolbar.copyToClipBoard"))));
		buttonEx.setToolTipText("Tool tip");
		linePanel.add(buttonEx);
		JLabel label = new JLabel("Selection Tool");
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		linePanel.add(label);
		aToolPanelEx.add(linePanel);
		
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				copyToClipboard();
			}
		});
		
		buttonEx.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				copyToClipboard();
			}
		});
	}
	
	private void copyToClipboard()
	{
		// Obtain the editor frame by going through the component graph
		Container parent = getParent();
		while( parent.getClass() != EditorFrame.class )
		{
			parent = parent.getParent();
		}
		((EditorFrame)parent).copyToClipboard();
	}
		
	public void createExpandButton()
	{
		final JButton expandButton = new JButton("<<");
		expandButton.setAlignmentX(CENTER_ALIGNMENT);
		expandButton.setToolTipText("Expand");
		expandButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
		expandButton.addActionListener(new ActionListener()
        {
       	 public void actionPerformed(ActionEvent pEvent)
       	 {
       		if(expandButton.getText().equals("<<"))
       		{
       			synchronizeToolSelection();
       			expandButton.setText(">>");
       			expandButton.setToolTipText("Collapse");
       			remove(aToolPanel);
       			add(aToolPanelEx, BorderLayout.NORTH);
       		}
       		else
       		{
       			synchronizeToolSelection();
       			expandButton.setText("<<");
       			expandButton.setToolTipText("Expand");
       			remove(aToolPanelEx);
       			add(aToolPanel, BorderLayout.NORTH);
       		}
       	 }
        });
		add(expandButton, BorderLayout.SOUTH);
	}
	
	private void synchronizeToolSelection()
	{
		if( isExpanded() )
		{
			int index = getSelectedButtonIndex(aButtonsEx);
			assert index >= 0;
			aButtons.get(index).setSelected(true);
		}
		else
		{
			int index = getSelectedButtonIndex(aButtons);
			assert index >= 0;
			aButtonsEx.get(index).setSelected(true);
		}
	}
	
	private int getSelectedButtonIndex(ArrayList<JToggleButton> pGroup)
	{
		for(int i = 0; i < aButtons.size(); i++)
		{
			JToggleButton button = aButtons.get(i);
			if(button.isSelected())
			{
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * The toolbar is expanded iff the main panel contains
	 * the expanded toolbar as one of its components.
	 */
	private boolean isExpanded()
	{
		for( Component component : getComponents() )
		{
			if( component == aToolPanelEx )
			{
				return true;
			}
		}
		return false;
	}
	
	/*
     * Adds a node to the tool bar.
     * @param pNode the node to add
     * @param pTip the tool tip
     * @param pPanel the panel to add the tool to
	 */
	private void add(final Node pNode, String pTip)
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
               	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
		aButtons.add(button);
		aToolPanel.add(button);
		aTools.add(pNode);
		
		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		final JToggleButton buttonEx = new JToggleButton(icon);
		buttonEx.setToolTipText(pTip);
		aGroupEx.add(buttonEx);   
		aButtonsEx.add(buttonEx);
		linePanel.add(buttonEx);
		JLabel label = new JLabel("Node Tool");
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		linePanel.add(label);
		aToolPanelEx.add(linePanel);
      
		JMenuItem item = new JMenuItem(pTip, icon);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
            {
				button.setSelected(true);
				buttonEx.setSelected(true);
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
   
	/*
     * Adds an edge to the tool bar.
     * @param pEdge the edge to add
     * @param pTip the tool tip
     * @param pPanel the Panel to add the edge tool to
	 */
	private void add(JPanel pPanel, final Edge pEdge, String pTip)
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
            	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            	
            	PointNode p = new PointNode();
            	p.translate(OFFSET, OFFSET);
            	PointNode q = new PointNode();
            	q.translate(BUTTON_SIZE - OFFSET, BUTTON_SIZE - OFFSET);
            	pEdge.connect(p, q);
               
            	Rectangle2D bounds = new Rectangle2D.Double();
            	bounds.add(p.getBounds());
            	bounds.add(q.getBounds());
            	bounds.add(pEdge.getBounds());
               
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
         aButtons.add(button);
         aToolPanel.add(button);
         aTools.add(pEdge);
         
 		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
         final JToggleButton buttonEx = new JToggleButton(icon);
         buttonEx.setToolTipText(pTip);
         aGroupEx.add(buttonEx);
         aButtonsEx.add(buttonEx);
         linePanel.add(buttonEx);
         JLabel label = new JLabel("Edge Tool");
 		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
 		linePanel.add(label);
         aToolPanelEx.add(linePanel);

         JMenuItem item = new JMenuItem(pTip, icon);
         item.addActionListener(new ActionListener()
         {
        	 public void actionPerformed(ActionEvent pEvent)
        	 {
        		 button.setSelected(true);
        		 buttonEx.setSelected(true);
        		 if(aPopupListener != null)
        		 {
        			 aPopupListener.actionPerformed(pEvent);
        		 }
        	 }
         });
         aPopupMenu.add(item);
	}
}