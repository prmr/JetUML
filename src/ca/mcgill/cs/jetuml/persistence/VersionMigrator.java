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
import java.util.ArrayList;
import java.util.List;

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
 * * DependencyEdge elements that have the same start and end node are removed
 * * The startLabel and endLabel property of DependencyEdges are removed
 */
public final class VersionMigrator
{
	private boolean aMigrated;
	
	/**
	 * Creates a new version migrator. Can be reused.
	 */
	public VersionMigrator() {}

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
			new VersionMigrator().migrate(new JSONObject(in.readLine()));
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
	public VersionedDiagram migrate(JSONObject pDiagram)
	{
		aMigrated = false;
		
		// JSONObject to JSONObject conversions
		convertPackageNodeToPackageDescriptionNode(pDiagram);
		removeSelfDependencies(pDiagram);
		
		Version version = Version.parse(pDiagram.getString("version"));
		return new VersionedDiagram(JsonDecoder.decode(pDiagram), version, aMigrated);
	}
	
	private void convertPackageNodeToPackageDescriptionNode(JSONObject pDiagram)
	{
		JSONArray nodes = pDiagram.getJSONArray("nodes");
		for( int i = 0; i < nodes.length(); i++ )
		{
			JSONObject object = nodes.getJSONObject(i);
			if( object.getString("type").equals("PackageNode") && !object.has("children") && object.has("contents"))
			{
				object.put("type", "PackageDescriptionNode");
				aMigrated = true;
			}
		}
	}
	
	private void removeSelfDependencies(JSONObject pDiagram)
	{
		JSONArray edges = pDiagram.getJSONArray("edges");
		List<JSONObject> newEdges = new ArrayList<>();
		for( int i = 0; i < edges.length(); i++ )
		{
			JSONObject object = edges.getJSONObject(i);
			if( object.getString("type").equals("DependencyEdge") && object.getInt("start") == object.getInt("end"))
			{
				aMigrated = true; // We don't add the dependency, essentially removing it.
			}
			else
			{
				newEdges.add(object);
			}
		}
		pDiagram.put("edges", new JSONArray(newEdges));
	}
}
