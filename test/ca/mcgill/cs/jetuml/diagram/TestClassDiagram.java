/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Iterator;

import org.junit.Assert.*;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * This class is used to test the methods of the abstract
 * class as well.
 */
public class TestClassDiagram
{
	private Diagram aDiagram;
	private ClassNode aClassNode1;
	private ClassNode aClassNode2;
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	private int numberOfEdges()
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Edge edge : aDiagram.edges() )
		{
			sum++;
		}
		return sum;
	}
	
	private Node getRootNode(int pIndex)
	{
		Iterator<Node> iterator = aDiagram.rootNodes().iterator();
		int i = 0;
		Node node = iterator.next();
		while( i < pIndex )
		{
			i++;
			node = iterator.next();
		}
		return node;
	}
	
	private Edge getEdge(int pIndex)
	{
		Iterator<Edge> iterator = aDiagram.edges().iterator();
		int i = 0;
		Edge edge = iterator.next();
		while( i < pIndex )
		{
			i++;
			edge = iterator.next();
		}
		return edge;
	}
	
	private int numberOfRootNodes()
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Node node : aDiagram.rootNodes() )
		{
			sum++;
		}
		return sum;
	}
	
	@Before
	public void setUp()
	{
		aDiagram = new ClassDiagram();
		aClassNode1 = new ClassNode();
		aClassNode2 = new ClassNode();
	}
	
	@Test
	public void testInit()
	{
		assertEquals(0, numberOfEdges());
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testAddRemoveRootNode()
	{
		aDiagram.addRootNode(aClassNode1);
		assertEquals(1, numberOfRootNodes());
		assertSame(aClassNode1, getRootNode(0));
		aDiagram.addRootNode(aClassNode2);
		assertEquals(2, numberOfRootNodes());
		assertSame(aClassNode2, getRootNode(1));
		
		aDiagram.removeRootNode(aClassNode2);
		assertEquals(1, numberOfRootNodes());
		assertSame(aClassNode1, getRootNode(0));
		
		aDiagram.removeRootNode(aClassNode1);
		assertEquals(0, numberOfRootNodes());
	}
}
