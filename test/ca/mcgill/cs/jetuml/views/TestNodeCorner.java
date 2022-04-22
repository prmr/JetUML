package ca.mcgill.cs.jetuml.views;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.NodeCorner;
import ca.mcgill.cs.jetuml.viewers.edges.NodeIndex;

/**
 * Tests for the enumerated type NodeCorner
 *
 */
public class TestNodeCorner 
{
	private static final Node aNode = new ClassNode();
	private static final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	@BeforeEach
	private void setUpNode()
	{
		aDiagram.addRootNode(aNode);
	}
	

	@Test
	public void testGetHorizontalIndex_right()
	{
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.TOP_RIGHT), NodeIndex.PLUS_THREE);
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.BOTTOM_RIGHT), NodeIndex.PLUS_THREE);
	}
	
	@Test
	public void testGetHorizontalIndex_left()
	{
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.TOP_LEFT), NodeIndex.MINUS_THREE);
		assertEquals(NodeCorner.getHorizontalIndex(NodeCorner.BOTTOM_LEFT), NodeIndex.MINUS_THREE);
	}
	
	@Test
	public void testGetVerticalIndex_top()
	{
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.TOP_LEFT), NodeIndex.MINUS_ONE);
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.TOP_RIGHT), NodeIndex.MINUS_ONE);
	}
	
	@Test
	public void testGetVerticalIndex_bottom()
	{
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.BOTTOM_LEFT), NodeIndex.PLUS_ONE);
		assertEquals(NodeCorner.getVerticalIndex(NodeCorner.BOTTOM_RIGHT), NodeIndex.PLUS_ONE);
	}
	
	@Test
	public void testHorizontalSide_top()
	{
		assertEquals(NodeCorner.horizontalSide(NodeCorner.TOP_LEFT), Direction.NORTH);
		assertEquals(NodeCorner.horizontalSide(NodeCorner.TOP_RIGHT), Direction.NORTH);
	}
	
	@Test
	public void testHorizontalSide_bottom()
	{
		assertEquals(NodeCorner.horizontalSide(NodeCorner.BOTTOM_LEFT), Direction.SOUTH);
		assertEquals(NodeCorner.horizontalSide(NodeCorner.BOTTOM_RIGHT), Direction.SOUTH);
	}
	
	@Test
	public void testGetVerticalSide_right()
	{
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_RIGHT), Direction.EAST);
		assertEquals(NodeCorner.verticalSide(NodeCorner.BOTTOM_RIGHT), Direction.EAST);
	}
	
	@Test
	public void testGetVerticalSide_left()
	{
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_LEFT), Direction.WEST);
		assertEquals(NodeCorner.verticalSide(NodeCorner.TOP_LEFT), Direction.WEST);
	}
	
	@Test
	public void testToPoints_topRight()
	{
		setUpNode();
		assertEquals(new Point(80, 0), NodeCorner.toPoints(NodeCorner.TOP_RIGHT, aNode)[0]);
		assertEquals(new Point(100, 20), NodeCorner.toPoints(NodeCorner.TOP_RIGHT, aNode)[1]);
	}
	
	@Test
	public void testToPoints_bottomRight()
	{
		setUpNode();
		assertEquals(new Point(80, 60), NodeCorner.toPoints(NodeCorner.BOTTOM_RIGHT, aNode)[0]);
		assertEquals(new Point(100, 40), NodeCorner.toPoints(NodeCorner.BOTTOM_RIGHT, aNode)[1]);
	}
	
	@Test
	public void testToPoints_topLeft()
	{
		setUpNode();
		assertEquals(new Point(20, 0), NodeCorner.toPoints(NodeCorner.TOP_LEFT, aNode)[0]);
		assertEquals(new Point(0, 20), NodeCorner.toPoints(NodeCorner.TOP_LEFT, aNode)[1]);
	}
	
	@Test
	public void testToPoints_bottomLeft()
	{
		setUpNode();
		assertEquals(new Point(20, 60), NodeCorner.toPoints(NodeCorner.BOTTOM_LEFT, aNode)[0]);
		assertEquals(new Point(0, 40), NodeCorner.toPoints(NodeCorner.BOTTOM_LEFT, aNode)[1]);
	}
	
	
}