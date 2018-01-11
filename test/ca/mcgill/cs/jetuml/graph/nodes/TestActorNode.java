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
package ca.mcgill.cs.jetuml.graph.nodes;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.graph.Properties;

/**
 * @author Martin P. Robillard
 */
public class TestActorNode
{
	private ActorNode aNode;
	
	@Before
	public void setup()
	{
		aNode = new ActorNode();
	}
	
	@Test
	public void testGetProperties()
	{
		Properties properties = aNode.properties();
		
		assertEquals("Actor", properties.get("name").get());
		assertEquals(0, properties.get("x").get());
		assertEquals(0, properties.get("y").get());
		
		aNode.setName("Foo");
		aNode.translate(10, 20);
		properties = aNode.properties();
		assertEquals("Foo", properties.get("name").get());
		assertEquals(10, properties.get("x").get());
		assertEquals(20, properties.get("y").get());
	}
}
