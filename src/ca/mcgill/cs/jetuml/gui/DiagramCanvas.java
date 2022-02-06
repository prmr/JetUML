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
package ca.mcgill.cs.jetuml.gui;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreferenceChangeHandler;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.DiagramViewer;
import ca.mcgill.cs.jetuml.viewers.Grid;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import ca.mcgill.cs.jetuml.viewers.ViewerUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
	
	private final Diagram aDiagram;
	private DiagramCanvasController aController;
	
	/**
	 * Constructs the canvas, assigns the diagram to it.
	 * 
	 * @param pDiagram The diagram to draw on this canvas.
	 * @pre pDiagram != null;
	 */
	public DiagramCanvas(Diagram pDiagram)
	{
		assert pDiagram != null;
		Dimension dimension = getDiagramCanvasWidth(pDiagram);
		setWidth(dimension.width());
		setHeight(dimension.height());
		getGraphicsContext2D().setLineWidth(LINE_WIDTH);
		getGraphicsContext2D().setFill(Color.WHITE);
		aDiagram = pDiagram;
	}
	
	/**
	 * Should only be called once immediately after the constructor call.
	 * 
	 * @param pController The controller for this canvas.
	 */
	public void setController(DiagramCanvasController pController)
	{
		aController = pController;
	}
	
	@Override
	public boolean isResizable()
	{
	    return false;
	}
	
	/**
	 * @return The diagram painted on this canvas.
	 */
	public Diagram getDiagram()
	{
		return aDiagram;
	}
	
	/**
	 * Paints the panel and all the graph elements in aDiagramView.
	 * Called after the panel is resized.
	 */
	public void paintPanel()
	{
		GraphicsContext context = getGraphicsContext2D();
		context.setFill(Color.WHITE); 
		context.fillRect(0, 0, getWidth(), getHeight());
		if(UserPreferences.instance().getBoolean(BooleanPreference.showGrid)) 
		{
			Grid.draw(context, new Rectangle(0, 0, (int) getWidth(), (int) getHeight()));
		}
		DiagramType.viewerFor(aDiagram).draw(aDiagram, context);
		aController.synchronizeSelectionModel();
		aController.getSelectionModel().forEach( selected -> ViewerUtils.drawSelectionHandles(selected, context));
		aController.getSelectionModel().getRubberband().ifPresent( rubberband -> ToolGraphics.drawRubberband(context, rubberband));
		aController.getSelectionModel().getLasso().ifPresent( lasso -> ToolGraphics.drawLasso(context, lasso));
	}
	
	@Override
	public void selectionModelChanged()
	{
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
		Rectangle bounds = DiagramViewer.getBounds(pDiagram);
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
}
