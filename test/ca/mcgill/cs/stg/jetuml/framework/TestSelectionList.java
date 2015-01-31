package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;

public class TestSelectionList
{
	private Edge aEdge1; 
	private Edge aEdge2; 
	private Node aNode1;
	private SelectionList aList;
	
	@Before
	public void setup()
	{
		aEdge1 = new CallEdge();
		aEdge2 = new NoteEdge();
		aNode1 = new ClassNode();
		aList = new SelectionList();
	}
	
	@Test
	public void testEmpty()
	{
		aList.clearSelection();
		assertNull(aList.getLastSelected());
		aList.remove(aEdge1);
		assertFalse(aList.iterator().hasNext());
		assertEquals(0, aList.size());
		assertFalse(aList.contains(aEdge1));
	}
	
	@Test
	public void testContains()
	{
		aList.add(aNode1);
		aList.add(aEdge1);
		assertTrue(aList.contains(aNode1));
		assertTrue(aList.contains(aEdge1));
		assertFalse(aList.contains(aEdge2));
	}
	
	@Test
	public void testAdd()
	{
		aList.add(aEdge1);
		assertEquals(1, aList.size());
		assertEquals(aEdge1, aList.getLastSelected());
		aList.add(aNode1);
		assertEquals(2, aList.size());
		assertEquals(aNode1, aList.getLastSelected());
		aList.add(aEdge1);
		assertEquals(2, aList.size());
		assertEquals(aEdge1, aList.getLastSelected());
	}
	
	@Test
	public void testClearSelection()
	{
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.add(aNode1);
		aList.clearSelection();
		assertEquals(0, aList.size());
		assertNull(aList.getLastSelected());
	}
	
	@Test
	public void testRemove()
	{
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.add(aNode1);
		aList.remove(aEdge1);
		assertEquals(2,  aList.size());
		Iterator<GraphElement> iterator = aList.iterator();
		assertEquals(aEdge2, iterator.next());
		assertEquals(aNode1, iterator.next());
		assertEquals(aNode1, aList.getLastSelected());
	}
	
	@Test
	public void testSet()
	{
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.set(aNode1);
		assertEquals(1, aList.size());
		assertEquals(aNode1, aList.getLastSelected());
		aList.clearSelection();
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.set(aEdge1);
		assertEquals(1, aList.size());
		assertEquals(aEdge1, aList.getLastSelected());
	}
}
