package org.jetuml.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import org.jetuml.JavaFXLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TestNotificationService {

    private static Field aListField;
    private static Field aStageField;
    private static Stage aStage;
    private static NotificationService aNotificationService;

    /*
     * Allows waiting for the platform instructions to be executed before continuing on.
     * It then prevents having a test finishing before the JavaFX instructions are run.
     */
    public static void waitForRunLater() throws InterruptedException
    {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    @BeforeAll
    public static void setup() throws ReflectiveOperationException, InterruptedException
    {
        aListField = NotificationService.class.getDeclaredField("aNotifications");
        aListField.setAccessible(true);
        aStageField = ToastNotification.class.getDeclaredField("aStage");
        aStageField.setAccessible(true);

        aNotificationService = NotificationService.instance();

        // Manually setting the parent stage of the NotificationService
        JavaFXLoader.load();
        Platform.runLater(() -> {
            aStage = new Stage();
            NotificationService.instance().setMainStage(aStage);
            aStage.show();
        });
        waitForRunLater();

        // Adding the Stage position listener for testing notification anchoring to the window
        ChangeListener<Number> stageMoveListener = (pObservableValue, pOldValue, pNewValue) ->
                NotificationService.instance().updateNotificationPosition();
        aStage.xProperty().addListener(stageMoveListener);
        aStage.yProperty().addListener(stageMoveListener);
    }

    @AfterAll
    public static void closeStage() throws InterruptedException {
        Platform.runLater(() -> aStage.close());
        waitForRunLater();
        NotificationService.instance().setMainStage(null);
    }

    @BeforeEach
    public void resetList() throws ReflectiveOperationException
    {
        ArrayList<Notification> newList = new ArrayList<>();
        aListField.set(aNotificationService, newList);
    }

    @Test
    public void testSpawnToast() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aNotificationService.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aNotificationService.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
            aNotificationService.spawnNotification("This is a test warning notification.", ToastNotification.Type.WARNING);
            aNotificationService.spawnNotification("This is a test success notification.", ToastNotification.Type.SUCCESS);
        });

        waitForRunLater();

        List<Notification> notificationList = (List<Notification>) aListField.get(aNotificationService);
        assertEquals(4, notificationList.size());
    }

    /*
        Notifications should stack, one on top of the other.
     */
    @Test
    public void testNotificationPosition() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aNotificationService.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aNotificationService.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
            aNotificationService.spawnNotification("This is a test warning notification.", ToastNotification.Type.WARNING);
        });
        waitForRunLater();

        ArrayList<Notification> notificationList = (ArrayList<Notification>) aListField.get(aNotificationService);

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));
        Stage stage2 = (Stage) aStageField.get(notificationList.get(1));
        Stage stage3 = (Stage) aStageField.get(notificationList.get(2));

        assertTrue(stage1.getY() < stage2.getY());
        assertTrue(stage2.getY() < stage3.getY());
    }

    /*
        If the stage is moved, the notifications should move as well.
     */
    @Test
    public void testNotificationPositionWhenStageMoved() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aStage.setX(0.5);
            aNotificationService.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aNotificationService.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        ArrayList<Notification> notificationList = (ArrayList<Notification>) aListField.get(aNotificationService);

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));
        Stage stage2 = (Stage) aStageField.get(notificationList.get(1));

        double stage1X = stage1.getX();
        double stage2X = stage2.getX();

        Platform.runLater(() -> aStage.setX(200));
        waitForRunLater();

        assertTrue(stage1.getX() > stage1X);
        assertTrue(stage2.getX() > stage2X);

        assertTrue(stage1.getY() < stage2.getY());
    }

    @Test
    public void testNotificationPositionWhenRemoval() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aNotificationService.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aNotificationService.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        ArrayList<Notification> notificationList = (ArrayList<Notification>) aListField.get(aNotificationService);

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));

        double stage1Y = stage1.getY();

        notificationList.remove(1);
        aNotificationService.updateNotificationPosition();

        assertTrue(stage1.getY() > stage1Y);
    }

}
