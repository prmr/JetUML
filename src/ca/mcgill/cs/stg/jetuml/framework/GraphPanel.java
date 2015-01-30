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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * A panel to draw a graph.
 */
@SuppressWarnings("serial")
public class GraphPanel extends JPanel
{
	private static final int DRAG_NONE = 0;
	private static final int DRAG_MOVE = 1;
	private static final int DRAG_RUBBERBAND = 2;
	private static final int DRAG_LASSO = 3;
	private static final int CONNECT_THRESHOLD = 8;
	private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
	
	private Graph aGraph;
	private GraphFrame aFrame;
	private ToolBar aToolBar;
	private double aZoom;	
	private boolean aHideGrid;
	private boolean aModified;
	private Object aLastSelected;
	private Set aSelectedItems;
	private Point2D aLastMousePoint;
	private Point2D aMouseDownPoint;   
	private int aDragMode;
	
	/**
	 * Constructs a graph.
	 * @param pToolBar the tool bar with the node and edge tools
	 */
	public GraphPanel(ToolBar pToolBar)
	{
		aZoom = 1;
		aToolBar = pToolBar;
		setBackground(Color.WHITE);
		aSelectedItems = new HashSet();
      
		addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent pEvent)
			{
				requestFocus();
				final Point2D mousePoint = new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
				boolean isCtrl = (pEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 
				Node n = aGraph.findNode(mousePoint);
				Edge e = aGraph.findEdge(mousePoint);
				Object tool = aToolBar.getSelectedTool();
				if(pEvent.getClickCount() > 1 || (pEvent.getModifiers() & InputEvent.BUTTON1_MASK) == 0)
				{  
					// double/right-click
					if(e != null)
					{
						setSelectedItem(e);
						editSelected();
					}
					else if(n != null)
					{
						setSelectedItem(n);
						editSelected();
					}
					else
					{
						aToolBar.showPopup(GraphPanel.this, mousePoint, new ActionListener()
                        {
                           public void actionPerformed(ActionEvent pEvent)
                           {
                        	   Object tool = aToolBar.getSelectedTool();
                        	   if(tool instanceof Node)
                        	   {
                        		   Node prototype = (Node) tool;
                        		   Node newNode = (Node) prototype.clone();
                        		   boolean added = aGraph.add(newNode, mousePoint);
                        		   if(added)
                        		   {
                        			   setModified(true);
                        			   setSelectedItem(newNode);
                        		   }
                        	   }
                           	}
                        });
					}
				}
				else if(tool == null) // select
				{
					if(e != null)
					{
						setSelectedItem(e);
					}
					else if(n != null)
					{
						if(isCtrl) 
						{
							addSelectedItem(n);
						}
						else if(!aSelectedItems.contains(n)) 
						{
							setSelectedItem(n);
						}
						aDragMode = DRAG_MOVE;
					}
					else
					{
						if(!isCtrl) 
						{
							clearSelection();
						}
						aDragMode = DRAG_LASSO;
					}
				}
				else if(tool instanceof Node)
				{
					Node prototype = (Node) tool;
					Node newNode = (Node) prototype.clone();
					boolean added = aGraph.add(newNode, mousePoint);
					if(added)
					{
						setModified(true);
						setSelectedItem(newNode);
						aDragMode = DRAG_MOVE;
					}
					else if(n != null)
					{
						if(isCtrl) 
						{
							addSelectedItem(n);
						}
						else if(!aSelectedItems.contains(n))
						{
							setSelectedItem(n);
						}
						aDragMode = DRAG_MOVE;
					}
				}
				else if(tool instanceof Edge)
				{
					if(n != null) 
					{
						aDragMode = DRAG_RUBBERBAND;
					}
				}
				aLastMousePoint = mousePoint;
				aMouseDownPoint = mousePoint;
				repaint();
			}

			public void mouseReleased(MouseEvent pEvent)
			{
				Point2D mousePoint = new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
				Object tool = aToolBar.getSelectedTool();
				if(aDragMode == DRAG_RUBBERBAND)
				{
					Edge prototype = (Edge) tool;
					Edge newEdge = (Edge) prototype.clone();
					if(mousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD && aGraph.connect(newEdge, aMouseDownPoint, mousePoint))
					{
						setModified(true);
						setSelectedItem(newEdge);
					}
				}
				else if(aDragMode == DRAG_MOVE)
				{
					aGraph.layout();
					setModified(true);
				}
				aDragMode = DRAG_NONE;
				revalidate();
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent pEvent)
			{
				Point2D mousePoint = new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
				boolean isCtrl = (pEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 
				if(aDragMode == DRAG_MOVE && aLastSelected instanceof Node)
				{               
					Node lastNode = (Node) aLastSelected;
					Rectangle2D bounds = lastNode.getBounds();
					double dx = mousePoint.getX() - aLastMousePoint.getX();
					double dy = mousePoint.getY() - aLastMousePoint.getY();
                            
					// we don't want to drag nodes into negative coordinates
					// particularly with multiple selection, we might never be 
					// able to get them back.
					Iterator iter = aSelectedItems.iterator();
					while(iter.hasNext())
					{
						Object selected = iter.next();                 
						if(selected instanceof Node)
						{
							Node n = (Node) selected;
							bounds.add(n.getBounds());
						}
					}
					dx = Math.max(dx, -bounds.getX());
					dy = Math.max(dy, -bounds.getY());
               
					iter = aSelectedItems.iterator();
					while(iter.hasNext())
					{
						Object selected = iter.next();                 
						if(selected instanceof Node)
						{
							Node n = (Node) selected;
							n.translate(dx, dy);                           
						}
					}
					// we don't want continuous layout any more because of multiple selection
					// graph.layout();
				}            
				else if(aDragMode == DRAG_LASSO)
				{
					double x1 = aMouseDownPoint.getX();
					double y1 = aMouseDownPoint.getY();
					double x2 = mousePoint.getX();
					double y2 = mousePoint.getY();
					Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
					Iterator iter = aGraph.getNodes().iterator();
					while(iter.hasNext())
					{
						Node n = (Node) iter.next();
						if(!isCtrl && !lasso.contains(n.getBounds())) 
						{
							removeSelectedItem(n);
						}
						else if (lasso.contains(n.getBounds())) 
						{
							addSelectedItem(n);
						}
					}
				}
				aLastMousePoint = mousePoint;
				repaint();
			}
		});
	}

	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		Object edited = aLastSelected;
		if(aLastSelected == null)
		{
			if (aSelectedItems.size() == 1)
			{
				edited = aSelectedItems.iterator().next();
			}
			else
			{
				return;
			}
		}
		PropertySheet sheet = new PropertySheet(edited);
		sheet.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent pEvent)
			{
				aGraph.layout();
				repaint();
			}
		});
		JOptionPane.showInternalMessageDialog(this, sheet, 
            ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings").getString("dialog.properties"),            
            JOptionPane.PLAIN_MESSAGE);
		setModified(true);
	}

	/**
	 * Removes the selected nodes or edges.
	 */
	public void removeSelected()
	{
		Iterator iter = aSelectedItems.iterator();
		while(iter.hasNext())
		{
			Object selected = iter.next();                 
			if(selected instanceof Node)
			{
				aGraph.removeNode((Node) selected);
			}
			else if(selected instanceof Edge)
			{
				aGraph.removeEdge((Edge) selected);
			}
		}
		if(aSelectedItems.size() > 0)
		{
			setModified(true);
		}
		repaint();
	}

	/**
	 * Set the graph in the panel.
	 * @param pGraph the graph to be displayed and edited
	 */
	public void setGraph(Graph pGraph)
	{
		aGraph = pGraph;
		setModified(false);
		revalidate();
		repaint();
	}

	@Override
	public void paintComponent(Graphics pGraphics)
	{
		super.paintComponent(pGraphics);
		Graphics2D g2 = (Graphics2D) pGraphics;
		g2.scale(aZoom, aZoom);
		Rectangle2D bounds = getBounds();
		Rectangle2D graphBounds = aGraph.getBounds(g2);
		if(!aHideGrid) 
		{
			Grid.draw(g2, new Rectangle2D.Double(0, 0, Math.max(bounds.getMaxX() / aZoom, graphBounds.getMaxX()), 
				   Math.max(bounds.getMaxY() / aZoom, graphBounds.getMaxY())));
		}
		aGraph.draw(g2, new Grid());

		Iterator iter = aSelectedItems.iterator();
		Set toBeRemoved = new HashSet();
		while(iter.hasNext())
		{
			Object selected = iter.next();                 
			if(!aGraph.getNodes().contains(selected) && !aGraph.getEdges().contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
			else if(selected instanceof Node)
			{
				Rectangle2D grabberBounds = ((Node) selected).getBounds();
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMinY());
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMaxY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMinY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY());
			}
			else if(selected instanceof Edge)
			{
				Line2D line = ((Edge) selected).getConnectionPoints();
				drawGrabber(g2, line.getX1(), line.getY1());
				drawGrabber(g2, line.getX2(), line.getY2());
			}
		}

		iter = toBeRemoved.iterator();
		while(iter.hasNext())
		{
			removeSelectedItem(iter.next());
		}                 
      
		if(aDragMode == DRAG_RUBBERBAND)
		{
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			g2.draw(new Line2D.Double(aMouseDownPoint, aLastMousePoint));
			g2.setColor(oldColor);
		}      
		else if(aDragMode == DRAG_LASSO)
		{
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			double x1 = aMouseDownPoint.getX();
			double y1 = aMouseDownPoint.getY();
			double x2 = aLastMousePoint.getX();
			double y2 = aLastMousePoint.getY();
			Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), 
					Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
			g2.draw(lasso);
			g2.setColor(oldColor);
		}      
	}

	/**
	 * Draws a single "grabber", a filled square.
	 * @param pGraphics2D the graphics context
	 * @param pX the x coordinate of the center of the grabber
	 * @param pY the y coordinate of the center of the grabber
	 */
	public static void drawGrabber(Graphics2D pGraphics2D, double pX, double pY)
	{
		final int size = 5;
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(PURPLE);
		pGraphics2D.fill(new Rectangle2D.Double(pX - size / 2, pY - size / 2, size, size));
		pGraphics2D.setColor(oldColor);
	}

	@Override
	public Dimension getPreferredSize()
	{
		Rectangle2D bounds = aGraph.getBounds((Graphics2D) getGraphics());
		return new Dimension((int) (aZoom * bounds.getMaxX()), (int) (aZoom * bounds.getMaxY()));
	}

	/**
	 * Changes the zoom of this panel. The zoom is 1 by default and is multiplied
	 * by sqrt(2) for each positive stem or divided by sqrt(2) for each negative step.
	 * @param pSteps the number of steps by which to change the zoom. A positive
	 * value zooms in, a negative value zooms out.
	 */
	public void changeZoom(int pSteps)
	{
      final double factor = Math.sqrt(2);
      for(int i = 1; i <= pSteps; i++)
      {
    	  aZoom *= factor;
      }
      for(int i = 1; i <= -pSteps; i++)
      {
    	  aZoom /= factor;
      }
      revalidate();
      repaint();
	}

	/**
	 * Checks whether this graph has been modified since it was last saved.
	 * @return true if the graph has been modified
	 */
	public boolean isModified()
	{	
		return aModified;
	}

	/**
	 * Sets or resets the modified flag for this graph.
	 * @param pModified true to indicate that the graph has been modified
	 */
	public void setModified(boolean pModified)
	{
		aModified = pModified;

		if(aFrame == null)
		{
			Component parent = this;
			do
			{
				parent = parent.getParent();
			}
			while (parent != null && !(parent instanceof GraphFrame));
			if(parent != null)
			{
				aFrame = (GraphFrame) parent;
			}
		}
		if(aFrame != null)
		{
			String title = aFrame.getFileName();
			if(title != null)
			{
				if(aModified)
				{
					if(!aFrame.getTitle().endsWith("*"))
					{
						aFrame.setTitle(title + "*");
					}
				}
				else
				{
					aFrame.setTitle(title);
				}
			}
		}
	}

	private void addSelectedItem(Object pObject)
	{
		aLastSelected = pObject;      
		aSelectedItems.add(pObject);
	}
   
	private void removeSelectedItem(Object pObject)
	{
		if(pObject == aLastSelected)
		{
			aLastSelected = null;
		}
		aSelectedItems.remove(pObject);
	}
   
	private void setSelectedItem(Object pObject)
	{
		aSelectedItems.clear();
		aLastSelected = pObject;
		if (pObject != null)
		{
			aSelectedItems.add(pObject);
		}
	}
   
	private void clearSelection()
	{
		aSelectedItems.clear();
		aLastSelected = null;
	}
   
	/**
	 * Sets the value of the hideGrid property.
	 * @param pHideGrid true if the grid is being hidden
	 */
	public void setHideGrid(boolean pHideGrid)
	{
		aHideGrid = pHideGrid;
		repaint();
	}

	/**
	 * Gets the value of the hideGrid property.
	 * @return true if the grid is being hidden
	 */
	public boolean getHideGrid()
	{
		return aHideGrid;
	}
}