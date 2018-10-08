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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ca.mcgill.cs.jetuml.application.Clipboard;
import ca.mcgill.cs.jetuml.application.MoveTracker;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperationProcessor;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.Grid;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * An instance of this class is responsible to handle the user
 * interface events on a diagram canvas.
 */
public class DiagramCanvasController
{
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
	
	private static final int CONNECT_THRESHOLD = 8;
	
	private final SelectionModel aSelectionModel;
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final DiagramCanvas aCanvas;
	private final DiagramBuilder aDiagramBuilder;
	private final DiagramTabToolBar aToolBar;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;  
	private DiagramOperationProcessor aProcessor = new DiagramOperationProcessor();
	private boolean aModified = false;
	private MouseDraggedGestureHandler aHandler;

	
	/**
	 * Creates a new controller.
	 * @param pCanvas The canvas being controlled
	 * @param pToolBar The toolbar.
	 * @param pHandler A handler for when the mouse is dragged
	 */
	public DiagramCanvasController(DiagramCanvas pCanvas, DiagramTabToolBar pToolBar, MouseDraggedGestureHandler pHandler)
	{
		aCanvas = pCanvas;
		aDiagramBuilder = DiagramType.newBuilderInstanceFor(aCanvas.getDiagram());
		aDiagramBuilder.setCanvasDimension(new Dimension((int) aCanvas.getWidth(), (int)aCanvas.getHeight()));
		aSelectionModel = new SelectionModel(aCanvas);
		aToolBar = pToolBar;
		aCanvas.setOnMousePressed(e -> mousePressed(e));
		aCanvas.setOnMouseReleased(e -> mouseReleased(e));
		aCanvas.setOnMouseDragged( e -> mouseDragged(e));
		aHandler = pHandler;
	}
	
	/**
	 * Removes any element in the selection model that is not in the diagram.
	 * TODO a hack which will hopefully be factored out.
	 */
	public void synchronizeSelectionModel()
	{
		Set<DiagramElement> toBeRemoved = new HashSet<>();
		for(DiagramElement selected : aSelectionModel )
		{
			if(!aCanvas.getDiagram().contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
		}

		toBeRemoved.forEach( element -> aSelectionModel.removeFromSelection(element));            
	}
	
	/**
	 * @return The selection model associated with this controller
	 */
	public SelectionModel getSelectionModel()
	{
		return aSelectionModel;
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
	}
	
	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		Optional<DiagramElement> edited = aSelectionModel.getLastSelected();
		if( edited.isPresent() )
		{
			PropertyEditorDialog dialog = new PropertyEditorDialog((Stage)aCanvas.getScene().getWindow(), 
					edited.get(), ()-> aCanvas.paintPanel());
			
			CompoundOperation operation = dialog.show();
			if(!operation.isEmpty())
			{
				aProcessor.storeAlreadyExecutedOperation(operation);
				setModified(true);
			}
		}
	}
	
	/**
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		if( !Clipboard.instance().validPaste(aDiagramBuilder.getView().getDiagram()))
		{
			return;
		}
		Iterable<DiagramElement> newElements = Clipboard.instance().getElements();
		aProcessor.executeNewOperation(aDiagramBuilder.createAddElementsOperation(newElements));
		List<DiagramElement> newElementList = new ArrayList<>();
		for( DiagramElement element : newElementList )
		{
			newElementList.add(element);
		}
		aSelectionModel.setSelectionTo(newElementList);
		aCanvas.paintPanel();
	}
	
	/**
	 * Undoes the most recent command.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void undo()
	{
		if( aProcessor.canUndo() )
		{
			aProcessor.undoLastExecutedOperation();
			aCanvas.paintPanel();
		}
	}
	
	/**
	 * Removes the last undone action and performs it.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void redo()
	{
		if( aProcessor.canRedo() )
		{
			aProcessor.redoLastUndoneOperation();
			aCanvas.paintPanel();
		}
	}
	
	/**
	 * Copy the currently selected elements to the clip board.
	 */
	public void copy()
	{
		Clipboard.instance().copy(aSelectionModel);
	}
	
	/**
	 * Removes the selected graph elements.
	 */
	public void removeSelected()
	{
		aProcessor.executeNewOperation(aDiagramBuilder.createRemoveElementsOperation(aSelectionModel));
		aSelectionModel.clearSelection();
		aCanvas.paintPanel();
	}
	
	/**
	 * Copy the currently selected elements to the clip board and removes them
	 * from the graph managed by this panel.
	 */
	public void cut()
	{
		Clipboard.instance().copy(aSelectionModel);
		removeSelected();
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

	private Optional<? extends DiagramElement> getSelectedElement(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		Optional<? extends DiagramElement> element = aDiagramBuilder.getView().findEdge(mousePoint);
		if(!element.isPresent())
		{
			element = aDiagramBuilder.getView().findNode(new Point(mousePoint.getX(), mousePoint.getY())); 
		}
		return element;
	}

	private void handleSelection(MouseEvent pEvent)
	{
		Optional<? extends DiagramElement> element = getSelectedElement(pEvent);
		if(element.isPresent()) 
		{
			if(pEvent.isControlDown())
			{
				if(!aSelectionModel.contains(element.get()))
				{
					aSelectionModel.addToSelection(element.get());
				}
				else
				{
					aSelectionModel.removeFromSelection(element.get());
				}
			}
			else if(!aSelectionModel.contains(element.get()))
			{
				// The test is necessary to ensure we don't undo multiple selections
				aSelectionModel.set(element.get());
			}
			aDragMode = DragMode.DRAG_MOVE;
			aMoveTracker.startTrackingMove(aSelectionModel);
		}
		else // Nothing is selected
		{
			if(!pEvent.isControlDown()) 
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
		assert aToolBar.getCreationPrototype().isPresent();
		Node newNode = ((Node) aToolBar.getCreationPrototype().get()).clone();
		Point point = Grid.snapped(getMousePoint(pEvent));
		if(aDiagramBuilder.canAdd(newNode, point))
		{
			aProcessor.executeNewOperation(aDiagramBuilder.createAddNodeOperation(newNode, new Point(point.getX(), point.getY())));
			setModified(true);
			aSelectionModel.set(newNode);
			aCanvas.paintPanel();
		}
		else // Special behavior, if we can't add a node, we select any element at the point
		{
			handleSelection(pEvent);
		}
	}

	private void handleEdgeStart(MouseEvent pEvent)
	{
		Optional<? extends DiagramElement> element = getSelectedElement(pEvent);
		if(element.isPresent() && element.get() instanceof Node) 
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
		Optional<DiagramElement> tool = aToolBar.getCreationPrototype();
		Optional<? extends DiagramElement> selected = getSelectedElement(pEvent);

		if( tool.isPresent() && tool.get() instanceof Node)
		{
			if(selected.isPresent() && selected.get() instanceof Node)
			{
				if(!(tool.get() instanceof ChildNode && selected.get() instanceof ParentNode))
				{
					aToolBar.setToolToBeSelect();
					tool = Optional.empty();
				}
			}
		}	
		return tool;
	}
	
	/**
	 * Select all elements in the diagram.
	 */
	public void selectAll()
	{
		aToolBar.setToolToBeSelect();
		aSelectionModel.selectAll(aCanvas.getDiagram());
	}

	private void mousePressed(MouseEvent pEvent)
	{
		if( pEvent.isSecondaryButtonDown() )
		{
			aToolBar.showPopup(pEvent.getScreenX(), pEvent.getScreenY());
		}
		else if( pEvent.getClickCount() > 1 )
		{
			editSelected();
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
			alignMoveToGrid(getMousePoint(pEvent));
			releaseMove();
		}
		else if( aDragMode == DragMode.DRAG_LASSO )
		{
			aSelectionModel.deactivateLasso();
		}
		aDragMode = DragMode.DRAG_NONE;
	}
	
	/*
	 * Move by a delta that will align the result of the move gesture with the grid.
	 */
	private void alignMoveToGrid(Point pMousePoint)
	{
		Iterator<Node> selectedNodes = aSelectionModel.getSelectedNodes().iterator();
		if( selectedNodes.hasNext() )
		{
			// Pick one node in the selection model, arbitrarily
			Node firstSelected = selectedNodes.next();
			Point position = firstSelected.position();
			Point snappedPosition = Grid.snapped(position);
			final int dx = snappedPosition.getX() - position.getX();
			final int dy = snappedPosition.getY() - position.getY();
			for(Node selected : aSelectionModel.getSelectedNodes())
			{
				selected.translate(dx, dy);
			}
			aCanvas.paintPanel();
		}
	}
	
	private void releaseRubberband(Point pMousePoint)
	{
		assert aToolBar.getCreationPrototype().isPresent();
		Edge newEdge = (Edge) ((Edge) aToolBar.getCreationPrototype().get()).clone();
		if(pMousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD )
		{
			if( aDiagramBuilder.canAdd(newEdge, aMouseDownPoint, pMousePoint))
			{
				aProcessor.executeNewOperation(aDiagramBuilder.createAddEdgeOperation(newEdge, 
						aMouseDownPoint, pMousePoint));
				setModified(true);
				aSelectionModel.set(newEdge);
				aCanvas.paintPanel();
			}
		}
		aSelectionModel.deactivateRubberband();
	}
	
	private void releaseMove()
	{
		setModified(true);
		CompoundOperation operation = aMoveTracker.endTrackingMove(aDiagramBuilder);
		if(!operation.isEmpty())
		{
			aProcessor.storeAlreadyExecutedOperation(operation);
		}
		aCanvas.paintPanel();
	}

	private void mouseDragged(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		Point pointToReveal = mousePoint;
		if(aDragMode == DragMode.DRAG_MOVE ) 
		{
			pointToReveal = computePointToReveal(mousePoint);
			moveSelection(mousePoint);
		}
		else if(aDragMode == DragMode.DRAG_LASSO)
		{
			aLastMousePoint = mousePoint;
			if( !pEvent.isControlDown() )
			{
				aSelectionModel.clearSelection();
			}
			aSelectionModel.activateLasso(computeLasso(), aCanvas.getDiagram());
		}
		else if(aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			aLastMousePoint = mousePoint;
			aSelectionModel.activateRubberband(computeRubberband());
		}
		aHandler.interactionTo(pointToReveal);
	}
	
	// finds the point to reveal based on the entire selection
	private Point computePointToReveal(Point pMousePoint)
	{
		Rectangle bounds = aSelectionModel.getSelectionBounds();
		int x = bounds.getMaxX();
		int y = bounds.getMaxY();
		
		if( pMousePoint.getX() < aLastMousePoint.getX()) 	 // Going left, reverse coordinate
		{
			x = bounds.getX(); 
		}
		if( pMousePoint.getY() < aLastMousePoint.getY())	// Going up, reverse coordinate
		{
			y = bounds.getY(); 
		}
		return new Point(x, y);
	}
	
	// TODO, include edges between selected nodes in the bounds check.
	// This will be doable by collecting all edges connected to a transitively selected node.
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