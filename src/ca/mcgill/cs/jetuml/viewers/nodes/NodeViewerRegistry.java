package ca.mcgill.cs.jetuml.viewers.nodes;

import java.util.IdentityHashMap;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Keeps track of the association between a node type and the viewer
 * that needs to be used to view it.
 */
public final class NodeViewerRegistry
{	
	private static final NodeViewerRegistry INSTANCE = new NodeViewerRegistry();
	
	private IdentityHashMap<Class<? extends Node>, NodeViewer> aRegistry = 
			new IdentityHashMap<>();
	
	private NodeViewerRegistry() 
	{
		aRegistry.put(ActorNode.class, new ActorNodeViewer());
		aRegistry.put(CallNode.class, new CallNodeViewer());
		aRegistry.put(ClassNode.class, new ClassNodeViewer());
		aRegistry.put(FieldNode.class, new FieldNodeViewer());
		aRegistry.put(FinalStateNode.class, new CircularStateNodeViewer(true));
		aRegistry.put(ImplicitParameterNode.class, new ImplicitParameterNodeViewer());
		aRegistry.put(InitialStateNode.class, new CircularStateNodeViewer(false));
		aRegistry.put(InterfaceNode.class, new InterfaceNodeViewer());
		aRegistry.put(NoteNode.class, new NoteNodeViewer());
		aRegistry.put(ObjectNode.class, new ObjectNodeViewer());
		aRegistry.put(PackageNode.class, new PackageNodeViewer());
		aRegistry.put(PointNode.class, new PointNodeViewer());
		aRegistry.put(StateNode.class, new StateNodeViewer());
		aRegistry.put(UseCaseNode.class, new UseCaseNodeViewer());
	}
	
	/**
	 * @param pNode The node to view.
	 * @return A viewer for pNode
	 * @pre pNode != null;
	 */
	private NodeViewer viewerFor(Node pNode)
	{
		assert pNode != null && aRegistry.containsKey(pNode.getClass());
		return aRegistry.get(pNode.getClass());
	}
	
   	/**
     * Tests whether pNode contains a point.
     * @param pNode the edge to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
	public static boolean contains(Node pNode, Point pPoint)
	{
		return INSTANCE.viewerFor(pNode).contains(pNode, pPoint);
	}
	
  	/**
   	 * Returns an icon that represents pNode.
   	 * @param pNode The node for which we need an icon.
     * @return A canvas object on which the icon is painted.
     * @pre pNode != null
   	 */
   	public static Canvas createIcon(Node pNode)
   	{
   		return INSTANCE.viewerFor(pNode).createIcon(pNode);
   	}
   	
	/**
     * Draws pNode.
     * @param pNode The edge to draw.
     * @param pGraphics the graphics context
     * @pre pNode != null
	 */
   	public static void draw(Node pNode, GraphicsContext pGraphics)
   	{
   		INSTANCE.viewerFor(pNode).draw(pNode, pGraphics);
   	}
   	
   	/**
     * Draw selection handles around pNode.
     * @param pNode The target edge
     * @param pGraphics the graphics context
     * @pre pNode != null && pGraphics != null
	 */
   	public static void drawSelectionHandles(Node pNode, GraphicsContext pGraphics)
   	{
   		INSTANCE.viewerFor(pNode).drawSelectionHandles(pNode, pGraphics);
   	}
   	
	/**
     * Gets the smallest rectangle that bounds pNode.
     * The bounding rectangle contains all labels.
     * @param pNode The node whose bounds we wish to compute.
     * @return the bounding rectangle
     * @pre pNode != null
   	 */
	public static Rectangle getBounds(Node pNode)
	{
		return INSTANCE.viewerFor(pNode).getBounds(pNode);
	}
	
  	/**
     * Gets the points at which pNode is connected to
     * its nodes.
     * @param pNode The target node
     * @param pDirection The desired direction.
     * @return A connection point on the node.
     * @pre pNode != null && pDirection != null
     * 
     */
   	public static Point getConnectionPoints(Node pNode, Direction pDirection)
   	{
		return INSTANCE.viewerFor(pNode).getConnectionPoint(pNode, pDirection);
   	}
}
