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

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A class to collect and manage the properties of a diagram element.
 * 
 * It is not possible to change a property once it's added to a Properties
 * object. Properties objects are intended to be constructed once, then queried only.
 * 
 * This class provides support for storing properties in a meaningful order. 
 * By default, this is the order of insertion. However, use of the method <code>addAt</code>
 * allows client code to insert a property at a specific index. Keeping properties in order
 * allows for uses such as displaying properties in a predictable order, for instance 
 * in GUI forms.
 */
public class Properties implements Iterable<Property>
{
	private final Map<PropertyName, Property> aProperties = new LinkedHashMap<>();
	
	/**
	 * Adds a property to the end of the list.
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @pre pPropertyName != null && pGetter != null && pSetter != null && !containsKey(pName)
	 */
	public void add(PropertyName pName, Supplier<Object> pGetter, Consumer<Object> pSetter)
	{
		assert pName != null && pGetter != null && pSetter != null && !aProperties.containsKey(pName);
		aProperties.put(pName, new Property(pName, pGetter, pSetter));
	}
	
	/**
	 * @param pName The name of the property to get.
	 * @return The property with pName.
	 * @pre pName != null && containsKey(pName)
	 */
	public Property get(PropertyName pName)
	{
		assert pName != null && aProperties.containsKey(pName);
		return aProperties.get(pName);
	}
	
	/**
	 * Inserts a property at the specified 0-based index, shifting all other 
	 * properties down by one. 
	 * 
	 * @param pName The name of the property.
	 * @param pGetter The getter for this property.
	 * @param pSetter The setter for this property.
	 * @param pIndex Where to insert the property. Must be between 0 and size()-1, inclusive.
	 * @pre pPropertyName != null && pGetter != null && pSetter != null && !containsKey(pName)
	 * @pre pIndex >=0 && pIndex <= aProperties.size();
	 */
	public void addAt(PropertyName pName, Supplier<Object> pGetter, Consumer<Object> pSetter, int pIndex)
	{
		// Rebuilds the map in the proper iteration order
		assert pName != null && pGetter != null && pSetter != null && !aProperties.containsKey(pName);
		assert pIndex >=0 && pIndex <= aProperties.size();
		List<Property> properties = aProperties.values().stream().collect(toList());
		properties.add(pIndex, new Property(pName, pGetter, pSetter));
		aProperties.clear();
		properties.stream().forEach(property -> aProperties.put(property.name(), property));
	}

	@Override
	public Iterator<Property> iterator()
	{
		return Collections.unmodifiableCollection(aProperties.values()).iterator();
	}
}
