package ca.mcgill.cs.jetuml.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;


import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.PointNode;
import ca.mcgill.cs.jetuml.gui.GraphPanel;

/**
 * Utility class to create icons that are drawn
 * using graphic primitives.
 * 
 * @author Kaylee I. Kutschera - Based on code by Martin P. Robillard
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
		BufferedImage image = new BufferedImage(BUTTON_SIZE, BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Rectangle bounds = pNode.view().getBounds();
		int width = bounds.getWidth();
		int height = bounds.getHeight();
		Graphics2D graphics = image.createGraphics();
		graphics.setComposite(AlphaComposite.Clear);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
		graphics.setComposite(AlphaComposite.Src);
		graphics.setBackground(Color.WHITE);
		graphics.setColor(Color.BLACK);
		pNode.view().draw(graphics);
		return SwingFXUtils.toFXImage(image, null);
	}

	private static Image createEdgeImage( final Edge pEdge )
	{
		BufferedImage image = new BufferedImage(BUTTON_SIZE, BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));

		graphics.setColor(Color.black);
		pEdge.view().draw(graphics);
		return SwingFXUtils.toFXImage(image, null);
	}
	
	/**
	 * @return An image that represents the selection tool.
	 */
	public static Image createSelectionImage()
	{
		BufferedImage image = new BufferedImage(BUTTON_SIZE, BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		int offset = OFFSET + 3;
		GraphPanel.drawGrabber(graphics, offset, offset);
		GraphPanel.drawGrabber(graphics, offset, BUTTON_SIZE - offset);
		GraphPanel.drawGrabber(graphics, BUTTON_SIZE - offset, offset);
		GraphPanel.drawGrabber(graphics, BUTTON_SIZE - offset, BUTTON_SIZE - offset);
		return SwingFXUtils.toFXImage(image, null);
	}
}