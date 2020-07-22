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
 *  An edge that that represents a UML association, with optional 
 *  labels and directionality.
 */
public final class AssociationEdge extends ThreeLabelEdge
{
	/**
	 * Possible directionalities for an association.
	 */
//	public enum Directionality 
//	{None, End, Both}
	
	public enum Directionality 
	{ Unspecified, Unidirectional, Bidirectional }
	
	private Directionality aDirectionality = Directionality.Unspecified;
	
	/**
	 * @param pDirectionality The desired directionality.
	 */
	public void setDirectionality( Directionality pDirectionality )
	{
		aDirectionality = pDirectionality;
	}
	
	/**
	 * @return The directionality of this association.
	 */
	public Directionality getDirectionality()
	{
		return aDirectionality;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("directionality", () -> aDirectionality, 
				pDirectionality -> aDirectionality = Directionality.valueOf((String)pDirectionality ));
	}
}
