package org.jetuml.gui;

import javafx.stage.Stage;
import org.jetuml.annotations.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton object that manages the notification object positions and display states.
 */
@Singleton
public final class NotificationHandler
{

    private static final int NOTIFICATION_DISPLAY_SPACING = 8;
    private static final int NOTIFICATION_DISPLAY_X_MARGIN = 18;
    private static final int NOTIFICATION_DISPLAY_Y_MARGIN = 18;

    private static final NotificationHandler INSTANCE = new NotificationHandler();

    private Stage aMainStage;
    private final List<Notification> aNotifications = new ArrayList<>();

    private NotificationHandler() {}

    /**
     * @return The NotificationHandler singleton instance
     */
    public static NotificationHandler instance()
    {
        return INSTANCE;
    }

    /**
     * Sets the parent stage of all the notification stages.
     *
     * @param pStage The target parent stage of the notification objects
     */
    public void setMainStage(Stage pStage)
    {
        this.aMainStage = pStage;
    }

    /**
     * @return The application stage
     */
    public Stage getMainStage()
    {
        return this.aMainStage;
    }

    /**
     * Rearranges the notifications so that they are stacked properly and do not overlap.
     */
    public void updateNotificationPosition()
    {
        double y = aMainStage.getY() + aMainStage.getHeight() - NOTIFICATION_DISPLAY_Y_MARGIN;
        double x = aMainStage.getX() + NOTIFICATION_DISPLAY_X_MARGIN;

        ArrayList<Notification> reverseNotifications = new ArrayList<>(aNotifications);
        Collections.reverse(reverseNotifications);
        for (Notification notification : reverseNotifications)
        {
            notification.setPosition(x, y);
            y = y - notification.getHeight() - NOTIFICATION_DISPLAY_SPACING;
        }
    }

    /**
     * Spawns a notification (it should be instantiated first).
     *
     * @param pNotification The notification object to spawn
     */
    public void spawnNotification(Notification pNotification)
    {
        if (aMainStage == null)
        {
            return;
        }

        aNotifications.add(pNotification);

        pNotification.show(() -> aNotifications.remove(pNotification));
        updateNotificationPosition();
    }

    /**
     * Spawns a new toast notification without having to pass a ToastNotification object, i.e. without having
     * to fetch the main stage.
     *
     * @param pText The text to show on the toast
     */
    public void spawnNotification(String pText, ToastNotification.Type pType)
    {
        if (aMainStage == null)
        {
            return;
        }

        ToastNotification toast = new ToastNotification(pText, pType, aMainStage);
        spawnNotification(toast);
    }
}
