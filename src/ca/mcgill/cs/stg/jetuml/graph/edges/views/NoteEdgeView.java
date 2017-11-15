package ca.mcgill.cs.stg.jetuml.graph.edges.views;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import ca.mcgill.cs.stg.jetuml.geom.Line;
import ca.mcgill.cs.stg.jetuml.graph.edges.Edge;

/**
 * A straight dotted line.
 * 
 * @author Martin P. Robillard
 */
public class NoteEdgeView extends AbstractEdgeView
{
	private static final Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, 
			  BasicStroke.JOIN_ROUND, 0.0f, new float[] { 3.0f, 3.0f }, 0.0f);
		
	/**
	 * @param pEdge The edge to wrap.
	 */
	public NoteEdgeView(Edge pEdge)
	{
		super(pEdge);
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Stroke oldStroke = pGraphics2D.getStroke();
		pGraphics2D.setStroke(DOTTED_STROKE);
		pGraphics2D.draw(getShape());
		pGraphics2D.setStroke(oldStroke);
	}
	
	
	@Override
	protected Shape getShape()
	{
		GeneralPath path = new GeneralPath();
		Line conn = getConnectionPoints();
		path.moveTo((float)conn.getX1(), (float)conn.getY1());
		path.lineTo((float)conn.getX2(), (float)conn.getY2());
		return path;
	}
}
