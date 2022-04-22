package ca.mcgill.cs.jetuml.viewers.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * Test for the NodeIndex methods. 
 */
public class TestNodeIndex 
{
	private final Node aNode = new ClassNode();
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	
	@BeforeEach
	private void setUp()
	{
		aDiagram.addRootNode(aNode);
		aNode.moveTo(new Point(0, 0));
	}
	
	@Test
	public void testToPoint_north()
	{
		Line nodeFace = new Line(new Point(0, 0), new Point(100, 0));
		assertEquals(new Point(30, 0), NodeIndex.toPoint(nodeFace, Direction.NORTH, NodeIndex.MINUS_TWO));
		assertEquals(new Point(50, 0), NodeIndex.toPoint(nodeFace, Direction.NORTH, NodeIndex.ZERO));
		assertEquals(new Point(90, 0), NodeIndex.toPoint(nodeFace, Direction.NORTH, NodeIndex.PLUS_FOUR));
	}
	
	@Test
	public void testToPoint_south()
	{
		Line nodeFace = new Line(new Point(0, 60), new Point(100, 60));
		assertEquals(new Point(30, 60), NodeIndex.toPoint(nodeFace, Direction.SOUTH, NodeIndex.MINUS_TWO));
		assertEquals(new Point(50, 60), NodeIndex.toPoint(nodeFace, Direction.SOUTH, NodeIndex.ZERO));
		assertEquals(new Point(90, 60), NodeIndex.toPoint(nodeFace, Direction.SOUTH, NodeIndex.PLUS_FOUR));
	}
	
	@Test
	public void testToPoint_west()
	{
		Line nodeFace = new Line(new Point(0, 0), new Point(0, 60));
		assertEquals(new Point(0, 10), NodeIndex.toPoint(nodeFace, Direction.WEST, NodeIndex.MINUS_TWO));
		assertEquals(new Point(0, 30), NodeIndex.toPoint(nodeFace, Direction.WEST, NodeIndex.ZERO));
		assertEquals(new Point(0, 40), NodeIndex.toPoint(nodeFace, Direction.WEST, NodeIndex.PLUS_ONE));
	}
	
	@Test
	public void testToPoint_east()
	{
		Line nodeFace = new Line(new Point(100, 0), new Point(100, 60));
		assertEquals(new Point(100, 10), NodeIndex.toPoint(nodeFace, Direction.EAST, NodeIndex.MINUS_TWO));
		assertEquals(new Point(100, 30), NodeIndex.toPoint(nodeFace, Direction.EAST, NodeIndex.ZERO));
		assertEquals(new Point(100, 40), NodeIndex.toPoint(nodeFace, Direction.EAST, NodeIndex.PLUS_ONE));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_north()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(100, 0));
		Line largerSize = new Line(new Point(0, 0), new Point(200, 0));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Direction.NORTH));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Direction.NORTH));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_south()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(100, 0));
		Line largerSize = new Line(new Point(0, 0), new Point(200, 0));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Direction.SOUTH));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Direction.SOUTH));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_east()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(0, 60));
		Line largerSize = new Line(new Point(0, 0), new Point(0, 120));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Direction.EAST));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Direction.EAST));
	}
	
	@Test
	public void testSpaceBetweenConnectionPoints_west()
	{
		Line regularSize = new Line(new Point(0, 0), new Point(0, 60));
		Line largerSize = new Line(new Point(0, 0), new Point(0, 120));
		assertEquals(10.0, spaceBetweenConnectionPoints(regularSize, Direction.WEST));
		assertEquals(20.0, spaceBetweenConnectionPoints(largerSize, Direction.WEST));
	}
	
	private static float spaceBetweenConnectionPoints(Line pNodeFace, Direction pAttachmentSide)
	{
		try
		{
			Method method = NodeIndex.class.getDeclaredMethod("spaceBetweenConnectionPoints", Line.class, Direction.class);
			method.setAccessible(true);
			return (float) method.invoke(null, pNodeFace, pAttachmentSide);
		}
		catch(ReflectiveOperationException e)
		{
			e.printStackTrace();
			fail();
			return -1;
		}
	}
	
}
