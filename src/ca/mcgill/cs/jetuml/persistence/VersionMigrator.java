/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.persistence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.application.Version;

/**
 * Utility class to migrate a pre-3.0 saved diagram to post 3.0.
 * 
 * Rules:
 * * A PackageNode with both content and children will lose its content
 * * A PackageNode with only content (no children) will be transformed into a PackageDescriptionNode
 */
public final class VersionMigrator
{
	private VersionMigrator() {}

	/**
	 * Currently for testing.
	 * @param pArgs Not used
	 * @throws IOException If there's a problem.
	 */
	public static void main(String[] pArgs) throws IOException
	{
		try( BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream("test.class.jet"), StandardCharsets.UTF_8)))
		{
			migrate(new JSONObject(in.readLine()));
		}
		catch( JSONException e )
		{
			throw new DeserializationException("Cannot decode the file", e);
		}
	}
	
	/**
	 * @param pDiagram The loaded diagram to migrate
	 * @return A migrated Diagram object.
	 */
	public static VersionedDiagram migrate(JSONObject pDiagram)
	{
		assert Version.parse(pDiagram.getString("version")).compareTo(Version.create(3, 0)) < 0;
		// JSONObject to JSONObject conversions
		convertPackageNodeToPackageDescriptionNode(pDiagram);
		return JsonDecoder.decode(pDiagram);
	}
	
	private static void convertPackageNodeToPackageDescriptionNode(JSONObject pDiagram)
	{
		JSONArray nodes = pDiagram.getJSONArray("nodes");
		for( int i = 0; i < nodes.length(); i++ )
		{
			JSONObject object = nodes.getJSONObject(i);
			if( object.getString("type").equals("PackageNode") && !object.has("children") )
			{
				object.put("type", "PackageDescriptionNode");
			}
		}
	}
}
