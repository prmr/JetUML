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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestPropertyName
{
	@Test
	public void test_externalName()
	{
		assertEquals("Aggregation Type", PropertyName.AGGREGATION_TYPE.external());
	}
	
	/*
	 * Tests that all properties have a visible name
	 */
	@Test
	public void test_visibleNames()
	{
		for( PropertyName propertyName : PropertyName.values() )
		{
			assertNotEquals("[Resource cannot be found]", propertyName.visible(), propertyName.toString());
		}
	}
}
