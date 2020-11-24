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

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.File;
import java.util.Optional;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.geom.Point;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * A tab holding a single diagram.
 */
public class DiagramTab extends Tab implements MouseDraggedGestureHandler, KeyEventHandler
{
	private static final double DEFAULT_SCALE = 1.0;
	private static final double SCALE_MULTIPLIER = 1.25;
	private static final double ZOOM_MIN = DEFAULT_SCALE / (SCALE_MULTIPLIER * SCALE_MULTIPLIER);
	private static final double ZOOM_MAX = DEFAULT_SCALE * SCALE_MULTIPLIER * SCALE_MULTIPLIER;
	
	private final DoubleProperty aZoom;
	private final Diagram aDiagram;
	private DiagramCanvas aDiagramCanvas;
	private final DiagramCanvasController aDiagramCanvasController;
	private Optional<File> aFile = Optional.empty(); // The file associated with this diagram
	
	/**
     * Constructs a diagram tab initialized with pDiagram.
     * @param pDiagram The initial diagram
	 */
	public DiagramTab(Diagram pDiagram)
	{
		aDiagram = pDiagram;
		DiagramTabToolBar sideBar = new DiagramTabToolBar(pDiagram);
		UserPreferences.instance().addBooleanPreferenceChangeHandler(sideBar);
		aDiagramCanvas = new DiagramCanvas(pDiagram);
		UserPreferences.instance().addBooleanPreferenceChangeHandler(aDiagramCanvas);
		aDiagramCanvasController = new DiagramCanvasController(aDiagramCanvas, sideBar, this);
		aDiagramCanvas.setController(aDiagramCanvasController);
		aDiagramCanvas.paintPanel();
		
		BorderPane layout = new BorderPane();
		layout.setRight(sideBar);

		// We put the diagram in a fixed-size StackPane for the sole purpose of being able to
		// decorate it with CSS. The StackPane needs to have a fixed size so the border fits the 
		// canvas and not the parent container.
		StackPane pane = new StackPane(aDiagramCanvas);
		final int buffer = 12; // (border insets + border width + 1)*2
		pane.setMaxSize(aDiagramCanvas.getWidth() + buffer, aDiagramCanvas.getHeight() + buffer);
		final String cssDefault = "-fx-border-color: grey; -fx-border-insets: 4;"
				+ "-fx-border-width: 1; -fx-border-style: solid;";
		pane.setStyle(cssDefault);
		
		aZoom = new SimpleDoubleProperty(DEFAULT_SCALE);
		pane.scaleXProperty().bind(aZoom);
		pane.scaleYProperty().bind(aZoom);
		
		// First, wrap the StackPane in a Group to allow the scrolling to be based around the visual bounds
		// of the canvas rather than its layout bounds.
		// Then we wrap the Group within an additional, resizable StackPane that can grow to fit the parent
		// ScrollPane and thus center the decorated canvas.
		ScrollPane scroll = new ScrollPane(new StackPane(new Group(pane)));
		
		// The call below is necessary to removes the focus highlight around the Canvas
		// See issue #250
		scroll.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"); 

		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		layout.setCenter(scroll);
		
		setTitle();
		setContent(layout);

		setOnCloseRequest(pEvent -> 
		{
			pEvent.consume();
			EditorFrame editorFrame = (EditorFrame) getTabPane().getParent();
			editorFrame.close(this);
		});
	}
	
	/* retrieves the toolbar from the component graph */
	private DiagramTabToolBar toolBar()
	{
		return (DiagramTabToolBar)((BorderPane)getContent()).getRight();
	}
	
	/**
	 * This method should be called immediately before closing the tab.
	 */
	public void close()
	{
		UserPreferences.instance().removeBooleanPreferenceChangeHandler(aDiagramCanvas);
		UserPreferences.instance().removeBooleanPreferenceChangeHandler((DiagramTabToolBar)((BorderPane)getContent()).getRight());
	}

	/**
     * @return The diagram being edited within this tab.
	 */
	public Diagram getDiagram()
	{
		return aDiagram;
	}
	
	/**
	 * Copy the current selection to the clipboard.
	 */
	public void copy()
	{
		aDiagramCanvasController.copy();
	}
	
	/**
	 * Cuts the current selection to the clip board.
	 */
	public void cut()
	{
		aDiagramCanvasController.cut();
	}
	
	/**
	 * Pastes the current clip board content to the diagram.
	 */
	public void paste()
	{
		aDiagramCanvasController.paste();
	}
	
	/**
	 * Open a dialog to edit the properties of the currently selected element.
	 */
	public void editSelected()
	{
		aDiagramCanvasController.editSelected();
	}
	
	/**
	 * Undoes the last command.
	 */
	public void undo()
	{
		aDiagramCanvasController.undo();
	}
	
	/**
	 * Redoes the last undone command.
	 */
	public void redo()
	{
		aDiagramCanvasController.redo();
	}
	
	/**
	 * Copy the current selection to the clipboard.
	 */
	public void removeSelected()
	{
		aDiagramCanvasController.removeSelected();
	}
	
	/**
	 * Selects all elements in the diagram.
	 */
	public void selectAll()
	{
		aDiagramCanvasController.selectAll();
	}
	
	/**
	 * Zooms in the diagram.
	 */
	public void zoomIn()
	{
		aZoom.set(min(aZoom.get() * SCALE_MULTIPLIER, ZOOM_MAX));
	}
	
	/**
	 * Zooms out the diagram.
	 */
	public void zoomOut()
	{
		aZoom.set(max(aZoom.get() / SCALE_MULTIPLIER, ZOOM_MIN));
	}
	
	/**
	 * Resets the diagram's zoom to its default value.
	 */
	public void resetZoom()
	{
		aZoom.set(DEFAULT_SCALE);
	}
	
	/**
	 * Sets the title of the frame as the file name if there
	 * is a file name. 
	 * 
	 */
	public void setTitle()
	{
		if(aFile.isPresent())
		{
			String title = aFile.get().getName();
			setText(title); 
		}
		else
		{
			setText(RESOURCES.getString(getDiagram().getType().getName().toLowerCase() + ".text"));
		}
	}
	
	/**
	 * Notify the tab that its diagram has been saved.
	 */
	public void diagramSaved()
	{
		aDiagramCanvasController.diagramSaved();
	}
	
	/**
	 * @return True if the diagram in this tab
	 *     has unsaved changes.
	 */
	public boolean hasUnsavedChanges()
	{
		return aDiagramCanvasController.hasUnsavedChanges();
	}

	/**
     * Gets the file property.
     * @return the file associated with this diagram, if available.
	 */
	public Optional<File> getFile()
	{
		return aFile;
	}

	/**
     * Sets the file property.
     * @param pFile The file associated with this graph
	 */
	public void setFile(File pFile)
	{
		assert pFile != null;
		aFile = Optional.of(pFile);
		setTitle();
	}

	@Override
	public void interactionTo(Point pTo)
	{
		ViewportProjection projection = getViewportProjection();
		((ScrollPane)((BorderPane)getContent()).getCenter()).setHvalue(projection.getAdjustedHValueToRevealX(pTo.getX()));
		((ScrollPane)((BorderPane)getContent()).getCenter()).setVvalue(projection.getAdjustedVValueToRevealY(pTo.getY()));
	}
	
	private ViewportProjection getViewportProjection()
	{
		ScrollPane scrollPane = (ScrollPane)((BorderPane)getContent()).getCenter();
		Bounds bounds = scrollPane.getViewportBounds();
		// Because, when the scrollbars are not displayed, the Scrollpane will increase
		// the viewport size beyond the canvas size, it's necessary to max out the dimensions
		// at the size of the canvas.
		int viewportWidth = Math.min((int) bounds.getWidth(), (int) aDiagramCanvas.getWidth());
		int viewportHeight = Math.min((int) bounds.getHeight(), (int) aDiagramCanvas.getHeight());
		return new ViewportProjection(viewportWidth, viewportHeight, 
				(int) aDiagramCanvas.getWidth(), (int) aDiagramCanvas.getHeight(), 
				scrollPane.getHvalue(), scrollPane.getVvalue());
	}

	@Override
	public void shiftKeyPressed() 
	{
		aDiagramCanvasController.shiftKeyPressed();
	}
	
	/* Converts the key typed to an 1-based index that represents
	 * the tool to select in the toolbar. The Keys 1-0 map to 1-10, then
	 * a maps to 11, b to 12, etc. Capitalization does not matter.
	 * Returns -1 is the key is not in a range between 0 and Z.
	 * CSOFF:
	 */
	private static int toolIndex(String pChar)
	{
		assert pChar != null;
		if( pChar.length() != 1 )
		{
			return -1;
		}
		int symbol = pChar.toUpperCase().charAt(0);
		if( symbol == 48 ) // char "0"
		{
			return 10;
		}
		else if( symbol >= 49 && symbol <= 57) // char 1-9
		{
			return symbol - 48;
		}
		else if( symbol >= 65 && symbol <= 90 ) // char A-Z
		{
			return symbol - 54; 
		}
		else
		{
			return -1;
		}
	} // CSON:
	
	@Override
	public void keyTyped(String pChar)
	{   // -1 because the input is 1-index and setSelectedTool is 0-indexed
		toolBar().setSelectedTool(toolIndex(pChar)-1); 
	}
}	        
