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
package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.graph.PropertyOrder;
import ca.mcgill.cs.stg.jetuml.graph.edges.AggregationEdge;
import ca.mcgill.cs.stg.jetuml.graph.edges.AssociationEdge;
import ca.mcgill.cs.stg.jetuml.graph.edges.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.edges.SingleLabelEdge;

public class TestPropertyOrder
{
	private PropertyOrder aOrder;
	private Method addIndex;
	
	@Before
	public void setup() throws Exception
	{
		Constructor<PropertyOrder> constructor = PropertyOrder.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		aOrder = constructor.newInstance();
		addIndex = PropertyOrder.class.getDeclaredMethod("addIndex", Class.class, String.class, int.class);
		addIndex.setAccessible(true);
	}
	
	private void addIndex(Class<?> pClass, String pProperty, int pValue) throws Exception
	{
		addIndex.invoke(aOrder, pClass, pProperty, new Integer(pValue));
	}
	
	
	@Test
	public void testEmpty()
	{
		assertEquals(0, aOrder.getIndex(AggregationEdge.class, ""));
	}
	
	@Test
	public void testFirstLevelQuery() throws Exception
	{
		addIndex(AggregationEdge.class, "type", 28);
		assertEquals( 28, aOrder.getIndex(AggregationEdge.class, "type"));
		addIndex(AssociationEdge.class, "directionality", 2);
		assertEquals( 2, aOrder.getIndex(AssociationEdge.class, "directionality"));
		assertEquals( 0, aOrder.getIndex(AssociationEdge.class, "foo"));
	}
	
	@Test
	public void testSecondLevelQuery() throws Exception
	{
		addIndex(ClassRelationshipEdge.class, "endLabel", 28);
		assertEquals( 28, aOrder.getIndex(ClassRelationshipEdge.class, "endLabel"));
		assertEquals( 28, aOrder.getIndex(AssociationEdge.class, "endLabel"));
	}
	
	@Test
	public void testThirdLevelQuery() throws Exception
	{
		addIndex(SingleLabelEdge.class, "middleLabel", 28);
		assertEquals( 28, aOrder.getIndex(SingleLabelEdge.class, "middleLabel"));
		assertEquals( 28, aOrder.getIndex(ClassRelationshipEdge.class, "middleLabel"));
		assertEquals( 28, aOrder.getIndex(AssociationEdge.class, "middleLabel"));
	}
}
