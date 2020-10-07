package ca.mcgill.cs.jetuml.gui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcgill.cs.jetuml.application.UserPreferences;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
	private static final String TIP_DEFAULT = RESOURCES.getString("tips.default");
	
	private static final List<Tip> TIPS = getTips();
	private static final Tip DEFAULT_TIP = new Tip(new JSONObject(TIP_DEFAULT));
	
	private TipLoader() {}
	
	private static List<Tip> getTips()
	{
		List<JSONObject> tipsAsJsonObjects = loadTipsAsJsonObjects();
		List<Tip> tips = new ArrayList<>();
		for(JSONObject tipAsJsonObject : tipsAsJsonObjects)
		{
			tips.add(new Tip(tipAsJsonObject));
		}
		return tips;
	}
	
	private static List<JSONObject> loadTipsAsJsonObjects() 
	{
		String tipsJSONFilePath = RESOURCES.getString("tips.json.file.path");
		InputStream tipsInputStream = TipLoader.class.getResourceAsStream(tipsJSONFilePath);
		
		if(tipsInputStream == null)
		{
			System.err.println("Tip JSON File not Found");
			return new ArrayList<>();
		}
		else
		{
			InputStreamReader tipsReader = new InputStreamReader(tipsInputStream);
			JSONTokener jsonTokener = new JSONTokener(tipsReader);
			JSONArray jsonArray = new JSONArray(jsonTokener);
			return getWellFormattedTips(jsonArray);
		}
	}
	
	private static List<JSONObject> getWellFormattedTips(JSONArray pJSONArray)
	{
		List<JSONObject> list = new ArrayList<>();
		for (Object tip : pJSONArray)
		{
			if (tipIsWellFormatted(tip))
			{
				list.add((JSONObject)tip);
			}
		}
		return list;
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
	
	public static Optional<Tip> getTip(int pId)
	{
		for(Tip tip : TIPS)
		{
			if(tip.aId == pId)
			{
				return Optional.of(tip);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Method that returns the tip of the day and updates the user preferences 
	 * to set the user's next tip of the day's id to the id of the following tip
	 * (i.e. it updates the tip of the day for the next time this method gets 
	 * called).
	 * 
	 * @return Tip the tip of the day
	 */
	public static Tip getTipOfTheDay()
	{	
		//If no tip was successfully loaded;
		if(TIPS.size() == 0)
		{
			return DEFAULT_TIP;
		}
		
		int tipID = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.nextTipID);
		Optional<Tip> tipOptional = getTip(tipID);
		Optional<Integer> followingTipIdOptional = getFollowingTipId(tipID);
		
		Tip tip;
		if(!tipOptional.isEmpty()) // Getting the expected tip of the day as given by the preferences
		{
			tip = tipOptional.get();
		}
		// Getting the first tip in the tip list as the tip of the day if the proper tip of the day was not found
		else if(!followingTipIdOptional.isEmpty() && !getTip(followingTipIdOptional.get()).isEmpty())
		{
			int followingTipId = followingTipIdOptional.get();
			tip = getTip(followingTipId).get();
		}
		else //This branch should never occur. Leaving it for robustness.
		{
			tip = DEFAULT_TIP;
		}
		
		// Setting the tip of the day to the next tip
		Optional<Integer> followingTipID = getFollowingTipId(tipID);
		if(!followingTipID.isEmpty())
		{
			UserPreferences.instance().setInteger(UserPreferences.IntegerPreference.nextTipID, followingTipID.get());
		}
	
		return tip;
	}
	
	private static Optional<Integer> getFollowingTipId(int pId)
	{
		if(TIPS.size() == 0)
		{
			Optional.empty();
		}
		for(int i = 0; i<TIPS.size(); i++)
		{
			Tip tip = TIPS.get(i);
			boolean tipHasNext = i < TIPS.size() - 1;
			
			if(tip.aId == pId && tipHasNext)
			{
				Tip nextTip = TIPS.get(i+1);
				return Optional.of(nextTip.aId);
			}
		}
		Tip firstTip = TIPS.get(0);
		return Optional.of(firstTip.aId); 
	}
	
	public static final class Tip
	{
		
		private int aId;
		private List<TipElement> aElements;
		
		private Tip(JSONObject pTip)
		{
			aId = (int) pTip.get(TIP_ID_FIELD);
			aElements = convertJSONObjectToTipElements(pTip);
		}
		
		public List<TipElement>getElements()
		{
			return new ArrayList<TipElement>(aElements);
		}
		

		private static List<TipElement> convertJSONObjectToTipElements(JSONObject pTip)
		{
			List<TipElement> elements = new ArrayList<>();
			Map<String, Object> tipMap = pTip.toMap();
			TipElement title = new TipTitle((String) tipMap.get(TIP_TITLE_FIELD));
			elements.add(title);
			List<Map<String, String>> contentList = (List<Map<String, String>>) tipMap.get(TIP_CONTENT_FIELD);
			for(Map<String, String> contentElement : contentList)
			{
				TipElement element;
				Set<String> keys = contentElement.keySet();
				if(keys.contains(TIP_CONTENT_TEXT_FIELD))
				{
					element = new TipText(contentElement.get(TIP_CONTENT_TEXT_FIELD));
					elements.add(element);
				}
				else if (keys.contains(TIP_CONTENT_IMAGE_FIELD))
				{
					element = new TipImage(contentElement.get(TIP_CONTENT_IMAGE_FIELD));
					elements.add(element);
				}
				else
				{
					Iterator<String> keyIterator = keys.iterator();
					if(keyIterator.hasNext())
					{
						String key = keys.iterator().next();
						System.err.println("Error: unknown tip element type " + key);
					}
					else
					{
						System.err.println("Error: empty tip content element found.");
					}
				}
			}
			return elements;
		}
	}
	
}
