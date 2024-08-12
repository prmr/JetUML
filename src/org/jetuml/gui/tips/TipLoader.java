/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.gui.tips;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jetuml.persistence.json.JsonObject;
import org.jetuml.persistence.json.JsonParser;

/**
 * Class that statically loads the tips.
 */
final class TipLoader
{
	public static final int NUM_TIPS = Integer.parseInt(RESOURCES.getString("tips.quantity"));

	private static final int BYTES_IN_KILOBYTE = 1024;
	private static final String TIP_FILE_PATH_FORMAT = RESOURCES.getString("tips.jsons.directory") + "/tip-%d.json";

	private TipLoader() {}

	/**
	 * Returns the tip associated with the given tip id.
	 * 
	 * @param pId id of the tip to return
	 * @return the tip with id pId.
	 * 
	 * @pre pId >= 1 && pId <= NUM_TIPS
	 */
	public static Tip loadTip(int pId)
	{
		assert pId >= 1 && pId <= NUM_TIPS;

		// Running the unit tests ensures that all resources can be correctly
		// loaded
		// in the tool.
		try (InputStream tipsInputStream = TipLoader.class
				.getResourceAsStream(String.format(TIP_FILE_PATH_FORMAT, pId)))
		{
			JsonObject jsonObject = JsonParser.parse(inputStreamToString(tipsInputStream));
			Tip tip = new Tip(pId, jsonObject);
			return tip;
		}
		catch (IOException e)
		{
			assert false;
			return null;
		}
	}

	private static String inputStreamToString(InputStream pStream) throws IOException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[BYTES_IN_KILOBYTE];
		for(int length; (length = pStream.read(buffer)) != -1; )
		{
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}

	/**
	 * A tip that contains TipElement instances.
	 */
	public static final class Tip
	{
		private final int aId;
		private final String aTitle;
		private final List<TipElement> aElements;
		private final List<TipCategory> aCategories;

		/**
		 * @param pId the id associated with the tip (number in the tip's file's
		 * name)
		 * @param pTip the JSONObject obtained from the file tip-pId.json, where
		 * pId is the same as the pId parameter.
		 */
		private Tip(int pId, JsonObject pTip)
		{

			aId = pId;
			aTitle = (String) pTip.get(TipFieldName.TITLE.asString());
			aElements = convertJsonObjectToTipElements(pTip);
			aCategories = convertJsonObjectToTipCategories(pTip);
		}

		/**
		 * @return the tip's id
		 */
		public int getId()
		{
			return aId;
		}

		/**
		 * @return the tip's title
		 */
		public String getTitle()
		{
			return aTitle;
		}

		/**
		 * @return List of TipElements contained in the Tip
		 */
		public List<TipElement> getElements()
		{
			return new ArrayList<>(aElements);
		}
		
		public List<TipCategory> getCategories()
		{
			return new ArrayList<>(aCategories);
		}

		/**
		 * @param pTip A JsonObject obtained from the JsonArray gotten by
		 * loading the tips.
		 */
		private static List<TipElement> convertJsonObjectToTipElements(JsonObject pTip)
		{
			List<TipElement> elements = new ArrayList<>();

			for(Object contentObject : pTip.getJsonArray(TipFieldName.CONTENT.asString()))
			{
				JsonObject contentJsonObject = (JsonObject) contentObject;
				Media media = discoverMediaUsed(contentJsonObject);
				elements.add(new TipElement(media, contentJsonObject.getString(media.name().toLowerCase())));
			}
			return elements;
		}
		
		private static List<TipCategory> convertJsonObjectToTipCategories(JsonObject pTip)
		{
			List<TipCategory> categories = new ArrayList<>();
			
			for(Object tagsObject : pTip.getJsonArray(TipFieldName.TAGS.asString()))
			{
				JsonObject tagsJsonObject = (JsonObject) tagsObject;
				TipCategory category = discoverCategory(tagsJsonObject);
				categories.add(category);
			}
			return categories;
		}
		
		/*
	     * A tip content contains one property whose name is any Media value
	     * in lower case: discover which one it is.
		 */
		private static Media discoverMediaUsed(JsonObject pTipContent)
		{
			for(Media media : Media.values())
			{
				if(pTipContent.hasProperty(media.name().toLowerCase()))
				{
					return media;
				}
			}
			assert false;
			return null;
		}
		
		/*
	     * A tip view contains one property whose name is any View value
	     * in lower case: discover which one it is.
		 */
		private static TipCategory discoverCategory(JsonObject pTipContent)
		{
			for(View view : View.values())
			{
				if(pTipContent.hasProperty(view.name().toLowerCase()))
				{
					return TipCategory.valueOf(pTipContent.getString(view.name().toLowerCase()).toUpperCase());
				}
			}
			assert false;
			return null;
		}
	}
}
