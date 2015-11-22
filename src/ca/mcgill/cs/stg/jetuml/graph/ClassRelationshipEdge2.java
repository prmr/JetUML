/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

/**
 *  An edge that is shaped like a line with up to 
 *  three segments and with up to three labels and optional
 *  directions.
 */
public abstract class ClassRelationshipEdge2 extends SegmentedLabeledEdge
{	
	private String aStartLabel = "";
	private String aMiddleLabel = "";
	private String aEndLabel = "";
	
	@Override
	protected String obtainStartLabel()
	{
		return aStartLabel;
	}
	
	@Override
	protected String obtainMiddleLabel()
	{
		return aMiddleLabel;
	}
	
	@Override
	protected String obtainEndLabel()
	{
		return aEndLabel;
	}
	
	/**
	 * @param pLabel The new start label.
	 */
	public void setStartLabel(String pLabel)
	{
		aStartLabel = pLabel;
	}
	
	/**
	 * @param pLabel The new middle label.
	 */
	public void setMiddleLabel(String pLabel)
	{
		aMiddleLabel = pLabel;
	}
	
	/**
	 * @param pLabel The new end label.
	 */
	public void setEndLabel(String pLabel)
	{
		aEndLabel = pLabel;
	}
	
	/**
	 * @return The start label.
	 */
	public String getStartLabel()
	{
		return aStartLabel;
	}
	
	/**
	 * @return The middle label.
	 */
	public String getMiddleLabel()
	{
		return aMiddleLabel;
	}
	
	/**
	 * @return The middle label.
	 */
	public String getEndLabel()
	{
		return aEndLabel;
	}
}
