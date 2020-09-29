package ca.mcgill.cs.jetuml.gui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

/**
 * Class that statically loads the tips in tipsJSON.json and stores them as JSONObject instances.
 */
public final class TipLoader
{
	private static final String TIP_ID_FIELD = RESOURCES.getString("tips.json.field.name.id");
	private static final String TIP_TITLE_FIELD = RESOURCES.getString("tips.json.field.name.title");
	private static final String TIP_CONTENT_FIELD = RESOURCES.getString("tips.json.field.name.content");
	private static final String TIP_CONTENT_TEXT_FIELD = RESOURCES.getString("tips.json.field.name.content.text");
	private static final String TIP_CONTENT_IMAGE_FIELD = RESOURCES.getString("tips.json.field.name.content.image");
	private static final TipLoader INSTANCE = new TipLoader();
	
	private ArrayList<JSONObject> aTipsAsJSONObjects = new ArrayList<>();
	
	private TipLoader() 
	{
		String tipsJSONFilePath = RESOURCES.getString("tips.json.file.path");
		InputStream tipsInputStream = TipLoader.class.getResourceAsStream(tipsJSONFilePath);
		
		if(tipsInputStream == null)
		{
			System.err.println("Tip JSON File not Found");
		}
		else
		{
			InputStreamReader tipsReader = new InputStreamReader(tipsInputStream);
			JSONTokener jsonTokener = new JSONTokener(tipsReader);
			JSONArray jsonArray = new JSONArray(jsonTokener);
			this.addWellFormattedTips(jsonArray);
		}
	}
	
	/**
	 * Returns the single TipLoader instance.
	 * 
	 * @return TipLoader instance
	 */
	public static TipLoader getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * Fetches the id of a tip from its associated Map pTipMap and returns
	 * it as an int.
	 * 
	 * @param pTipMap a Map instance associated with a tip
	 * @return Id of the Map's associated tip
	 * @pre pTipMap is a Map given by the iterator returned by getTipMapsIterator()
	 */
	public static int getTipMapID(Map<String, Object> pTipMap)
	{
		assert pTipMap.containsKey(TIP_ID_FIELD);
		Integer id = (Integer) pTipMap.get(TIP_ID_FIELD);
		return id;
	}
	
	/**
	 * Fetches the text content and image content of a tip from its associated Map pTipMap 
	 * and returns String containing both in HTML format (maintaining the order of the text 
	 * and image content elements in the List pTipMap.get(TIP_CONTENT_FIELD).
	 * 
	 * @param pTipMap a Map instance associated with a tip
	 * @return Text and image content of the Map's associated tip as an HTML formatted String
	 * @pre pTipMap is a Map given by the iterator returned by getTipMapsIterator()
	 */
	@SuppressWarnings("unchecked") //is checked with assert
	public static String getTipMapContentAsHTML(Map<String, Object> pTipMap) 
	{
		assert pTipMap.containsKey(TIP_TITLE_FIELD) && pTipMap.get(TIP_TITLE_FIELD).getClass() == String.class;
		assert pTipMap.containsKey(TIP_CONTENT_FIELD) && List.class.isInstance(pTipMap.get(TIP_CONTENT_FIELD));

		
		String htmlContent = "<h2>" + (String) pTipMap.get(TIP_TITLE_FIELD) + "</h2>";
		
		List<Map<String, String>> contents = (List<Map<String, String>>) pTipMap.get(TIP_CONTENT_FIELD);
		for(Map<String, String> contentElement : contents)
		{
			htmlContent += "<br>";
			if(contentElement.containsKey(TIP_CONTENT_TEXT_FIELD))
			{
				htmlContent += contentElement.get(TIP_CONTENT_TEXT_FIELD) + "<br>"; 
			}
			else
			{
				URL imageURL = TipLoader.class.getResource("/ca/mcgill/cs/jetuml/gui/" +
							   contentElement.get(TIP_CONTENT_IMAGE_FIELD));
				
				htmlContent += "<img src=\"" + imageURL.toString() + 
						"\" alt=\"An error occured when loading this image\"" +
						"><br>";
			}
		}
		return htmlContent;
	}
	
	/**
	 * @return An iterator that iterates over the statically loaded tips
	 *         (represented as Map instances).
	 */
	public Iterator<Map<String, Object>> getTipMapsIterator()
	{
		List<Map<String, Object>> tipList = new ArrayList<>();
		for(JSONObject jsonObj : aTipsAsJSONObjects)
		{
			tipList.add(jsonObj.toMap());
		}
		return tipList.iterator();
	}
	
	private static boolean tipIsWellFormatted(Object pTip)
	{
		boolean isJSONObj = pTip instanceof JSONObject;
		if (isJSONObj) //The tip is a JSONObject
		{
			JSONObject tip = (JSONObject) pTip;
			boolean hasExpectedFields = 
					tip.has(TIP_ID_FIELD) && 
					tip.has(TIP_TITLE_FIELD) && 
					tip.has(TIP_CONTENT_FIELD);
			
			if(hasExpectedFields) //The tip has the expected fields
			{
				boolean fieldsHaveRightType = 
						tip.get(TIP_ID_FIELD).getClass() == Integer.class &&
						tip.get(TIP_TITLE_FIELD).getClass() == String.class &&
						tip.get(TIP_CONTENT_FIELD).getClass() == JSONArray.class;
				
				boolean idIsNotNull = tip.get(TIP_ID_FIELD) != null;
				
				if(fieldsHaveRightType && idIsNotNull) //The fields have the right types and the ID is not null
				{
					// True if the tip content field is well formatted (last necessary check), false otherwise
					return tipContentsAreWellFormatted((JSONArray) tip.get(TIP_CONTENT_FIELD)); 
				}
			}
		}
		return false; // if any of the previous checks failed
	}
	
	/**
	 * @param pContents A JSONArray of tip content elements
	 * @return true if the tip's contents are well formatted, false otherwise
	 */
	private static boolean tipContentsAreWellFormatted(JSONArray pContents)
	{
		for(Object contentElement : pContents)
		{
			if(contentElement.getClass() != JSONObject.class) 
			{
				return false;
			}
			else // the content element is a JSONObject as desired
			{
				JSONObject contentElementJO = (JSONObject) contentElement;
				int numKeys = contentElementJO.length();
				
				boolean contentElementHasCorrectTextField = 
						contentElementJO.has(TIP_CONTENT_TEXT_FIELD) && 
						contentElementJO.get(TIP_CONTENT_TEXT_FIELD).getClass() == String.class && 
						numKeys == 1;
				
				boolean contentElementHasCorrectImageField =
						contentElementJO.has(TIP_CONTENT_IMAGE_FIELD) &&
						contentElementJO.get(TIP_CONTENT_IMAGE_FIELD).getClass() == String.class &&
						numKeys == 1; 
				
				// the content element has only one field (one of text or image) and it is of type string
				boolean contentElementHasCorrectField = contentElementHasCorrectTextField ||
						contentElementHasCorrectImageField;
				if(!contentElementHasCorrectField)
				{
					return false; //false if any of the elements are wrongly formatted
				}
			}
		}
		return true; //Every content element is a JSON object with one of the correct fields
	}
	
	
	private void addWellFormattedTips(JSONArray pJSONArray)
	{
		for (Object tip : pJSONArray)
		{
			if (tipIsWellFormatted(tip))
			{
				aTipsAsJSONObjects.add((JSONObject)tip);
			}
		}
	}
}
