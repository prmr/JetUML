/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package org.jetuml.diagram;

import static org.jetuml.testutils.CollectionAssertions.assertThat;
import static org.jetuml.testutils.CollectionAssertions.extract;
import static org.jetuml.testutils.CollectionAssertions.hasElementsEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

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
		aProperties.add(PropertyName.AGGREGATION_TYPE, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get(PropertyName.AGGREGATION_TYPE);
		assertEquals(PropertyName.AGGREGATION_TYPE, prop.name());
		assertEquals("", prop.get());
	}
	
	@Test
	public void testAddTwo()
	{
		aProperties.add(PropertyName.AGGREGATION_TYPE, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertEquals(1, size());
		Property prop = aProperties.get(PropertyName.AGGREGATION_TYPE);
		assertSame(PropertyName.AGGREGATION_TYPE, prop.name());
		assertEquals("", prop.get());
		
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue + "X", val -> aStub.aValue = (String) val + "X");
		assertEquals(2, size());
		prop = aProperties.get(PropertyName.ATTRIBUTES);
		assertSame(PropertyName.ATTRIBUTES, prop.name());
		assertEquals("X", prop.get());
	}
	
	@Test
	public void testAddAt0()
	{
		aProperties.addAt(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertEquals(1, size());
		Property prop = aProperties.iterator().next();
		assertEquals(PropertyName.ATTRIBUTES, prop.name());
		assertEquals("", prop.get());
	}
	
	@Test
	public void testAddAt0of2()
	{
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt(PropertyName.CONTENTS, () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertThat(extract(getProperties(), Property::name), hasElementsEqualTo, PropertyName.CONTENTS, PropertyName.ATTRIBUTES);
	}
	
	@Test
	public void testAddAt1of2()
	{
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt(PropertyName.CONTENTS, () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		assertThat(extract(getProperties(), Property::name), hasElementsEqualTo, PropertyName.ATTRIBUTES, PropertyName.CONTENTS);
	}
	
	@Test
	public void testAddAt0of3()
	{
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add(PropertyName.CONTENTS, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt(PropertyName.DIRECTIONALITY, () -> aStub.aValue, val -> aStub.aValue = (String) val, 0);
		assertThat(extract(getProperties(), Property::name), hasElementsEqualTo, PropertyName.DIRECTIONALITY, PropertyName.ATTRIBUTES, PropertyName.CONTENTS);
	}
	
	@Test
	public void testAddAt1of3()
	{
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add(PropertyName.CONTENTS, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt(PropertyName.DIRECTIONALITY, () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		assertThat(extract(getProperties(), Property::name), hasElementsEqualTo, PropertyName.ATTRIBUTES, PropertyName.DIRECTIONALITY, PropertyName.CONTENTS);
	}
	
	@Test
	public void testAddAt2of3()
	{
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add(PropertyName.CONTENTS, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt(PropertyName.DIRECTIONALITY, () -> aStub.aValue, val -> aStub.aValue = (String) val, 2);
		assertThat(extract(getProperties(), Property::name), hasElementsEqualTo, PropertyName.ATTRIBUTES, PropertyName.CONTENTS, PropertyName.DIRECTIONALITY);
	}
	
	@Test
	public void testAddAt1of3AndSome()
	{
		aProperties.add(PropertyName.ATTRIBUTES, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.add(PropertyName.CONTENTS, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		aProperties.addAt(PropertyName.DIRECTIONALITY, () -> aStub.aValue, val -> aStub.aValue = (String) val, 1);
		aProperties.add(PropertyName.END_LABEL, () -> aStub.aValue, val -> aStub.aValue = (String) val);
		assertThat(extract(getProperties(), Property::name), hasElementsEqualTo, PropertyName.ATTRIBUTES, PropertyName.DIRECTIONALITY, PropertyName.CONTENTS, PropertyName.END_LABEL);
	}
	
	private int size()
	{
		int size = 0;
		for(Iterator<Property> iterator = aProperties.iterator(); iterator.hasNext();)
		{
			iterator.next();
			size++;
		}
		return size;
	}

}
