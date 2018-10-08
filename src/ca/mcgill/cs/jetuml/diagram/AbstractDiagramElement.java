/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram;

/**
 * Base class for nodes and edges. Responsible for storing the single
 * Properties object used to describe the properties of this element.
 * There is only a single Properties object associated with a DiagramElement, 
 * used to represent the element's properties through its life-cycle.
 */
public abstract class AbstractDiagramElement implements DiagramElement
{
	private Properties aProperties;
	
	/**
	 * Initializes the properties for this object.
	 */
	protected AbstractDiagramElement()
	{
		buildProperties();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected DiagramElement clone()
	{
		try
		{
			AbstractDiagramElement clone = (AbstractDiagramElement) super.clone();
			clone.buildProperties();
			return clone;
		}
		catch(CloneNotSupportedException pException)
		{
			return null;
		}
	}
	
	@Override
	public final Properties properties()
	{
		return aProperties;
	}
	
	/**
	 * Builds the properties object associated with this object.
	 * Must be outside the constructor because of cloning.
	 * Subclasses should call super.buildProperties() before
	 * adding their own properties.
	 */
	protected void buildProperties()
	{
		aProperties = new Properties();
	}
}
