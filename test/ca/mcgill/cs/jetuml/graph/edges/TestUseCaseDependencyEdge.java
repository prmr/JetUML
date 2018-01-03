/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.graph.edges;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.graph.Properties;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseDependencyEdge.Type;

public class TestUseCaseDependencyEdge
{
	private UseCaseDependencyEdge aEdge;
	
	@Before
	public void setup()
	{
		aEdge = new UseCaseDependencyEdge();
	}
	
	@Test
	public void testGetProperties()
	{
		Properties properties = aEdge.properties();
		
		assertEquals(Type.None, properties.get("dependencyType").get());
		aEdge.setType(Type.Extend);
		properties = aEdge.properties();
		assertEquals(Type.Extend, properties.get("dependencyType").get());
	}
}
