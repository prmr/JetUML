package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.gui.TipLoader.Tip;

public class TestTipLoader 
{
	
	private static final String TIP_ID_FIELD = RESOURCES.getString("tips.json.field.name.id");
	private static final String TIP_TITLE_FIELD = RESOURCES.getString("tips.json.field.name.title");
	private static final String TIP_CONTENT_FIELD = RESOURCES.getString("tips.json.field.name.content");
	private static final String TIP_CONTENT_TEXT_FIELD = RESOURCES.getString("tips.json.field.name.content.text");
	private static final String TIP_CONTENT_IMAGE_FIELD = RESOURCES.getString("tips.json.field.name.content.image");
	
	private static JSONObject WELL_FORMATTED_TIP;
	private final static String WELL_FORMATTED_TIP_STRING = 
			"{"
			+ " \"" + TIP_ID_FIELD + "\": 1,"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"},"
			+ 				 				   "{ \"" + TIP_CONTENT_IMAGE_FIELD + "\": \"image.png\"}] "
			+ "}";
	
	private static JSONObject INCORRECTLY_SPELLED_ID_FIELD_TIP;
	private final static String INCORRECTLY_SPELLED_ID_FIELD_TIP_STRING = 
			"{"
			+ " \"ip\": 1,"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	private static JSONObject STRING_ID_TIP;
	private final static String STRING_ID_TIP_STRING = 
			"{"
			+ " \"" + TIP_ID_FIELD + "\": hello,"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	private static JSONObject INCORRECTLY_SPELLED_TITLE_FIELD_TIP;
	private final static String INCORRECTLY_SPELLED_TITLE_FIELD_TIP_STRING = 
			"{"
			+ " \"" + TIP_ID_FIELD + "\": 1,"
			+ " \"tie\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	
	private static JSONObject INT_TITLE_TIP;
	private final static String INT_TITLE_TIP_STRING = 
			"{"
			+ " \"" + TIP_ID_FIELD + "\": 1,"
			+ " \"title\": 5,"
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	private static JSONObject INCORRECTLY_SPELLED_CONTENT_FIELD_TIP;
	private final static String INCORRECTLY_SPELLED_CONTENT_FIELD_TIP_STRING = 
			"{"
			+ " \"" + TIP_ID_FIELD + "\": 1,"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"abc\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	private static JSONObject INCORRECTLY_FORMATED_CONTENT_TIP;
	private final static String INCORRECTLY_FORMATTED_CONTENT_TIP_STRING = 
			"{"
			+ " \"" + TIP_ID_FIELD + "\": 1,"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"},"
					   + "{ \"abc\": \"defg\" } ] "
			+ "}";
	
	
	private static JSONObject UNEXPECTED_FIELD_TIP;
	private final static String UNEXPECTED_FIELD_TIP_STRING =
			"{"
			+ " \"" + TIP_ID_FIELD + "\": 1,"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"newField\": this is the new field,"
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	private static JSONObject MISSING_FIELD_TIP;
	private final static String MISSING_FIELD_TIP_STRING =
			"{"
			+ " \"" + TIP_TITLE_FIELD + "\": \"First Tip\","
			+ " \"" + TIP_CONTENT_FIELD + "\": [{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"}] "
			+ "}";
	
	private static JSONArray EMPTY_CONTENT;
	private final static String EMPTY_CONTENT_STRING = "[]";
	
	private static JSONArray EMPTY_JSON_CONTENT;
	private final static String EMPTY_JSON_CONTENT_STRING = "[{}]";
	
	private static JSONArray UNEXPECTED_FIELD_CONTENT;
	private final static String UNEXPECTED_FIELD_CONTENT_STRING = "[{ \"abc\": \"sample text\"}] ";
	
	private static JSONArray INT_TEXT_ELEMENT_CONTENT;
	private final static String INT_TEXT_ELEMENT_CONTENT_STRING = "[{ \"" + TIP_CONTENT_TEXT_FIELD + "\": 1}]";
	
	private static JSONArray INT_IMAGE_ELEMENT_CONTENT;
	private final static String INT_IMAGE_ELEMENT_CONTENT_STRING = "[{ \"" + TIP_CONTENT_IMAGE_FIELD + "\": 1}]";
	
	private static JSONArray ELEMENT_WITH_TWO_FIELDS_CONTENT;
	private final static String ELEMENT_WITH_TWO_FIELDS_CONTENT_STRING =
			"[{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\","
			+  "\"" + TIP_CONTENT_IMAGE_FIELD + "\": \"image.png\"}] ";
	
	
	private static JSONArray TWO_WELL_FORMATTED_ELEMENTS_CONTENT;
	private final static String TWO_WELL_FORMATTED_ELEMENTS_CONTENT_STRING =
			"[{ \"" + TIP_CONTENT_TEXT_FIELD + "\": \"sample text\"},"
			+"{ \"" + TIP_CONTENT_IMAGE_FIELD + "\": \"image.png\"}] ";
	
	private static List<Tip> TIPS;
	
	
	@SuppressWarnings("unchecked")
	@BeforeAll
	public static void setupClass() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		WELL_FORMATTED_TIP = new JSONObject(WELL_FORMATTED_TIP_STRING);
		INCORRECTLY_SPELLED_ID_FIELD_TIP = new JSONObject(INCORRECTLY_SPELLED_ID_FIELD_TIP_STRING);
		STRING_ID_TIP = new JSONObject(STRING_ID_TIP_STRING);
		INCORRECTLY_SPELLED_TITLE_FIELD_TIP = new JSONObject(INCORRECTLY_SPELLED_TITLE_FIELD_TIP_STRING);
		INT_TITLE_TIP = new JSONObject(INT_TITLE_TIP_STRING);
		INCORRECTLY_SPELLED_CONTENT_FIELD_TIP = new JSONObject(INCORRECTLY_SPELLED_CONTENT_FIELD_TIP_STRING);
		INCORRECTLY_FORMATED_CONTENT_TIP = new JSONObject(INCORRECTLY_FORMATTED_CONTENT_TIP_STRING);
		UNEXPECTED_FIELD_TIP = new JSONObject(UNEXPECTED_FIELD_TIP_STRING);
		MISSING_FIELD_TIP = new JSONObject(MISSING_FIELD_TIP_STRING);
		
		JSONTokener jTok = new JSONTokener(EMPTY_CONTENT_STRING);
		EMPTY_CONTENT = new JSONArray(jTok);
		
		jTok = new JSONTokener(EMPTY_JSON_CONTENT_STRING);
		EMPTY_JSON_CONTENT = new JSONArray(jTok);
		
		jTok = new JSONTokener(UNEXPECTED_FIELD_CONTENT_STRING);
		UNEXPECTED_FIELD_CONTENT = new JSONArray(jTok);
		
		jTok = new JSONTokener(INT_TEXT_ELEMENT_CONTENT_STRING);
		INT_TEXT_ELEMENT_CONTENT = new JSONArray(jTok);
		
		jTok = new JSONTokener(INT_IMAGE_ELEMENT_CONTENT_STRING);
		INT_IMAGE_ELEMENT_CONTENT = new JSONArray(jTok);
		
		jTok = new JSONTokener(ELEMENT_WITH_TWO_FIELDS_CONTENT_STRING);
		ELEMENT_WITH_TWO_FIELDS_CONTENT = new JSONArray(jTok);
		
		jTok = new JSONTokener(TWO_WELL_FORMATTED_ELEMENTS_CONTENT_STRING);
		TWO_WELL_FORMATTED_ELEMENTS_CONTENT = new JSONArray(jTok);	
		
		Field tipsField = TipLoader.class.getDeclaredField("TIPS");
		tipsField.setAccessible(true);
		TIPS = (List<Tip>) tipsField.get(null);
	}
	
	
	@Test
	public void testTipIsWellFormatted_wellFormatted()
	{
		assertTrue(tipIsWellFormatted(WELL_FORMATTED_TIP));
	}
	
	@Test
	public void testTipIsWellFormatted_incorrectlySpelledFields()
	{
		assertFalse(tipIsWellFormatted(INCORRECTLY_SPELLED_ID_FIELD_TIP));
		assertFalse(tipIsWellFormatted(INCORRECTLY_SPELLED_TITLE_FIELD_TIP));
		assertFalse(tipIsWellFormatted(INCORRECTLY_SPELLED_CONTENT_FIELD_TIP));
	}
	
	@Test
	public void testTipIsWellFormatted_fieldValuesIncorrectlyFormatted()
	{
		assertFalse(tipIsWellFormatted(STRING_ID_TIP));
		assertFalse(tipIsWellFormatted(INT_TITLE_TIP));
		assertFalse(tipIsWellFormatted(INCORRECTLY_FORMATED_CONTENT_TIP));
	}
	
	@Test
	public void testTipIsWellFormatted_incorrectFields()
	{
		assertFalse(tipIsWellFormatted(UNEXPECTED_FIELD_TIP));
		assertFalse(tipIsWellFormatted(MISSING_FIELD_TIP));
	}
	
	@Test
	public void testTipContentsAreWellFormatted_twoWellFormattedContentElements()
	{
		assertTrue(tipContentsAreWellFormatted(TWO_WELL_FORMATTED_ELEMENTS_CONTENT));
	}
	
	@Test
	public void testTipContentsAreWellFormatted_emptyContents()
	{
		assertFalse(tipContentsAreWellFormatted(EMPTY_CONTENT));
		assertFalse(tipContentsAreWellFormatted(EMPTY_JSON_CONTENT));
	}
	
	@Test
	public void testTipContentsAreWellFormatted_incorrectContentElementFields()
	{
		assertFalse(tipContentsAreWellFormatted(UNEXPECTED_FIELD_CONTENT));
		assertFalse(tipContentsAreWellFormatted(ELEMENT_WITH_TWO_FIELDS_CONTENT));
	}
	
	@Test
	public void testTipContentsAreWellFormatted_incorrectFieldValueTypes()
	{
		assertFalse(tipContentsAreWellFormatted(INT_TEXT_ELEMENT_CONTENT));
		assertFalse(tipContentsAreWellFormatted(INT_IMAGE_ELEMENT_CONTENT));
	}
	
	@Test
	public void testGetWellFormattedTips_tipsAreWellFormatted()
	{
		JSONObject[] tips = new JSONObject[4];
		tips[0] = WELL_FORMATTED_TIP;
		tips[1] = INCORRECTLY_SPELLED_CONTENT_FIELD_TIP;
		tips[2] = WELL_FORMATTED_TIP;
		tips[3] = STRING_ID_TIP;
		JSONArray tipJsonArray = new JSONArray(tips);
		List<JSONObject> wellFormattedTips = getWellFormattedTips(tipJsonArray);
		for(JSONObject tip : wellFormattedTips)
		{
			assertTrue(tipIsWellFormatted(tip));
		}
	}
	
	@Test
	public void testGetWellFormattedTips_expectedNumberOfTipsReturned()
	{
		JSONObject[] tips = new JSONObject[4];
		tips[0] = WELL_FORMATTED_TIP;
		tips[1] = INCORRECTLY_SPELLED_CONTENT_FIELD_TIP;
		tips[2] = WELL_FORMATTED_TIP;
		tips[3] = STRING_ID_TIP;
		JSONArray tipJsonArray = new JSONArray(tips);
		List<JSONObject> wellFormattedTips = getWellFormattedTips(tipJsonArray);
		assertTrue(wellFormattedTips.size() == 2);
	}
	
	@Test
	public void testLoadTipsAsJsonArray_nullCheck()
	{
		JSONArray tips = loadTipsAsJsonArray();
		assertTrue(tips != null);
	}
	
	@Test
	public void testLoadTipsAsJsonArray_loadsAtLeastTwoTips() //assumes that tips.json has at least two elements
	{
		JSONArray tips = loadTipsAsJsonArray();
		assertTrue(tips.length() >= 2);
	}
	
	@Test
	public void testGetTips_nullCheck()
	{
		List<Tip> tips = getTips();
		assertTrue(tips != null);
	}
	
	@Test
	public void testGetTips_getsAtLeastTwoTips()
	{
		List<Tip> tips = getTips();
		assertTrue(tips.size() >= 2);
	}
	
	@Test
	public void testGetTipOfTheDay_nullCheck()
	{
		Tip tip = TipLoader.getTipOfTheDay();
		assertTrue(tip != null);
	}
	
	@Test
	public void testGetTipOfTheDay_tipChanges()
	{
		Tip tip1 = TipLoader.getTipOfTheDay();
		Tip tip2 = TipLoader.getTipOfTheDay();
		assertTrue(tip1 != tip2);
	}
	
	@Test
	public void testGetDefaultTip_nullCheck()
	{
		Tip defaultTip = TipLoader.getDefaultTip();
		assertTrue(defaultTip != null);
	}
	
	@Test
	public void testGetFollowingTipId_getFirstIdIfUnattributedParamId()
	{
		Optional<Integer> tipIdOpt = getFollowingTipID(Integer.MIN_VALUE); //Assuming that no tip has id MIN_VALUE
		assertTrue(!tipIdOpt.isEmpty());
		int tipId = tipIdOpt.get();
		Tip firstTip = TIPS.get(0);
		assertTrue(firstTip.getId() == tipId);
	}
	
	@Test
	public void testGetFollowingTipId_idChanges()
	{
		Tip tip1 = TIPS.get(0);
		int tip1Id = tip1.getId();
		int followingTipId = getFollowingTipID(tip1Id).get(); //Assuming more than one tip is in tips.json
		// otherwise the test didn't necessarily fail, but it is incorrect, so throwing the exception is proper.
		
		assertTrue(tip1Id != followingTipId);
	}
	
	@Test
	public void testGetTip_returnsExpectedTip()
	{
		Tip tip = TIPS.get(0);
		Optional<Tip> fetchedTip = TipLoader.getTip(tip.getId());
		assertTrue(!fetchedTip.isEmpty());
		assertTrue(tip == fetchedTip.get());
	}
	
	@Test
	public void testGetTip_returnsEmptyIfNoSuchId()
	{
		Optional<Tip> fetchedTip = TipLoader.getTip(Integer.MIN_VALUE); //assuming no tip has id MIN_VALUE
		assertTrue(fetchedTip.isEmpty());
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
	
	private boolean tipIsWellFormatted(JSONObject pJSONObject)
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("tipIsWellFormatted", Object.class);
			method.setAccessible(true);
			return (boolean) method.invoke(null, pJSONObject);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
	}
	
	private boolean tipContentsAreWellFormatted(JSONArray pJSONArray)
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("tipContentsAreWellFormatted", JSONArray.class);
			method.setAccessible(true);
			return (boolean) method.invoke(null, pJSONArray);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return false;
		}
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
	
	private JSONArray loadTipsAsJsonArray()
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("loadTipsAsJsonArray");
			method.setAccessible(true);
			return (JSONArray) method.invoke(null);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Tip> getTips()
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("getTips");
			method.setAccessible(true);
			return (List<Tip>) method.invoke(null);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Optional<Integer> getFollowingTipID(int pId)
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("getFollowingTipId", int.class);
			method.setAccessible(true);
			return (Optional<Integer>) method.invoke(null, pId);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<JSONObject> getWellFormattedTips(JSONArray pTipJsonArray)
	{
		try
		{
			Method method = TipLoader.class.getDeclaredMethod("getWellFormattedTips", JSONArray.class);
			method.setAccessible(true);
			return (List<JSONObject>) method.invoke(null, pTipJsonArray);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
}
