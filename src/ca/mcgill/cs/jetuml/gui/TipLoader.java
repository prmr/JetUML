package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * Class that statically loads the tips in tipsJSON.json and stores them as Tip instances.
 */
public final class TipLoader
{
	private static final String TIP_ID_FIELD = RESOURCES.getString("tips.json.field.name.id");
	private static final String TIP_TITLE_FIELD = RESOURCES.getString("tips.json.field.name.title");
	private static final String TIP_CONTENT_FIELD = RESOURCES.getString("tips.json.field.name.content");
	
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
		 */
		private Tip(JSONObject pTip)
		{
			
			aId = (int) pTip.get(TIP_ID_FIELD);
			aTitle = (String) pTip.get(TIP_TITLE_FIELD);
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
			return new ArrayList<TipElement>(aElements);
		}
		

		/**
		 * @param pTip a JSONObject obtained from the JSONArray gotten by loading the tips.
		 */
		@SuppressWarnings("unchecked")
		private static List<TipElement> convertJSONObjectToTipElements(JSONObject pTip)
		{
			List<TipElement> elements = new ArrayList<>();
			Map<String, Object> tipMap = pTip.toMap();
			List<Map<String, String>> contentList = (List<Map<String, String>>) tipMap.get(TIP_CONTENT_FIELD);
			for(Map<String, String> contentElement : contentList)
			{
				String mediaName = (String) contentElement.keySet().toArray()[0];
				Media media = Media.getMedia(mediaName).get();
				String content = contentElement.get(mediaName);
				TipElement tipElement = new TipElement(media, content);
				elements.add(tipElement);
			}
			return elements;
		}
	}
	
	
}
