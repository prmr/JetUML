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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.mcgill.cs.stg.jetuml.commands.AddEdgeCommand;
import ca.mcgill.cs.stg.jetuml.commands.AddNodeCommand;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.DeleteNodeCommand;
import ca.mcgill.cs.stg.jetuml.commands.RemoveEdgeCommand;
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
	private ToolBar aSideBar;
	private double aZoom;	
	private boolean aHideGrid;
	private boolean aModified;
	private SelectionList aSelectedElements = new SelectionList();
	private Point2D aLastMousePoint;
	private Point2D aMouseDownPoint;   
	private DragMode aDragMode;
	private UndoManager aUndoManager = new UndoManager();
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final PropertyChangeTracker aPropertyChangeTracker = new PropertyChangeTracker();
	
	/**
	 * Constructs the panel, assigns the graph to it, and registers
	 * the panel as a listener for the graph.
	 * 
	 * @param pGraph The graph managed by this panel.
	 * @param pSideBar the Side Bar which contains all of the tools for nodes and edges.
	 */
	public GraphPanel(Graph pGraph, ToolBar pSideBar)
	{
		aGraph = pGraph;
		aGraph.setGraphModificationListener(new PanelGraphModificationListener());
		aZoom = 1;
		aSideBar = pSideBar;
		setBackground(Color.WHITE);
		addMouseListener(new GraphPanelMouseListener());
		addMouseMotionListener(new GraphPanelMouseMotionListener());
	}

	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		GraphElement edited = aSelectedElements.getLastSelected();
		if( edited == null )
		{
			return;
		}
		aPropertyChangeTracker.startTrackingPropertyChange(edited);
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
		CompoundCommand command = aPropertyChangeTracker.stopTrackingPropertyChange(aGraph);
		if(command.size() > 0)
		{
			aUndoManager.add(command);
		}
		setModified(true);
	}

	/**
	 * Removes the selected graph elements.
	 */
	public void removeSelected()
	{
		aUndoManager.startTracking();
		Stack<Node> nodes = new Stack<Node>();
		for( GraphElement element : aSelectedElements )
		{
			if(element instanceof Node)
			{
				aGraph.removeAllEdgesConnectedTo((Node)element);
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
		aUndoManager.endTracking();
		if(aSelectedElements.size() > 0)
		{
			setModified(true);
		}
		repaint();
	}
	
	/**
	 * Indicate to the GraphPanel that is should 
	 * consider all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void startCompoundGraphOperation()
	{
		aUndoManager.startTracking();
	}
	
	/**
	 * Indicate to the GraphPanel that is should 
	 * stop considering all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void finishCompoundGraphOperation()
	{
		aUndoManager.endTracking();
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
		aUndoManager.startTracking();
	}
	
	/**
	 * Ends collecting all coming calls into single undo - redo command.
	 */
	public void endCompoundListening() 
	{
		aUndoManager.endTracking();
	}
	
	/**
	 * Undoes the most recent command.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void undo()
	{
		aUndoManager.undoCommand();
		revalidate();
		repaint();
	}
	
	/**
	 * Removes the last undone action and performs it.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void redo()
	{
		aUndoManager.redoCommand();
		revalidate();
		repaint();
	}
	
	/**
	 * Clears the selection list and adds all the root nodes and edges to 
	 * it. Makes the selection tool the active tool.
	 */
	public void selectAll()
	{
		aSelectedElements.clearSelection();
		for( Node node : aGraph.getRootNodes() )
		{
			aSelectedElements.add(node);
		}
		for( Edge edge : aGraph.getEdges() )
		{
			aSelectedElements.add(edge);
		}
		aSideBar.setToolToBeSelect();
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

		GraphFrame graphFrame = getFrame();
		if(graphFrame != null)
		{
			graphFrame.setTitle(aModified);
		}
	}
	
	/* 
	 * Obtains the parent frame of this panel through the component hierarchy.
	 */
	private GraphFrame getFrame()
	{
		Component parent = this;
		do
		{
			parent = parent.getParent();
		}
		while(parent != null && !(parent instanceof GraphFrame));
		return (GraphFrame) parent;
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
		/**
		 * Also adds the inner edges of parent nodes to the selection list.
		 * @param pElement
		 */
		private void setSelection(GraphElement pElement)
		{
			aSelectedElements.set(pElement);
			for( Edge edge : aGraph.getEdges() )
			{
				if( hasSelectedParent(edge.getStart()) && hasSelectedParent(edge.getEnd()))
				{
					aSelectedElements.add(edge);
				}
			}
			aSelectedElements.add(pElement); // Necessary to make a parent node the last node selected so it can be edited.
		}
		
		/**
		 * Also adds the inner edges of parent nodes to the selection list.
		 * @param pElement
		 */
		private void addToSelection(GraphElement pElement)
		{
			aSelectedElements.add(pElement);
			for( Edge edge : aGraph.getEdges() )
			{
				if( hasSelectedParent(edge.getStart()) && hasSelectedParent(edge.getEnd()))
				{
					aSelectedElements.add(edge);
				}
			}
			aSelectedElements.add(pElement); // Necessary to make a parent node the last node selected so it can be edited.
		}
		
		/**
		 * @param pNode a Node to check.
		 * @return True if pNode or any of its parent is selected
		 */
		private boolean hasSelectedParent(Node pNode)
		{
			if( pNode == null )
			{
				return false;
			}
			else if( aSelectedElements.contains(pNode) )
			{
				return true;
			}
			else if( pNode instanceof ChildNode )
			{
				return hasSelectedParent( ((ChildNode)pNode).getParent() );
			}
			else
			{
				return false;
			}
		}
		
		private boolean isCtrl(MouseEvent pEvent)
		{
			return (pEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
		}
		
		private Point2D getMousePoint(MouseEvent pEvent)
		{
			return new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
		}
		
		/*
		 * Will return null if nothing is selected.
		 */
		private GraphElement getSelectedElement(MouseEvent pEvent)
		{
			Point2D mousePoint = getMousePoint(pEvent);
			GraphElement element = aGraph.findEdge(mousePoint);
			if( element == null )
			{
				element = aGraph.findNode(mousePoint);
			}
			return element;
		}
		
		private void handleSelection(MouseEvent pEvent)
		{
			GraphElement element = getSelectedElement(pEvent);
			if(element != null) // Something is selected
			{
				if( isCtrl(pEvent) )
				{
					if(!aSelectedElements.contains(element))
					{
						addToSelection(element);
					}
					else
					{
						aSelectedElements.remove(element);
					}
				}
				else if( !aSelectedElements.contains(element))
				{
					// The test is necessary to ensure we don't undo multiple selections
					setSelection(element);
				}
				aDragMode = DragMode.DRAG_MOVE;
				aMoveTracker.startTrackingMove(aSelectedElements);
			}
			else // Nothing is selected
			{
				if(!isCtrl(pEvent)) 
				{
					aSelectedElements.clearSelection();
				}
				aDragMode = DragMode.DRAG_LASSO;
			}
		}
		
		private void handleDoubleClick(MouseEvent pEvent)
		{
			GraphElement element = getSelectedElement(pEvent);
			if( element != null )
			{
				setSelection(element);
				editSelected();
			}
			else
			{
				final Point2D mousePoint = getMousePoint(pEvent);
				aSideBar.showPopup(GraphPanel.this, mousePoint);
			}
		}
		
		private void handleNodeCreation(MouseEvent pEvent)
		{
			Node newNode = ((Node)aSideBar.getSelectedTool()).clone();
			boolean added = aGraph.addNode(newNode, getMousePoint(pEvent));
			if(added)
			{
				setModified(true);
				setSelection(newNode);
			}
			else // Special behavior, if we can't add a node, we select any element at the point
			{
				handleSelection(pEvent);
			}
		}
		
		private void handleEdgeStart(MouseEvent pEvent)
		{
			GraphElement element = getSelectedElement(pEvent);
			if(element != null && element instanceof Node ) 
			{
				aDragMode = DragMode.DRAG_RUBBERBAND;
			}
		}
		
		/*
	     * Implements a convenience feature. Normally returns 
	     * aSideBar.getSelectedTool(), except if the mouse points
	     * to an existing node, in which case defaults to select
	     * mode because it's likely the user wanted to select the node
	     * and forgot to switch tool. The only exception is when adding
	     * children nodes, where the parent node obviously has to be selected.
		 */
		private GraphElement getTool(MouseEvent pEvent)
		{
			GraphElement tool = aSideBar.getSelectedTool();
			GraphElement selected = getSelectedElement(pEvent);
			
			if(tool !=null && tool instanceof Node)
			{
				if( selected != null && selected instanceof Node )
				{
					if(!(tool instanceof ChildNode && selected instanceof ParentNode ))
					{
						aSideBar.setToolToBeSelect();
						tool = null;
					}
				}
			}	
			return tool;
		}
		
		@Override
		public void mousePressed(MouseEvent pEvent)
		{
			GraphElement tool = getTool(pEvent);

			if(pEvent.getClickCount() > 1 || (pEvent.getModifiers() & InputEvent.BUTTON1_MASK) == 0) // double/right click
			{  
				handleDoubleClick(pEvent);
			}
			else if(tool == null)
			{
				handleSelection(pEvent);
			}
			else if(tool instanceof Node)
			{
				handleNodeCreation(pEvent);
			}
			else if(tool instanceof Edge)
			{
				handleEdgeStart(pEvent);
			}
			aLastMousePoint = getMousePoint(pEvent);
			aMouseDownPoint = aLastMousePoint;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent pEvent)
		{
			Point2D mousePoint = new Point2D.Double(pEvent.getX() / aZoom, pEvent.getY() / aZoom);
			Object tool = aSideBar.getSelectedTool();
			if(aDragMode == DragMode.DRAG_RUBBERBAND)
			{
				Edge prototype = (Edge) tool;
				Edge newEdge = (Edge) prototype.clone();
				if(mousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD && aGraph.addEdge(newEdge, aMouseDownPoint, mousePoint))
				{
					setModified(true);
					setSelection(newEdge);
				}
			}
			else if(aDragMode == DragMode.DRAG_MOVE)
			{
				aGraph.layout();
				setModified(true);
				CompoundCommand command = aMoveTracker.endTrackingMove(aGraph);
				if( command.size() > 0 )
				{
					aUndoManager.add(command);
				}
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
				Node lastNode = aSelectedElements.getLastNode();
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
						if(aSelectedElements.transitivelyContains(edge.getStart()) && aSelectedElements.transitivelyContains(edge.getEnd()))
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
	
	private class PanelGraphModificationListener implements GraphModificationListener
	{
		@Override
		public void startingCompoundOperation() 
		{
			aUndoManager.startTracking();
		}
		
		@Override
		public void finishingCompoundOperation()
		{
			aUndoManager.endTracking();
		}
		
		@Override
		public void nodeAdded(Graph pGraph, Node pNode)
		{
			aUndoManager.add(new AddNodeCommand(pGraph, pNode));
		}
		
		@Override
		public void nodeRemoved(Graph pGraph, Node pNode)
		{
			aUndoManager.add(new DeleteNodeCommand(pGraph, pNode));
		}
		
		@Override
		public void edgeAdded(Graph pGraph, Edge pEdge)
		{
			aUndoManager.add(new AddEdgeCommand(pGraph, pEdge));
		}
		
		@Override
		public void edgeRemoved(Graph pGraph, Edge pEdge)
		{
			aUndoManager.add(new RemoveEdgeCommand(pGraph, pEdge));
		}

		@Override
		public void propertyChanged(Graph pGraph, GraphElement pElement, String pProperty, Object pOldValue, Object pNewValue)
		{
			aUndoManager.add(PropertyChangeTracker.createPropertyChangeCommand(pGraph, pElement, pProperty, pOldValue, pNewValue));
		}
	}
}
