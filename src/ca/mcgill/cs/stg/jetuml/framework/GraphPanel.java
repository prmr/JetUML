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
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * A panel to draw a graph.
 */
@SuppressWarnings("serial")
public class GraphPanel extends JPanel
{
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
	
	private static final int CONNECT_THRESHOLD = 8;
	private static final Color GRABBER_COLOR = new Color(77, 115, 153);
	private static final Color GRABBER_FILL_COLOR = new Color(173, 193, 214);
	private static final Color GRABBER_FILL_COLOR_TRANSPARENT = new Color(173, 193, 214, 75);
	
	private Graph aGraph;
	private GraphFrame aFrame;
	private ToolBar aToolBar;
	private double aZoom;	
	private boolean aHideGrid;
	private boolean aModified;
	private SelectionList aSelectedElements = new SelectionList();
	private Point2D aLastMousePoint;
	private Point2D aMouseDownPoint;   
	private DragMode aDragMode;
	
	/**
	 * Constructs a graph.
	 * @param pToolBar the tool bar with the node and edge tools
	 */
	public GraphPanel(ToolBar pToolBar)
	{
		aZoom = 1;
		aToolBar = pToolBar;
		setBackground(Color.WHITE);
		addMouseListener(new GraphPanelMouseListener());
		addMouseMotionListener(new GraphPanelMouseMotionListener());
	}

	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		Object edited = aSelectedElements.getLastSelected();
		if( edited == null )
		{
			return;
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
	 * Removes the selected graph elements.
	 */
	public void removeSelected()
	{
		for( GraphElement element : aSelectedElements )
		{
			aGraph.removeElement(element);
		}
		if(aSelectedElements.size() > 0)
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
		Rectangle2D graphBounds = aGraph.getBounds();
		if(!aHideGrid) 
		{
			Grid.draw(g2, new Rectangle2D.Double(0, 0, Math.max(bounds.getMaxX() / aZoom, graphBounds.getMaxX()), 
				   Math.max(bounds.getMaxY() / aZoom, graphBounds.getMaxY())));
		}
		aGraph.draw(g2, new Grid());

		Set<GraphElement> toBeRemoved = new HashSet<>();
		for(GraphElement selected : aSelectedElements)
		{
			if(!aGraph.contains(selected)) 
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

		for( GraphElement element : toBeRemoved )
		{
			aSelectedElements.remove(element);
		}                 
      
		if(aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			Color oldColor = g2.getColor();
			g2.setColor(GRABBER_COLOR);
			g2.draw(new Line2D.Double(aMouseDownPoint, aLastMousePoint));
			g2.setColor(oldColor);
		}      
		else if(aDragMode == DragMode.DRAG_LASSO)
		{
			Color oldColor = g2.getColor();
			g2.setColor(GRABBER_COLOR);
			double x1 = aMouseDownPoint.getX();
			double y1 = aMouseDownPoint.getY();
			double x2 = aLastMousePoint.getX();
			double y2 = aLastMousePoint.getY();
			Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), 
					Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
			g2.draw(lasso);
			g2.setColor(GRABBER_FILL_COLOR_TRANSPARENT);
			g2.fill(lasso);
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
		final int size = 6;
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(GRABBER_COLOR);
		pGraphics2D.drawRect((int)(pX - size / 2), (int)(pY - size / 2), size, size);
		pGraphics2D.setColor(GRABBER_FILL_COLOR);
		pGraphics2D.fillRect((int)(pX - size / 2)+1, (int)(pY - size / 2)+1, size-1, size-1);
		pGraphics2D.setColor(oldColor);
	}

	@Override
	public Dimension getPreferredSize()
	{
		Rectangle2D bounds = aGraph.getBounds();
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
			aFrame.setTitle(aModified);
		}
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
	
	private class GraphPanelMouseListener extends MouseAdapter
	{
		@Override
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
					aSelectedElements.set(e);
					editSelected();
				}
				else if(n != null)
				{
					aSelectedElements.set(n);
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
                    			   aSelectedElements.set(newNode);
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
					aSelectedElements.set(e);
				}
				else if(n != null)
				{
					if(isCtrl) 
					{
						aSelectedElements.add(n);
					}
					else if(!aSelectedElements.contains(n)) 
					{
						aSelectedElements.set(n);
					}
					aDragMode = DragMode.DRAG_MOVE;
				}
				else
				{
					if(!isCtrl) 
					{
						aSelectedElements.clearSelection();
					}
					aDragMode = DragMode.DRAG_LASSO;
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
					aSelectedElements.set(newNode);
					aDragMode = DragMode.DRAG_MOVE;
				}
				else if(n != null)
				{
					if(isCtrl) 
					{
						aSelectedElements.add(n);
					}
					else if( !aSelectedElements.contains(n) )
					{
						aSelectedElements.set(n);
					}
					aDragMode = DragMode.DRAG_MOVE;
				}
			}
			else if(tool instanceof Edge)
			{
				if(n != null) 
				{
					aDragMode = DragMode.DRAG_RUBBERBAND;
				}
			}
			aLastMousePoint = mousePoint;
			aMouseDownPoint = mousePoint;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent pEvent)
		{
			Point2D mousePoint = new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
			Object tool = aToolBar.getSelectedTool();
			if(aDragMode == DragMode.DRAG_RUBBERBAND)
			{
				Edge prototype = (Edge) tool;
				Edge newEdge = (Edge) prototype.clone();
				if(mousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD && aGraph.connect(newEdge, aMouseDownPoint, mousePoint))
				{
					setModified(true);
					aSelectedElements.set(newEdge);
				}
			}
			else if(aDragMode == DragMode.DRAG_MOVE)
			{
				aGraph.layout();
				setModified(true);
			}
			aDragMode = DragMode.DRAG_NONE;
			revalidate();
			repaint();
		}
	}
	
	private class GraphPanelMouseMotionListener extends MouseMotionAdapter
	{
		@Override
		public void mouseDragged(MouseEvent pEvent)
		{
			Point2D mousePoint = new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
			boolean isCtrl = (pEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 
			if(aDragMode == DragMode.DRAG_MOVE && aSelectedElements.getLastSelected() instanceof Node)
			{               
				Node lastNode = (Node) aSelectedElements.getLastSelected();
				Rectangle2D bounds = lastNode.getBounds();
				double dx = mousePoint.getX() - aLastMousePoint.getX();
				double dy = mousePoint.getY() - aLastMousePoint.getY();
                        
				// we don't want to drag nodes into negative coordinates
				// particularly with multiple selection, we might never be 
				// able to get them back.
				for( GraphElement selected : aSelectedElements )
				{
					if(selected instanceof Node)
					{
						Node n = (Node) selected;
						bounds.add(n.getBounds());
					}
				}
				dx = Math.max(dx, -bounds.getX());
				dy = Math.max(dy, -bounds.getY());
           
				for( GraphElement selected : aSelectedElements )
				{
					if(selected instanceof Node)
					{
						Node n = (Node) selected;
						if (!aSelectedElements.contains(n.getParent())) // parents are responsible for translating their children
						{
							n.translate(dx, dy); 
						}	
					}
				}
				// we don't want continuous layout any more because of multiple selection
				// graph.layout();
			}            
			else if(aDragMode == DragMode.DRAG_LASSO)
			{
				double x1 = aMouseDownPoint.getX();
				double y1 = aMouseDownPoint.getY();
				double x2 = mousePoint.getX();
				double y2 = mousePoint.getY();
				Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
				for( Node node : aGraph.getNodes() )
				{
					if(!isCtrl && !lasso.contains(node.getBounds())) 
					{
						aSelectedElements.remove(node);
					}
					else if(lasso.contains(node.getBounds())) 
					{
						aSelectedElements.add(node);
					}
				}
			}
			aLastMousePoint = mousePoint;
			repaint();
		}
	}
}