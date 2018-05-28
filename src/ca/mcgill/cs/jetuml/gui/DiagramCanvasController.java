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
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
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
	
	private final SelectionModel aSelectionModel;
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final DiagramCanvas aCanvas;
	private final UndoManager aUndoManager;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;   
	
	DiagramCanvasController(DiagramCanvas pCanvas, UndoManager pManager)
	{
		aCanvas = pCanvas;
		aUndoManager = pManager;
		aSelectionModel = new SelectionModel(aCanvas);
		aCanvas.setOnMousePressed(e -> mousePressed(e));
		aCanvas.setOnMouseReleased(e -> mouseReleased(e));
		aCanvas.setOnMouseDragged( e -> mouseDragged(e));
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
		DiagramElement element = aCanvas.getDiagram().findEdge(mousePoint);
		if (element == null)
		{
			element = aCanvas.getDiagram().findNode(new Point(mousePoint.getX(), mousePoint.getY())); 
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
					aSelectionModel.addEdgesIfContained(aCanvas.getDiagram().getEdges());
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
		boolean added = aCanvas.getDiagram().addNode(newNode, new Point(point.getX(), point.getY()), 
				(int) aCanvas.getWidth(), (int) aCanvas.getHeight());
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

	private void mousePressed(MouseEvent pEvent)
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

	private void mouseReleased(MouseEvent pEvent)
	{
		if (aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			releaseRubberband(getMousePoint(pEvent));
		}
		else if(aDragMode == DragMode.DRAG_MOVE)
		{
			releaseMove();
		}
		else if( aDragMode == DragMode.DRAG_LASSO )
		{
			aSelectionModel.deactivateLasso();
		}
		aDragMode = DragMode.DRAG_NONE;
	}
	
	private void releaseRubberband(Point pMousePoint)
	{
		assert aCanvas.getCreationPrototype().isPresent();
		Edge newEdge = (Edge) ((Edge) aCanvas.getCreationPrototype().get()).clone();
		if(pMousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD )
		{
			if( aCanvas.getDiagram().addEdge(newEdge, aMouseDownPoint, pMousePoint) )
			{
				aCanvas.setModified(true);
				aSelectionModel.setSelection(newEdge);
			}
		}
		aSelectionModel.deactivateRubberband();
	}
	
	private void releaseMove()
	{
		// For optimization purposes, some of the layouts are not done on every move event.
		aCanvas.getDiagram().requestLayout();
		aCanvas.setModified(true);
		CompoundCommand command = aMoveTracker.endTrackingMove(aCanvas.getDiagram());
		if(command.size() > 0)
		{
			aUndoManager.add(command);
		}
		aCanvas.paintPanel();
	}

	private void mouseDragged(MouseEvent pEvent)
	{
		if(aDragMode == DragMode.DRAG_MOVE ) 
		{
			moveSelection(getMousePoint(pEvent));
		}
		else if(aDragMode == DragMode.DRAG_LASSO)
		{
			aLastMousePoint = getMousePoint(pEvent);
			aSelectionModel.activateLasso(computeLasso(), aCanvas.getDiagram(), pEvent.isControlDown());
		}
		else if(aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			aLastMousePoint = getMousePoint(pEvent);
			aSelectionModel.activateRubberband(computeRubberband());
		}
	}
	
	// TODO, include edges between selected nodes in the bounds check.
	private void moveSelection(Point pMousePoint)
	{
		int dx = (int)(pMousePoint.getX() - aLastMousePoint.getX());
		int dy = (int)(pMousePoint.getY() - aLastMousePoint.getY());

		// Ensure the selection does not exceed the canvas bounds
		Rectangle bounds = aSelectionModel.getSelectionBounds();
		dx = Math.max(dx, -bounds.getX());
		dy = Math.max(dy, -bounds.getY());
		dx = Math.min(dx, (int) aCanvas.getWidth() - bounds.getMaxX());
		dy = Math.min(dy, (int) aCanvas.getHeight() - bounds.getMaxY());

		for(Node selected : aSelectionModel.getSelectedNodes())
		{
			selected.translate(dx, dy);
		}
		aLastMousePoint = pMousePoint; 
		aCanvas.paintPanel();
	}
}