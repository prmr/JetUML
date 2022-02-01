package ca.mcgill.cs.jetuml.viewers.nodes;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * Tests the NodeStorage. 
 */
public class TestNodeStorage 
{	
	private NodeStorage aNodeStorage;

	@BeforeEach
	public void setup()
	{
		aNodeStorage = new NodeStorage();
	}

	@Test
	public void testGetBoundsReturnsDifferentBoundsWhenNodeStorageIsNotActive()
	{
		Node node = new NoteNode();
		Rectangle boundsA = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		Rectangle boundsB = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		assertNotSame(boundsA, boundsB);
	}

	@Test
	public void testGetBoundsReturnsSameBoundsWhenNodeStorageIsActive()
	{
		aNodeStorage.activate();
		Node node = new NoteNode();
		Rectangle boundsA = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		Rectangle boundsB = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		assertSame(boundsA, boundsB);
	}

	@Test
	public void testGetBoundsReturnsDifferentBoundsForDifferentNodesWhenNodeStorageIsActive()
	{
		aNodeStorage.activate();
		Node node1 = new NoteNode();
		Node node2 = new NoteNode();
		Rectangle boundsA = aNodeStorage.getBounds(node1, createDefaultBoundCalculator());
		Rectangle boundsB = aNodeStorage.getBounds(node2, createDefaultBoundCalculator());
		assertNotSame(boundsA, boundsB);
	}
	
	@Test
	public void testGetBoundsReturnsDifferentBoundsBeforeAndAfterDeactivationOfNodeStorage()
	{
		aNodeStorage.activate();
		Node node = new NoteNode();
		Rectangle boundsBeforeDeactivation = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		aNodeStorage.deactivateAndClear();
		Rectangle boundsAfterDeactivation = aNodeStorage.getBounds(node, createDefaultBoundCalculator());
		assertNotSame(boundsBeforeDeactivation, boundsAfterDeactivation);
	}

	private static Function<Node, Rectangle> createDefaultBoundCalculator()
	{
		return new Function<>()
		{
			@Override
			public Rectangle apply(Node pNode) 
			{
				return new Rectangle(pNode.position().getX(), pNode.position().getY(), 100, 100);
			}
		};
	}
} 