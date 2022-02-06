/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.application;

import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.prefs.Preferences;

import ca.mcgill.cs.jetuml.JetUML;

/**
 * A Singleton that manages all user preferences global to
 * the application.
 */
public final class UserPreferences
{
	/**
	 * A boolean preference.
	 */
	public enum BooleanPreference
	{	
		showGrid(true), showToolHints(false), autoEditNode(false), verboseToolTips(false),
		showTips(true);
		
		private boolean aDefault;
		
		BooleanPreference( boolean pDefault )
		{ 
			aDefault = pDefault;
		}
		
		String getDefault()
		{
			return Boolean.toString(aDefault);
		}
	}
	
	/**
	 * An integer preference.
	 */
	public enum IntegerPreference
	{
		diagramWidth(0), diagramHeight(0), nextTipId(1), fontSize(DEFAULT_FONT_SIZE);
		
		private int aDefault;
		
		IntegerPreference( int pDefault )
		{
			aDefault = pDefault;
		}
		
		String getDefault()
		{
			return Integer.toString(aDefault);
		}
	}
	
	/**
	 * An object that can react to a change to a boolean user preference.
	 */
	public interface BooleanPreferenceChangeHandler
	{
		/**
		 * Callback for change in boolean preference values.
		 * 
		 * @param pPreference The preference that just changed.
		 */
		void booleanPreferenceChanged(BooleanPreference pPreference);
	}

	/**
	 * An object that can react to a change to an integer user preference.
	 */
	public interface IntegerPreferenceChangeHandler
	{
		/**
		 * Callback for change in integer preference values.
		 * 
		 * @param pPreference The preference that just changed.
		 */
		void integerPreferenceChanged(IntegerPreference pPreference);
	}
	
	private static final UserPreferences INSTANCE = new UserPreferences();
	
	private EnumMap<BooleanPreference, Boolean> aBooleanPreferences = new EnumMap<>(BooleanPreference.class);
	private final List<BooleanPreferenceChangeHandler> aBooleanPreferenceChangeHandlers = new ArrayList<>();
	private EnumMap<IntegerPreference, Integer> aIntegerPreferences = new EnumMap<>(IntegerPreference.class);
	private final List<IntegerPreferenceChangeHandler> aIntegerPreferenceChangeHandlers = new ArrayList<>();
	
	private UserPreferences()
	{
		for( BooleanPreference preference : BooleanPreference.values() )
		{
			aBooleanPreferences.put(preference, 
					Boolean.valueOf(Preferences.userNodeForPackage(JetUML.class)
							.get(preference.name(), preference.getDefault())));
		}
		for( IntegerPreference preference : IntegerPreference.values() )
		{
			aIntegerPreferences.put( preference, 
					Integer.valueOf(Preferences.userNodeForPackage(JetUML.class)
							.get(preference.name(), preference.getDefault())));
		}
	}
	
	public static UserPreferences instance() 
	{ return INSTANCE; }
	
	/**
	 * @param pPreference The property whose value to obtain.
	 * @return The value of the property.
	 */
	public boolean getBoolean(BooleanPreference pPreference)
	{
		return aBooleanPreferences.get(pPreference);
	}
	
	/**
	 * @param pPreference The property whose value to obtain.
	 * @return The value of the property.
	 */
	public int getInteger(IntegerPreference pPreference)
	{
		return aIntegerPreferences.get(pPreference);
	}
	
	/**
	 * Sets and persists the value of a preference.
	 * 
	 * @param pPreference The property to set.
	 * @param pValue The value to set.
	 */
	public void setBoolean(BooleanPreference pPreference, boolean pValue)
	{
		aBooleanPreferences.put(pPreference, pValue);
		Preferences.userNodeForPackage(JetUML.class).put(pPreference.name(), Boolean.toString(pValue));
		aBooleanPreferenceChangeHandlers.forEach(handler -> handler.booleanPreferenceChanged(pPreference));
	}
	
	/**
	 * Sets and persists the value of a preference.
	 * 
	 * @param pPreference The property to set.
	 * @param pValue The value to set.
	 */
	public void setInteger(IntegerPreference pPreference, int pValue)
	{
		aIntegerPreferences.put(pPreference, pValue);
		Preferences.userNodeForPackage(JetUML.class).put(pPreference.name(), Integer.toString(pValue));
		aIntegerPreferenceChangeHandlers.forEach(handler -> handler.integerPreferenceChanged(pPreference));
	}
	
	/**
	 * Adds a handler for a boolean property change. Don't forget to remove handers if 
	 * objects are removed, e.g., diagram Tabs.
	 * 
	 * @param pHandler A handler for a change in boolean preferences.
	 */
	public void addBooleanPreferenceChangeHandler(BooleanPreferenceChangeHandler pHandler)
	{
		aBooleanPreferenceChangeHandlers.add(pHandler);
	}
	
	/**
	 * Removes a handler.
	 * 
	 * @param pHandler The handler to remove.
	 */
	public void removeBooleanPreferenceChangeHandler(BooleanPreferenceChangeHandler pHandler)
	{
		aBooleanPreferenceChangeHandlers.remove(pHandler);
	}
	
	/**
	 * Adds a handler for an integer property change. Don't forget to remove handers if 
	 * objects are removed, e.g., diagram Tabs.
	 * 
	 * @param pHandler A handler for a change in integer preferences.
	 */
	public void addIntegerPreferenceChangeHandler(IntegerPreferenceChangeHandler pHandler)
	{
		aIntegerPreferenceChangeHandlers.add(pHandler);
	}

	/**
	 * Removes a handler.
	 * 
	 * @param pHandler The handler to remove.
	 */
	public void removeIntegerPreferenceChangeHandler(IntegerPreferenceChangeHandler pHandler)
	{
		aIntegerPreferenceChangeHandlers.remove(pHandler);
	}
}
