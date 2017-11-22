package ca.mcgill.cs.stg.jetuml.views.nodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.nodes.NoteNode;

/**
 * An object to render a NoteNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class NoteNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 40;
	private static final int FOLD_X = 8;
	private static final int FOLD_Y = 8;
	private static final Color DEFAULT_COLOR = new Color(0.9f, 0.9f, 0.6f); // Pale yellow
	
	/**
	 * @param pNode The node to wrap.
	 */
	public NoteNodeView(NoteNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private MultiLineString name()
	{
		return ((NoteNode)node()).getName();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(DEFAULT_COLOR);

		Shape path = getShape();
		pGraphics2D.fill(path);
		pGraphics2D.setColor(oldColor);
		pGraphics2D.draw(path);

		final Rectangle bounds = getBounds();
		GeneralPath fold = new GeneralPath();
		fold.moveTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
		fold.lineTo((float)bounds.getMaxX() - FOLD_X, (float)bounds.getY() + FOLD_X);
		fold.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
		fold.closePath();
		oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(pGraphics2D.getBackground());
		pGraphics2D.fill(fold);
		pGraphics2D.setColor(oldColor);      
		pGraphics2D.draw(fold);      
      
		name().draw(pGraphics2D, getBounds());
	}
	
	@Override
	public Shape getShape()
	{
		Rectangle bounds = getBounds();
		GeneralPath path = new GeneralPath();
		path.moveTo((float)bounds.getX(), (float)bounds.getY());
		path.lineTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
		path.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
		path.lineTo((float)bounds.getMaxX(), (float)bounds.getMaxY());
		path.lineTo((float)bounds.getX(), (float)bounds.getMaxY());
		path.closePath();
		return path;
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		Rectangle b = name().getBounds(); 
		Rectangle bounds = getBounds();
		b = new Rectangle(bounds.getX(), bounds.getY(), Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid.snapped(b));
	}
}
