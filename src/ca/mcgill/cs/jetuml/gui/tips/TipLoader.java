package ca.mcgill.cs.jetuml.gui.tips;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcgill.cs.jetuml.application.UserPreferences;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

/**
 * Class that statically loads the tips.
 */
final class TipLoader
{
	public static final int NUM_TIPS = Integer.parseInt(RESOURCES.getString("tips.quantity"));
	private static final String TIP_FILE_PATH_FORMAT = RESOURCES.getString("tips.jsons.directory") + "/tip-%d.json";
	
	private TipLoader(){}
	
	/**
	 * Returns the tip associated with the given tip id if there is such a tip.
	 * 
	 * @param pId id of the tip to return
	 * @return Optional of the tip associated to pId if one exists, Optional.empty() otherwise.
	 * 
	 * @pre pId >= 1 && pId <= NUM_TIPS 
	 */
	public static Tip loadTip(int pId)
	{
		assert pId >= 1 && pId <= NUM_TIPS;
		
		String tipFileName = String.format(TIP_FILE_PATH_FORMAT, pId);
		InputStream tipsInputStream = TipLoader.class.getResourceAsStream(tipFileName); //Not null if tests pass
		InputStreamReader tipsReader = new InputStreamReader(tipsInputStream);
		JSONTokener jsonTokener = new JSONTokener(tipsReader);
		JSONObject jsonObject = new JSONObject(jsonTokener); // Won't throw JSONException if tests pass
		Tip tip = new Tip(pId, jsonObject);
		return tip;
	}
	
	/**
	 * Method that returns the tip of the day and updates the user preferences 
	 * to set the user's next tip of the day's id to the id of the following tip
	 * (i.e. it updates the tip of the day for the next time this method gets 
	 * called).
	 * 
	 * @return Tip the tip of the day
	 */
	public static Tip loadTipOfTheDay()
	{	
		int tipId = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.nextTipId);
		Tip tip = loadTip(tipId);
		
		// Setting the tip of the day to the next tip
		int followingTipID = getNextTipId(tipId); 
		UserPreferences.instance().setInteger(UserPreferences.IntegerPreference.nextTipId, followingTipID);

		return tip;
	}
	
	/**
	 * @param pId id of the current Tip 
	 * @return the id of the next Tip (following an id-wise increasing order, 
	 * 		   wrapping around when pId is the greatest id) 
	 * 
	 * @pre pId >= 1 && pId <= NUM_TIPS
	 */
	private static int getNextTipId(int pId)
	{
		assert pId >= 1 && pId <= NUM_TIPS;
		
		if (pId == NUM_TIPS)
		{
			return 1;
		}
		else
		{
			return pId + 1;
		}
	}
	
	/**
	 * @param pId id of the current Tip 
	 * @return the id of the previous Tip (following an id-wise increasing order, 
	 * 		   wrapping around when pId is 1) 
	 * 
	 * @pre pId >= 1 && pId <= NUM_TIPS
	 */
	private static int getPreviousTipId(int pId)
	{
		assert pId >= 1 && pId <= NUM_TIPS;
		
		if (pId == 1)
		{
			return NUM_TIPS;
		}
		else
		{
			return pId - 1;
		}
	}
	
	/**
	 * Loads and returns the next Tip following an id-wise increasing order. 
	 * 
	 * @param pCurrentTipId the id of the current Tip
	 * @return the next Tip 
	 * 
	 * @pre pCurrentTipId >= 1 && pCurrentTipId <= NUM_TIPS
	 */
	public static Tip loadNextTip(int pCurrentTipId)
	{
		assert pCurrentTipId >= 1 && pCurrentTipId <= NUM_TIPS;
		
		int nextTipId = getNextTipId(pCurrentTipId);
		return loadTip(nextTipId);
	}
	
	/**
	 * Loads and returns the previous Tip following an id-wise increasing order. 
	 * 
	 * @param pCurrentTipId the id of the current Tip
	 * @return the previous Tip 
	 * 
	 * @pre pCurrentTipId >= 1 && pCurrentTipId <= NUM_TIPS
	 */
	public static Tip loadPreviousTip(int pCurrentTipId)
	{
		assert pCurrentTipId >= 1 && pCurrentTipId <= NUM_TIPS;
		
		int previousTipId = getPreviousTipId(pCurrentTipId);
		return loadTip(previousTipId);
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
		 * @param pId the id associated with the tip (number in the tip's file's name)
		 * @param pTip the JSONObject obtained from the file tip-pId.json, where pId is the 
		 * 		  same as the pId parameter.
		 */
		private Tip(int pId, JSONObject pTip)
		{
			
			aId = pId;
			aTitle = (String) pTip.get(TipFieldName.TITLE.asString());
			aElements = convertJSONObjectToTipElements(pTip);
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
		
		/**
		 * @param pTip a JSONObject obtained from the JSONArray gotten by loading the tips.
		 */
		@SuppressWarnings("unchecked")
		private static List<TipElement> convertJSONObjectToTipElements(JSONObject pTip)
		{
			List<TipElement> elements = new ArrayList<>();
			Map<String, Object> tipMap = pTip.toMap();
			List<Map<String, String>> contentList = (List<Map<String, String>>) tipMap.get(TipFieldName.CONTENT.asString());
			for(Map<String, String> contentElement : contentList)
			{
				String mediaName = (String) contentElement.keySet().toArray()[0];
				Media media = Media.valueOf(mediaName.toUpperCase());
				String content = contentElement.get(mediaName);
				TipElement tipElement = new TipElement(media, content);
				elements.add(tipElement);
			}
			return elements;
		}
	}
}
