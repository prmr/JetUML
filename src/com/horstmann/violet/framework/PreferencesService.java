package com.horstmann.violet.framework;

import java.util.prefs.Preferences;

/**
 * A service for storing and loading user preferences.
 * This service uses either the standard Java preferences API or the WebStart 
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
      catch (SecurityException exception)
      {
         // that happens when we run under Web Start         
      }
      try
      {
         // we load this lazily so that the JAR can load without WebStart
         service = (PreferencesService) Class.forName("com.horstmann.violet.framework.JNLPPreferencesService").newInstance();
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

   private Preferences prefs;
}

/**
 * The null preferences service that is returned when we are an applet. 
 */
class NullPreferencesService extends PreferencesService
{   
   public String get(String key, String defval) { return defval; }
   public void put(String key, String defval) { }
}