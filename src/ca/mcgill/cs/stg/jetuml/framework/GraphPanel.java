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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.ObjectNode;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;
import ca.mcgill.cs.stg.jetuml.graph.ParentNode;

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
	private SideBar aSideBar;
	private ToolBar aToolBar;
	private ExtendedToolBar aExtendedToolBar;
	private double aZoom;	
	private boolean aHideGrid;
	private boolean aModified;
	private SelectionList aSelectedElements = new SelectionList();
	private Point2D aLastMousePoint;
	private Point2D aMouseDownPoint;   
	private DragMode aDragMode;
	private UndoManager aUndo = new UndoManager();
	private GraphModificationListener aModListener = new GraphModificationListener(aUndo);
	
	/**
	 * Constructs a graph.
	 * @param pSideBar the Side Bar which contains all of the tools for nodes and edges.
	 */
	public GraphPanel(SideBar pSideBar)
	{
		aZoom = 1;
		aSideBar = pSideBar;
		aToolBar = aSideBar.getToolBar();
		aExtendedToolBar = pSideBar.getExtendedToolBar();
		setBackground(Color.WHITE);
		addMouseListener(new GraphPanelMouseListener());
		addMouseMotionListener(new GraphPanelMouseMotionListener());
		
		//The following maps the command CTRL-G to the select grabber.
        getActionMap().put("switch_to_select", new AbstractAction() {
                public void actionPerformed(ActionEvent pEvent)
                {
                	Object tool;
    				if(aSideBar.isExtended())
    				{
    					tool = aExtendedToolBar.getSelectedTool();
    					if(tool !=null)
    					{
    						aExtendedToolBar.setToolToBeSelect();
    					}
    				}
    				else
    				{
    					tool = aToolBar.getSelectedTool();
    					if(tool!=null)
    					{
    						aToolBar.setToolToBeSelect();
    					}
    				}
                }
            });
		
		InputMap inputMap = getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK), "switch_to_select");
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
		aModListener.trackPropertyChange(aGraph, edited);
		PropertySheet sheet = new PropertySheet(edited);
		if(sheet.isEmpty())
		{
			return;
		}
		sheet.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent pEvent)
			{
				aGraph.layout();
				repaint();
			}
		});
		 String[] options = {"OK"};
		 JOptionPane.showOptionDialog(this, sheet, 
		            ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings").getString("dialog.properties"),
		            		JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		aModListener.finishPropertyChange(aGraph, edited);
		setModified(true);
	}

	/**
	 * Removes the selected graph elements.
	 */
	public void removeSelected()
	{
		aUndo.startTracking();
		Stack<Node> nodes = new Stack<Node>();
		for( GraphElement element : aSelectedElements )
		{
			if (element instanceof Node)
			{
				for(Edge e : aGraph.getNodeEdges((Node) element))
				{
					aGraph.removeEdge(e);
				}
				nodes.add((Node) element);
			}
			else if(element instanceof Edge)
			{
				aGraph.removeEdge((Edge) element);
			}
		}
		while(!nodes.empty())
		{
			aGraph.removeNode(nodes.pop());
		}
		aUndo.endTracking();
		if(aSelectedElements.size() > 0)
		{
			setModified(true);
		}
		repaint();
	}
	
	/**
	 * Resets the layout of the graph if there was a change made.
	 */
	public void layoutGraph()
	{
		aGraph.layout();
	}
	
	/**
	 * @return the graph in this panel.
	 */
	public Graph getGraph()
	{
		return aGraph;
	}
	
	/**
	 * Collects all coming calls into single undo - redo command.
	 */
	public void startCompoundListening() 
	{
		aUndo.startTracking();
	}
	
	/**
	 * Ends collecting all coming calls into single undo - redo command.
	 */
	public void endCompoundListening() 
	{
		aUndo.endTracking();
	}
	
	/**
	 * Undoes the most recent command.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void undo()
	{
		aUndo.undoCommand();
		repaint();
	}
	
	/**
	 * Removes the last undone action and performs it.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void redo()
	{
		aUndo.redoCommand();
		repaint();
	}

	/**
	 * Set the graph in the panel.
	 * @param pGraph the graph to be displayed and edited
	 */
	public void setGraph(Graph pGraph)
	{
		aGraph = pGraph;
		aGraph.addModificationListener(aModListener);
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
	
	/**
	 * @return the currently SelectedElements from the GraphPanel.
	 */
	public SelectionList getSelectionList()
	{
		return aSelectedElements;
	}
	
	/**
	 * @param pSelectionList the new SelectedElements for the GraphPanel.
	 */
	public void setSelectionList(SelectionList pSelectionList)
	{
		aSelectedElements = pSelectionList;
	}
	
	/**
	 * @param pNode the currently selected Node
	 * @return whether or not there is a problem with switching to the selection tool.
	 */
	public boolean switchToSelectException(Node pNode)
	{
		if(pNode instanceof PackageNode || pNode instanceof ImplicitParameterNode || pNode instanceof ObjectNode)
		{
			return true;
		}
		return false;
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
			Object tool;
			if(aSideBar.isExtended())
			{
				tool = aExtendedToolBar.getSelectedTool();
			}
			else
			{
				tool = aToolBar.getSelectedTool();
			}
			if(tool !=null)
			{
				//Set it to be the selection tool if you have a Node tool selected.
				if(n!=null && !(tool instanceof Edge))
				{
					if(!switchToSelectException(n))
					{
						if(aSideBar.isExtended())
						{
							aExtendedToolBar.setToolToBeSelect();
						}
						else
						{
							aToolBar.setToolToBeSelect();
						}
					tool = null;
					}
				}
			}	
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
					if(aSideBar.isExtended())
					{
						aExtendedToolBar.showPopup(GraphPanel.this, mousePoint, new ActionListener()
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
			}
			else if(tool == null) // select
			{
				if(e != null)
				{
					aSelectedElements.set(e);
				}
				else if(n != null)
				{
					if(isCtrl && !aSelectedElements.contains(n)) 
					{
						aSelectedElements.add(n);
					}
					else if(isCtrl && aSelectedElements.contains(n)) 
					{
						aSelectedElements.remove(n);
					}
					else if(!aSelectedElements.contains(n)) 
					{
						aSelectedElements.set(n);
					}
					aDragMode = DragMode.DRAG_MOVE;
					aModListener.startTrackingMove(aGraph, aSelectedElements);
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
					// @prmr Disabled the option to "slide" an object on creation to greatly
					// simplify the node creation logic. For now.
					//aDragMode = DragMode.DRAG_MOVE;
					//aModListener.startTrackingMove(aGraph, aSelectedElements);
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
					aModListener.startTrackingMove(aGraph, aSelectedElements);
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
			Object tool;
			if(aSideBar.isExtended())
			{
				tool = aExtendedToolBar.getSelectedTool();
			}
			else
			{
				tool = aToolBar.getSelectedTool();
			}	
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
				aModListener.endTrackingMove(aGraph, aSelectedElements);
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

			if(aDragMode == DragMode.DRAG_MOVE && aSelectedElements.getLastNode()!=null)
			{               
				Node lastNode = (Node) aSelectedElements.getLastNode();
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
					if(selected instanceof ChildNode)
					{
						ChildNode n = (ChildNode) selected;
						if (!aSelectedElements.parentContained(n)) // parents are responsible for translating their children
						{
							n.translate(dx, dy); 
						}	
					}
					else if(selected instanceof Node)
					{
						Node n = (Node) selected;
						n.translate(dx, dy); 
					}
				}
			}
			else if(aDragMode == DragMode.DRAG_LASSO)
			{
				double x1 = aMouseDownPoint.getX();
				double y1 = aMouseDownPoint.getY();
				double x2 = mousePoint.getX();
				double y2 = mousePoint.getY();
				Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
				for( Node node : aGraph.getRootNodes() )
				{
					selectNode(isCtrl, node, lasso);
				}
				//Edges need to be added too when highlighted, but only if both their endpoints have been highlighted.
				for (Edge edge: aGraph.getEdges())
				{
					if(!isCtrl && !lasso.contains(edge.getBounds()))
					{
						aSelectedElements.remove(edge);
					}
					else if(lasso.contains(edge.getBounds()))
					{
						if(aSelectedElements.contains(edge.getStart()) && aSelectedElements.contains(edge.getEnd()))
						{
							aSelectedElements.add(edge);
						}
					}
				}
			}
			aLastMousePoint = mousePoint;
			repaint();
		}
		
		private void selectNode( boolean pCtrl, Node pNode, Rectangle2D.Double pLasso )
		{
			if(!pCtrl && !pLasso.contains(pNode.getBounds())) 
			{
				aSelectedElements.remove(pNode);
			}
			else if(pLasso.contains(pNode.getBounds())) 
			{
				aSelectedElements.add(pNode);
			}
			if( pNode instanceof ParentNode )
			{
				for( ChildNode child : ((ParentNode) pNode).getChildren() )
				{
					selectNode(pCtrl, child, pLasso);
				}
			}
		}
	}
}
