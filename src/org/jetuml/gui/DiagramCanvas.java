/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package org.jetuml.gui;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetuml.application.Clipboard;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.BooleanPreference;
import org.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.IntegerPreferenceChangeHandler;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.builder.ClassDiagramBuilder;
import org.jetuml.diagram.builder.CompoundOperation;
import org.jetuml.diagram.builder.DiagramBuilder;
import org.jetuml.diagram.builder.DiagramOperationProcessor;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.RenderingFacade;
import org.jetuml.viewers.Grid;
import org.jetuml.viewers.ToolGraphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * A canvas on which to view diagrams.
 */
public class DiagramCanvas extends Canvas implements SelectionObserver, BooleanPreferenceChangeHandler, IntegerPreferenceChangeHandler
{	
	private static final double LINE_WIDTH = 0.6;
	/* The number of pixels to leave around a diagram when the canvas size
	 * is automatically increased to accommodate a diagram larger than the 
	 * preferred size. */
	private static final int DIMENSION_BUFFER = 20;
	private static final int GRID_SIZE = 10;
	
	private final SelectionModel aSelectionModel;
	private DiagramOperationProcessor aProcessor = new DiagramOperationProcessor();
	private final DiagramBuilder aDiagramBuilder;
	private final DiagramTabToolBar aToolBar;
	private MouseDraggedGestureHandler aHandler;
	
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
	
	private static final int CONNECT_THRESHOLD = 8;
	
	private final MoveTracker aMoveTracker = new MoveTracker();
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;  
	
	/**
	 * Constructs the canvas, assigns the diagram to it.
	 * 
	 * @param pDiagramBuilder The builder wrapping the diagram to draw on this canvas.
	 * @pre pDiagramBuilder != null;
	 */
	public DiagramCanvas(DiagramBuilder pDiagramBuilder, DiagramTabToolBar pToolBar, MouseDraggedGestureHandler pHandler)
	{
		assert pDiagramBuilder != null;
		aToolBar = pToolBar;
		aSelectionModel = new SelectionModel(this);
		aDiagramBuilder = pDiagramBuilder;
		aDiagramBuilder.setCanvasDimension(new Dimension((int) getWidth(), (int)getHeight()));
		RenderingFacade.prepareFor(pDiagramBuilder.diagram());
		Dimension dimension = getDiagramCanvasWidth(pDiagramBuilder.diagram());
		setWidth(dimension.width());
		setHeight(dimension.height());
		getGraphicsContext2D().setLineWidth(LINE_WIDTH);
		getGraphicsContext2D().setFill(Color.WHITE);
		aHandler = pHandler;
		setOnMousePressed(this::mousePressed);
		setOnMouseReleased(this::mouseReleased);
		setOnMouseDragged(this::mouseDragged);
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
			if(!diagram().contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
		}
		toBeRemoved.forEach( element -> aSelectionModel.removeFromSelection(element));            
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
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		if( !Clipboard.instance().validPaste(aDiagramBuilder.diagram()))
		{
			return;
		}
		Iterable<DiagramElement> newElements = Clipboard.instance().getElements();
		if(Clipboard.instance().overlapsWithElementOf(aDiagramBuilder.diagram()))
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
		paintPanel();
	}
	
	/**
	 * @param pElements The elements to shift.
	 * @param pShiftAmount Amount to shift elements by to prevent overlapping.
	 */
	private static void shiftElements(Iterable<DiagramElement> pElements, int pShiftAmount) 
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
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		Optional<DiagramElement> edited = aSelectionModel.getLastSelected();
		if( edited.isPresent() )
		{
			PropertyEditorDialog dialog = new PropertyEditorDialog((Stage)getScene().getWindow(), 
					edited.get(), ()-> paintPanel());
			
			CompoundOperation operation = dialog.show();
			if(!operation.isEmpty())
			{
				aProcessor.storeAlreadyExecutedOperation(operation);
			}
		}
	}
	
	@Override
	public boolean isResizable()
	{
	    return false;
	}
	
	/**
	 * @return The diagram painted on this canvas.
	 */
	public Diagram diagram()
	{
		return aDiagramBuilder.diagram();
	}
	
	/**
	 * Paints the panel and all the graph elements in aDiagramView.
	 * Called after the panel is resized.
	 */
	public void paintPanel()
	{
		RenderingFacade.prepareFor(diagram());
		GraphicsContext context = getGraphicsContext2D();
		context.setFill(Color.WHITE); 
		context.fillRect(0, 0, getWidth(), getHeight());
		if(UserPreferences.instance().getBoolean(BooleanPreference.showGrid)) 
		{
			Grid.draw(context, new Rectangle(0, 0, (int) getWidth(), (int) getHeight()));
		}
		RenderingFacade.draw(diagram(), context);
		synchronizeSelectionModel();
		aSelectionModel.forEach( selected -> RenderingFacade.drawSelectionHandles(selected, context));
		aSelectionModel.getRubberband().ifPresent( rubberband -> ToolGraphics.drawRubberband(context, rubberband));
		aSelectionModel.getLasso().ifPresent( lasso -> ToolGraphics.drawLasso(context, lasso));
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
			paintPanel();
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
			paintPanel();
		}
	}
	
	@Override
	public void selectionModelChanged()
	{
		paintPanel();		
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
		paintPanel();
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
	
	/**
	 * Select all elements in the diagram.
	 */
	public void selectAll()
	{
		aToolBar.setToolToBeSelect();
		aSelectionModel.selectAll(diagram());
	}
	
	/**
	 * When the shift key is pressed on a class diagram, perform node attachment or detachment if possible.
	 */
	public void shiftKeyPressed()
	{
		if(diagram().getType() != DiagramType.CLASS)
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
		selectedNodes.forEach(node -> diagram().placeOnTop(node));
		paintPanel();
	}

	@Override
	public void booleanPreferenceChanged(BooleanPreference pPreference)
	{
		if( pPreference == BooleanPreference.showGrid )
		{
			paintPanel();
		}
	}
	
	@Override
	public void integerPreferenceChanged(IntegerPreference pPreference) 
	{
		if ( pPreference == IntegerPreference.fontSize )
		{
			paintPanel();
		}

	}
	
	/*
	 * If the diagram is smaller than the preferred dimension, return
	 * the preferred dimension. Otherwise, grow the dimensions to accommodate
	 * the diagram.
	 */
	private static Dimension getDiagramCanvasWidth(Diagram pDiagram)
	{
		Rectangle bounds = RenderingFacade.getBounds(pDiagram);
		return new Dimension(
				Math.max(getPreferredDiagramWidth(), bounds.getMaxX() + DIMENSION_BUFFER),
				Math.max(getPreferredDiagramHeight(), bounds.getMaxY() + DIMENSION_BUFFER));
	}
	
	private static int getPreferredDiagramWidth()
	{
		int preferredWidth = UserPreferences.instance().getInteger(IntegerPreference.diagramWidth);
		if( preferredWidth == 0 )
		{
			int width = GuiUtils.defaultDiagramWidth();
			UserPreferences.instance().setInteger(IntegerPreference.diagramWidth, width);
			return width;
		}
		else
		{
			return preferredWidth;
		}
	}
	
	private static int getPreferredDiagramHeight()
	{
		int preferredHeight = UserPreferences.instance().getInteger(IntegerPreference.diagramHeight);
		if( preferredHeight == 0 )
		{
			int height = GuiUtils.defaultDiagramHeight();
			UserPreferences.instance().setInteger(IntegerPreference.diagramHeight, height);
			return height;
		}
		else
		{
			return preferredHeight;
		}
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
	
	private static Point getMousePoint(MouseEvent pEvent)
	{
		return new Point((int)pEvent.getX(), (int)pEvent.getY());
	}

	private Optional<? extends DiagramElement> getSelectedElement(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		Optional<? extends DiagramElement> element = 
				RenderingFacade.edgeAt(aDiagramBuilder.diagram(), mousePoint);
		if(!element.isPresent())
		{
			element = RenderingFacade
					.selectableNodeAt(aDiagramBuilder.diagram(), new Point(mousePoint.getX(), mousePoint.getY())); 
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
				diagram().placeOnTop(pSelected);
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
			diagram().placeOnTop(newNode);
			paintPanel();
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
		paintPanel();
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
		Rectangle entireBounds = RenderingFacade.getBoundsIncludingParents(aSelectionModel);
		
		if( selectedNodes.hasNext() )
		{
			// Pick one node in the selection model, arbitrarily
			Node firstSelected = selectedNodes.next();
			Rectangle bounds = RenderingFacade.getBounds(firstSelected);
			Rectangle snappedPosition = Grid.snapped(bounds);
			
			int dx = snappedPosition.getX() - bounds.getX();
			int dy = snappedPosition.getY() - bounds.getY();
			
			//ensure the bounds of the entire selection are not outside the walls of the canvas
			if (entireBounds.getMaxX() + dx > getWidth()) 
			{
				dx -= GRID_SIZE;
			}
			else if (entireBounds.getX() + dx <= 0) 
			{
				dx += GRID_SIZE;
			}
			if (entireBounds.getMaxY() + dy > getHeight()) 
			{
				dy -= GRID_SIZE;
			}
			else if (entireBounds.getY() <= 0) 
			{
				dy += GRID_SIZE;
			}
			
			for(Node selected : aSelectionModel.getSelectedNodes())
			{
				selected.translate(dx, dy);
			}
			paintPanel();
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
				paintPanel();
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
		paintPanel();
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
			aSelectionModel.activateLasso(computeLasso(), diagram());
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
		Rectangle bounds = RenderingFacade.getBoundsIncludingParents(aSelectionModel);
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
		
		// Perform the move without painting it
		aSelectionModel.getSelectedNodes().forEach(selected -> selected.translate(dx, dy));
		
		// If this translation results in exceeding the canvas bounds, roll back.
		Rectangle bounds =RenderingFacade.getBoundsIncludingParents(aSelectionModel);
		int dxCorrection = Math.max(-bounds.getX(), 0) 
				+ Math.min((int)getWidth() - bounds.getMaxX(), 0);
		int dyCorrection = Math.max(-bounds.getY(), 0) 
				+ Math.min((int)getHeight() - bounds.getMaxY(), 0);
		aSelectionModel.getSelectedNodes().forEach(selected -> selected.translate(dxCorrection, dyCorrection));
		
		aLastMousePoint = pMousePoint; 
		paintPanel();
	}
	
	/**
	 * Tracks the movement of a set of selected diagram elements.
	 */
	private static final class MoveTracker
	{
		private final List<Node> aTrackedNodes = new ArrayList<>();
		private final List<Rectangle> aOriginalBounds = new ArrayList<>();
		
		/**
		 * Records the elements in pSelectedElements and their position at the 
		 * time where the method is called.
		 * 
		 * @param pSelectedElements The elements that are being moved. Not null.
		 */
		void startTrackingMove(Iterable<DiagramElement> pSelectedElements)
		{
			assert pSelectedElements != null;
			
			aTrackedNodes.clear();
			aOriginalBounds.clear();
			
			for(DiagramElement element : pSelectedElements)
			{
				assert element != null;
				if(element instanceof Node)
				{
					aTrackedNodes.add((Node) element);
					aOriginalBounds.add(RenderingFacade.getBounds((Node)element));
				}
			}
		}

		/**
		 * Creates and returns a CompoundOperation that represents the movement
		 * of all tracked nodes between the time where startTrackingMove was 
		 * called and the time endTrackingMove was called.
		 * 
		 * @param pDiagramBuilder The Diagram containing the selected elements.
		 * @return A CompoundCommand describing the move.
		 * @pre pDiagramBuilder != null
		 */
		CompoundOperation endTrackingMove(DiagramBuilder pDiagramBuilder)
		{
			assert pDiagramBuilder != null;
			CompoundOperation operation = new CompoundOperation();
			Rectangle[] selectionBounds2 = new Rectangle[aOriginalBounds.size()];
			int i = 0;
			for(Node node : aTrackedNodes)
			{
				selectionBounds2[i] = RenderingFacade.getBounds(node);
				i++;
			}
			for(i = 0; i < aOriginalBounds.size(); i++)
			{
				int dY = selectionBounds2[i].getY() - aOriginalBounds.get(i).getY();
				int dX = selectionBounds2[i].getX() - aOriginalBounds.get(i).getX();
				if(dX != 0 || dY != 0)
				{
					operation.add(DiagramBuilder.createMoveNodeOperation(aTrackedNodes.get(i), dX, dY));
				}
			}
			return operation;
		}
	}
}
