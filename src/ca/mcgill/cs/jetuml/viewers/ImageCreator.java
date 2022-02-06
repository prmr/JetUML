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
package ca.mcgill.cs.jetuml.viewers;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Utility class to create icons that are drawn
 * using graphic primitives.
 */
public final class ImageCreator 
{
	private static final double LINE_WIDTH = 0.6;
	private static final int DIAGRAM_PADDING = 4;
	
	private ImageCreator() {}
	
	/**
	 * Creates an image of an entire diagram, with a white border around.
	 * @param pDiagram The diagram to create an image off.
	 * @return An image of the diagram.
	 * @pre pDiagram != null.
	 */
	public static Image createImage(Diagram pDiagram)
	{
		assert pDiagram != null;
		Rectangle bounds = DiagramViewer.getBounds(pDiagram);
		Canvas canvas = new Canvas(bounds.getWidth() + DIAGRAM_PADDING * 2, 
				bounds.getHeight() + DIAGRAM_PADDING *2);
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.setLineWidth(LINE_WIDTH);
		context.setFill(Color.WHITE);
		context.translate(-bounds.getX()+DIAGRAM_PADDING, -bounds.getY()+DIAGRAM_PADDING);
		DiagramType.viewerFor(pDiagram).draw(pDiagram, context);
		WritableImage image = new WritableImage(bounds.getWidth() + DIAGRAM_PADDING * 2, 
				bounds.getHeight() + DIAGRAM_PADDING *2);
		canvas.snapshot(null, image);
		return image;
	}
}