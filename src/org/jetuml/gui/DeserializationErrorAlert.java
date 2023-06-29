/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.gui;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.IOException;

import org.jetuml.persistence.DeserializationException;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Alert dialog specialized for displaying the various errors 
 * that can happen when opening a file.
 */
public class DeserializationErrorAlert extends Alert
{	
	private static final String ROOT_RESOURCE_KEY_SUFFIX = "error.open_file_";
	private static final String ROOT_RESOURCE_KEY_SUFFIX_IO = "io";
	private static final String DETAILS_SUFFIX = "_details";
	
	/**
	 * @param pException The exception received when opening a file.
	 */
	public DeserializationErrorAlert(Exception pException)
	{
		super(AlertType.ERROR, "", ButtonType.OK);
		assert pException instanceof IOException || pException instanceof DeserializationException;
		String keySuffix = determineResourceKeyPrefix(pException);
		setTitle(RESOURCES.getString("alert.error.title"));
		setHeaderText(RESOURCES.getString(ROOT_RESOURCE_KEY_SUFFIX + keySuffix));
		
		StringBuilder contentText = new StringBuilder(RESOURCES.getString(ROOT_RESOURCE_KEY_SUFFIX + keySuffix + DETAILS_SUFFIX));
		contentText.append(" ").append(RESOURCES.getString("alert.error.details")).append(" ").append(pException.getMessage());
		setContentText(contentText.toString());
	}
	
	/*
	 * Returns the key prefix in application resources for the header text and
	 * details of the error. The suffix for the header text is the empty string,
	 * and the suffix for the detail is "details"
	 */
	private static String determineResourceKeyPrefix(Exception pException)
	{
		if( pException instanceof IOException )
		{
			return ROOT_RESOURCE_KEY_SUFFIX_IO;
		}
		else
		{
			return ((DeserializationException)pException).category().toString().toLowerCase();
		}
	}
}
