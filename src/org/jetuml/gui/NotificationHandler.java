package org.jetuml.gui;

import javafx.stage.Stage;

import java.util.LinkedList;

/**
 * Singleton object that manages the notification object positions and display states.
 */
public final class NotificationHandler
{

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
