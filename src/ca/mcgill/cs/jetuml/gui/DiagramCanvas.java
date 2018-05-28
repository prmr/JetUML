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
import java.util.Set;
import java.util.prefs.Preferences;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.Clipboard;
import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;

/**
 * A canvas on which to view, create, and modify diagrams.
 */
public class DiagramCanvas extends Canvas implements SelectionObserver
{	
	private static final double SIZE_RATIO = 0.65;
	
	private Diagram aDiagram;
	private boolean aShowGrid;
	private boolean aModified;
	private DiagramCanvasController aController;
	
	/**
	 * Constructs the canvas, assigns the diagram to it, and registers
	 * the canvas as a listener for the diagram.
	 * 
	 * @param pDiagram the graph managed by this panel.
	 * @param pScreenBoundaries the boundaries of the user's screen. 
	 */
	public DiagramCanvas(Diagram pDiagram, Rectangle2D pScreenBoundaries)
	{
		super(pScreenBoundaries.getWidth()*SIZE_RATIO, pScreenBoundaries.getHeight()*SIZE_RATIO);
		aDiagram = pDiagram;
		aShowGrid = Boolean.valueOf(Preferences.userNodeForPackage(UMLEditor.class).get("showGrid", "true"));
	}
	
	/**
	 * Should only be called once immediately after the constructor call.
	 * 
	 * @param pController The controller for this canvas.
	 */
	public void setController(DiagramCanvasController pController)
	{
		aController = pController;
		aDiagram.setGraphModificationListener(pController.createGraphModificationListener());
	}
	
	public DiagramCanvasController getController()
	{
		return aController;
	}
	
	@Override
	public boolean isResizable()
	{
	    return false;
	}
	
	/*
     * Gets the ScrollPane containing this panel.
     * Will return null if not yet contained in a ScrollPane.
     * @return the scroll pane
	 */
	private ScrollPane getScrollPane()
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
	 * Pastes the content of the clip board into the graph managed by this panel.
	 */
	public void paste()
	{
		aController.getSelectionModel().resetSelection(Clipboard.instance().paste(this));
	}
	
	/**
	 * Indicate to the DiagramCanvas that is should 
	 * consider all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void startCompoundGraphOperation()
	{
		aController.getUndoManager().startTracking();
	}
	
	/**
	 * Indicate to the DiagramCanvas that is should 
	 * stop considering all following operations on the graph
	 * to be part of a single conceptual one.
	 */
	public void finishCompoundGraphOperation()
	{
		aController.getUndoManager().endTracking();
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

	@Override
	public void selectionModelChanged()
	{
		paintPanel();		
	}
}
