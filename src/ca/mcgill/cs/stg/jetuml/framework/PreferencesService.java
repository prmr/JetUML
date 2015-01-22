/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.stg.jetuml.framework;

import java.util.prefs.Preferences;

/**
 * A service for storing and loading user preferences.
 * This service uses the standard Java preferences API 
 * persistence service ("muffins"). 
 */
public abstract class PreferencesService
{
   /**
    * Gets an instance of the service, suitable for the package of the given class.
    * @param appClass the main application class (only the package name is used as the path to  
    * app-specific preferences storage)
    * @return an instance of the service
    */
   public static PreferencesService getInstance(Class appClass)
   {
      if (service != null) return service;
      try
      {
    	  service = new DefaultPreferencesService(appClass);
          return service;
      }
      catch (Throwable exception)
      {
         // that happens when we are an applet
      }
      
      return new NullPreferencesService();
   }
   
   /**
    * Gets a previously stored string from the service.
    * @param key the key of the string
    * @param defval the value to return if no matching value was found
    * @return the value stored with the given key, or defval if none was found 
    */
   public abstract String get(String key, String defval);
   /**
    * Saves a key/value pair for later retrieval.
    * @param key the key of the string to be stored
    * @param value the value to to be stored
    */
   public abstract void put(String key, String value);
   
   private static PreferencesService service;
}

/**
 * The default preferences service that uses the java.util.prefs API. 
 */
class DefaultPreferencesService extends PreferencesService
{   
	
	private Preferences prefs;
	
   /**
    * Gets an instance of the service, suitable for the package of the given class.
    * @param appClass the main application class (only the package name is used as the path to  
    * app-specific preferences storage)
    * @return an instance of the service
    */
   public DefaultPreferencesService(Class appClass)
   {
      prefs = Preferences.userNodeForPackage(appClass);   
   }
   
   public String get(String key, String defval) { return prefs.get(key, defval); }
   public void put(String key, String defval) { prefs.put(key, defval); }

   
}

/**
 * The null preferences service that is returned when we are an applet. 
 */
class NullPreferencesService extends PreferencesService
{   
   public String get(String key, String defval) { return defval; }
   public void put(String key, String defval) { }
}