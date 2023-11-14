package org.jetuml.gui;

import javafx.application.Platform;
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

public class TestEditorFrame {

    private static Field aListField;
    private static Field aStageField;
    private static Stage aStage;
    private static EditorFrame aFrame;

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
        aListField = EditorFrame.class.getDeclaredField("aNotifications");
        aListField.setAccessible(true);
        aStageField = ToastNotification.class.getDeclaredField("aStage");
        aStageField.setAccessible(true);

        JavaFXLoader.load();
        Platform.runLater(() -> {
            aStage = new Stage();
            aFrame = new EditorFrame(aStage);
            aStage.show();
        });
        waitForRunLater();
    }

    @AfterAll
    public static void closeStage() throws InterruptedException {
        Platform.runLater(() -> aStage.close());
        waitForRunLater();
    }

    @BeforeEach
    public void resetList() throws ReflectiveOperationException
    {
        ArrayList<Notification> newList = new ArrayList<>();
        aListField.set(aFrame, newList);
    }

    @Test
    public void testSpawnToast() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aFrame.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aFrame.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
            aFrame.spawnNotification("This is a test warning notification.", ToastNotification.Type.WARNING);
            aFrame.spawnNotification("This is a test success notification.", ToastNotification.Type.SUCCESS);
        });

        waitForRunLater();

        List<Notification> notificationList = (List<Notification>) aListField.get(aFrame);
        assertEquals(4, notificationList.size());
    }

    @Test
    public void testNotificationPosition() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aFrame.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aFrame.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
            aFrame.spawnNotification("This is a test warning notification.", ToastNotification.Type.WARNING);
        });
        waitForRunLater();

        ArrayList<Notification> notificationList = (ArrayList<Notification>) aListField.get(aFrame);

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));
        Stage stage2 = (Stage) aStageField.get(notificationList.get(1));
        Stage stage3 = (Stage) aStageField.get(notificationList.get(2));

        assertTrue(stage1.getY() < stage2.getY());
        assertTrue(stage2.getY() < stage3.getY());
    }

    @Test
    public void testNotificationPositionWhenStageMoved() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aStage.setX(0.5);
            aFrame.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aFrame.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        ArrayList<Notification> notificationList = (ArrayList<Notification>) aListField.get(aFrame);

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));
        Stage stage2 = (Stage) aStageField.get(notificationList.get(1));

        double stage1X = stage1.getX();
        double stage2X = stage2.getX();

        Platform.runLater(() -> aStage.setX(200));
        waitForRunLater();

        assertTrue(stage1.getX() > stage1X);
        assertTrue(stage2.getX() > stage2X);
    }

    @Test
    public void testNotificationPositionWhenRemoval() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
            aFrame.spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            aFrame.spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        ArrayList<Notification> notificationList = (ArrayList<Notification>) aListField.get(aFrame);

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));

        double stage1Y = stage1.getY();

        notificationList.remove(1);
        aFrame.updateNotificationPosition();

        assertTrue(stage1.getY() > stage1Y);
    }

}
