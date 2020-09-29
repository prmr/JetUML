package ca.mcgill.cs.jetuml.gui;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import ca.mcgill.cs.jetuml.application.UserPreferences;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

public class TipManager {

	private static final TipManager INSTANCE = new TipManager();
	
	private final Hashtable<Integer, Tip> aTips = new Hashtable<>();
	private final ArrayList<Integer> aIDList = new ArrayList<>(); //Same order as in tipsJSON.json
	private final Tip aDefaultTip = new Tip(-1, RESOURCES.getString("tips.default.HTMLcontent"));
	
	private TipManager()
	{
		TipLoader tipLoader = TipLoader.getInstance();
		Iterator<Map<String, Object>> tipMapsIterator = tipLoader.getTipMapsIterator();
		while(tipMapsIterator.hasNext())
		{
			Map<String, Object> tipMap = tipMapsIterator.next();
			Tip tip = new Tip(TipLoader.getTipMapID(tipMap), TipLoader.getTipMapContentAsHTML(tipMap));
			if (aTips.containsKey(tip.aID))
			{
				System.err.println("Another tip with the ID " + tip.aID + " exists");
				continue;
			}
			aTips.put(tip.aID, tip);
			aIDList.add(tip.getID());
		}
	}
	
	/**
	 * Returns the single TipManager instance.
	 * 
	 * @return TipManager instance
	 */
	public static TipManager getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * @param pID the id of the tip to get
	 * @return The tip that has id pID
	 * @pre Tip.tipExists(pID)
	 */
	public Tip getTip(int pID)
	{
		return aTips.get(pID);
	}
	
	/**
	 * @return List of the tips.
	 */
	public ArrayList<Tip> getTips()
	{
		return new ArrayList<Tip>(aTips.values()); 
	}
	
	/**
	 * Method that returns the tip of the day and updates the user preferences to set the user's next tip
	 * of the day's id to the next id in ID_LIST (i.e. updates the tip of the day for the 
	 * next time this method gets called).
	 * @return Tip the tip of the day
	 */
	public Tip getTipOfTheDay()
	{
		int tipID = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.nextTipID);
		Tip tip;
		if(tipExists(tipID))
		{
			tip = aTips.get(tipID);
		}
		else
		{
			tip = getDefaultTip();
		}
		
		int nextTipID = getNextTipIdInSequence(tipID);
		UserPreferences.instance().setInteger(UserPreferences.IntegerPreference.nextTipID, nextTipID);
		
		return tip;
	}
	
	private int getNextTipIdInSequence(int pID)
	{
		if(aIDList.isEmpty())
		{
			return 1; //returning default ID value
		}
		for (int i = 0; i<aIDList.size() - 1; i++)
		{
			if (aIDList.get(i) == pID)
			{
				return aIDList.get(i+1);
			}
		}
		return aIDList.get(0); // return the first index if pID was never found or is the last in the list
	}
	
	/**
	 * Checks if there exists a Tip with id pID.
	 * @param pID the id of the tip
	 * @return True if the Tip exists, false otherwise.
	 */
	public boolean tipExists(int pID)
	{
		return aTips.containsKey(pID);
	}
	
	public Tip getDefaultTip()
	{
		return aDefaultTip;
	}
	
	public class Tip {
		
		private final int aID;
		private final String aHTMLContent;
		
		private Tip(int pID, String pHTMLContent)
		{
			aID = pID;
			aHTMLContent = pHTMLContent;
		}
		
		/**
		 * @return The id of the tip
		 */
		public int getID()
		{
			return aID;
		}
		
		/**
		 * @return The HTML content of the tip
		 */
		public String getHTMLContent()
		{
			return aHTMLContent;
		}
		
	}
	
}
