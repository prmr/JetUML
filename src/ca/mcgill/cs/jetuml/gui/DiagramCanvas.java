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

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.DiagramView;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A canvas on which to view diagrams.
 */
public class DiagramCanvas extends Canvas implements SelectionObserver, BooleanPreferenceChangeHandler
{	
	private static final double LINE_WIDTH = 0.6;
	
	private DiagramView aDiagramView;
	private DiagramCanvasController aController;
	
	/**
	 * Constructs the canvas, assigns the diagram view to it.
	 * 
	 * @param pDiagramView Rhe diagram view to draw on this canvas.
	 * @param pWidth The fixed width of the canvas.
	 * @param pHeight The fixed height of the canvas.
	 */
	public DiagramCanvas(DiagramView pDiagramView, int pWidth, int pHeight)
	{
		super(pWidth, pHeight);
		getGraphicsContext2D().setLineWidth(LINE_WIDTH);
		getGraphicsContext2D().setFill(Color.WHITE);
		aDiagramView = pDiagramView;
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
		return aDiagramView.getDiagram();
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
		aDiagramView.draw(context);
		aController.synchronizeSelectionModel();
		aController.getSelectionModel().forEach( selected -> selected.view().drawSelectionHandles(context));
		aController.getSelectionModel().getRubberband().ifPresent( rubberband -> ToolGraphics.drawRubberband(context, rubberband));
		aController.getSelectionModel().getLasso().ifPresent( lasso -> ToolGraphics.drawLasso(context, lasso));
	}
	
	@Override
	public void selectionModelChanged()
	{
		paintPanel();		
	}

	@Override
	public void preferenceChanged(BooleanPreference pPreference)
	{
		if( pPreference == BooleanPreference.showGrid )
		{
			paintPanel();
		}
	}
}
