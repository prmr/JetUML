package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
	
	
	@BeforeAll
	public static void setupClass()
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
	
}
