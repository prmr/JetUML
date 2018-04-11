package ca.mcgill.cs.jetuml.views;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.PointNode;
import ca.mcgill.cs.jetuml.gui.GraphPanel;
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
 * 
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
public final class ImageCreator 
{
	private static final int BUTTON_SIZE = 25;
	private static final int OFFSET = 3;
	
	private ImageCreator() {}
	
	/**
	 * Creates a new image to represent the given type of graph element.
	 * 
	 * @param pElement The element whose class to get an icon for.
	 * @return An image for the element.
	 * @pre pElement != null;
	 */
	public static Image createImage(GraphElement pElement)
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
			new Scene(new Pane(canvas));
			canvas.snapshot(parameters, image);
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
			new Scene(new Pane(canvas));
			canvas.snapshot(parameters, image);
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
		GraphPanel.drawGrabber(graphics, offset, offset);
		GraphPanel.drawGrabber(graphics, offset, BUTTON_SIZE - offset);
		GraphPanel.drawGrabber(graphics, BUTTON_SIZE - offset, offset);
		GraphPanel.drawGrabber(graphics, BUTTON_SIZE - offset, BUTTON_SIZE - offset);
		WritableImage image = new WritableImage(BUTTON_SIZE, BUTTON_SIZE);
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		Platform.runLater(() -> 
		{
			new Scene(new Pane(canvas));
			canvas.snapshot(parameters, image);
		});
		return image;
	}
}