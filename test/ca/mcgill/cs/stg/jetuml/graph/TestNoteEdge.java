/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

public class TestNoteEdge
{
	private NoteNode aNoteNode;
	private PointNode aPointNode;
	private NoteEdge aNoteEdge;
	
	@Before
	public void setup()
	{
		// Bounds [x=0.0,y=0.0,w=60.0,h=40.0]
		aNoteNode = new NoteNode(); 
		
		// Bounds[x=100,y=20,w=0.0,h=0.0]
		aPointNode = new PointNode(); 
		aPointNode.translate(100, 20);
		aNoteEdge = new NoteEdge();
	}
	
	@Test
	public void testBasicConnection()
	{
		aNoteEdge.connect(aNoteNode, aPointNode);
		assertTrue( aNoteEdge.getStart() == aNoteNode );
		assertTrue( aNoteEdge.getEnd() == aPointNode );
		aNoteEdge.connect(aPointNode, aNoteNode);
		assertTrue( aNoteEdge.getStart() == aPointNode );
		assertTrue( aNoteEdge.getEnd() == aNoteNode );
	}
	
	@Test
	public void testClone()
	{
		aNoteEdge.connect(aNoteNode, aPointNode);
		NoteEdge clonedEdge = (NoteEdge) aNoteEdge.clone();
		
		// Test that the start and end nodes are the same object
		// (shallow cloning)
		assertTrue( (NoteNode) clonedEdge.getStart() == aNoteNode );
		assertTrue( (PointNode) clonedEdge.getEnd() == aPointNode );
	}
	
	@Test
	public void testBoundsCalculation()
	{
		aNoteEdge.connect(aNoteNode, aPointNode);
		assertEquals(new Rectangle2D.Double(60,20,40,1), aNoteEdge.getBounds());
		
		Line2D connectionPoints = aNoteEdge.getConnectionPoints();
		assertEquals( 60, connectionPoints.getX1(), 0.01 );
		assertEquals( 20, connectionPoints.getY1(), 0.01 );
		assertEquals( 100, connectionPoints.getX2(), 0.01 );
		assertEquals( 20, connectionPoints.getY2(), 0.01 );
		
		
		aPointNode.translate(20, 0);
		assertEquals(new Rectangle2D.Double(60,20,60,1), aNoteEdge.getBounds());
		
		connectionPoints = aNoteEdge.getConnectionPoints();
		assertEquals( 60, connectionPoints.getX1(), 0.01 );
		assertEquals( 20, connectionPoints.getY1(), 0.01 );
		assertEquals( 120, connectionPoints.getX2(), 0.01 );
		assertEquals( 20, connectionPoints.getY2(), 0.01 );
		
		
		aPointNode.translate(0, 20); // Now at x=120, y = 40
		
		// The edge should intersect the note edge at x=26, y=60
		// (basic correspondence of proportions between triangles)
		// yielding bounds of [x=60,y=26,width=60,height=14]
		assertEquals(new Rectangle2D.Double(60,26,60,14), aNoteEdge.getBounds());
		
		connectionPoints = aNoteEdge.getConnectionPoints();
		assertEquals( 60, connectionPoints.getX1(), 1 );
		assertEquals( 26, connectionPoints.getY1(), 1 );
		assertEquals( 120, connectionPoints.getX2(), 1 );
		assertEquals( 40, connectionPoints.getY2(), 1 );
	}
}
