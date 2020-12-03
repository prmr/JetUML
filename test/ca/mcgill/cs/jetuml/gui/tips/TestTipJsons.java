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

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test Class to check the formatting of the tip jsons (data testing).
 */
public class TestTipJsons {

	private static final String TIPS_JSONS_DIR = RESOURCES.getString("tips.jsons.directory");
	private static String TIP_FILE_PATH_FORMAT;
	
	@BeforeAll
	public static void setupClass() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		TIP_FILE_PATH_FORMAT = getTipFilePathFormatString();
	}
	
	@Test
	public void testTipJsons_testCorrectNumberOfTips() throws URISyntaxException
	{
		File dir = getTipJsonsDirectoryAsFile();
        int numTipFiles = dir.listFiles().length;
		assertEquals(NUM_TIPS, numTipFiles);
	}
	
	@Test
	public void testTipJsons_atLeastTwoTips() throws URISyntaxException
	{
		File dir = getTipJsonsDirectoryAsFile();
        int numTipFiles = dir.listFiles().length;
		assertTrue(numTipFiles >= 2);
	}
	
	@Test 
	public void testTipJsons_testAllTipIdsInRangeOpenableAsInputStream()
	{
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			try(InputStream inputStream = TestTipJsons.class.getResourceAsStream(String.format(TIP_FILE_PATH_FORMAT, id)))
			{
				assertTrue(inputStream != null);
			}
			catch( IOException e )
			{
				fail();
			}
		}
	}
	
	@Test
	public void testTipJsons_testTipsCanBeOpenedAsJsonObjects() throws IOException
	{
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			JSONObject tip = loadTipAsJsonObject(id);
			assertTrue(tip != null);
		}
	}
	
	@Test
	public void testTipJsons_testTipsHaveTwoFieldsOnly() throws IOException
	{
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			JSONObject jObj = loadTipAsJsonObject(id);
			assertEquals(jObj.length(), 2);
		}
	}
	
	@Test
	public void testTipJsons_testTipTitleIsWellFormatted() throws IOException
	{
		assertTrue(tipsAllHaveField(TipFieldName.TITLE));
		
		for (int id = 1; id<= NUM_TIPS; id++)
		{
			JSONObject jObj = loadTipAsJsonObject(id);
			Object title = jObj.get(TipFieldName.TITLE.asString()); 
			assertTrue(title instanceof String);
		}
	}
	
	@Test 
	public void testTipJsons_testTipContentsAreWellFormatted() throws IOException
	{
		assertTrue(tipsAllHaveField(TipFieldName.CONTENT));
		
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			JSONObject jObj = loadTipAsJsonObject(id);
			Object obj = jObj.get(TipFieldName.CONTENT.asString());
			assertTrue(obj instanceof JSONArray);
			JSONArray jArr = (JSONArray) obj;
			for(Object contentElement : jArr)
			{
				assertTrue(contentElement instanceof JSONObject);
				JSONObject contentElementJsonObj = (JSONObject) contentElement;
				assertEquals(contentElementJsonObj.length(), 1);
				
				Set<String> tipMediaSet = contentElementJsonObj.keySet();
				String tipMediaName = (String)tipMediaSet.toArray()[0];
				assertTrue(contentElementJsonObj.get(tipMediaName) instanceof String);
				try
				{
					Media.valueOf(tipMediaName.toUpperCase());
				}
				catch(IllegalArgumentException e)
				{
					fail();
				}
			}
		}
	}
	
	private static boolean tipsAllHaveField(TipFieldName pTipFieldName) throws IOException
	{
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			JSONObject jObj = loadTipAsJsonObject(id);
			if(!jObj.has(pTipFieldName.asString()))
			{
				return false;
			}
		}
		return true;
	}
	
	private static JSONObject loadTipAsJsonObject(int pId) throws IOException
	{
		try( InputStream inputStream = TipLoader.class.getResourceAsStream(String.format(TIP_FILE_PATH_FORMAT, pId)))
		{
			JSONTokener jTok = new JSONTokener(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			JSONObject jObj = new JSONObject(jTok);
			return jObj;
		}
	}
	
	private static String getTipFilePathFormatString() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field field = TipLoader.class.getDeclaredField("TIP_FILE_PATH_FORMAT");
		field.setAccessible(true);
		return (String) field.get(null);
	}
	
	private static File getTipJsonsDirectoryAsFile() throws URISyntaxException 
	{
		URI uri = TestTipLoader.class.getResource(TIPS_JSONS_DIR).toURI();
        Path tipsDirPath = Paths.get(uri);
        File dir = tipsDirPath.toFile();
		return dir;
	}
}
