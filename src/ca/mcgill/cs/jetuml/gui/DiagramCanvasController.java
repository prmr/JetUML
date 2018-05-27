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
 * 
 * Right-click: Show toolbar;
 * Double-click: Edit properties of last selected, if available.
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
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;   
	
	DiagramCanvasController(SelectionList pSelectionList, Diagram pDiagram, DiagramCanvas pCanvas,
			UndoManager pManager)
	{
		aSelectedElements = pSelectionList;
		aDiagram = pDiagram;
		aCanvas = pCanvas;
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
					aSelectedElements.add(element);
					aSelectedElements.addEdgesIfContained(aDiagram.getEdges());
				}
				else
				{
					aSelectedElements.remove(element);
				}
			}
			else if (!aSelectedElements.contains(element))
			{
				// The test is necessary to ensure we don't undo multiple selections
				aSelectedElements.set(element);
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
			aSelectedElements.set(element);
			aCanvas.editSelected();
		}
	}
	
	private void handleSingleClick(MouseEvent pEvent)
	{
		Optional<DiagramElement> tool = getTool(pEvent);
		if(!tool.isPresent())
		{
			handleSelection(pEvent);
		}
		else if(tool.get() instanceof Node)
		{
			handleNodeCreation(pEvent);
		}
		else if(tool.get() instanceof Edge)
		{
			handleEdgeStart(pEvent);
		}
	}
	
	private void handleRightClick(MouseEvent pEvent)
	{
		aCanvas.showPopup(pEvent.getScreenX(), pEvent.getScreenY());
	}

	private void handleNodeCreation(MouseEvent pEvent)
	{
		assert aCanvas.getCreationPrototype().isPresent();
		Node newNode = ((Node)aCanvas.getCreationPrototype().get()).clone();
		Point point = getMousePoint(pEvent);
		boolean added = aDiagram.addNode(newNode, new Point(point.getX(), point.getY()), getViewWidth(), getViewHeight());
		if(added)
		{
			aCanvas.setModified(true);
			aSelectedElements.set(newNode);
		}
		else // Special behavior, if we can't add a node, we select any element at the point
		{
			handleSelection(pEvent);
		}
	}

	private void handleEdgeStart(MouseEvent pEvent)
	{
		DiagramElement element = getSelectedElement(pEvent);
		if(element != null && element instanceof Node) 
		{
			aDragMode = DragMode.DRAG_RUBBERBAND;
		}
	}

	/*
	 * Implements a convenience feature. Normally returns 
	 * aSideBar.getSelectedTool(), except if the mouse points
	 * to an existing node, in which case defaults to select
	 * mode because it's likely the user wanted to select the element
	 * and forgot to switch tool. The only exception is when adding
	 * children nodes, where the parent node obviously has to be selected.
	 */
	private Optional<DiagramElement> getTool(MouseEvent pEvent)
	{
		Optional<DiagramElement> tool = aCanvas.getCreationPrototype();
		DiagramElement selected = getSelectedElement(pEvent);

		if( tool.isPresent() && tool.get() instanceof Node)
		{
			if(selected != null && selected instanceof Node)
			{
				if(!(tool.get() instanceof ChildNode && selected instanceof ParentNode))
				{
					aCanvas.setToolToSelect();
					tool = Optional.empty();
				}
			}
		}	
		return tool;
	}

	public void mousePressed(MouseEvent pEvent)
	{
		if( pEvent.isSecondaryButtonDown() )
		{
			handleRightClick(pEvent);
		}
		else if( pEvent.getClickCount() > 1 )
		{
			handleDoubleClick(pEvent);
		}
		else
		{
			handleSingleClick(pEvent);
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
			assert aCanvas.getCreationPrototype().isPresent();
			Edge prototype = (Edge) aCanvas.getCreationPrototype().get();
			Edge newEdge = (Edge) prototype.clone();
			if (mousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD && aDiagram.addEdge(newEdge, aMouseDownPoint, mousePoint))
			{
				aCanvas.setModified(true);
				aSelectedElements.set(newEdge);
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
					if (!aSelectedElements.containsParent(n)) // parents are responsible for translating their children
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