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

import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;

import ca.mcgill.cs.jetuml.application.Clipboard2;
import ca.mcgill.cs.jetuml.application.GraphModificationListener2;
import ca.mcgill.cs.jetuml.application.MoveTracker2;
import ca.mcgill.cs.jetuml.application.PropertyChangeTracker;
import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.application.UndoManager;
import ca.mcgill.cs.jetuml.commands.AddEdgeCommand2;
import ca.mcgill.cs.jetuml.commands.AddNodeCommand2;
import ca.mcgill.cs.jetuml.commands.ChangePropertyCommand;
import ca.mcgill.cs.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.jetuml.commands.DeleteNodeCommand2;
import ca.mcgill.cs.jetuml.commands.RemoveEdgeCommand2;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.geom.Zoom;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.Property;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.graph.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.graph.nodes.PackageNode;
import ca.mcgill.cs.jetuml.graph.nodes.ParentNode;
import ca.mcgill.cs.jetuml.views.Grid2;
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A panel to draw a graph.
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
public class GraphPanel2 extends Canvas
{
	private enum DragMode 
	{ DRAG_NONE, DRAG_MOVE, DRAG_RUBBERBAND, DRAG_LASSO }
	
	private static final int CONNECT_THRESHOLD = 8;
	private static final int LAYOUT_PADDING = 20;
	private static final Color GRABBER_COLOR = Color.rgb(77, 115, 153);
	private static final Color GRABBER_FILL_COLOR = Color.rgb(173, 193, 214);
	private static final Color GRABBER_FILL_COLOR_TRANSPARENT = Color.rgb(173, 193, 214, 0.75);
	private static final int CANVAS_SCREEN_FACTOR = 2;	// factor to compute maximum canvas size
	
	private GraphicsContext aGraphics;
	private Graph2 aGraph;
	private ToolBar2 aSideBar;
	private Zoom aZoom = new Zoom();	
	private boolean aHideGrid;
	private boolean aModified;
	private SelectionList aSelectedElements = new SelectionList();
	private Point aLastMousePoint;
	private Point aMouseDownPoint;   
	private DragMode aDragMode;
	private UndoManager aUndoManager = new UndoManager();
	private final MoveTracker2 aMoveTracker = new MoveTracker2();
	
	/**
	 * Constructs the panel, assigns the graph to it, and registers
	 * the panel as a listener for the graph.
	 * 
	 * @param pGraph the graph managed by this panel.
	 * @param pSideBar the ToolBar which contains all of the tools for nodes and edges.
	 * @param pScreenBoundaries the boundaries of the users screen. 
	 */
	public GraphPanel2(Graph2 pGraph, ToolBar2 pSideBar, Rectangle2D pScreenBoundaries)
	{
		super(pScreenBoundaries.getWidth() * CANVAS_SCREEN_FACTOR, pScreenBoundaries.getHeight() * CANVAS_SCREEN_FACTOR);
		aGraphics = getGraphicsContext2D();
		aGraph = pGraph;
		aGraph.setGraphModificationListener(new PanelGraphModificationListener());
		aSideBar = pSideBar;

		GraphPanelMouseListener listener = new GraphPanelMouseListener();
		setOnMousePressed(listener);
		setOnMouseReleased(listener);
		setOnMouseDragged(listener);
	}
	
	@Override
	public double minWidth(double pWidth)
	{
		return 0;
	}
	
	@Override
	public double minHeight(double pHeight)
	{
		return 0;
	}
	
	@Override
	public double prefWidth(double pWidth)
	{
		if (getParent() != null)
		{
			Rectangle bounds = aGraph.getBounds();
			return Math.max(getScrollPane().getWidth()-2, aZoom.zoom(bounds.getMaxX()));
		}
		return pWidth;
	}
	
	@Override
	public double prefHeight(double pHeight)
	{
		if (getParent() != null)
		{
			Rectangle bounds = aGraph.getBounds();
			return Math.max(getScrollPane().getHeight()-2, aZoom.zoom(bounds.getMaxY()));
		}
		return pHeight;
	}
	
	@Override
	public double maxWidth(double pWidth)
	{
		return Double.MAX_VALUE;
	}
	
	@Override
	public double maxHeight(double pHeight)
	{
		return Double.MAX_VALUE;
	}

	@Override
	public boolean isResizable()
	{
	    return true;
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
		if (aSelectedElements.size() > 0)
		{
			Clipboard2.instance().copy(aSelectedElements);
		}
	}
	
	/**
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		aSelectedElements = Clipboard2.instance().paste(this);
	}
	
	/**
	 * Copy the currently selected elements to the clip board and removes them
	 * from the graph managed by this panel.
	 */
	public void cut()
	{
		if (aSelectedElements.size() > 0)
		{
			Clipboard2.instance().cut(this);
		}
	}
	
	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		GraphElement edited = aSelectedElements.getLastSelected();
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
				aGraph.requestLayout();
				paintPanel();
			}
		});
		if (sheet.isEmpty())
		{
			return;
		}

		Stage window = new Stage();
		window.setTitle(ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("dialog.properties"));
		window.getIcons().add(new Image(ResourceBundle.getBundle("ca.mcgill.cs.jetuml.UMLEditorStrings").getString("app.icon")));
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
		for (GraphElement element : aSelectedElements)
		{
			if (element instanceof Node)
			{
				aGraph.removeAllEdgesConnectedTo((Node)element);
				nodes.add((Node) element);
			}
			else if (element instanceof Edge)
			{
				aGraph.removeEdge((Edge) element);
			}
		}
		while(!nodes.empty())
		{
			aGraph.removeNode(nodes.pop());
		}
		aUndoManager.endTracking();
		if (aSelectedElements.size() > 0)
		{
			setModified(true);
		}
		paintPanel();
	}
	
	/**
	 * Indicate to the GraphPanel that is should 
	 * consider all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void startCompoundGraphOperation()
	{
		aUndoManager.startTracking();
	}
	
	/**
	 * Indicate to the GraphPanel that is should 
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
		aGraph.requestLayout();
	}
	
	/**
	 * @return the graph in this panel.
	 */
	public Graph2 getGraph()
	{
		return aGraph;
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
		aSelectedElements.clearSelection();
		for (Node node : aGraph.getRootNodes())
		{
			aSelectedElements.add(node);
		}
		for (Edge edge : aGraph.getEdges())
		{
			aSelectedElements.add(edge);
		}
		aSideBar.setToolToBeSelect();
		paintPanel();
	}

	/**
	 * Paints the panel and all the graph elements in aGraph.
	 * Called after the panel is resized.
	 */
	public void paintPanel()
	{
		aGraphics.setFill(Color.WHITE); 
		aGraphics.fillRect(0, 0, getWidth(), getHeight());
		aGraphics.setTransform(aZoom.factor(), 0, 0, aZoom.factor(), 0, 0);
		Bounds bounds = getBoundsInLocal();
		Rectangle graphBounds = aGraph.getBounds();
		if (!aHideGrid) 
		{
			Grid2.draw(aGraphics, new Rectangle(0, 0, Math.max(aZoom.dezoom((int) Math.round(bounds.getMaxX())), graphBounds.getMaxX()),
					Math.max(aZoom.dezoom((int) Math.round(bounds.getMaxY())), graphBounds.getMaxY())));
		}
		aGraph.draw(aGraphics);

		Set<GraphElement> toBeRemoved = new HashSet<>();
		for (GraphElement selected : aSelectedElements)
		{
			if (!aGraph.contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
			else if (selected instanceof Node)
			{
				Rectangle grabberBounds = ((Node) selected).view2().getBounds();
				drawGrabber(aGraphics, grabberBounds.getX(), grabberBounds.getY());
				drawGrabber(aGraphics, grabberBounds.getX(), grabberBounds.getMaxY());
				drawGrabber(aGraphics, grabberBounds.getMaxX(), grabberBounds.getY());
				drawGrabber(aGraphics, grabberBounds.getMaxX(), grabberBounds.getMaxY());
			}
			else if (selected instanceof Edge)
			{
				Line line = ((Edge) selected).view2().getConnectionPoints();
				drawGrabber(aGraphics, line.getX1(), line.getY1());
				drawGrabber(aGraphics, line.getX2(), line.getY2());
			}
		}

		for (GraphElement element : toBeRemoved)
		{
			aSelectedElements.remove(element);
		}                 
      
		if (aDragMode == DragMode.DRAG_RUBBERBAND)
		{
			Paint oldFill = aGraphics.getFill();
			aGraphics.setFill(GRABBER_COLOR);
			aGraphics.strokeLine(aMouseDownPoint.getX(), aMouseDownPoint.getY(), aLastMousePoint.getX(), aLastMousePoint.getY());
			aGraphics.setFill(oldFill);
		}      
		else if (aDragMode == DragMode.DRAG_LASSO)
		{
			Paint oldFill = aGraphics.getFill();
			Paint oldStroke = aGraphics.getStroke();
			aGraphics.setFill(GRABBER_FILL_COLOR_TRANSPARENT);
			aGraphics.setStroke(GRABBER_COLOR);
			double x1 = aMouseDownPoint.getX();
			double y1 = aMouseDownPoint.getY();
			double x2 = aLastMousePoint.getX();
			double y2 = aLastMousePoint.getY();
			Rectangle2D lasso = new Rectangle2D(Math.min(x1, x2), Math.min(y1, y2),
					Math.abs(x1 - x2) , Math.abs(y1 - y2));
			aGraphics.fillRect(lasso.getMinX(), lasso.getMinY(), lasso.getWidth(), lasso.getHeight());
			aGraphics.strokeRect(lasso.getMinX(), lasso.getMinY(), lasso.getWidth(), lasso.getHeight());
			aGraphics.setFill(oldFill);
			aGraphics.setStroke(oldStroke);
		}      
		if (getScrollPane() != null)
		{
			getScrollPane().requestLayout();
		}
	}

	/**
	 * Draws a single "grabber", a filled square.
	 * @param pGraphics the graphics context
	 * @param pX the x coordinate of the center of the grabber
	 * @param pY the y coordinate of the center of the grabber
	 */
	public static void drawGrabber(GraphicsContext pGraphics, double pX, double pY)
	{
		final int size = 6;
		Paint oldStroke = pGraphics.getStroke();
		Paint oldFill = pGraphics.getFill();
		pGraphics.setStroke(GRABBER_COLOR);
		pGraphics.strokeRect((int)(pX - size / 2), (int)(pY - size / 2), size, size);
		pGraphics.setFill(GRABBER_FILL_COLOR);
		pGraphics.fillRect((int)(pX - size / 2), (int)(pY - size / 2), size, size);
		pGraphics.setStroke(oldStroke);
		pGraphics.setFill(oldFill);
	}
	
	/**
	 * Increase the zoom level of this panel
	 * by one step.
	 */
	public void zoomIn()
	{
		aZoom.increaseLevel();
		paintPanel();
	}
	
	/**
	 * Decrease the zoom level of this panel 
	 * by one step.
	 */
	public void zoomOut()
	{
		aZoom.decreaseLevel();
		paintPanel();
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
		Optional<GraphFrame2> graphFrame = getFrame();
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
	private Optional<GraphFrame2> getFrame()
	{
		Parent parent = getScrollPane();
		while (!(parent instanceof TabPane))
		{
			parent = parent.getParent();
		}
		for (Tab tab : ((TabPane) parent).getTabs())
		{
			if (tab instanceof GraphFrame2 && tab.getContent() == getScrollPane().getParent())
			{
				return Optional.of((GraphFrame2) tab);
			}
		}
		return Optional.empty();
	}
   
	/**
	 * Sets the value of the hideGrid property.
	 * @param pHideGrid true if the grid is being hidden
	 */
	public void setHideGrid(boolean pHideGrid)
	{
		aHideGrid = pHideGrid;
		paintPanel();
	}

	/**
	 * Gets the value of the hideGrid property.
	 * @return true if the grid is being hidden
	 */
	public boolean getHideGrid()
	{
		return aHideGrid;
	}
	
	/**
	 * @return the currently SelectedElements from the GraphPanel.
	 */
	public SelectionList getSelectionList()
	{
		return aSelectedElements;
	}
	
	/**
	 * @param pSelectionList the new SelectedElements for the GraphPanel.
	 */
	public void setSelectionList(SelectionList pSelectionList)
	{
		aSelectedElements = pSelectionList;
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
	
	private class GraphPanelMouseListener implements EventHandler<MouseEvent>
	{	
		/**
		 * Also adds the inner edges of parent nodes to the selection list.
		 * @param pElement
		 */
		private void setSelection(GraphElement pElement)
		{
			aSelectedElements.set(pElement);
			for (Edge edge : aGraph.getEdges())
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
		private void addToSelection(GraphElement pElement)
		{
			aSelectedElements.add(pElement);
			for (Edge edge : aGraph.getEdges())
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
			return new Point(aZoom.dezoom((int)pEvent.getX()), aZoom.dezoom((int)pEvent.getY()));
		}
		
		/*
		 * Will return null if nothing is selected.
		 */
		private GraphElement getSelectedElement(MouseEvent pEvent)
		{
			Point mousePoint = getMousePoint(pEvent);
			GraphElement element = aGraph.findEdge(mousePoint);
			if (element == null)
			{
				element = aGraph.findNode(new Point(mousePoint.getX(), mousePoint.getY())); 
			}
			return element;
		}
		
		private void handleSelection(MouseEvent pEvent)
		{
			GraphElement element = getSelectedElement(pEvent);
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
			GraphElement element = getSelectedElement(pEvent);
			if (element != null)
			{
				setSelection(element);
				editSelected();
			}
			else
			{
				Point point = getMousePoint(pEvent);
				final Point mousePoint = new Point(point.getX(), point.getY()); 
				aSideBar.showPopup(GraphPanel2.this, mousePoint);
			}
		}
		
		private void handleNodeCreation(MouseEvent pEvent)
		{
			Node newNode = ((Node)aSideBar.getSelectedTool()).clone();
			Point point = getMousePoint(pEvent);
			boolean added = aGraph.addNode(newNode, new Point(point.getX(), point.getY())); 
			if (added)
			{
				setModified(true);
				setSelection(newNode);
			}
			else // Special behavior, if we can't add a node, we select any element at the point
			{
				handleSelection(pEvent);
			}
		}
		
		private void handleEdgeStart(MouseEvent pEvent)
		{
			GraphElement element = getSelectedElement(pEvent);
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
		private GraphElement getTool(MouseEvent pEvent)
		{
			GraphElement tool = aSideBar.getSelectedTool();
			GraphElement selected = getSelectedElement(pEvent);
			
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
			GraphElement tool = getTool(pEvent);
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
			paintPanel();
		}

		public void mouseReleased(MouseEvent pEvent)
		{
			Point mousePoint = new Point(aZoom.dezoom((int)pEvent.getX()), aZoom.dezoom((int)pEvent.getY()));
			Object tool = aSideBar.getSelectedTool();
			if (aDragMode == DragMode.DRAG_RUBBERBAND)
			{
				Edge prototype = (Edge) tool;
				Edge newEdge = (Edge) prototype.clone();
				if (mousePoint.distance(aMouseDownPoint) > CONNECT_THRESHOLD && aGraph.addEdge(newEdge, aMouseDownPoint, mousePoint))
				{
					setModified(true);
					setSelection(newEdge);
				}
			}
			else if (aDragMode == DragMode.DRAG_MOVE)
			{
				aGraph.requestLayout();
				setModified(true);
				CompoundCommand command = aMoveTracker.endTrackingMove(aGraph);
				if (command.size() > 0)
				{
					aUndoManager.add(command);
				}
			}
			aDragMode = DragMode.DRAG_NONE;
			paintPanel();
		}
		
		public void mouseDragged(MouseEvent pEvent)
		{
			Point mousePoint = new Point(aZoom.dezoom((int)pEvent.getX()), aZoom.dezoom((int)pEvent.getY()));
			boolean isCtrl = pEvent.isControlDown();

			if (aDragMode == DragMode.DRAG_MOVE && aSelectedElements.getLastNode()!=null)
			{               
				Node lastNode = aSelectedElements.getLastNode();
				Rectangle bounds = lastNode.view().getBounds();
				int dx = (int)(mousePoint.getX() - aLastMousePoint.getX());
				int dy = (int)(mousePoint.getY() - aLastMousePoint.getY());
                   
				// we don't want to drag nodes into negative coordinates
				// particularly with multiple selection, we might never be 
				// able to get them back.
				for (GraphElement selected : aSelectedElements)
				{
					if (selected instanceof Node)
					{
						Node n = (Node) selected;
						bounds = bounds.add(n.view().getBounds());
					}
				}
				dx = Math.max(dx, -bounds.getX());
				dy = Math.max(dy, -bounds.getY());
            
				for (GraphElement selected : aSelectedElements)
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
			else if (aDragMode == DragMode.DRAG_LASSO)
			{
				double x1 = aMouseDownPoint.getX();
				double y1 = aMouseDownPoint.getY();
				double x2 = mousePoint.getX();
				double y2 = mousePoint.getY();
				Rectangle lasso = new Rectangle((int)Math.min(x1, x2), (int)Math.min(y1, y2), (int)Math.abs(x1 - x2) , (int)Math.abs(y1 - y2));
				for (Node node : aGraph.getRootNodes())
				{
					selectNode(isCtrl, node, lasso);
				}
				//Edges need to be added too when highlighted, but only if both their endpoints have been highlighted.
				for (Edge edge: aGraph.getEdges())
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
			paintPanel();
		}
		
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

		@Override
		public void handle(MouseEvent pEvent) 
		{
			if (pEvent.getEventType() == MouseEvent.MOUSE_PRESSED) 
			{
				mousePressed(pEvent);
			} 
			else if (pEvent.getEventType() == MouseEvent.MOUSE_RELEASED) 
			{
				mouseReleased(pEvent);
			}
			else if (pEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) 
			{
				mouseDragged(pEvent);
			}
			
		}
	}
	
	private class PanelGraphModificationListener implements GraphModificationListener2
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
		public void nodeAdded(Graph2 pGraph, Node pNode)
		{
			aUndoManager.add(new AddNodeCommand2(pGraph, pNode));
		}
		
		@Override
		public void nodeRemoved(Graph2 pGraph, Node pNode)
		{
			aUndoManager.add(new DeleteNodeCommand2(pGraph, pNode));
		}
		
		@Override
		public void edgeAdded(Graph2 pGraph, Edge pEdge)
		{
			aUndoManager.add(new AddEdgeCommand2(pGraph, pEdge));
		}
		
		@Override
		public void edgeRemoved(Graph2 pGraph, Edge pEdge)
		{
			aUndoManager.add(new RemoveEdgeCommand2(pGraph, pEdge));
		}

		@Override
		public void propertyChanged(Property pProperty, Object pOldValue)
		{
			aUndoManager.add(new ChangePropertyCommand(pProperty, pOldValue, pProperty.get()));
		}
	}
}
