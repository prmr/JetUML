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
 *  An edge that that represents a UML generalization (inheritance
 *  or implementation).
 */
public final class GeneralizationEdge extends AbstractEdge
{
	/**
	 * Type of UML generalization relation.
	 */
	public enum Type 
	{Inheritance, Implementation}
	
	private Type aType = Type.Inheritance;
	
	/**
	 * Creates a generalization edge by specifying its type.
	 * 
	 * @param pType the type of generalization
	 */
	public GeneralizationEdge( Type pType )
	{
		aType = pType;
	}
	
	/**
	 * Creates an inheritance edge.
	 */
	public GeneralizationEdge()
	{}
	
	/**
	 * @return The type of generalization.
	 */
	public Type getType()
	{
		return aType;
	}
	
	/**
	 * Sets the type of generalization.
	 * @param pType The desired type of generalization
	 */
	public void setType(Type pType)
	{
		aType = pType;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("Generalization Type", () -> aType, pType -> aType = Type.valueOf((String) pType));
	}
}
