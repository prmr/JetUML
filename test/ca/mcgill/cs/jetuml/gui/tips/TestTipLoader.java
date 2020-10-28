package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.gui.tips.TipLoader.Tip;

public class TestTipLoader 
{
	private static final String TIP_TITLE_FIELD = TipFieldName.TITLE.asString();
	private static final String TIP_CONTENT_FIELD = TipFieldName.CONTENT.asString();
	private static final String TIP_CONTENT_TEXT_FIELD = Media.TEXT.name().toLowerCase();
	private static final String TIP_CONTENT_IMAGE_FIELD = Media.IMAGE.name().toLowerCase();
	
	private static JSONObject WELL_FORMATTED_TIP;
	private final static String WELL_FORMATTED_TIP_STRING = 
			  "{"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"},"
			+ 				 				   "{ \"" + TIP_CONTENT_IMAGE_FIELD + "\": \"image.png\"}] "
			+ "}";
	
	
	@BeforeAll
	public static void setupClass()
	{
		WELL_FORMATTED_TIP = new JSONObject(WELL_FORMATTED_TIP_STRING);
	}
	
	@Test
	public void testTipLoader_loadTipCanLoadTipFromCorrectId()
	{
		Tip tip = TipLoader.loadTip(1);
		assertTrue(tip != null);
	}
	
	@Test
	public void testTipLoader_loadTipCreatesTipsWithRightId()
	{
		Tip tip1 = TipLoader.loadTip(1);
		assertEquals(tip1.getId(), 1);
		Tip tip2 = TipLoader.loadTip(2);
		assertEquals(tip2.getId(), 2);
	}
	
	@Test 
	public void testTipLoader_getNextTipIdIncrementsForIdOne()
	{
		assertEquals(getNextTipId(1), 2); // Assuming there are at least 2 tips, 
										  // this is checked by TestTipJsons.java
	}
	
	@Test
	public void testTipLoader_getNextTipIdWrapsAround()
	{
		assertEquals(getNextTipId(NUM_TIPS), 1);
	}
	
	@Test 
	public void testTipLoader_getPreviousTipIdDecrementsForGreatestId()
	{
		assertEquals(getPreviousTipId(NUM_TIPS), NUM_TIPS - 1); // Assuming there are at least 2 tips, 
										  						// this is checked by TestTipJsons.java
	}
	
	@Test
	public void testTipLoader_getPreviousTipIdWrapsAround()
	{
		assertEquals(getPreviousTipId(1), NUM_TIPS);
	}
	
	@Test
	public void testTipLoader_getNextTipIsDifferent()
	{
		
	}
	
	@Test
	public void testTipConvertJSONObjectToTipElements_listHasRightSize()
	{
		List<TipElement> tipElements = convertJSONObjectToTipElements(WELL_FORMATTED_TIP);
		assertEquals(tipElements.size(), 2);
	}
	
	@Test
	public void testTipConvertJSONObjectToTipElements_elementsHaveRightMedia()
	{
		List<TipElement> tipElements = convertJSONObjectToTipElements(WELL_FORMATTED_TIP);
		TipElement tipElement1 = tipElements.get(0);
		TipElement tipElement2 = tipElements.get(1);
		
		assertEquals(tipElement1.getMedia(), Media.TEXT);
		assertEquals(tipElement2.getMedia(), Media.IMAGE);
	}
	
	@Test
	public void testTipConvertJSONObjectToTipElements_elementsHaveRightContent()
	{
		List<TipElement> tipElements = convertJSONObjectToTipElements(WELL_FORMATTED_TIP);
		TipElement tipElement1 = tipElements.get(0);
		TipElement tipElement2 = tipElements.get(1);
		
		assertEquals(tipElement1.getContent(), "sample text");
		assertEquals(tipElement2.getContent(), "image.png");
	}
	
	@SuppressWarnings("unchecked")
	private List<TipElement> convertJSONObjectToTipElements(JSONObject pTip)
	{
		try
		{
			Method method = TipLoader.Tip.class.getDeclaredMethod("convertJSONObjectToTipElements", JSONObject.class);
			method.setAccessible(true);
			return (List<TipElement>) method.invoke(null, pTip);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private int getNextTipId(int pId)
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("getNextTipId", int.class);
			method.setAccessible(true);
			return (int) method.invoke(null, pId);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return 0;
		}
	}
	
	private int getPreviousTipId(int pId)
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("getPreviousTipId", int.class);
			method.setAccessible(true);
			return (int) method.invoke(null, pId);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return 0;
		}
	}
}
