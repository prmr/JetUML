/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
import static java.util.stream.Collectors.toList;

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
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.application.UserPreferences.StringPreferenceChangeHandler;
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
import org.jetuml.diagram.validator.DiagramValidator;
import org.jetuml.diagram.validator.Violation;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.GridUtils;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.AccessoriesRenderer;
import org.jetuml.rendering.GraphicsRenderingContext;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.SvgRenderingContext;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

/**
 * A canvas on which to view diagrams.
 */
public class DiagramCanvas extends Canvas implements SelectionObserver, BooleanPreferenceChangeHandler, IntegerPreferenceChangeHandler,
StringPreferenceChangeHandler
{	
	private static final double LINE_WIDTH = 0.6;
	/* The number of pixels to leave around a diagram when the canvas size
	 * is automatically increased to accommodate a diagram larger than the 
	 * preferred size. */
	private static final int DIMENSION_BUFFER = 20;
	private static final int GRID_SIZE = 10;
	private static final int DIAGRAM_PADDING = 4;
	private static final int CONNECT_THRESHOLD = 8;
	
	private DiagramOperationProcessor aProcessor = new DiagramOperationProcessor();
	private final DiagramBuilder aDiagramBuilder;
	private final DiagramValidator aDiagramValidator;
	private final DiagramTabToolBar aToolBar;
	private MouseDraggedGestureHandler aHandler;
	private final RenderingContext aRenderingContext;
	private final AccessoriesRenderer aAccessoriesRenderer;
	
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
		
	private final MoveTracker aMoveTracker;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;  
	
	private List<DiagramElement> aSelected = new ArrayList<>();
	private Optional<Line> aRubberband = Optional.empty();
	private Optional<Rectangle> aLasso = Optional.empty();
	
	/**
	 * Constructs the canvas, assigns the diagram to it.
	 * 
	 * @param pDiagramBuilder The builder wrapping the diagram to draw on this canvas.
	 * @param pDiagramValidator The validator that checks the diagram's semantic validity.
	 * @pre pDiagramBuilder != null;
	 */
	public DiagramCanvas(DiagramBuilder pDiagramBuilder,
						 DiagramTabToolBar pToolBar,
						 DiagramValidator pDiagramValidator, MouseDraggedGestureHandler pHandler)
	{
		assert pDiagramBuilder != null && pDiagramValidator.isValid();
		aToolBar = pToolBar;
		aDiagramBuilder = pDiagramBuilder;
		aDiagramValidator = pDiagramValidator;
		aMoveTracker = new MoveTracker(aDiagramBuilder.renderer()::getBounds);
		Dimension dimension = getDiagramCanvasWidth();
		setWidth(dimension.width());
		setHeight(dimension.height());
		aDiagramBuilder.setCanvasDimension(new Dimension(width(), height()));
		aRenderingContext = new GraphicsRenderingContext(getGraphicsContext2D());
		aAccessoriesRenderer = new AccessoriesRenderer(aRenderingContext);
		aHandler = pHandler;
		setOnMousePressed(this::mousePressed);
		setOnMouseReleased(this::mouseReleased);
		setOnMouseDragged(this::mouseDragged);
	}
	
	private int width()
	{
		return (int) getWidth();
	}
	
	private int height()
	{
		return (int) getHeight();
	}
	
	/**
	 * Removes any element in the selection model that is not in the diagram.
	 * TODO a hack which will hopefully be factored out.
	 */
	public void synchronizeSelectionModel()
	{
		Set<DiagramElement> toBeRemoved = new HashSet<>();
		for(DiagramElement selected : aSelected )
		{
			if(!diagram().contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
		}
		toBeRemoved.forEach( element -> removeFromSelection(element));            
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
		setSelectionTo(newElementList);
		Clipboard.instance().copy(newElements);
		paintPanel(); // TODO double-check if this is necessary since it's already called in setSelectionTo
	}
	
	/**
	 * @param pElements The elements to shift.
	 * @param pShiftAmount Amount to shift elements by to prevent overlapping.
	 */
	private static void shiftElements(Iterable<DiagramElement> pElements, int pShiftAmount) 
	{
		for(DiagramElement element: pElements) 
		{
			if(element instanceof Node node) 
			{
				node.translate(pShiftAmount, pShiftAmount);
			}
		}
	}
	
	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		Optional<DiagramElement> edited = getLastSelected();
		if( edited.isPresent() )
		{
			PropertyEditorDialog dialog = new PropertyEditorDialog( ((EditorFrame) getScene().getRoot()).getDialogStage(), 
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
		Rectangle area = new Rectangle(0, 0, width(), height());
		aRenderingContext.drawRectangle(area, ColorScheme.get().background(), 
				ColorScheme.get().background(), Optional.empty());
		if(UserPreferences.instance().getBoolean(BooleanPreference.showGrid)) 
		{
			aAccessoriesRenderer.drawGrid(area);
		}
		aDiagramBuilder.renderer().draw(aRenderingContext);
		synchronizeSelectionModel();
		drawHandlesOnSelectedEdges();
		drawHandlesOnSelectedNodes();
		aRubberband.ifPresent( rubberband -> aAccessoriesRenderer.drawRubberband(rubberband));
		aLasso.ifPresent( lasso -> aAccessoriesRenderer.drawLasso(lasso));
	}
	
	private void drawHandlesOnSelectedEdges()
	{
		aSelected.stream()
		.filter(Edge.class::isInstance)
		.map(Edge.class::cast)
		.forEach(edge -> aAccessoriesRenderer.drawHandles(aDiagramBuilder.renderer().getConnectionPoints(edge)));
	}
	
	private void drawHandlesOnSelectedNodes()
	{
		aSelected.stream()
		.filter(Node.class::isInstance)
		.map(Node.class::cast)
		.forEach(node -> aAccessoriesRenderer.drawHandles(aDiagramBuilder.renderer().getBounds(node)));
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
		Clipboard.instance().copy(aSelected);
	}
	
	/**
	 * Removes the selected graph elements.
	 */
	public void removeSelected()
	{
		aProcessor.executeNewOperation(aDiagramBuilder.createRemoveElementsOperation(aSelected));
		clearSelection();
		paintPanel();
	}
	
	/**
	 * Copy the currently selected elements to the clip board and removes them
	 * from the graph managed by this panel.
	 */
	public void cut()
	{
		Clipboard.instance().copy(aSelected);
		removeSelected();
	}
	
	/**
	 * Select all elements in the diagram.
	 */
	public void selectAll()
	{
		aToolBar.setToolToBeSelect();
		clearSelection();
		aDiagramBuilder.diagram().rootNodes().forEach(this::internalAddToSelection);
		aDiagramBuilder.diagram().edges().forEach(this::internalAddToSelection);
		paintPanel();
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
		List<Node> selectedNodes = selectedNodes();
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
		if( pPreference == BooleanPreference.showGrid || pPreference == BooleanPreference.darkMode )
		{
			paintPanel();
		}
	}
	
	@Override
	public void integerPreferenceChanged(IntegerPreference pPreference) 
	{
		if( pPreference == IntegerPreference.fontSize )
		{
			paintPanel();
		}
	}
	
	@Override
	public void stringPreferenceChanged(StringPreference pPreference) 
	{
		if( pPreference == StringPreference.fontName )
		{
			paintPanel();
		}
	}
	
	/*
	 * If the diagram is smaller than the preferred dimension, return
	 * the preferred dimension. Otherwise, grow the dimensions to accommodate
	 * the diagram.
	 */
	private Dimension getDiagramCanvasWidth()
	{
		Rectangle bounds = aDiagramBuilder.renderer().getBounds();
		return new Dimension(
				Math.max(getPreferredDiagramWidth(), bounds.maxX() + DIMENSION_BUFFER),
				Math.max(getPreferredDiagramHeight(), bounds.maxY() + DIMENSION_BUFFER));
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
		return new Line(new Point(aMouseDownPoint.x(), aMouseDownPoint.y()), 
				new Point(aLastMousePoint.x(), aLastMousePoint.y()));
	}
	
	private Rectangle computeLasso()
	{
		return new Rectangle(Math.min(aMouseDownPoint.x(), aLastMousePoint.x()), 
						     Math.min(aMouseDownPoint.y(), aLastMousePoint.y()), 
						     Math.abs(aMouseDownPoint.x() - aLastMousePoint.x()) , 
						     Math.abs(aMouseDownPoint.y() - aLastMousePoint.y()));
	}
	
	private static Point getMousePoint(MouseEvent pEvent)
	{
		return new Point((int)pEvent.getX(), (int)pEvent.getY());
	}

	private Optional<? extends DiagramElement> getSelectedElement(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		Optional<? extends DiagramElement> element = 
				aDiagramBuilder.renderer().edgeAt(mousePoint);
		if(!element.isPresent())
		{
			element = aDiagramBuilder.renderer()
					.selectableNodeAt(new Point(mousePoint.x(), mousePoint.y())); 
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
				if(!contains(element.get()))
				{
					addToSelection(element.get());
				}
				else
				{
					removeFromSelection(element.get());
				}
			}
			else if(!contains(element.get()))
			{
				// The test is necessary to ensure we don't undo multiple selections
				setSelection(element.get());
			}
			// Reorder the selected nodes to ensure that they appear on the top
			for(Node pSelected: selectedNodes()) 
			{
				diagram().placeOnTop(pSelected);
			}
			aDragMode = DragMode.DRAG_MOVE;
			aMoveTracker.start(aSelected);
		}
		else // Nothing is selected
		{
			if(!pEvent.isControlDown()) 
			{
				clearSelection();
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
		Point point = GridUtils.snapped(getMousePoint(pEvent));
		aProcessor.executeNewOperation(aDiagramBuilder.createAddNodeOperation(newNode, new Point(point.x(), point.y())));
		Optional<Violation> violation = aDiagramValidator.validate();
		
		if(violation.isEmpty())
		{
			setSelection(newNode);
			diagram().placeOnTop(newNode);
			paintPanel();
			if( UserPreferences.instance().getBoolean(BooleanPreference.autoEditNode))
			{
				editSelected();
			}
		}
		else
		{
			aProcessor.undoLastExecutedOperation();
			handleSelection(pEvent);
			NotificationService.instance().spawnNotification(violation.get().description(), ToastNotification.Type.ERROR);
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
		aLastMousePoint = new Point(point.x(), point.y()); 
		aMouseDownPoint = aLastMousePoint;
		paintPanel();
	}

	private void mouseReleased(MouseEvent pEvent)
	{
		if( aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			releaseRubberband(getMousePoint(pEvent));
		}
		else if( aDragMode == DragMode.DRAG_MOVE)
		{
			alignMoveToGrid();
			releaseMove();
		}
		else if( aDragMode == DragMode.DRAG_LASSO )
		{
			deactivateLasso();
		}
		aDragMode = DragMode.DRAG_NONE;
	}
	
	/*
	 * Move by a delta that will align the result of the move gesture with the grid.
	 */
	private void alignMoveToGrid()
	{
		Iterator<Node> selectedNodes = selectedNodes().iterator();
		Rectangle entireBounds = aDiagramBuilder.renderer().getBoundsIncludingParents(aSelected);
		
		if( selectedNodes.hasNext() )
		{
			// Pick one node in the selection model, arbitrarily
			Node firstSelected = selectedNodes.next();
			Rectangle bounds = aDiagramBuilder.renderer().getBounds(firstSelected);
			Rectangle snappedPosition = GridUtils.snapped(bounds);
			
			int dx = snappedPosition.x() - bounds.x();
			int dy = snappedPosition.y() - bounds.y();
			
			//ensure the bounds of the entire selection are not outside the walls of the canvas
			if(entireBounds.maxX() + dx > width()) 
			{
				dx -= GRID_SIZE;
			}
			else if(entireBounds.x() + dx <= 0) 
			{
				dx += GRID_SIZE;
			}
			if(entireBounds.maxY() + dy > height()) 
			{
				dy -= GRID_SIZE;
			}
			else if(entireBounds.y() <= 0) 
			{
				dy += GRID_SIZE;
			}
			
			for(Node selected : selectedNodes())
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
			aProcessor.executeNewOperation(aDiagramBuilder.createAddEdgeOperation(newEdge, aMouseDownPoint, pMousePoint));
			Optional<Violation> violation = aDiagramValidator.validate();
			
			if( violation.isPresent() )
			{
				aProcessor.undoLastExecutedOperation();
				NotificationService.instance().spawnNotification(violation.get().description(), ToastNotification.Type.ERROR);
			}
			else
			{
				setSelection(newEdge);
				paintPanel();
			}
		}
		deactivateRubberband();
	}
	
	private void releaseMove()
	{
		CompoundOperation operation = aMoveTracker.stop();
		if(!operation.isEmpty())
		{
			aProcessor.storeAlreadyExecutedOperation(operation);
		}
		paintPanel();
	}

	private void mouseDragged(MouseEvent pEvent)
	{
		Point mousePoint = getMousePoint(pEvent);
		
		// The second condition in the if is necessary in the case where a single 
		// element is selected with the Ctrl button is down, which immediately deselects it.
		if(aDragMode == DragMode.DRAG_MOVE && !aSelected.isEmpty() ) 
		{	
			// The local variable dragDirection cannot be inlined 
			// because moveSelection changes aLastMousePoint
			Direction dragDirection = Direction.fromLine(aLastMousePoint, mousePoint);
			moveSelection(mousePoint);
			aHandler.interactionTo(aDiagramBuilder.renderer().getBoundsNotIncludingParents(aSelected), dragDirection);
		}
		else if(aDragMode == DragMode.DRAG_LASSO)
		{
			aLastMousePoint = mousePoint;
			if( !pEvent.isControlDown() )
			{
				clearSelection();
			}
			activateLasso();
		}
		else if(aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			aLastMousePoint = mousePoint;
			activateRubberband(computeRubberband());
		}
	}
	
	// TODO, include edges between selected nodes in the bounds check.
	// This will be doable by collecting all edges connected to a transitively selected node.
	private void moveSelection(Point pMousePoint)
	{
		assert !aSelected.isEmpty();
		
		int dx = pMousePoint.x() - aLastMousePoint.x();
		int dy = pMousePoint.y() - aLastMousePoint.y();
		
		// Perform the move without painting it
		selectedNodes().forEach(selected -> selected.translate(dx, dy));
		
		// If this translation results in exceeding the canvas bounds, roll back.
		Rectangle bounds = aDiagramBuilder.renderer().getBoundsIncludingParents(aSelected);
		int dxCorrection = Math.max(-bounds.x(), 0) 
				+ Math.min(width() - bounds.maxX(), 0);
		int dyCorrection = Math.max(-bounds.y(), 0) 
				+ Math.min(height() - bounds.maxY(), 0);
		selectedNodes().forEach(selected -> selected.translate(dxCorrection, dyCorrection));
		
		aLastMousePoint = pMousePoint; 
		paintPanel();
	}
	
	/**
	 * Creates an image of an entire diagram, with a white border around.
	 * @return An image of the diagram.
	 */
	public Image createImage()
	{
		Rectangle bounds = aDiagramBuilder.renderer().getBounds();
		Canvas canvas = new Canvas(bounds.width() + DIAGRAM_PADDING * 2, 
				bounds.height() + DIAGRAM_PADDING *2);
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.setLineWidth(LINE_WIDTH);
		context.setFill(ColorScheme.get().background());
		context.fillRect(0, 0, width(), height());
		context.translate(-bounds.x()+DIAGRAM_PADDING, -bounds.y()+DIAGRAM_PADDING);
		aDiagramBuilder.renderer().draw(new GraphicsRenderingContext(context));
		WritableImage image = new WritableImage(bounds.width() + DIAGRAM_PADDING * 2, 
				bounds.height() + DIAGRAM_PADDING *2);
		canvas.snapshot(null, image);
		createSvgImage();
		return image;
	}

	/**
	 * @return A string that contains an SVG description of the rendered diagram.
	 */
	public String createSvgImage()
	{
		SvgRenderingContext context = new SvgRenderingContext(aDiagramBuilder.renderer().getBounds());
		aDiagramBuilder.renderer().draw(context);
		return context.create();
	}
	
	/**
	 * @return A list of all the selected nodes. 
	 */
	private List<Node> selectedNodes()
	{
		return aSelected.stream()
				.filter(e -> Node.class.isAssignableFrom(e.getClass()))
				.map(Node.class::cast)
				.collect(toList());
	}
	
	/**
	 * Records information about an active lasso selection tool, select all elements
	 * in the lasso, and triggers a notification.
	 * 
	 * @param pLasso The bounds of the current lasso.
	 * @pre pLasso != null;
	 */
	private void activateLasso()
	{
		aLasso = Optional.of(computeLasso());
		aDiagramBuilder.diagram().rootNodes().forEach( node -> selectNode(node, aLasso.get()));
		aDiagramBuilder.diagram().edges().forEach( edge -> selectEdge(edge, aLasso.get()));
		paintPanel();
	}
	
	private void selectNode(Node pNode, Rectangle pLasso)
	{
		if(pLasso.contains(aDiagramBuilder.renderer().getBounds(pNode)))
		{
			internalAddToSelection(pNode);
		}
		pNode.getChildren().forEach(child -> selectNode(child, pLasso));
	}
	
	private void selectEdge(Edge pEdge, Rectangle pLasso )
	{
		if(pLasso.contains(aDiagramBuilder.renderer().getBounds(pEdge)))
		{
			internalAddToSelection(pEdge);
		}		
	}
	
	/**
	 * Removes the active lasso from the model and triggers a notification.
	 */
	private void deactivateLasso()
	{
		aLasso = Optional.empty();
		paintPanel();
	}
	
	/**
	 * Records information about an active rubberband selection tool and triggers a notification.
	 * @param pLine The line that represents the rubberband.
	 * @pre pLine != null;
	 */
	private void activateRubberband(Line pLine)
	{
		assert pLine != null;
		aRubberband = Optional.of(pLine);
		paintPanel();
	}
	
	/**
	 * Removes the active rubberband from the model and triggers a notification.
	 */
	private void deactivateRubberband()
	{
		aRubberband = Optional.empty();
		paintPanel();
	}
	
	/**
	 * Clears any existing selection and initializes it with pNewSelection.
	 * Triggers a notification.
	 * 
	 * @param pNewSelection A list of elements to select.
	 * @pre pNewSelection != null;
	 */
	private void setSelectionTo(List<DiagramElement> pNewSelection)
	{
		assert pNewSelection != null;
		clearSelection();
		pNewSelection.forEach(this::internalAddToSelection);
		paintPanel();
	}
	
	/**
	 * Adds an element to the selection set and sets it as the last 
	 * selected element. If the element is already in the list, it
	 * is added to the end of the list. If the node is transitively 
	 * a child of any node in the list, it is not added.
	 * Triggers a notification.
	 * 
	 * @param pElement The element to add to the list.
	 * @pre pElement != null
	 */
	private void addToSelection(DiagramElement pElement)
	{
		assert pElement != null;
		internalAddToSelection(pElement);
		paintPanel();
	}
	
	private void internalAddToSelection(DiagramElement pElement)
	{
		if( !containsParent( pElement ))
		{
			aSelected.remove(pElement);
			aSelected.add(pElement);
			
			// Remove children in case a parent was added.
			ArrayList<DiagramElement> toRemove = new ArrayList<>();
			for( DiagramElement element : aSelected )
			{
				if( containsParent(element) )
				{
					toRemove.add(element);
				}
			}
			for( DiagramElement element : toRemove )
			{
				// Do no use removeFromSelection because it notifies the observer
				aSelected.remove(element); 
			}
		}
	}
	
	/*
	 * Returns true if any of the parents of pElement is contained
	 * (transitively).
	 * @param pElement The element to test
	 * @return true if any of the parents of pElement are included in the 
	 * selection.
	 */
	private boolean containsParent(DiagramElement pElement)
	{
		if( pElement instanceof Node node)
		{
			if( !node.hasParent() )
			{
				return false;
			}
			else if( aSelected.contains(node.getParent()))
			{
				return true;
			}
			else
			{
				return containsParent(node.getParent());
			}
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Removes all selections and triggers a notification.
	 */
	private void clearSelection()
	{
		aSelected.clear();
		paintPanel();
	}
	
	/**
	 * @return The last element that was selected, if present.
	 */
	private Optional<DiagramElement> getLastSelected()
	{
		if( aSelected.isEmpty() )
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(aSelected.get(aSelected.size()-1));
		}
	}
	
	/**
	 * @param pElement The element to test.
	 * @return True if pElement is in the list of selected elements.
	 */
	private boolean contains(DiagramElement pElement)
	{
		return aSelected.contains(pElement);
	}
	
	/**
	 * Removes pElement from the list of selected elements,
	 * or does nothing if pElement is not selected.
	 * Triggers a notification.
	 * @param pElement The element to remove.
	 * @pre pElement != null;
	 */
	private void removeFromSelection(DiagramElement pElement)
	{
		assert pElement != null;
		aSelected.remove(pElement);
		paintPanel();
	}
	
	/**
	 * Sets pElement as the single selected element.
	 * Triggers a notification.
	 * @param pElement The element to set as selected.
	 * @pre pElement != null;
	 */
	private void setSelection(DiagramElement pElement)
	{
		assert pElement != null;
		aSelected.clear();
		aSelected.add(pElement);
		paintPanel();
	}
}
