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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.File;
import java.util.Optional;

import org.jetuml.application.UserPreferences;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.builder.DiagramBuilder;
import org.jetuml.diagram.validator.DiagramValidator;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Rectangle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
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
	private DiagramCanvas aDiagramCanvas;
	private Optional<File> aFile = Optional.empty(); // The file associated with this diagram
	
	/**
     * Constructs a diagram tab initialized with pDiagram.
     * @param pDiagram The initial diagram
	 */
	public DiagramTab(Diagram pDiagram)
	{
		DiagramValidator validator = DiagramType.newValidatorInstanceFor(pDiagram);
		DiagramBuilder builder = DiagramType.newBuilderInstanceFor(pDiagram);
		DiagramTabToolBar sideBar = new DiagramTabToolBar(builder.renderer());
		aDiagramCanvas = new DiagramCanvas(builder, sideBar, validator, this);
		
		UserPreferences.instance().addBooleanPreferenceChangeHandler(sideBar);
		
		UserPreferences.instance().addBooleanPreferenceChangeHandler(aDiagramCanvas);
		UserPreferences.instance().addIntegerPreferenceChangeHandler(aDiagramCanvas);
		UserPreferences.instance().addStringPreferenceChangeHandler(aDiagramCanvas);
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
		
		setOnCloseRequest(event -> 
		{
			event.consume();
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
		UserPreferences.instance().removeIntegerPreferenceChangeHandler(aDiagramCanvas);
		UserPreferences.instance().removeStringPreferenceChangeHandler(aDiagramCanvas);
	}

	/**
     * @return The diagram being edited within this tab.
	 */
	public Diagram getDiagram()
	{
		return aDiagramCanvas.diagram();
	}
	
	/**
	 * Copy the current selection to the clipboard.
	 */
	public void copy()
	{
		aDiagramCanvas.copy();
	}
	
	/**
	 * Cuts the current selection to the clip board.
	 */
	public void cut()
	{
		aDiagramCanvas.cut();
	}
	
	/**
	 * Pastes the current clip board content to the diagram.
	 */
	public void paste()
	{
		aDiagramCanvas.paste();
	}
	
	/**
	 * Open a dialog to edit the properties of the currently selected element.
	 */
	public void editSelected()
	{
		aDiagramCanvas.editSelected();
	}
	
	/**
	 * Undoes the last command.
	 */
	public void undo()
	{
		aDiagramCanvas.undo();
	}
	
	/**
	 * Redoes the last undone command.
	 */
	public void redo()
	{
		aDiagramCanvas.redo();
	}
	
	/**
	 * Copy the current selection to the clipboard.
	 */
	public void removeSelected()
	{
		aDiagramCanvas.removeSelected();
	}
	
	/**
	 * Selects all elements in the diagram.
	 */
	public void selectAll()
	{
		aDiagramCanvas.selectAll();
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
		aDiagramCanvas.diagramSaved();
	}
	
	/**
	 * @return True if the diagram in this tab
	 *     has unsaved changes.
	 */
	public boolean hasUnsavedChanges()
	{
		return aDiagramCanvas.hasUnsavedChanges();
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
	public void interactionTo(Rectangle pBounds, Direction pDirection)
	{
		// Compute point to reveal
		int x = pBounds.maxX();
		int y = pBounds.maxY();
		
		if( pDirection.isWesterly() ) // Going left, reverse coordinate
		{
			x = pBounds.x(); 
		}
		if( pDirection.isNortherly() )	// Going up, reverse coordinate
		{
			y = pBounds.y(); 
		}
		
		// Special case: if the viewport is not large enough for the entire
		// selection, the use will experience unsettling jitter. 
		// We prevent this by not auto-scrolling
		ViewportProjection projection = getViewportProjection();
		if( pBounds.width() <= projection.width() )
		{
			scrollPane().setHvalue(projection.getAdjustedHValueToRevealX(x));
		}
		if( pBounds.height() <= projection.height() )
		{
			scrollPane().setVvalue(projection.getAdjustedVValueToRevealY(y));
		}
	}
	
	/*
	 * Fetches the ScrollPane component that wraps the canvas from the scene graph
	 */
	private ScrollPane scrollPane()
	{
		return (ScrollPane)((BorderPane)getContent()).getCenter();
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
		aDiagramCanvas.shiftKeyPressed();
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
	
	/**
	 * @return An image of this canvas.
	 */
	public Image createImage()
	{
		return aDiagramCanvas.createImage();
	}
}	        
