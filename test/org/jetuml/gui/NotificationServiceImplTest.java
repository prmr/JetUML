/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Semaphore;

import org.jetuml.JavaFXLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javafx.application.Platform;
import javafx.stage.Stage;

/*
 * Some of the tests in this class have timeouts because the tests
 * are only valid while a notification is alive, and notifications
 * have self timeout. The lightweight strategy is simply to assume
 * that the tests run more or less instantly, and add a timeout with
 * a value that is lower than the expected notification death to avoid
 * falsely failing the test in case of unexpected delays. 
 */
public class NotificationServiceImplTest {

	private static final int NOTIFICATION_DURATION = 5;
	private static final int STAGE_X = -10000;
	private static final int STAGE_Y = -10000;
	private static final int STAGE_WIDTH = 1000;
	private static final int STAGE_HEIGHT = 300;

	private Stage aStage;
	private NotificationServiceImpl aNotificationService;

	static {
		JavaFXLoader.load();
	}

	NotificationServiceImplTest() throws InterruptedException {

		runInJavaFX(() -> {
			aStage = new Stage();
			aStage.setX(STAGE_X);
			aStage.setY(STAGE_Y);
			aStage.setWidth(STAGE_WIDTH);
			aStage.setHeight(STAGE_HEIGHT);
			aNotificationService = new NotificationServiceImpl(aStage, () -> NOTIFICATION_DURATION);
			aStage.show();
		});
	}

	@BeforeEach
	@Timeout(1)
	void checkTimeout() {} // Ensures that the tests finish before any notification expires

	@AfterEach
	void tearDown() throws InterruptedException {
		runInJavaFX(aStage::close);
	}

	/*
	 * Runs some instructions in the JavaFX event thread and waits for these
	 * instructions to finish before returning. It then prevents having a test
	 * running before the JavaFX instructions are run.
	 */
	private static void runInJavaFX(Runnable pRunnable) {
		try {
			Platform.runLater(pRunnable);
			Semaphore semaphore = new Semaphore(0);
			Platform.runLater(semaphore::release);
			semaphore.acquire();
		}
		catch (InterruptedException exception) {
			Assertions.fail();
		}
	}

	@Test
	void testNotifyError_SingleNotification() {
		runInJavaFX(() -> aNotificationService.notifyError("Error"));
		assertEquals(1, aNotificationService.notifications().size());
	}

	@Test
	void testNotifySuccess_SingleNotification() {
		runInJavaFX(() -> aNotificationService.notifySuccess("Sucess"));
		assertEquals(1, aNotificationService.notifications().size());
	}

	/*
	 * notifyError and notifySuccess have essentially the same logic. The remaining
	 * tests only need to be executed on one of the two. We choose notifyError
	 * arbitrarily.
	 */

	@Test
	void testNotify_TwoNotifications() {
		runInJavaFX(() -> {
			aNotificationService.notifyError("Error 1");
			aNotificationService.notifyError("Error 2");
		});
		assertEquals(2, aNotificationService.notifications().size());
	}

	@Test
	void testPosition_SingleNotificationPosition() {
		runInJavaFX(() -> aNotificationService.notifyError("Error"));
		Stage stage = aNotificationService.notifications().get(0).stage();

		// Notifications are left-aligned with a margin from the edge
		double expectedX = STAGE_X + NotificationServiceImpl.NOTIFICATION_DISPLAY_X_MARGIN;

		// Notifications are bottom-aligned with a margin from the edge
		double expectedY = STAGE_Y + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				stage.getHeight();

		assertEquals(expectedX, stage.getX());
		assertEquals(expectedY, stage.getY());
	}

	@Test
	void testPosition_StackedNotificationsPositions() {
		runInJavaFX(() -> aNotificationService.notifyError("Error 1"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 2"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 3"));

		double expectedX = STAGE_X + NotificationServiceImpl.NOTIFICATION_DISPLAY_X_MARGIN;

		// Third = bottom notification
		Stage stage1 = aNotificationService.notifications().get(2).stage();
		double expectedY1 = STAGE_Y + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				stage1.getHeight();
		assertEquals(expectedX, stage1.getX());
		assertEquals(expectedY1, stage1.getY());

		// Second = 2nd notification from the bottom
		Stage stage2 = aNotificationService.notifications().get(1).stage();
		double expectedY2 = STAGE_Y + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING - stage1.getHeight() - stage2.getHeight();
		assertEquals(expectedX, stage2.getX());
		assertEquals(expectedY2, stage2.getY());

		// First = 3rd notification from the bottom
		Stage stage3 = aNotificationService.notifications().get(0).stage();
		double expectedY3 = STAGE_Y + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING * 2 - stage1.getHeight() - stage2.getHeight() - stage3.getHeight();
		assertEquals(expectedX, stage3.getX());
		assertEquals(expectedY3, stage3.getY());
	}

	@Test
	void testStageMoved() {
		runInJavaFX(() -> aNotificationService.notifyError("Error 1"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 2"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 3"));
		
		final int newX = -9000;
		final int newY = -9000;
		runInJavaFX(() -> {
			aStage.setX(newX);
			aStage.setY(newY);
		});
		
		double expectedX = newX + NotificationServiceImpl.NOTIFICATION_DISPLAY_X_MARGIN;

		// Third = bottom notification
		Stage stage1 = aNotificationService.notifications().get(2).stage();
		double expectedY1 = newY + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				stage1.getHeight();
		assertEquals(expectedX, stage1.getX());
		assertEquals(expectedY1, stage1.getY());

		// Second = 2nd notification from the bottom
		Stage stage2 = aNotificationService.notifications().get(1).stage();
		double expectedY2 = newY + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING - stage1.getHeight() - stage2.getHeight();
		assertEquals(expectedX, stage2.getX());
		assertEquals(expectedY2, stage2.getY());

		// First = 3rd notification from the bottom
		Stage stage3 = aNotificationService.notifications().get(0).stage();
		double expectedY3 = newY + STAGE_HEIGHT - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING * 2 - stage1.getHeight() - stage2.getHeight() - stage3.getHeight();
		assertEquals(expectedX, stage3.getX());
		assertEquals(expectedY3, stage3.getY());
	}
	
	@Test
	void testStageResized() {
		runInJavaFX(() -> aNotificationService.notifyError("Error 1"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 2"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 3"));
		
		final int stageHeight = 400;
		runInJavaFX(() -> aStage.setHeight(stageHeight));
		
		double expectedX = STAGE_X + NotificationServiceImpl.NOTIFICATION_DISPLAY_X_MARGIN;

		// Third = bottom notification
		Stage stage1 = aNotificationService.notifications().get(2).stage();
		double expectedY1 = STAGE_Y + stageHeight - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				stage1.getHeight();
		assertEquals(expectedX, stage1.getX());
		assertEquals(expectedY1, stage1.getY());

		// Second = 2nd notification from the bottom
		Stage stage2 = aNotificationService.notifications().get(1).stage();
		double expectedY2 = STAGE_Y + stageHeight - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING - stage1.getHeight() - stage2.getHeight();
		assertEquals(expectedX, stage2.getX());
		assertEquals(expectedY2, stage2.getY());

		// First = 3rd notification from the bottom
		Stage stage3 = aNotificationService.notifications().get(0).stage();
		double expectedY3 = STAGE_Y + stageHeight - NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN -
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING * 2 - stage1.getHeight() - stage2.getHeight() - stage3.getHeight();
		assertEquals(expectedX, stage3.getX());
		assertEquals(expectedY3, stage3.getY());
	}
	
	/*
	 * Notifications that overflow the stage height should be deleted.
	 */
	@Test
	void testNotificationsOverflowStageHeight() {
		
		runInJavaFX(() -> aNotificationService.notifyError("Error 1"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 2"));
		runInJavaFX(() -> aNotificationService.notifyError("Error 3"));
		
		// We artificially set the stage height to only fit two notifications
		final double newHeight = aNotificationService.notifications().get(0).stage().getHeight() * 2 + 
				NotificationServiceImpl.NOTIFICATION_DISPLAY_Y_MARGIN + 
				NotificationServiceImpl.NOTIFICATION_DISPLAY_SPACING * 2; // *2 is for a bit of extra buffer
		
		runInJavaFX(() -> aStage.setHeight(newHeight));
		assertEquals(2, aNotificationService.notifications().size());
	}
	

}
