package org.jetuml.gui;

import javafx.stage.Stage;

import java.util.LinkedList;

/**
 * Singleton object that manages the notification object positions and display states.
 */
public final class NotificationHandler
{

    /**
     * A callback for Notification objects called when they reached the end of their lifetime.
     * It removes them from the handler notification list.
     */
    public class CleanUpCallback
    {

        private CleanUpCallback()
        {}

        /**
         * Removes the specified notification object from the handler notification list.
         * @param pNotification The notification object that calls this function
         */
        public void execute(Notification pNotification)
        {
            if (INSTANCE.aNotificationList.contains(pNotification))
            {
                aNotificationList.remove(pNotification);
            }
        }

    }

    private static final NotificationHandler INSTANCE = new NotificationHandler();

    private Stage aMainStage;
    private LinkedList<Notification> aNotificationList = new LinkedList<>();

    private NotificationHandler()
    {}

    /**
     * @return The NotificationHandler singleton instance
     */
    public static NotificationHandler instance()
    { return INSTANCE; }

    /**
     * Sets the parent stage of all the notification stages.
     * @param pStage The target parent stage of the notification objects
     */
    public void setMainStage(Stage pStage)
    {
        this.aMainStage = pStage;
    }

}
