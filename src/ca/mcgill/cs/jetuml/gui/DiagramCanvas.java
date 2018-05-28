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
import java.util.prefs.Preferences;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.Clipboard;
import ca.mcgill.cs.jetuml.application.GraphModificationListener;
import ca.mcgill.cs.jetuml.application.PropertyChangeTracker;
import ca.mcgill.cs.jetuml.application.SelectionList;
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
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A canvas on which to view, create, and modify diagrams.
 */
public class DiagramCanvas extends Canvas implements SelectionObserver
{	
	private static final int LAYOUT_PADDING = 20;
	private static final double SIZE_RATIO = 0.65;
	
	private Diagram aDiagram;
	private DiagramTabToolBar aSideBar;
	private boolean aShowGrid;
	private boolean aModified;
	private final DiagramCanvasController aController;
	private UndoManager aUndoManager = new UndoManager();
	
	/**
	 * Constructs the canvas, assigns the diagram to it, and registers
	 * the canvas as a listener for the diagram.
	 * 
	 * @param pDiagram the graph managed by this panel.
	 * @param pSideBar the DiagramTabToolBar which contains all of the tools for nodes and edges.
	 * @param pScreenBoundaries the boundaries of the user's screen. 
	 */
	public DiagramCanvas(Diagram pDiagram, DiagramTabToolBar pSideBar, Rectangle2D pScreenBoundaries)
	{
		super(pScreenBoundaries.getWidth()*SIZE_RATIO, pScreenBoundaries.getHeight()*SIZE_RATIO);
		aDiagram = pDiagram;
		aDiagram.setGraphModificationListener(new PanelGraphModificationListener());
		aSideBar = pSideBar;

		aController = new DiagramCanvasController(aDiagram, this, aUndoManager);
		setOnMousePressed(aController.mousePressedHandler());
		setOnMouseReleased(aController.mouseReleasedHandler());
		setOnMouseDragged(aController.mouseDraggedHandler());
		aShowGrid = Boolean.valueOf(Preferences.userNodeForPackage(UMLEditor.class).get("showGrid", "true"));
	}
	
	/**
	 * @return The currently selected tool.
	 */
	public Optional<DiagramElement> getCreationPrototype()
	{
		return aSideBar.getCreationPrototype();
	}
	
	/**
	 * Toggles the selection tool.
	 */
	public void setToolToSelect()
	{
		aSideBar.setToolToBeSelect();
	}
	
	@Override
	public boolean isResizable()
	{
	    return false;
	}
	
	/**
	 * Shows the tool bar pop up menu at the specified screen location.
	 * 
	 * @param pX The X-coordinate of the popup.
	 * @param pY The Y-coordinate of the popup.
	 */
	public void showPopup(double pX, double pY)
	{
		aSideBar.showPopup(pX,  pY);
	}
	
	/**
     * Gets the ScrollPane containing this panel.
     * Will return null if not yet contained in a ScrollPane.
     * @return the scroll pane
	 */
	public ScrollPane getScrollPane()
	{
		if (getParent() != null) 
		{
			Parent parent = getParent();
			while (!(parent instanceof ScrollPane))
			{
				parent = parent.getParent();
			}
			return (ScrollPane) parent;
		}
		return null;
	}
	
	/**
	 * Copy the currently selected elements to the clip board.
	 */
	public void copy()
	{
		if (aController.getSelectionModel().getSelectionList().size() > 0)
		{
			Clipboard.instance().copy(aController.getSelectionModel().getSelectionList());
		}
	}
	
	/**
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		aController.getSelectionModel().resetSelection(Clipboard.instance().paste(this));
	}
	
	/**
	 * Copy the currently selected elements to the clip board and removes them
	 * from the graph managed by this panel.
	 */
	public void cut()
	{
		if (aController.getSelectionModel().getSelectionList().size() > 0)
		{
			Clipboard.instance().cut(this);
		}
	}
	
	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		DiagramElement edited = aController.getSelectionModel().getSelectionList().getLastSelected();
		if (edited == null)
		{
			return;
		}
		PropertyChangeTracker tracker = new PropertyChangeTracker(edited);
		tracker.startTracking();
		PropertySheet sheet = new PropertySheet(edited, new PropertySheet.PropertyChangeListener()
		{
			@Override
			public void propertyChanged()
			{
				aDiagram.requestLayout();
				paintPanel();
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
		window.initOwner(getScene().getWindow());
		window.show();
		
		CompoundCommand command = tracker.stopTracking();
		if (command.size() > 0)
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
		Stack<Node> nodes = new Stack<>();
		for (DiagramElement element : aController.getSelectionModel().getSelectionList())
		{
			if (element instanceof Node)
			{
				aDiagram.removeAllEdgesConnectedTo((Node)element);
				nodes.add((Node) element);
			}
			else if (element instanceof Edge)
			{
				aDiagram.removeEdge((Edge) element);
			}
		}
		while(!nodes.empty())
		{
			aDiagram.removeNode(nodes.pop());
		}
		aUndoManager.endTracking();
		if (aController.getSelectionModel().getSelectionList().size() > 0)
		{
			setModified(true);
		}
		paintPanel();
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
	 * Resets the layout of the graph if there was a change made.
	 */
	public void layoutGraph()
	{
		aDiagram.requestLayout();
	}
	
	/**
	 * @return the graph in this panel.
	 */
	public Diagram getDiagram()
	{
		return aDiagram;
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
		paintPanel();
	}
	
	/**
	 * Removes the last undone action and performs it.
	 * If the UndoManager performs a command, the method 
	 * it calls will repaint on its own
	 */
	public void redo()
	{
		aUndoManager.redoCommand();
		paintPanel();
	}
	
	/**
	 * Clears the selection list and adds all the root nodes and edges to 
	 * it. Makes the selection tool the active tool.
	 */
	public void selectAll()
	{
		aController.getSelectionModel().getSelectionList().clearSelection();
		for (Node node : aDiagram.getRootNodes())
		{
			aController.getSelectionModel().getSelectionList().add(node);
		}
		for (Edge edge : aDiagram.getEdges())
		{
			aController.getSelectionModel().getSelectionList().add(edge);
		}
		aSideBar.setToolToBeSelect();
		paintPanel();
	}

	/**
	 * Paints the panel and all the graph elements in aDiagram.
	 * Called after the panel is resized.
	 */
	public void paintPanel()
	{
		GraphicsContext context = getGraphicsContext2D();
		context.setFill(Color.WHITE); 
		context.fillRect(0, 0, getWidth(), getHeight());
		Bounds bounds = getBoundsInLocal();
		Rectangle graphBounds = aDiagram.getBounds();
		if(aShowGrid) 
		{
			Grid.draw(context, new Rectangle(0, 0, Math.max((int) Math.round(bounds.getMaxX()), graphBounds.getMaxX()),
					Math.max((int) Math.round(bounds.getMaxY()), graphBounds.getMaxY())));
		}
		aDiagram.draw(context);

		Set<DiagramElement> toBeRemoved = new HashSet<>();
		for (DiagramElement selected : aController.getSelectionModel().getSelectionList())
		{
			if(!aDiagram.contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
			else
			{
				selected.view().drawSelectionHandles(context);
			}
		}

		for (DiagramElement element : toBeRemoved)
		{
			aController.getSelectionModel().getSelectionList().remove(element);
		}                 
      
		Optional<Line> rubberband = aController.getSelectionModel().getRubberband();
		if( rubberband.isPresent() )
		{
			ToolGraphics.drawRubberband(context, rubberband.get());
		}
		
		Optional<Rectangle> lasso = aController.getSelectionModel().getLasso();
		if( lasso.isPresent() )
		{
			ToolGraphics.drawLasso(context, lasso.get());
		}
		
		if (getScrollPane() != null)
		{
			getScrollPane().requestLayout();
		}
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
		Optional<DiagramTab> graphFrame = getFrame();
		if (graphFrame.isPresent())
		{
			graphFrame.get().setTitle(aModified);
		}
	}
	
	/** 
	 * Obtains the parent frame of this panel through the component hierarchy.
	 * 
	 * getFrame().isPresent() will be false if panel not yet added to its parent 
	 * frame, for example if it is called in the constructor of this panel.
	 */
	private Optional<DiagramTab> getFrame()
	{
		try 
		{
			Parent parent = getScrollPane();
			while (!(parent instanceof TabPane))
			{
				parent = parent.getParent();
			}
			for (Tab tab : ((TabPane) parent).getTabs())
			{
				if (tab instanceof DiagramTab && tab.getContent() == getScrollPane().getParent())
				{
					return Optional.of((DiagramTab) tab);
				}
			}
		}
		catch (NullPointerException e) {}
		return Optional.empty();
	}
   
	/**
	 * Sets the value of the hideGrid property.
	 * @param pShowGrid true if the grid is being shown
	 */
	public void setShowGrid(boolean pShowGrid)
	{
		aShowGrid = pShowGrid;
		paintPanel();
	}

	/**
	 * @return the currently SelectedElements from the DiagramCanvas.
	 */
	public SelectionList getSelectionList()
	{
		return aController.getSelectionModel().getSelectionList();
	}
	
	/**
	 * @param pSelectionList the new SelectedElements for the DiagramCanvas.
	 */
	public void setSelectionList(SelectionList pSelectionList)
	{
		aController.getSelectionModel().resetSelection(pSelectionList);
	}
	
	/**
	 * @param pNode the currently selected Node
	 * @return whether or not there is a problem with switching to the selection tool.
	 */
	public boolean switchToSelectException(Node pNode)
	{
		if (pNode instanceof PackageNode || pNode instanceof ImplicitParameterNode || pNode instanceof ObjectNode)
		{
			return true;
		}
		return false;
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

	@Override
	public void selectionModelChanged()
	{
		paintPanel();		
	}
}
