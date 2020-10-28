package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ca.mcgill.cs.jetuml.gui.tips.Media;
import ca.mcgill.cs.jetuml.gui.tips.TipFieldName;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
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
	public void testTipJsons_testCorrectNumberOfTips() throws IOException, URISyntaxException
	{
		File dir = getTipJsonsDirectoryAsFile();
        int numTipFiles = dir.listFiles().length;
		assertEquals(NUM_TIPS, numTipFiles);
	}
	
	@Test
	public void testTipJsons_atLeastTwoTips() throws IOException, URISyntaxException
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
			InputStream inputStream = TestTipJsons.class.getResourceAsStream(String.format(TIP_FILE_PATH_FORMAT, id));
			assertTrue(inputStream != null);
		}
	}
	
	@Test
	public void testTipJsons_testTipsCanBeOpenedAsJsonObjects()
	{
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			JSONObject tip = loadTipAsJsonObject(id);
			assertTrue(tip != null);
		}
	}
	
	@Test
	public void testTipJsons_testTipsHaveTwoFieldsOnly()
	{
		for(int id = 1; id <= NUM_TIPS; id++)
		{
			JSONObject jObj = loadTipAsJsonObject(id);
			assertEquals(jObj.length(), 2);
		}
	}
	
	@Test
	public void testTipJsons_testTipTitleIsWellFormatted()
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
	public void testTipJsons_testTipContentsAreWellFormatted()
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
	
	private static boolean tipsAllHaveField(TipFieldName pTipFieldName)
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
	
	private static JSONObject loadTipAsJsonObject(int pId)
	{
		String tipFilePath = String.format(TIP_FILE_PATH_FORMAT, pId);
		InputStream inputStream = TipLoader.class.getResourceAsStream(tipFilePath);
		JSONTokener jTok = new JSONTokener(new InputStreamReader(inputStream));
		JSONObject jObj = new JSONObject(jTok);
		return jObj;
	}
	
	private static String getTipFilePathFormatString() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field field = TipLoader.class.getDeclaredField("TIP_FILE_PATH_FORMAT");
		field.setAccessible(true);
		return (String) field.get(null);
	}
	
	private static File getTipJsonsDirectoryAsFile() throws URISyntaxException {
		URI uri = TestTipLoader.class.getResource(TIPS_JSONS_DIR).toURI();
        Path tipsDirPath = Paths.get(uri);
        File dir = tipsDirPath.toFile();
		return dir;
	}
}
