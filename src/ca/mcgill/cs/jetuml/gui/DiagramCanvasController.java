/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

import java.util.Optional;

import ca.mcgill.cs.jetuml.application.MoveTracker;
import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.application.UndoManager;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * An instance of this class is responsible to handle the user
 * interface events on a diagram canvas.
 */
class DiagramCanvasController
{
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
	
	private static final int CONNECT_THRESHOLD = 8;
	private static final int VIEWPORT_PADDING = 5;
	
	private final SelectionList aSelectedElements;
	private final Diagram aDiagram;
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final DiagramCanvas aCanvas;
	private final UndoManager aUndoManager;
	private DiagramTabToolBar aSideBar;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;   
	
	DiagramCanvasController(SelectionList pSelectionList, Diagram pDiagram, DiagramCanvas pCanvas,
			DiagramTabToolBar pToolBar, UndoManager pManager)
	{
		aSelectedElements = pSelectionList;
		aDiagram = pDiagram;
		aCanvas = pCanvas;
		aSideBar = pToolBar;
		aUndoManager = pManager;
	}
	
	/**
	 * @return The line that defines the active rubberband, if the 
	 * controller is in rubberband dragging mode, or nothing otherwise.
	 */
	public Optional<Line> getRubberband()
	{
		if( aDragMode != DragMode.DRAG_RUBBERBAND )
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(new Line(new Point(aMouseDownPoint.getX(), aMouseDownPoint.getY()), 
							new Point(aLastMousePoint.getX(), aLastMousePoint.getY())));
		}
	}
	
	/**
	 * @return The rectangle that defines the active lasso, if the 
	 * controller is in lasso dragging mode, or nothing otherwise.
	 */
	public Optional<Rectangle> getLasso()
	{
		if( aDragMode != DragMode.DRAG_LASSO )
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(new Rectangle(
					Math.min(aMouseDownPoint.getX(), aLastMousePoint.getX()),
				    Math.min(aMouseDownPoint.getY(), aLastMousePoint.getY()), 
				    Math.abs(aMouseDownPoint.getX() - aLastMousePoint.getX()), 
				    Math.abs(aMouseDownPoint.getY() - aLastMousePoint.getY())));
		}
	}
	
	/**
	 * Also adds the inner edges of parent nodes to the selection list.
	 * @param pElement
	 */
	private void setSelection(DiagramElement pElement)
	{
		aSelectedElements.set(pElement);
		for (Edge edge : aDiagram.getEdges())
		{
			if (hasSelectedParent(edge.getStart()) && hasSelectedParent(edge.getEnd()))
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
	private void addToSelection(DiagramElement pElement)
	{
		aSelectedElements.add(pElement);
		for (Edge edge : aDiagram.getEdges())
		{
			if (hasSelectedParent(edge.getStart()) && hasSelectedParent(edge.getEnd()))
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
		if (pNode == null)
		{
			return false;
		}
		else if (aSelectedElements.contains(pNode))
		{
			return true;
		}
		else if (pNode instanceof ChildNode)
		{
			return hasSelectedParent(((ChildNode)pNode).getParent());
		}
		else
		{
			return false;
		}
	}

	private Point getMousePoint(MouseEvent pEvent)
	{
		return new Point((int)pEvent.getX(), (int)pEvent.getY());
	}

	/*
	 * Will return null if nothing is selected.
	 */
	private DiagramElement getSelectedElement(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		DiagramElement element = aDiagram.findEdge(mousePoint);
		if (element == null)
		{
			element = aDiagram.findNode(new Point(mousePoint.getX(), mousePoint.getY())); 
		}
		return element;
	}

	private void handleSelection(MouseEvent pEvent)
	{
		DiagramElement element = getSelectedElement(pEvent);
		if (element != null) // Something is selected
		{
			if (pEvent.isControlDown())
			{
				if (!aSelectedElements.contains(element))
				{
					addToSelection(element);
				}
				else
				{
					aSelectedElements.remove(element);
				}
			}
			else if (!aSelectedElements.contains(element))
			{
				// The test is necessary to ensure we don't undo multiple selections
				setSelection(element);
			}
			aDragMode = DragMode.DRAG_MOVE;
			aMoveTracker.startTrackingMove(aSelectedElements);
		}
		else // Nothing is selected
		{
			if (!pEvent.isControlDown()) 
			{
				aSelectedElements.clearSelection();
			}
			aDragMode = DragMode.DRAG_LASSO;
		}
	}

	private void handleDoubleClick(MouseEvent pEvent)
	{
		DiagramElement element = getSelectedElement(pEvent);
		if (element != null)
		{
			setSelection(element);
			aCanvas.editSelected();
		}
		else
		{
			aSideBar.showPopup(pEvent.getScreenX(), pEvent.getScreenY());
		}
	}

	private void handleNodeCreation(MouseEvent pEvent)
	{
		Node newNode = ((Node)aSideBar.getCreationPrototype()).clone();
		Point point = getMousePoint(pEvent);
		boolean added = aDiagram.addNode(newNode, new Point(point.getX(), point.getY()), getViewWidth(), getViewHeight());
		if (added)
		{
			aCanvas.setModified(true);
			setSelection(newNode);
		}
		else // Special behavior, if we can't add a node, we select any element at the point
		{
			handleSelection(pEvent);
		}
	}

	private void handleEdgeStart(MouseEvent pEvent)
	{
		DiagramElement element = getSelectedElement(pEvent);
		if (element != null && element instanceof Node) 
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
	private DiagramElement getTool(MouseEvent pEvent)
	{
		DiagramElement tool = aSideBar.getCreationPrototype();
		DiagramElement selected = getSelectedElement(pEvent);

		if (tool !=null && tool instanceof Node)
		{
			if (selected != null && selected instanceof Node)
			{
				if (!(tool instanceof ChildNode && selected instanceof ParentNode))
				{
					aSideBar.setToolToBeSelect();
					tool = null;
				}
			}
		}	
		return tool;
	}

	public void mousePressed(MouseEvent pEvent)
	{
		aSideBar.hidePopup();
		DiagramElement tool = getTool(pEvent);
		if (pEvent.getClickCount() > 1 || pEvent.isSecondaryButtonDown()) // double/right click
		{  
			handleDoubleClick(pEvent);
		}
		else if (tool == null)
		{
			handleSelection(pEvent);
		}
		else if (tool instanceof Node)
		{
			handleNodeCreation(pEvent);
		}
		else if (tool instanceof Edge)
		{
			handleEdgeStart(pEvent);
		}
		Point point = getMousePoint(pEvent);
		aLastMousePoint = new Point(point.getX(), point.getY()); 
		aMouseDownPoint = aLastMousePoint;
		aCanvas.paintPanel();
	}

	public void mouseReleased(MouseEvent pEvent)
	{
		Point mousePoint = new Point((int)pEvent.getX(), (int)pEvent.getY());
		if (aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			Edge prototype = (Edge) aSideBar.getCreationPrototype();
			Edge newEdge = (Edge) prototype.clone();
			if (mousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD && aDiagram.addEdge(newEdge, aMouseDownPoint, mousePoint))
			{
				aCanvas.setModified(true);
				setSelection(newEdge);
			}
		}
		else if (aDragMode == DragMode.DRAG_MOVE)
		{
			aDiagram.requestLayout();
			aCanvas.setModified(true);
			CompoundCommand command = aMoveTracker.endTrackingMove(aDiagram);
			if (command.size() > 0)
			{
				aUndoManager.add(command);
			}
		}
		aDragMode = DragMode.DRAG_NONE;
		aCanvas.paintPanel();
	}

	// CSOFF:
	public void mouseDragged(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		boolean isCtrl = pEvent.isControlDown();

		if(aDragMode == DragMode.DRAG_MOVE && aSelectedElements.getLastNode() != null)
		{
			// TODO, include edges between selected nodes in the bounds check.
			Node lastNode = aSelectedElements.getLastNode();
			Rectangle bounds = lastNode.view().getBounds();

			int dx = (int)(mousePoint.getX() - aLastMousePoint.getX());
			int dy = (int)(mousePoint.getY() - aLastMousePoint.getY());

			// require users mouse to be in the panel when dragging up or to the left
			// this prevents a disconnect between the user's mouse and the element's position
			if( mousePoint.getX() > getViewWidth() && dx < 0 )
			{
				dx = 0;
			}
			if( mousePoint.getY() > getViewHeight() && dy < 0 )
			{
				dy = 0;
			}

			// we don't want to drag nodes into negative coordinates
			// particularly with multiple selection, we might never be 
			// able to get them back.
			for(DiagramElement selected : aSelectedElements )
			{
				if (selected instanceof Node)
				{
					Node n = (Node) selected;
					bounds = bounds.add(n.view().getBounds());
				}
			}
			dx = Math.max(dx, -bounds.getX());
			dy = Math.max(dy, -bounds.getY());

			// Right bounds checks
			if(bounds.getMaxX() + dx > aCanvas.boundsInLocalProperty().get().getMaxX())
			{
				dx = (int) aCanvas.boundsInLocalProperty().get().getMaxX() - bounds.getMaxX();
			}
			if(bounds.getMaxY() + dy > aCanvas.boundsInLocalProperty().get().getMaxY())
			{
				dy = (int) aCanvas.boundsInLocalProperty().get().getMaxY() - bounds.getMaxY();
			}

			for(DiagramElement selected : aSelectedElements)
			{
				if (selected instanceof ChildNode)
				{
					ChildNode n = (ChildNode) selected;
					if (!aSelectedElements.parentContained(n)) // parents are responsible for translating their children
					{
						n.translate(dx, dy);
					}	
				}
				else if (selected instanceof Node)
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
			Rectangle lasso = new Rectangle((int)Math.min(x1, x2), (int)Math.min(y1, y2), (int)Math.abs(x1 - x2) , (int)Math.abs(y1 - y2));
			for (Node node : aDiagram.getRootNodes())
			{
				selectNode(isCtrl, node, lasso);
			}
			//Edges need to be added too when highlighted, but only if both their endpoints have been highlighted.
			for (Edge edge: aDiagram.getEdges())
			{
				if (!isCtrl && !lasso.contains(edge.view().getBounds()))
				{
					aSelectedElements.remove(edge);
				}
				else if (lasso.contains(edge.view().getBounds()))
				{
					if (aSelectedElements.transitivelyContains(edge.getStart()) && aSelectedElements.transitivelyContains(edge.getEnd()))
					{
						aSelectedElements.add(edge);
					}
				}
			}
		}
		aLastMousePoint = mousePoint;
		aCanvas.paintPanel();
	} // CSON:

	private void selectNode(boolean pCtrl, Node pNode, Rectangle pLasso)
	{
		if (!pCtrl && !pLasso.contains(pNode.view().getBounds())) 
		{
			aSelectedElements.remove(pNode);
		}
		else if (pLasso.contains(pNode.view().getBounds())) 
		{
			aSelectedElements.add(pNode);
		}
		if (pNode instanceof ParentNode)
		{
			for (ChildNode child : ((ParentNode) pNode).getChildren())
			{
				selectNode(pCtrl, child, pLasso);
			}
		}
	}

	private int getViewWidth()
	{
		return ((int) aCanvas.getScrollPane().getViewportBounds().getWidth()) - VIEWPORT_PADDING;
	}

	private int getViewHeight()
	{
		return ((int) aCanvas.getScrollPane().getViewportBounds().getHeight()) - VIEWPORT_PADDING;
	}
	
	EventHandler<MouseEvent> mousePressedHandler()
	{
		return e -> mousePressed(e);
	}
	
	EventHandler<MouseEvent> mouseReleasedHandler()
	{
		return e -> mouseReleased(e);
	}
	
	EventHandler<MouseEvent> mouseDraggedHandler()
	{
		return e -> mouseDragged(e);
	}
}