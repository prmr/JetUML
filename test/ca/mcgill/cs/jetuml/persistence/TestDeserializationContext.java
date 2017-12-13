package ca.mcgill.cs.jetuml.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;

public class TestDeserializationContext
{
	private ClassDiagramGraph aGraph;
	private DeserializationContext aContext;
	private ClassNode aClassNode1; 
	private ClassNode aClassNode2; 
	private ClassNode aClassNode3; 

	@Before
	public void setup()
	{
		aGraph = new ClassDiagramGraph();
		aClassNode1 = new ClassNode();
		aClassNode2 = new ClassNode();
		aClassNode3 = new ClassNode();
	}
	
	@Test
	public void textInit()
	{
		aContext = new DeserializationContext(aGraph);
		assertEquals(0, size());
		assertSame(aGraph, aContext.getGraph());
	}
	
	@Test
	public void testAddGet()
	{
		aContext = new DeserializationContext(aGraph);
		aContext.addNode(aClassNode1, 0);
		assertEquals(1, size());
		assertSame(aClassNode1, aContext.getNode(0));
		aContext.addNode(aClassNode2, 1);
		assertEquals(2, size());
		assertSame(aClassNode1, aContext.getNode(0));
		assertSame(aClassNode2, aContext.getNode(1));
		aContext.addNode(aClassNode3, 2);
		assertEquals(3, size());
		assertSame(aClassNode1, aContext.getNode(0));
		assertSame(aClassNode2, aContext.getNode(1));
		assertSame(aClassNode3, aContext.getNode(2));
		
		// Add the same node again, with the same id.
		aContext.addNode(aClassNode1, 0);
		assertEquals(3, size());
		assertSame(aClassNode1, aContext.getNode(0));
		assertSame(aClassNode2, aContext.getNode(1));
		assertSame(aClassNode3, aContext.getNode(2));
		
		// Add the same node again, with a different id
		aContext.addNode(aClassNode1, 4);
		assertEquals(3, size());
		assertSame(aClassNode1, aContext.getNode(4));
		assertSame(aClassNode2, aContext.getNode(1));
		assertSame(aClassNode3, aContext.getNode(2));
	}
	
	private int size()
	{
		int size = 0;
		for( @SuppressWarnings("unused") Node n : aContext )
		{
			size++;
		}
		return size;
	}
}
