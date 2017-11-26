package ca.mcgill.cs.stg.jetuml.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import ca.mcgill.cs.stg.jetuml.framework.GraphPanel;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.nodes.PointNode;

/**
 * Utility class to create icons that are drawn
 * using graphic primitives.
 * 
 * @author Martin P. Robillard
 */
public final class IconCreator
{
	private static final int BUTTON_SIZE = 25;
	private static final int OFFSET = 3;
	
	private IconCreator() {}
	
	/**
	 * Creates a new icon to represent the given type of graph element.
	 * 
	 * @param pElement The element whose class to get an icon for.
	 * @return An icon for the element.
	 * @pre pElement != null;
	 */
	public static Icon createIcon(GraphElement pElement)
	{
		assert pElement != null;
		
		if( pElement instanceof Node )
		{
			return createNodeIcon((Node)pElement);
		}
		else
		{
			assert pElement instanceof Edge;
			return createEdgeIcon((Edge)pElement);
		}
	}
	
	private static Icon createNodeIcon( Node pNode )
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
		return new ImageIcon(image);
	}

	private static Icon createEdgeIcon( final Edge pEdge )
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
		return new ImageIcon(image);
	}
	
	/**
	 * @return An icon that represents the selection tool.
	 */
	public static Icon createSelectionIcon()
	{
		BufferedImage image = new BufferedImage(BUTTON_SIZE, BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		int offset = OFFSET + 3;
		GraphPanel.drawGrabber(graphics, offset, offset);
		GraphPanel.drawGrabber(graphics, offset, BUTTON_SIZE - offset);
		GraphPanel.drawGrabber(graphics, BUTTON_SIZE - offset, offset);
		GraphPanel.drawGrabber(graphics, BUTTON_SIZE - offset, BUTTON_SIZE - offset);
		return new ImageIcon(image);
	}
}
