package ca.mcgill.cs.jetuml.gui.tips;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * Class that statically loads the tips.
 */
final class TipLoader
{
	/**
	 * A tip that contains TipElement instances.
	 */
	public static final class Tip
	{
		private final int aId;
		private final String aTitle;
		private final List<TipElement> aElements;
		
		/**
		 * @param pTip a JSONObject obtained from the JSONArray gotten by loading the tips.
		 */
		private Tip(JSONObject pTip)
		{
			
			aId = (int) pTip.get(TipFieldName.ID.asString());
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
