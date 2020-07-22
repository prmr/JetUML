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
package ca.mcgill.cs.jetuml.diagram;

import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.assertThat;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.extract;
import static ca.mcgill.cs.jetuml.testutils.CollectionAssertions.hasElementsEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestProperties
{
	static class Stub { String aValue = ""; }
	private Stub aStub;
	private Properties aProperties;
	
	@BeforeEach
	public void setup()
	{
		aStub = new Stub();
		aProperties = new Properties();
	}
	
	/* Convenience accessor */
	private List<Property> getProperties()
	{
		return StreamSupport
			.stream(aProperties.spliterator(), false)
			.collect(Collectors.toList());
	}
	
	@Test
	public void testEmpty()
	{
		assertFalse(aProperties.iterator().hasNext());
	}
	
	@Test
	public void testAddOne()
	{
		aProperties.add("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddTwo()
	{
		aProperties.add("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
		
		aProperties.add("test2", () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "X");
		assertEquals(2, size());
		prop = aProperties.get("test2");
		assertEquals("test2", prop.getName());
		assertEquals("X", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddTwiceSame()
	{
		aProperties.add("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test", () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "X");
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddInvisible()
	{
		aProperties.addInvisible("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertFalse(prop.isVisible());
	}
	
	@Test
	public void testAddInvisibleTwice()
	{
		aProperties.addInvisible("test", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addInvisible("test", () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "Y");
		assertEquals(1, size());
		Property prop = aProperties.get("test");
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertFalse(prop.isVisible());
	}
	
	@Test
	public void testAddVisibleInvisible()
	{
		aProperties.add("visible", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addInvisible("invisible", () -> "INVISIBLE", val -> {});
		assertEquals(2, size());
		Property prop = aProperties.get("visible");
		assertEquals("visible", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
		prop = aProperties.get("invisible");
		assertEquals("invisible", prop.getName());
		assertEquals("INVISIBLE", prop.get());
		assertFalse(prop.isVisible());
	}
	
	@Test
	public void testAddAt0()
	{
		aProperties.addAt("test", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertEquals(1, size());
		Property prop = aProperties.iterator().next();
		assertEquals("test", prop.getName());
		assertEquals("", prop.get());
		assertTrue(prop.isVisible());
	}
	
	@Test
	public void testAddAt0of2()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertThat(extract(getProperties(), Property::getName), hasElementsEqualTo, "test2", "test1");
	}
	
	@Test
	public void testAddAt1of2()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		assertThat(extract(getProperties(), Property::getName), hasElementsEqualTo, "test1", "test2");
	}
	
	@Test
	public void testAddAt0of3()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertThat(extract(getProperties(), Property::getName), hasElementsEqualTo, "test3", "test1", "test2");
	}
	
	@Test
	public void testAddAt1of3()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		assertThat(extract(getProperties(), Property::getName), hasElementsEqualTo, "test1", "test3", "test2");
	}
	
	@Test
	public void testAddAt2of3()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 2);
		assertThat(extract(getProperties(), Property::getName), hasElementsEqualTo, "test1", "test2", "test3");
	}
	
	@Test
	public void testAddAt1of3AndSome()
	{
		aProperties.add("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add("test2", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt("test3", () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		aProperties.add("test4", () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertThat(extract(getProperties(), Property::getName), hasElementsEqualTo, "test1", "test3", "test2", "test4");
	}
	
	@Test
	public void testAddAtTwice()
	{
		aProperties.addAt("test1", () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		aProperties.addAt("test1", () -> "X", val -> aStub.aValue = "Y", 0);
		assertEquals(1, size());
		Property prop = aProperties.get("test1");
		assertEquals("test1", prop.getName());
		assertEquals("", prop.get());
	}
	
	private int size()
	{
		int size = 0;
		for (Iterator<Property> iterator = aProperties.iterator(); iterator.hasNext();)
		{
			iterator.next();
			size++;
		}
		return size;
	}

}
