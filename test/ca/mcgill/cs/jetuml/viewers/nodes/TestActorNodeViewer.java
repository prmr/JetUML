/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestActorNodeViewer
{
	private ActorNode aNode; 
	private final ActorNodeViewer aViewer = new ActorNodeViewer();
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aNode = new ActorNode();
	}
	
	@Test
	public void testGetBounds_NoName()
	{
		aNode.setName("");
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertEquals(48, aViewer.getBounds(aNode).getWidth());
		assertEquals(64, aViewer.getBounds(aNode).getHeight());
	}
	
	@Test
	public void testGetBounds_ShortName()
	{
		aNode.setName("X");
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertEquals(48, aViewer.getBounds(aNode).getWidth());
		assertTrue(aViewer.getBounds(aNode).getHeight() > 64);
	}
	
	@Test
	public void testGetBounds_LongName()
	{
		aNode.setName("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertEquals(new Point(0,0), aViewer.getBounds(aNode).getOrigin());
		assertTrue(aViewer.getBounds(aNode).getWidth() > 48);
		assertTrue(aViewer.getBounds(aNode).getHeight() > 64);
	}
}
