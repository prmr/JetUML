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
package ca.mcgill.cs.jetuml.gui.tips;

/**
 * Tip element represented as a Media/content pair.
 */
final class TipElement 
{
	private final Media aMedia;
	private final String aContent;
	
	/**
	 * @param pMedia Media of the tip element
	 * @param pContent content that will be displayed by the tip (image name with file
	 * 		  extension if the Media is IMAGE). 
	 * @pre pMedia != null && pContent != null
	 */
	TipElement(Media pMedia, String pContent)
	{
		assert pMedia != null && pContent != null;
		aMedia = pMedia;
		aContent = pContent;
	}
	
	/**
	 * @return String containing the tip content
	 */
	public String getContent()
	{
		return aContent;
	}
	
	/**
	 * @return Media type of the TipElement
	 */
	public Media getMedia()
	{
		return aMedia;
	}
}
