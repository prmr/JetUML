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

package ca.mcgill.cs.jetuml.diagram.nodes;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * A type that can represent either classes or interfaces. A type node 
 * always has at least methods.
 */
public abstract class TypeNode extends NamedNode
{
	private String aMethods = ""; 
	private Optional<Node> aContainer = Optional.empty();

	/**
     * Sets the methods property value.
     * @param pMethods the methods of this type node.
     * @pre pMethods != null;
	 */
	public void setMethods(String pMethods)
	{
		assert pMethods != null;
		aMethods = pMethods;
	}
	
	/**
     * Gets the methods property value.
     * @return the methods of this interface
	 */
	public String getMethods()
	{
		return aMethods;
	}
	
	/**
     * Gets the attributes property value.
     * By default, this is the empty string.
     * @return the attributes of this class
	 */
	public String getAttributes()
	{
		return "";
	}

	@Override
	public Node getParent()
	{
		assert hasParent();
		return aContainer.get();
	}

	@Override
	public void link(Node pNode)
	{
		assert pNode instanceof PackageNode && pNode != null;
		aContainer = Optional.of(pNode);
	}
	
	@Override
	public void unlink()
	{
		assert hasParent();
		aContainer = Optional.empty();
	}
	
	@Override
	public boolean requiresParent()
	{
		return false;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("methods", () -> aMethods, pMethods -> aMethods = (String)pMethods);
	}
	
	@Override
	public boolean hasParent()
	{
		return aContainer.isPresent();
	}
}
