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
 * Class that statically loads the tips in tipsJSON.json and stores them as Tip instances.
 * 
 * This class relies on assumptions about the format in /tip_resources/tips.json. If new
 * possible tip content fields are added or modified, tipIsWellFormatted(JSONObject pTip)
 * and tipContentsAreWellFormated(JSONObject pTip) may need to be modified.
 */
public final class TipLoader
{
	private static final String TIP_ID_FIELD = RESOURCES.getString("tips.json.field.name.id");
	private static final String TIP_TITLE_FIELD = RESOURCES.getString("tips.json.field.name.title");
	private static final String TIP_CONTENT_FIELD = RESOURCES.getString("tips.json.field.name.content");
	private static final String TIP_CONTENT_TEXT_FIELD = RESOURCES.getString("tips.json.field.name.content.text");
	private static final String TIP_CONTENT_IMAGE_FIELD = RESOURCES.getString("tips.json.field.name.content.image");
	private static final String TIP_DEFAULT = RESOURCES.getString("tips.default");
	private static final int TIP_NUM_FIELDS = 3;
	private static final int TIP_CONTENT_NUM_FIELDS = 1;
	
	private static final List<Tip> TIPS = getTips();
	private static final Tip DEFAULT_TIP = new Tip(new JSONObject(TIP_DEFAULT));
	
	private TipLoader() {}
	
	private static List<Tip> getTips()
	{
		JSONArray tipsAsJsonArray = loadTipsAsJsonArray();
		List<JSONObject> tipsAsJsonObjects = getWellFormattedTips(tipsAsJsonArray);
		List<Tip> tips = new ArrayList<>();
		for(JSONObject tipAsJsonObject : tipsAsJsonObjects)
		{
			tips.add(new Tip(tipAsJsonObject));
		}
		return tips;
	}
	
	private static JSONArray loadTipsAsJsonArray() 
	{
		String tipsJSONFilePath = RESOURCES.getString("tips.json.file.path");
		InputStream tipsInputStream = TipLoader.class.getResourceAsStream(tipsJSONFilePath);
		
		if(tipsInputStream == null)
		{
			System.err.println("Tip JSON File not Found");
			return new JSONArray();
		}
		else
		{
			InputStreamReader tipsReader = new InputStreamReader(tipsInputStream);
			JSONTokener jsonTokener = new JSONTokener(tipsReader);
			JSONArray jsonArray = new JSONArray(jsonTokener);
			return jsonArray;
		}
	}
	
	/**
	 * @param pJSONArray JSONArray containing tips whose format will be checked
	 * @return list of well formatted JSONObjects
	 * @pre pJSONArray != null
	 */
	private static List<JSONObject> getWellFormattedTips(JSONArray pJSONArray)
	{
		assert pJSONArray != null;
		
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
	
	/**
	 * Checks if a tip is well formatted based on current assumptions about the tip format
	 * in /tip_resources/tips.json. If the format is modified, this method may need to be changed.
	 * Furthermore, if this method is changed, calling methods may need to be modified also. In
	 * particular Tip(JSONObject pTip) and Tip.convertJSONObjectToTipElements(JSONOBject pTip)
	 * rely on assumptions based on the checks made by this method.
	 * 
	 * @param pTip the tip whose format is to be checked. 
	 * @return true if the tip is well formatted, false otherwise (including if pTip is null)
	 */
	private static boolean tipIsWellFormatted(Object pTip)
	{
		if(pTip == null)
		{
			return false;
		}
		
		boolean isJSONObj = pTip instanceof JSONObject;
		if (isJSONObj) //The tip is a JSONObject
		{
			JSONObject tip = (JSONObject) pTip;
			boolean hasExpectedFields = 
					tip.has(TIP_ID_FIELD) && 
					tip.has(TIP_TITLE_FIELD) && 
					tip.has(TIP_CONTENT_FIELD);
			
			boolean hasOnlyExpectedFields = tip.length() == TIP_NUM_FIELDS;
			
			if(hasExpectedFields && hasOnlyExpectedFields) //The tip has the expected fields only
			{
				boolean fieldsHaveRightType = 
						tip.get(TIP_ID_FIELD).getClass() == Integer.class &&
						tip.get(TIP_TITLE_FIELD).getClass() == String.class &&
						tip.get(TIP_CONTENT_FIELD).getClass() == JSONArray.class;
				
				boolean fieldsAreNotNull = 
						tip.get(TIP_ID_FIELD) != null &&
						tip.get(TIP_TITLE_FIELD) != null &&
						tip.get(TIP_CONTENT_FIELD) != null;
				
				if(fieldsHaveRightType && fieldsAreNotNull) //The fields have the right types and the ID is not null
				{
					// True if the tip content field is well formatted (last necessary check), false otherwise
					return tipContentsAreWellFormatted((JSONArray) tip.get(TIP_CONTENT_FIELD)); 
				}
			}
		}
		return false; // if any of the previous checks failed
	}
	
	/**
	 * Checks if a tip's contents are well formatted based on current assumptions about the tip 
	 * format in /tip_resources/tips.json. If the format is modified, this method may need to be
	 * changed. Furthermore, if this method is changed, methods that rely on it may need to be
	 * modified also. In particular Tip(JSONObject pTip) and 
	 * Tip.convertJSONObjectToTipElements(JSONOBject pTip) rely on assumptions based on the
	 * checks made by this method.
	 * 
	 * Note that tips with no contents are deemed as incorrectly formatted.
	 * 
	 * @param pContents JSONArray containing a JSONObject tip's contents. 
	 * @return true if the tip's contents are well formatted, false otherwise
	 * @pre pContents != null;
	 */
	private static boolean tipContentsAreWellFormatted(JSONArray pContents)
	{
		assert pContents != null;
		if(pContents.length() == 0)
		{
			return false;
		}
		for(Object contentElement : pContents)
		{
			if(contentElement.getClass() != JSONObject.class) 
			{
				return false;
			}
			else // the content element is a JSONObject as desired
			{
				JSONObject contentElementJO = (JSONObject) contentElement;
				int numFields = contentElementJO.length();
				
				boolean contentElementIsProperTextElement = 
						contentElementJO.has(TIP_CONTENT_TEXT_FIELD) && 
						contentElementJO.get(TIP_CONTENT_TEXT_FIELD).getClass() == String.class && 
						numFields == TIP_CONTENT_NUM_FIELDS;
				
				boolean contentElementIsProperImageElement =
						contentElementJO.has(TIP_CONTENT_IMAGE_FIELD) &&
						contentElementJO.get(TIP_CONTENT_IMAGE_FIELD).getClass() == String.class &&
						numFields == TIP_CONTENT_NUM_FIELDS; 
				
				// the content element has only one field (one of text or image) and it is of type string
				boolean contentElementHasCorrectField = contentElementIsProperTextElement ||
						contentElementIsProperImageElement;
				if(!contentElementHasCorrectField)
				{
					return false; //false if any of the elements are wrongly formatted
				}
			}
		}
		return true; //Every content element is a JSON object with one of the correct fields
	}
	
	/**
	 * Returns the tip associated with the given tip id if there is such a tip.
	 * 
	 * @param pId id of the tip to return
	 * @return Optional of the tip associated to pId if one exists, Optional.empty() otherwise.
	 */
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
	
	/**
	 * Returns the default tip stored in JetUML.properties ("tips.default").
	 * 
	 * @return the default tip
	 */
	public static Tip getDefaultTip()
	{
		return DEFAULT_TIP;
	}
	
	/**
	 * Returns the id of the following tip. 
	 * 
	 * @param pId id of the current Tip 
	 * @return Id of the next tip if there is a next tip, Optional.empty() if there are no
	 * 		   loaded tips, and the id of the first tip if the given id matches no tip in 
	 * 		   the list, but the list is non-empty.
	 */
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
	
	/**
	 * A tip that contains TipElement instances.
	 */
	public static final class Tip
	{
		
		private final int aId;
		private final String aTitle;
		private final List<TipElement> aElements;
		
		/**
		 * @param pTip a JSONObject
		 * @pre tipIsWellFormatted(pTip)
		 */
		private Tip(JSONObject pTip)
		{
			assert tipIsWellFormatted(pTip);
			
			aId = (int) pTip.get(TIP_ID_FIELD);
			aTitle = (String) pTip.get(TIP_TITLE_FIELD);
			aElements = convertJSONObjectToTipElements(pTip);
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
			return new ArrayList<TipElement>(aElements);
		}
		

		/**
		 * @param pTip a JSONObject
		 * @pre tipIsWellFormatted(pTip)
		 */
		@SuppressWarnings("unchecked")
		private static List<TipElement> convertJSONObjectToTipElements(JSONObject pTip)
		{
			assert tipIsWellFormatted(pTip);
			
			List<TipElement> elements = new ArrayList<>();
			Map<String, Object> tipMap = pTip.toMap();
			List<Map<String, String>> contentList = (List<Map<String, String>>) tipMap.get(TIP_CONTENT_FIELD);
			for(Map<String, String> contentElement : contentList)
			{
				TipElement element;
				Set<String> keys = contentElement.keySet();
				if(keys.contains(TIP_CONTENT_TEXT_FIELD))
				{
					String textContent = contentElement.get(TIP_CONTENT_TEXT_FIELD);
					element = new TipElement(Media.TEXT, textContent);
					elements.add(element);
				}
				else // we know from @pre that keys.contains(TIP_CONTENT_IMAGE_FIELD)
				{
					String imageContent = contentElement.get(TIP_CONTENT_IMAGE_FIELD);
					element = new TipElement(Media.IMAGE, imageContent);
					elements.add(element);
				}
			}
			return elements;
		}
	}
	
}
