package ca.mcgill.cs.stg.jetuml.graph.views.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.edges.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.edges.Edge;
import ca.mcgill.cs.stg.jetuml.graph.nodes.CallNode;
import ca.mcgill.cs.stg.jetuml.graph.nodes.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.nodes.Node;

/**
 * An object to render an implicit parameter in a Sequence diagram.
 * 
 * @author Martin P. Robillard
 *
 */
public class CallNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 16;
	private static final int DEFAULT_HEIGHT = 30;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public CallNodeView(CallNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private boolean openBottom()
	{
		return ((CallNode)node()).isOpenBottom();
	}
	
	private ImplicitParameterNode implicitParameter()
	{
		return (ImplicitParameterNode)((CallNode)node()).getParent();
	}
	
	@Override
	public void setBounds(Rectangle pNewBounds)
	{
		super.setBounds(pNewBounds);
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(Color.WHITE);
		pGraphics2D.fill(Conversions.toRectangle2D(getBounds()));
		pGraphics2D.setColor(oldColor);
		if(openBottom())
		{
			Rectangle b = getBounds();
			double x1 = b.getX();
			double x2 = x1 + b.getWidth();
			double y1 = b.getY();
			double y3 = y1 + b.getHeight();
			double y2 = y3 - CallNode.CALL_YGAP;
			pGraphics2D.draw(new Line2D.Double(x1, y1, x2, y1));
			pGraphics2D.draw(new Line2D.Double(x1, y1, x1, y2));
			pGraphics2D.draw(new Line2D.Double(x2, y1, x2, y2));
			Stroke oldStroke = pGraphics2D.getStroke();
			// CSOFF:
			pGraphics2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] { 5.0f, 5.0f }, 0.0f));
			// CSON:
			pGraphics2D.draw(new Line2D.Double(x1, y2, x1, y3));
			pGraphics2D.draw(new Line2D.Double(x2, y2, x2, y3));
			pGraphics2D.setStroke(oldStroke);
		}
		else
		{
			pGraphics2D.draw(Conversions.toRectangle2D(getBounds()));
		}
	}
	
	@Override
	public void layout(Graph pGraph)
	{
		assert implicitParameter() != null;
		assert pGraph instanceof SequenceDiagramGraph;
		SequenceDiagramGraph graph = (SequenceDiagramGraph) pGraph;

		// Shift the node to its proper place on the X axis.
		node().translate(computeMidX(pGraph) - getBounds().getCenter().getX(), 0);

		// Compute the Y coordinate of the bottom of the node
		double bottomY = computeBottomY(graph);

		Rectangle bounds = getBounds();

		double minHeight = DEFAULT_HEIGHT;
		Edge returnEdge = graph.findEdge(node(), graph.getCaller(this.node()));
		if(returnEdge != null)
		{
			Rectangle edgeBounds = returnEdge.getBounds();
			minHeight = Math.max(minHeight, edgeBounds.getHeight());         
		}
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), (int)Math.max(minHeight, bottomY - bounds.getY())));
	}
	
	/*
	 * @return The X coordinate that should be the middle
	 * of this call node. Takes into account nested calls.
	 */
	private int computeMidX(Graph pGraph)
	{
		int xmid = implicitParameter().getBounds().getCenter().getX();

		// Calculate a shift for each caller with the same implicit parameter
		for(CallNode node = ((SequenceDiagramGraph)pGraph).getCaller(node()); node != null && node != node(); 
				node = ((SequenceDiagramGraph)pGraph).getCaller(node))
		{
			if(((CallNode)node).getParent() == implicitParameter())
			{
				xmid += getBounds().getWidth() / 2;
			}
		}
		return xmid;
	}

	/*
	 * Compute the Y coordinate of the bottom of the CallNode. This 
	 * triggers the layout of all callee nodes.
	 */
	private double computeBottomY(SequenceDiagramGraph pGraph)
	{
		// Compute the Y coordinate of the bottom of the node
		int bottomY = getBounds().getY() + CallNode.CALL_YGAP;

		for(Node node : getCallees(pGraph))
		{
			if(node instanceof ImplicitParameterNode) // <<create>>
			{
				node.translate(0, bottomY - ((ImplicitParameterNode) node).getTopRectangle().getCenter().getY());
				bottomY += ((ImplicitParameterNode)node).getTopRectangle().getHeight() / 2 + CallNode.CALL_YGAP;
			}
			else if(node instanceof CallNode)
			{  
				Edge callEdge = pGraph.findEdge(node(), node);
				// compute height of call edge
				if(callEdge != null)
				{
					bottomY += callEdge.getBounds().getHeight() - CallNode.CALL_YGAP;
				}

				node.translate(0, bottomY - node.getBounds().getY());
				node.layout(pGraph);
				if(((CallNode) node).isSignaled(pGraph))
				{
					bottomY += CallNode.CALL_YGAP;
				}
				else
				{
					bottomY += node.getBounds().getHeight() + CallNode.CALL_YGAP;
				}
			}
		}
		if(openBottom())
		{
			bottomY += 2 * CallNode.CALL_YGAP;
		}
		return bottomY;
	}
	
	/*
	 * @param pGraph
	 * @return All the nodes (CallNodes or ImplicitParameterNodes) that have a calledge
	 * originating at this CallNode. If an ImplicitParameterNode is in the list, it's always
	 * returned first.
	 */
	private List<Node> getCallees(Graph pGraph)
	{
		List<Node> callees = new ArrayList<>();
		for( Edge edge : pGraph.getEdges())
		{
			if( edge.getStart() == node() && edge instanceof CallEdge )
			{
				if( edge.getEnd() instanceof ImplicitParameterNode )
				{
					callees.add(0, edge.getEnd());
				}
				else
				{
					callees.add(edge.getEnd());
				}
			}
		}
		return callees;
	}


	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		if(pDirection.getX() > 0)
		{
			return new Point(getBounds().getMaxX(), getBounds().getY());
		}
		else
		{
			return new Point(getBounds().getX(), getBounds().getY());
		}
	}
}
