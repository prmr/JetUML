package ca.mcgill.cs.jetuml.persistence;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.PackageNode;

import static org.junit.Assert.*;

public class TestSerializationContext
{
	private SerializationContext aContext;
	private ClassDiagramGraph aGraph;
	private PackageNode aPackage1; // Root
	private PackageNode aPackage2; // Child of aPackage1
	private ClassNode aClassNode1; // Root
	private ClassNode aClassNode2; // Child of aPackage1
	private ClassNode aClassNode3; // Child of aPackage2
	private NoteNode aNoteNode; // Root
	
	@Before
	public void setup()
	{
		aGraph = new ClassDiagramGraph();
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		aClassNode1 = new ClassNode();
		aClassNode2 = new ClassNode();
		aClassNode3 = new ClassNode();
		aNoteNode = new NoteNode();
		aPackage1.addChild(aPackage2);
		aPackage1.addChild(aClassNode2);
		aPackage2.addChild(aClassNode3);
	}
	
	private void loadNodes()
	{
		aGraph.restoreRootNode(aPackage1);
		aGraph.restoreRootNode(aClassNode1);
		aGraph.restoreRootNode(aNoteNode);
	}
	
	@Test
	public void textInit()
	{
		aContext = new SerializationContext(aGraph);
		assertEquals(0, size());
		assertSame(aGraph, aContext.getGraph());
	}
	
	@Test 
	public void testMultipleInsertions()
	{
		loadNodes();
		aContext = new SerializationContext(aGraph);
	}
	
	@Test 
	public void testBasicRetrieval()
	{
		loadNodes();
		aContext = new SerializationContext(aGraph);
		assertEquals(6, size());
		boolean[] slots = new boolean[6];
		for( Node node : aContext )
		{
			int index = aContext.getId(node);
			if( slots[index] )
			{
				fail("Reused id");
			}
			else
			{
				slots[index] = true;
			}
		}
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
