/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.diagram.DiagramType.viewerFor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ca.mcgill.cs.jetuml.application.Clipboard;
import ca.mcgill.cs.jetuml.application.MoveTracker;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.ClassDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.builder.DiagramOperationProcessor;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
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
	private static final int GRID_SIZE = 10;
	
	private final SelectionModel aSelectionModel;
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final DiagramCanvas aCanvas;
	private final DiagramBuilder aDiagramBuilder;
	private final DiagramTabToolBar aToolBar;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;  
	private DiagramOperationProcessor aProcessor = new DiagramOperationProcessor();
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
		aCanvas.setOnMousePressed(this::mousePressed);
		aCanvas.setOnMouseReleased(this::mouseReleased);
		aCanvas.setOnMouseDragged(this::mouseDragged);
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
	 * Notify the controller that its diagram has been saved.
	 */
	public void diagramSaved()
	{
		aProcessor.diagramSaved();
	}
	
	/**
	 * @return True if the diagram controlled by this controller 
	 *     has unsaved changes.
	 */
	public boolean hasUnsavedChanges()
	{
		return aProcessor.hasUnsavedOperations();
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
			}
		}
	}
	
	/**
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		if( !Clipboard.instance().validPaste(aDiagramBuilder.getDiagram()))
		{
			return;
		}
		Iterable<DiagramElement> newElements = Clipboard.instance().getElements();
		if(!aSelectionModel.isEmpty() && 
				viewerFor(aDiagramBuilder.getDiagram()).isOverlapping(aSelectionModel.getSelectionBounds(), newElements)) 
		{
			shiftElements(newElements, GRID_SIZE);
		}
		aProcessor.executeNewOperation(aDiagramBuilder.createAddElementsOperation(newElements));
		List<DiagramElement> newElementList = new ArrayList<>();
		for( DiagramElement element : newElements )
		{
			newElementList.add(element);
		}
		aSelectionModel.setSelectionTo(newElementList);
		Clipboard.instance().copy(newElements);
		aCanvas.paintPanel();
	}
	
	/**
	 * @param pElements The elements to shift.
	 * @param pShiftAmount Amount to shift elements by to prevent overlapping.
	 */
	private void shiftElements(Iterable<DiagramElement> pElements, int pShiftAmount) 
	{
		for (DiagramElement element: pElements) 
		{
			if(element instanceof Node) 
			{
				((Node)element).translate(pShiftAmount, pShiftAmount);
			}
		}
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
		return new Rectangle(Math.min(aMouseDownPoint.getX(), aLastMousePoint.getX()), 
						     Math.min(aMouseDownPoint.getY(), aLastMousePoint.getY()), 
						     Math.abs(aMouseDownPoint.getX() - aLastMousePoint.getX()) , 
						     Math.abs(aMouseDownPoint.getY() - aLastMousePoint.getY()));
	}
	
	private Point getMousePoint(MouseEvent pEvent)
	{
		return new Point((int)pEvent.getX(), (int)pEvent.getY());
	}

	private Optional<? extends DiagramElement> getSelectedElement(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		Optional<? extends DiagramElement> element = 
				viewerFor(aDiagramBuilder.getDiagram()).findEdge(aDiagramBuilder.getDiagram(), mousePoint);
		if(!element.isPresent())
		{
			element = viewerFor(aDiagramBuilder.getDiagram())
					.findNode(aDiagramBuilder.getDiagram(), new Point(mousePoint.getX(), mousePoint.getY())); 
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
			// Reorder the selected nodes to ensure that they appear on the top
			for(Node pSelected: aSelectionModel.getSelectedNodes()) 
			{
				aCanvas.getDiagram().placeOnTop(pSelected);
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
		Optional<DiagramElement> tool = aToolBar.getCreationPrototype();
		Optional<? extends DiagramElement> element = getSelectedElement(pEvent);
		if(tool.isEmpty())
		{
			handleSelection(pEvent);
		}
		else
		{
			if(tool.get() instanceof Node)
			{
				if( creationEnabled(element, tool.get()) )
				{
					handleNodeCreation(pEvent);
				}
				else 
				{
					handleSelection(pEvent);
				}
			}
			else if(tool.get() instanceof Edge)
			{
				handleEdgeStart(pEvent);
			}
		}
	}
	
	/*
	 * The creation of a node is not allowed if there is an element under the mouse,
	 * except if the target node is a PackageNode OR the tool is a field node.
	 */
	private static boolean creationEnabled(Optional<? extends DiagramElement> pElement, DiagramElement pTool)
	{
		if( pElement.isEmpty() )
		{
			return true;
		}
		if( pElement.get() instanceof PackageNode || pTool instanceof FieldNode)
		{
			return true;
		}
		return false;
	}
	
	private void handleNodeCreation(MouseEvent pEvent)
	{
		assert aToolBar.getCreationPrototype().isPresent();
		Node newNode = ((Node) aToolBar.getCreationPrototype().get()).clone();
		Point point = Grid.snapped(getMousePoint(pEvent));
		if(aDiagramBuilder.canAdd(newNode, point))
		{
			aProcessor.executeNewOperation(aDiagramBuilder.createAddNodeOperation(newNode, new Point(point.getX(), point.getY())));
			aSelectionModel.set(newNode);
			aCanvas.getDiagram().placeOnTop(newNode);
			aCanvas.paintPanel();
			if( UserPreferences.instance().getBoolean(BooleanPreference.autoEditNode))
			{
				editSelected();
			}
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
			alignMoveToGrid();
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
	private void alignMoveToGrid()
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
		Edge newEdge = ((Edge) aToolBar.getCreationPrototype().get()).clone();
		if(pMousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD )
		{
			if( aDiagramBuilder.canAdd(newEdge, aMouseDownPoint, pMousePoint))
			{
				aProcessor.executeNewOperation(aDiagramBuilder.createAddEdgeOperation(newEdge, 
						aMouseDownPoint, pMousePoint));
				aSelectionModel.set(newEdge);
				aCanvas.paintPanel();
			}
		}
		aSelectionModel.deactivateRubberband();
	}
	
	private void releaseMove()
	{
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
		if(aDragMode == DragMode.DRAG_MOVE && !aSelectionModel.isEmpty() ) 
		{
			// The second condition in the if is necessary in the case where a single 
			// element is selected with the Ctrl button is down, which immediately deselects it.
			Point pointToReveal = computePointToReveal(mousePoint);
			moveSelection(mousePoint);
			aHandler.interactionTo(pointToReveal);
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
	}
	
	// finds the point to reveal based on the entire selection
	private Point computePointToReveal(Point pMousePoint)
	{
		Rectangle bounds = aSelectionModel.getEntireSelectionBounds();
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
		assert !aSelectionModel.isEmpty();
		
		int dx = pMousePoint.getX() - aLastMousePoint.getX();
		int dy = pMousePoint.getY() - aLastMousePoint.getY();

		// Ensure the selection does not exceed the canvas bounds
		Rectangle bounds = aSelectionModel.getEntireSelectionBounds();
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
	
	/**
	 * When the shift key is pressed on a class diagram, perform node attachment or detachment if possible.
	 */
	public void shiftKeyPressed()
	{
		if(aCanvas.getDiagram().getType() != DiagramType.CLASS)
		{
			return;
		}
		List<Node> selectedNodes = aSelectionModel.getSelectedNodes();
		if(((ClassDiagramBuilder)aDiagramBuilder).canLinkToPackage(selectedNodes))
		{
			aProcessor.executeNewOperation(((ClassDiagramBuilder)aDiagramBuilder).createLinkToPackageOperation(selectedNodes));
		}
		else if(((ClassDiagramBuilder)aDiagramBuilder).canUnlinkFromPackage(selectedNodes))
		{
			aProcessor.executeNewOperation(((ClassDiagramBuilder)aDiagramBuilder).createUnlinkFromPackageOperation(selectedNodes));
		}
		// Place the modified nodes on the top
		selectedNodes.forEach(node -> aCanvas.getDiagram().placeOnTop(node));
		aCanvas.paintPanel();
	}
}
