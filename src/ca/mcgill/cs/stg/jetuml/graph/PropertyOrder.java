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

import java.util.HashMap;

/**
 * Singleton object that can return the presentation
 * sequence index number at which an object property should 
 * be displayed. The order does not have to be total.
 * 
 * @author Martin P. Robillard
 *
 */
public final class PropertyOrder
{
	private static final PropertyOrder INSTANCE = new PropertyOrder();
	
	static
	{
		INSTANCE.addIndex(ClassRelationshipEdge.class, "startLabel", 1);
		INSTANCE.addIndex(ClassRelationshipEdge.class, "middleLabel", 2);
		INSTANCE.addIndex(ClassRelationshipEdge.class, "endLabel", 3);
		INSTANCE.addIndex(CallEdge.class, "middleLabel", 1);
		INSTANCE.addIndex(ClassNode.class, "name", 1);
		INSTANCE.addIndex(ClassNode.class, "attributes", 2);
		INSTANCE.addIndex(ClassNode.class, "methods", 3);
		INSTANCE.addIndex(InterfaceNode.class, "name", 1);
		INSTANCE.addIndex(InterfaceNode.class, "methods", 2);
		INSTANCE.addIndex(PackageNode.class, "name", 1);
		INSTANCE.addIndex(PackageNode.class, "contents", 2);
	}
	
	private HashMap<Class<?>, HashMap<String, Integer>> aProperties = new HashMap<>();
	
	private PropertyOrder()
	{}
	
	/**
	 * @return The singleton instance of PropertyOrder
	 */
	public static PropertyOrder getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * Return the sequence index of pProperty of class pClass. Always returns 0 if the 
	 * sequence index is not specified for a property. If pProperty is not found in pClass, 
	 * the superclasses are searched.
	 * @param pClass The class supporting a property.
	 * @param pProperty The property supported.
	 * @return The sequence order for the property.
	 */
	public int getIndex( Class<?> pClass, String pProperty )
	{
		if( pClass == null )
		{
			return 0;
		}
		HashMap<String, Integer> properties = aProperties.get(pClass);
		if( properties != null )
		{
			if( properties.containsKey(pProperty))
			{
				return properties.get(pProperty);
			}
			else
			{
				return getIndex( pClass.getSuperclass(), pProperty);
			}
		}
		else
		{
			return getIndex( pClass.getSuperclass(), pProperty);
		}
	}
	
	private void addIndex( Class<?> pClass, String pProperty, int pIndex )
	{
		HashMap<String, Integer> properties = aProperties.get(pClass);
		if( properties == null )
		{
			properties = new HashMap<String, Integer>();
			aProperties.put(pClass, properties);
		}
		properties.put(pProperty, pIndex);
	}
}
