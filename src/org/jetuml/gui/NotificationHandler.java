package org.jetuml.gui;

import javafx.stage.Stage;

import java.util.LinkedList;

/**
 * Singleton object that manages the notification object positions and display states.
 */
public final class NotificationHandler
{

    private static final int NOTIFICATION_STACK_SPACING = 8;
    private static final int NOTIFICATION_STACK_X_MARGIN = 18;
    private static final int NOTIFICATION_STACK_Y_MARGIN = 18;

    /**
     * A callback for Notification objects called when they reached the end of their lifetime.
     * It removes them from the handler notification list.
     */
    public final class CleanUpCallback
    {

        private CleanUpCallback()
        {}

        /**
         * Removes the specified notification object from the handler notification list.
         *
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
     *
     * @param pStage The target parent stage of the notification objects
     */
    public void setMainStage(Stage pStage)
    {
        this.aMainStage = pStage;
    }

    /**
     * Rearranges the notifications so that they are stacked properly and do not overlap.
     */
    public void updatePosition()
    {

        double yBuf = this.aMainStage.getY() + this.aMainStage.getHeight() - NOTIFICATION_STACK_Y_MARGIN;
        double x = this.aMainStage.getX() + NOTIFICATION_STACK_X_MARGIN;

        for (int i = aNotificationList.size() - 1; i >= 0; i--)
        {
            Notification notification = aNotificationList.get(i);
            notification.setX(x);
            notification.setY(yBuf);
            yBuf = yBuf - notification.getHeight() - NOTIFICATION_STACK_SPACING;
        }
    }

    /**
     * Spawns a new toast notification.
     *
     * @param pText The text to show on the toast
     */
    public void spawn(String pText)
    {

        if (this.aMainStage == null)
        {
            return;
        }

        double x = this.aMainStage.getX() + NOTIFICATION_STACK_X_MARGIN;
        double y = this.aMainStage.getY() + this.aMainStage.getHeight() - NOTIFICATION_STACK_Y_MARGIN;

        ToastNotification toast = new ToastNotification(pText, this.aMainStage);

        aNotificationList.add(toast);

        toast.show(x, y, new CleanUpCallback());
        this.updatePosition();

    }

}
