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
	
	private final SelectionModel aSelectionModel;
	private final Diagram aDiagram;
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final DiagramCanvas aCanvas;
	private final UndoManager aUndoManager;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;   
	
	DiagramCanvasController(Diagram pDiagram, DiagramCanvas pCanvas,
			UndoManager pManager)
	{
		aDiagram = pDiagram;
		aCanvas = pCanvas;
		aUndoManager = pManager;
		aSelectionModel = new SelectionModel(aCanvas);
	}
	
	SelectionModel getSelectionModel()
	{
		return aSelectionModel;
	}
	
	private Line computeRubberband()
	{
		return new Line(new Point(aMouseDownPoint.getX(), aMouseDownPoint.getY()), 
				new Point(aLastMousePoint.getX(), aLastMousePoint.getY()));
	}
	
	private Rectangle computeLasso()
	{
		return new Rectangle((int) Math.min(aMouseDownPoint.getX(), aLastMousePoint.getX()), 
						     (int) Math.min(aMouseDownPoint.getY(), aLastMousePoint.getY()), 
						     (int) Math.abs(aMouseDownPoint.getX() - aLastMousePoint.getX()) , 
						     (int) Math.abs(aMouseDownPoint.getY() - aLastMousePoint.getY()));
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
				if (!aSelectionModel.contains(element))
				{
					aSelectionModel.addToSelection(element);
					aSelectionModel.addEdgesIfContained(aDiagram.getEdges());
				}
				else
				{
					aSelectionModel.removeFromSelection(element);
				}
			}
			else if (!aSelectionModel.contains(element))
			{
				// The test is necessary to ensure we don't undo multiple selections
				aSelectionModel.setSelection(element);
			}
			aDragMode = DragMode.DRAG_MOVE;
			aMoveTracker.startTrackingMove(aSelectionModel);
		}
		else // Nothing is selected
		{
			if (!pEvent.isControlDown()) 
			{
				aSelectionModel.clearSelection();
			}
			aDragMode = DragMode.DRAG_LASSO;
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
	
	private void handleNodeCreation(MouseEvent pEvent)
	{
		assert aCanvas.getCreationPrototype().isPresent();
		Node newNode = ((Node)aCanvas.getCreationPrototype().get()).clone();
		Point point = getMousePoint(pEvent);
		boolean added = aDiagram.addNode(newNode, new Point(point.getX(), point.getY()), getViewWidth(), getViewHeight());
		if(added)
		{
			aCanvas.setModified(true);
			aSelectionModel.setSelection(newNode);
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
			aCanvas.showPopup(pEvent.getScreenX(), pEvent.getScreenY());
		}
		else if( pEvent.getClickCount() > 1 )
		{
			aCanvas.editSelected();
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
				aSelectionModel.setSelection(newEdge);
			}
			aSelectionModel.deactivateRubberband();
			aCanvas.paintPanel();
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
			aCanvas.paintPanel();
		}
		else if( aDragMode == DragMode.DRAG_LASSO )
		{
			aSelectionModel.deactivateLasso();
		}
		aDragMode = DragMode.DRAG_NONE;
	}

	// CSOFF:
	public void mouseDragged(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		boolean isCtrl = pEvent.isControlDown();

		Optional<Node> lastSelected = aSelectionModel.getLastSelectedNode();
		if(aDragMode == DragMode.DRAG_MOVE && lastSelected.isPresent() )
		{
			// TODO, include edges between selected nodes in the bounds check.
			Node lastNode = lastSelected.get();
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
			for(DiagramElement selected : aSelectionModel )
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

			for(DiagramElement selected : aSelectionModel)
			{
				((Node) selected).translate(dx, dy);
			}
			aCanvas.paintPanel();
		}
		else if(aDragMode == DragMode.DRAG_LASSO)
		{
			aLastMousePoint = mousePoint;
			aSelectionModel.activateLasso(computeLasso(), aDiagram, isCtrl);
		}
		else if(aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			aLastMousePoint = mousePoint;
			aSelectionModel.activateRubberband(computeRubberband());
		}
		aLastMousePoint = mousePoint; // TODO move into if-else, so it's no longer redundant
	} // CSON:

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