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
 * An edge that that represents a UML dependency with an optional label.
 * A DependencyEdge can be either unidirectional (from start to end),
 * or bidirectional.
 */
public final class DependencyEdge extends SingleLabelEdge
{
	/**
	 * Possible directionalities for an association.
	 */
	public enum Directionality
	{
		Unidirectional, Bidirectional
	}

	private Directionality aDirectionality = Directionality.Unidirectional;

	/**
	 * @param pDirectionality The desired directionality.
	 * @pre pDirectionality != null
	 */
	public void setDirectionality(Directionality pDirectionality)
	{
		assert pDirectionality != null;
		aDirectionality = pDirectionality;
	}

	/**
	 * @return The directionality of this edge.
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
				directionality -> aDirectionality = Directionality.valueOf((String) directionality));
	}
}
