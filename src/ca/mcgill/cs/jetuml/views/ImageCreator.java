/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views;

import java.util.concurrent.RejectedExecutionException;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Utility class to create icons that are drawn
 * using graphic primitives.
 */
public final class ImageCreator 
{
	private static final double LINE_WIDTH = 0.6;
	private static final int BUTTON_SIZE = 25;
	private static final int OFFSET = 3;
	private static final int DIAGRAM_PADDING = 4;
	
	private ImageCreator() {}
	
	/**
	 * Creates a new image to represent the given type of graph element.
	 * 
	 * @param pElement The element whose class to get an icon for.
	 * @return An image for the element.
	 * @pre pElement != null;
	 */
	public static Image createImage(DiagramElement pElement)
	{
		assert pElement != null;
		
		if( pElement instanceof Node )
		{
			return createNodeImage((Node)pElement);
		}
		else
		{
			assert pElement instanceof Edge;
			return createEdgeImage((Edge)pElement);
		}
	}
	
	/**
	 * Creates an image of an entire diagram, with a white border around.
	 * @param pDiagram The diagram to create an image off.
	 * @return An image of the diagram.
	 * @pre pDiagram != null.
	 */
	public static Image createImage(Diagram pDiagram)
	{
		assert pDiagram != null;
		DiagramView diagramView = DiagramType.newViewInstanceFor(pDiagram);
		Rectangle bounds = diagramView.getBounds();
		Canvas canvas = new Canvas(bounds.getWidth() + DIAGRAM_PADDING * 2, 
				bounds.getHeight() + DIAGRAM_PADDING *2);
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.setLineWidth(LINE_WIDTH);
		context.setFill(Color.WHITE);
		context.translate(-bounds.getX()+DIAGRAM_PADDING, -bounds.getY()+DIAGRAM_PADDING);
		diagramView.draw(context);
		WritableImage image = new WritableImage(bounds.getWidth() + DIAGRAM_PADDING * 2, 
				bounds.getHeight() + DIAGRAM_PADDING *2);
		canvas.snapshot(null, image);
		return image;
	}
	
	private static Image createNodeImage( Node pNode )
	{
		Rectangle bounds = pNode.view().getBounds();
		int width = bounds.getWidth();
		int height = bounds.getHeight();
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate((int) Math.max((height - width) / 2, 0), (int)Math.max((width - height) / 2, 0));
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		pNode.view().draw(graphics);
		WritableImage image = new WritableImage(BUTTON_SIZE, BUTTON_SIZE);
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		Platform.runLater(() -> 
		{
			try 
			{
				new Scene(new Pane(canvas));
				canvas.snapshot(parameters, image);
			}
			catch (NullPointerException | RejectedExecutionException e)
			{
				// should only be caught in JUnit tests
			}
		});
		return image;
	}

	private static Image createEdgeImage( final Edge pEdge )
	{
		PointNode pointNode = new PointNode();
		pointNode.translate(OFFSET, OFFSET);
		PointNode destination = new PointNode();
		destination.translate(BUTTON_SIZE - OFFSET, BUTTON_SIZE - OFFSET);
		pEdge.connect(pointNode, destination, null);
		Rectangle bounds = new Rectangle(0, 0, 0, 0);
		bounds = bounds.add(pointNode.view().getBounds());
		bounds = bounds.add(destination.view().getBounds());
		bounds = bounds.add(pEdge.view().getBounds());
		int width = bounds.getWidth();
		int height = bounds.getHeight();
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
		graphics.setStroke(Color.BLACK);
		pEdge.view().draw(graphics);
		WritableImage image = new WritableImage(BUTTON_SIZE, BUTTON_SIZE);
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		Platform.runLater(() -> 
		{
			try 
			{
				new Scene(new Pane(canvas));
				canvas.snapshot(parameters, image);
			}
			catch (NullPointerException | RejectedExecutionException e)
			{
				// should only be caught in JUnit tests
			}
		});
		return image;
	}
	
	/**
	 * @return An image that represents the selection tool.
	 */
	public static Image createSelectionImage()
	{
		int offset = OFFSET + 3;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		ToolGraphics.drawHandles(graphics, new Rectangle(offset, offset, BUTTON_SIZE - (offset*2), BUTTON_SIZE-(offset*2) ));
		WritableImage image = new WritableImage(BUTTON_SIZE, BUTTON_SIZE);
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		Platform.runLater(() -> 
		{
			try 
			{
				new Scene(new Pane(canvas));
				canvas.snapshot(parameters, image);
			}
			catch (NullPointerException | RejectedExecutionException e)
			{
				// should only be caught in JUnit tests
			}
		});
		return image;
	}
}