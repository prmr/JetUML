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

/**
 * A package description node in a UML diagram.
 */
public final class PackageDescriptionNode extends AbstractPackageNode
{
	private String aContents = "";
	
	/**
     * Sets the contents property value.
     * @param pContents the contents of this class
     * @pre pContents != null
	 */
	public void setContents(String pContents)
	{
		assert pContents != null;
		aContents = pContents;
	}
	
	/**
     * Gets the contents property value.
     * @return the contents of this class
	 */
	public String getContents()
	{
		return aContents;
	}

	@Override
	public PackageDescriptionNode clone()
	{
		return (PackageDescriptionNode) super.clone();
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("contents", () -> aContents, pContents -> aContents = (String)pContents);
	}
}
