/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.gui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.jetuml.JavaFXLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TestNotificationService 
{
	/* The aNotifications field in NotificationService. */
    private static Field aNotifications;
    
    /* The aStage field in ToastNotification. */ 
    private static Field aStageField;
    
    /* A working stage used for testing. */
    private static Stage aStage;

    /*
     * Allows waiting for the platform instructions to be executed before continuing on.
     * It then prevents having a test finishing before the JavaFX instructions are run.
     */
    private static void waitForRunLater() throws InterruptedException
    {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    @BeforeAll
    private static void setup() throws ReflectiveOperationException, InterruptedException
    {
        aNotifications = NotificationService.class.getDeclaredField("aNotifications");
        aNotifications.setAccessible(true);
        aStageField = ToastNotification.class.getDeclaredField("aStage");
        aStageField.setAccessible(true);

        // Manually setting the parent stage of the NotificationService
        JavaFXLoader.load();
        Platform.runLater(() -> {
            aStage = new Stage();
            NotificationService.instance().setMainStage(aStage);
            aStage.show();
        });
        waitForRunLater();
    }

    @AfterAll
    private static void closeStage() throws InterruptedException {
        Platform.runLater(aStage::close);
        waitForRunLater();
        NotificationService.instance().setMainStage(null);
    }

    @SuppressWarnings("static-method") // Cannot be statis as per JUnit
	@BeforeEach
    private void resetListAndProperties() throws ReflectiveOperationException
    {
        ArrayList<Notification> newList = new ArrayList<>();
        aNotifications.set(NotificationService.instance(), newList);

        aStage.setHeight(695.5);
        aStage.setWidth(1125.0);
        aStage.setX(220.5);
        aStage.setY(220.5);
    }

    @Test
    void testSpawnToast() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
        	NotificationService.instance().spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
        	NotificationService.instance().spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        	NotificationService.instance().spawnNotification("This is a test warning notification.", ToastNotification.Type.WARNING);
        	NotificationService.instance().spawnNotification("This is a test success notification.", ToastNotification.Type.SUCCESS);
        });

        waitForRunLater();

        @SuppressWarnings("unchecked")
		List<Notification> notificationList = (List<Notification>) aNotifications.get(NotificationService.instance());
        assertEquals(4, notificationList.size());
    }

    /*
        Notifications should stack, one on top of the other.
     */
    @Test
    void testNotificationPosition() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
        	NotificationService.instance().spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
        	NotificationService.instance().spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        	NotificationService.instance().spawnNotification("This is a test warning notification.", ToastNotification.Type.WARNING);
        });
        waitForRunLater();

        @SuppressWarnings("unchecked")
		ArrayList<Notification> notificationList = (ArrayList<Notification>) aNotifications.get(NotificationService.instance());

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
    void testNotificationPositionWhenStageMoved() throws InterruptedException, ReflectiveOperationException
    {
        aStage.setX(0.5);
        Platform.runLater(() -> {
        	NotificationService.instance().spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
        	NotificationService.instance().spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        @SuppressWarnings("unchecked")
		ArrayList<Notification> notificationList = (ArrayList<Notification>) aNotifications.get(NotificationService.instance());

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));
        Stage stage2 = (Stage) aStageField.get(notificationList.get(1));

        double stage1X = stage1.getX();
        double stage2X = stage2.getX();

        aStage.setX(200);

        assertTrue(stage1.getX() > stage1X);
        assertTrue(stage2.getX() > stage2X);

        assertTrue(stage1.getY() < stage2.getY());
    }

    @Test
    void testNotificationPositionWhenStageResized() throws InterruptedException, ReflectiveOperationException
    {
        aStage.setHeight(600);
        Platform.runLater(() -> {
        	NotificationService.instance().spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
        	NotificationService.instance().spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        @SuppressWarnings("unchecked")
        ArrayList<Notification> notificationList = (ArrayList<Notification>) aNotifications.get(NotificationService.instance());

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));
        Stage stage2 = (Stage) aStageField.get(notificationList.get(1));

        double stage1Y = stage1.getY();
        double stage2Y = stage2.getY();

        aStage.setHeight(300);

        assertNotEquals(stage1.getY(), stage1Y);
        assertNotEquals(stage2.getY(), stage2Y);

        assertTrue(stage1.getY() < stage2.getY());
    }

    @Test
    void testNotificationPositionWhenRemoval() throws InterruptedException, ReflectiveOperationException
    {
        Platform.runLater(() -> {
        	NotificationService.instance().spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
        	NotificationService.instance().spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        @SuppressWarnings("unchecked")
		ArrayList<Notification> notificationList = (ArrayList<Notification>) aNotifications.get(NotificationService.instance());

        Stage stage1 = (Stage) aStageField.get(notificationList.get(0));

        double stage1Y = stage1.getY();

        notificationList.remove(1);
        NotificationService.instance().updateNotificationPosition();

        assertTrue(stage1.getY() > stage1Y);
    }

    /*
        If the notifications overshoot the main window, the oldest ones are removed to keep
        the whole notification stack in the frame.
     */
    @Test
    void testNotificationDeletionWhenWindowBorderReached() throws InterruptedException, ReflectiveOperationException
    {
        Field notificationSpacingField = NotificationService.class.getDeclaredField("NOTIFICATION_DISPLAY_SPACING");
        notificationSpacingField.setAccessible(true);
        Field notificationYMarginField = NotificationService.class.getDeclaredField("NOTIFICATION_DISPLAY_Y_MARGIN");
        notificationYMarginField.setAccessible(true);

        int notificationSpacing = (int) notificationSpacingField.get(null);
        int yMargin = (int) notificationYMarginField.get(null);

        Platform.runLater(() -> {
            ToastNotification errorNotification = new ToastNotification("This is a test error notification.", ToastNotification.Type.ERROR, aStage);
            ToastNotification infoNotification = new ToastNotification("This is a test info notification.", ToastNotification.Type.INFO, aStage);

            NotificationService.instance().spawnNotification(errorNotification);
            NotificationService.instance().spawnNotification(infoNotification);

            // Let's compute the necessary height of the main window to fit two notifications
            double height = yMargin + notificationSpacing*2 + errorNotification.getHeight() + infoNotification.getHeight()+1;
            aStage.setHeight(height);

            NotificationService.instance().spawnNotification("This is a test error notification.", ToastNotification.Type.ERROR);
            NotificationService.instance().spawnNotification("This is a test info notification.", ToastNotification.Type.INFO);
        });
        waitForRunLater();

        @SuppressWarnings("unchecked")
        ArrayList<Notification> notificationList = (ArrayList<Notification>) aNotifications.get(NotificationService.instance());

        assertEquals(2, notificationList.size());
    }
}
