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
package ca.mcgill.cs.jetuml.diagram.edges;

/**
 *  An edge that that represents a UML dependency
 *  between use cases.
 */
public final class UseCaseDependencyEdge extends AbstractEdge
{
	/**
	 * Type of use case dependency with corresponding edge label.
	 */
	public enum Type 
	{
		None(""), Include("\u00ABinclude\u00BB"), Extend("\u00ABextend\u00BB");
		
		private final String aLabel;
		
		Type(String pLabel)
		{ aLabel = pLabel; }
		
		public String getLabel()
		{ return aLabel; }
	}
	
	private Type aType = Type.None;
	
	/**
	 * Creates a general dependency.
	 */
	public UseCaseDependencyEdge()
	{}
	
	/**
	 * Creates a typed dependency.
	 * @param pType The type of dependency.
	 */
	public UseCaseDependencyEdge(Type pType)
	{
		aType = pType;
	}
	
	/**
	 * @return The type of this dependency edge.
	 */
	public Type getType()
	{
		return aType;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("Dependency Type", () -> aType, pType -> aType = Type.valueOf((String)pType));
	}
}
