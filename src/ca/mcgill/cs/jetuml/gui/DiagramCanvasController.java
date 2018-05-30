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

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import ca.mcgill.cs.jetuml.application.Clipboard;
import ca.mcgill.cs.jetuml.application.GraphModificationListener;
import ca.mcgill.cs.jetuml.application.MoveTracker;
import ca.mcgill.cs.jetuml.application.PropertyChangeTracker;
import ca.mcgill.cs.jetuml.application.UndoManager;
import ca.mcgill.cs.jetuml.commands.AddEdgeCommand;
import ca.mcgill.cs.jetuml.commands.AddNodeCommand;
import ca.mcgill.cs.jetuml.commands.ChangePropertyCommand;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.jetuml.commands.DeleteNodeCommand;
import ca.mcgill.cs.jetuml.commands.RemoveEdgeCommand;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.Property;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * An instance of this class is responsible to handle the user
 * interface events on a diagram canvas.
 */
public class DiagramCanvasController
{
	private static final int LAYOUT_PADDING = 20;
	
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
	
	private static final int CONNECT_THRESHOLD = 8;
	
	private final SelectionModel aSelectionModel;
	private final MoveTracker aMoveTracker = new MoveTracker();
	private final DiagramCanvas aCanvas;
	private final DiagramTabToolBar aToolBar;
	private DragMode aDragMode;
	private Point aLastMousePoint;
	private Point aMouseDownPoint;  
	private UndoManager aUndoManager = new UndoManager();	
	private boolean aModified = false;

	
	/**
	 * Creates a new controller.
	 * @param pCanvas The canvas being controlled
	 * @param pToolBar The toolbar.
	 */
	public DiagramCanvasController(DiagramCanvas pCanvas, DiagramTabToolBar pToolBar)
	{
		aCanvas = pCanvas;
		aSelectionModel = new SelectionModel(aCanvas);
		aToolBar = pToolBar;
		aCanvas.setOnMousePressed(e -> mousePressed(e));
		aCanvas.setOnMouseReleased(e -> mouseReleased(e));
		aCanvas.setOnMouseDragged( e -> mouseDragged(e));
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
	 * @return The diagram associated with this controller.
	 */
	public Diagram getDiagram()
	{
		return aCanvas.getDiagram();
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
		if( !edited.isPresent() )
		{
			return;
		}
		PropertyChangeTracker tracker = new PropertyChangeTracker(edited.get());
		tracker.startTracking();
		PropertySheet sheet = new PropertySheet(edited.get(), new PropertySheet.PropertyChangeListener()
		{
			@Override
			public void propertyChanged()
			{
				aCanvas.getDiagram().requestLayout();
				aCanvas.paintPanel();
			}
		});
		if (sheet.isEmpty())
		{
			return;
		}

		Stage window = new Stage();
		window.setTitle(RESOURCES.getString("dialog.properties"));
		window.getIcons().add(new Image(RESOURCES.getString("application.icon")));
		window.initModality(Modality.APPLICATION_MODAL);
		
		BorderPane layout = new BorderPane();
		Button button = new Button("OK");
		button.setOnAction(pEvent -> window.close());
		BorderPane.setAlignment(button, Pos.CENTER_RIGHT);
		
		layout.setPadding(new Insets(LAYOUT_PADDING));
		layout.setCenter(sheet);
		layout.setBottom(button);
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.setResizable(false);
		window.initOwner(aCanvas.getScene().getWindow());
		window.show();
		
		CompoundCommand command = tracker.stopTracking();
		if (command.size() > 0)
		{
			aUndoManager.add(command);
		}
		setModified(true);
	}
	
	/**
	 * Indicate to the DiagramCanvas that is should 
	 * consider all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void startCompoundGraphOperation()
	{
		aUndoManager.startTracking();
	}
	
	/**
	 * Indicate to the DiagramCanvas that is should 
	 * stop considering all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void finishCompoundGraphOperation()
	{
		aUndoManager.endTracking();
	}
	
	/**
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		aSelectionModel.setSelectionTo(Clipboard.instance().paste(this));
	}
	
	/**
	 * Undoes the most recent command.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void undo()
	{
		aUndoManager.undoCommand();
		aCanvas.paintPanel();
	}
	
	/**
	 * Removes the last undone action and performs it.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void redo()
	{
		aUndoManager.redoCommand();
		aCanvas.paintPanel();
	}
	
	/**
	 * Copy the currently selected elements to the clip board.
	 */
	public void copy()
	{
		if(!aSelectionModel.isEmpty())
		{
			Clipboard.instance().copy(aSelectionModel);
		}
	}
	
	/**
	 * Removes the selected graph elements.
	 */
	public void removeSelected()
	{
		aUndoManager.startTracking();
		Stack<Node> nodes = new Stack<>();
		for(DiagramElement element : aSelectionModel)
		{
			if(element instanceof Node)
			{
				aCanvas.getDiagram().removeAllEdgesConnectedTo((Node)element);
				nodes.add((Node) element);
			}
			else if(element instanceof Edge)
			{
				aCanvas.getDiagram().removeEdge((Edge) element);
			}
		}
		while(!nodes.empty())
		{
			aCanvas.getDiagram().removeNode(nodes.pop());
		}
		aUndoManager.endTracking();
		if(aSelectionModel.isEmpty())
		{
			setModified(true);
		}
		aCanvas.paintPanel();
	}
	
	/**
	 * Copy the currently selected elements to the clip board and removes them
	 * from the graph managed by this panel.
	 */
	public void cut()
	{
		if(!aSelectionModel.isEmpty())
		{
			Clipboard.instance().cut(this);
		}
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
				}
				else
				{
					aSelectionModel.removeFromSelection(element);
				}
			}
			else if (!aSelectionModel.contains(element))
			{
				// The test is necessary to ensure we don't undo multiple selections
				aSelectionModel.set(element);
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
		assert aToolBar.getCreationPrototype().isPresent();
		Node newNode = ((Node) aToolBar.getCreationPrototype().get()).clone();
		Point point = getMousePoint(pEvent);
		boolean added = aCanvas.getDiagram().addNode(newNode, new Point(point.getX(), point.getY()), 
				(int) aCanvas.getWidth(), (int) aCanvas.getHeight());
		if(added)
		{
			setModified(true);
			aSelectionModel.set(newNode);
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
		Optional<DiagramElement> tool = aToolBar.getCreationPrototype();
		DiagramElement selected = getSelectedElement(pEvent);

		if( tool.isPresent() && tool.get() instanceof Node)
		{
			if(selected != null && selected instanceof Node)
			{
				if(!(tool.get() instanceof ChildNode && selected instanceof ParentNode))
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
		assert aToolBar.getCreationPrototype().isPresent();
		Edge newEdge = (Edge) ((Edge) aToolBar.getCreationPrototype().get()).clone();
		if(pMousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD )
		{
			if( aCanvas.getDiagram().addEdge(newEdge, aMouseDownPoint, pMousePoint) )
			{
				setModified(true);
				aSelectionModel.set(newEdge);
			}
		}
		aSelectionModel.deactivateRubberband();
	}
	
	private void releaseMove()
	{
		// For optimization purposes, some of the layouts are not done on every move event.
		aCanvas.getDiagram().requestLayout();
		setModified(true);
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
			if( !pEvent.isControlDown() )
			{
				aSelectionModel.clearSelection();
			}
			aSelectionModel.activateLasso(computeLasso(), aCanvas.getDiagram());
		}
		else if(aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			aLastMousePoint = getMousePoint(pEvent);
			aSelectionModel.activateRubberband(computeRubberband());
		}
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
	
	/**
	 * @return A graph modification listener.
	 */
	public GraphModificationListener createGraphModificationListener()
	{
		return new PanelGraphModificationListener();
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
		public void nodeAdded(Diagram pGraph, Node pNode)
		{
			aUndoManager.add(new AddNodeCommand(pGraph, pNode));
		}
		
		@Override
		public void nodeRemoved(Diagram pGraph, Node pNode)
		{
			aUndoManager.add(new DeleteNodeCommand(pGraph, pNode));
		}
		
		@Override
		public void edgeAdded(Diagram pGraph, Edge pEdge)
		{
			aUndoManager.add(new AddEdgeCommand(pGraph, pEdge));
		}
		
		@Override
		public void edgeRemoved(Diagram pGraph, Edge pEdge)
		{
			aUndoManager.add(new RemoveEdgeCommand(pGraph, pEdge));
		}

		@Override
		public void propertyChanged(Property pProperty, Object pOldValue)
		{
			aUndoManager.add(new ChangePropertyCommand(pProperty, pOldValue, pProperty.get()));
		}
	}
}