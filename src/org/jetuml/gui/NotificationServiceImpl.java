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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntSupplier;

import org.jetuml.annotations.OnlyForTesting;
import org.jetuml.annotations.VisibleForTesting;
import org.jetuml.application.UserPreferences;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Object that manages the notification object positions and display states.
 */
public final class NotificationServiceImpl implements NotificationService {

	@VisibleForTesting
	static final int NOTIFICATION_DISPLAY_SPACING = 8;
	
	@VisibleForTesting
	static final int NOTIFICATION_DISPLAY_X_MARGIN = 25;
	
	@VisibleForTesting
	static final int NOTIFICATION_DISPLAY_Y_MARGIN = 25;

	private static final String FX_COLOR_ERROR = "rgb(255, 217, 217)";
	private static final String FX_COLOR_SUCCESS = "rgb(224, 255, 217)";

	private static final int MILLISECONDS_PER_SECOND = 1000;
	private static final Color TEXT_COLOR = Color.grayRgb(50);

	private static final int FADE_IN_DELAY = 500;
	private static final int FADE_OUT_DELAY = 500;

	private static final int FX_PADDING = 8;
	private static final int FX_BACKGROUND_RADIUS = 6;
	private static final String FX_FORMAT_STRING = "-fx-padding: %dpx; -fx-background-color: %s; -fx-background-radius: %dpx";

	/*
	 * This attribute sets the parent stage of all notifications. It is set at
	 * initialization, in JetUML.java. It is impossible to spawn a notification if
	 * the main stage attribute is null.
	 */
	private Stage aMainStage;
	private IntSupplier aNotificationDurationInSecondsSupplier;
	private final List<Notification> aNotifications = new ArrayList<>();
	
	/*
	 * Dead notifications are notifications that reached the end of their lifespan
	 * and should be removed. We store them in a separate list so that we can close
	 * them when a new notification is spawned, to prevent having the JetUML
	 * taskbar window flashing. - See Issue #524
	 */
	private final List<Notification> aDeadNotifications = new ArrayList<>();

	/**
	 * Create a new NotificationService implementation.
	 * 
	 * @param pMainStage The parent stage for notifications.
	 * @param pDurationSupplier An supplier for the notification duration, in seconds.
	 */
	public NotificationServiceImpl(Stage pMainStage, IntSupplier pDurationSupplier) {
		assert pMainStage != null;
		aMainStage = pMainStage;
		aNotificationDurationInSecondsSupplier = pDurationSupplier;

		// Window position and size listener for notifications
		ChangeListener<Number> stageTransformationListener = (pObservableValue, pOldValue,
				pNewValue) -> updateNotificationPositions();

		// When the stage is moved, update the notification positions
		aMainStage.xProperty().addListener(stageTransformationListener);
		aMainStage.yProperty().addListener(stageTransformationListener);

		// When the stage is resized, update the notification positions
		aMainStage.heightProperty().addListener(stageTransformationListener);
		aMainStage.widthProperty().addListener(stageTransformationListener);
	}
	
	@OnlyForTesting
	List<Notification> notifications() {
		return Collections.unmodifiableList(aNotifications);
	}

	/*
	 * Rearranges the notifications so that they are stacked properly and do not
	 * overlap.
	 */
	private void updateNotificationPositions() {
		double y = aMainStage.getY() + aMainStage.getHeight() - NOTIFICATION_DISPLAY_Y_MARGIN;
		double x = aMainStage.getX() + NOTIFICATION_DISPLAY_X_MARGIN;

		ArrayList<Notification> reverseNotifications = new ArrayList<>(aNotifications);
		Collections.reverse(reverseNotifications);
		for (Notification notification : reverseNotifications) {
			notification.setPosition(x, y);
			y = y - notification.height() - NOTIFICATION_DISPLAY_SPACING;

			if (y < aMainStage.getY()) {
				notification.close();
				aNotifications.remove(notification);
			}
		}
	}

	private void spawnNotification(String pMessage, String pColorString) {
		assert aMainStage != null;
		Notification notification = new Notification(pMessage, pColorString);
		aDeadNotifications.forEach(Notification::close);
		aDeadNotifications.clear();
		
		int durationInSeconds = aNotificationDurationInSecondsSupplier.getAsInt();
		if (durationInSeconds > 0) {
			aNotifications.add(notification);
			notification.show(durationInSeconds);
			updateNotificationPositions();
		}
	}

	/**
	 * Spawns a new error notification.
	 * 
	 * @param pText The text of the notification.
	 */
	@Override
	public void notifyError(String pText) {
		assert aMainStage != null;
		spawnNotification(pText, FX_COLOR_ERROR);
	}

	/**
	 * Spawns a new success notification.
	 * 
	 * @param pText The text of the notification.
	 */
	@Override
	public void notifySuccess(String pText) {
		assert aMainStage != null;
		spawnNotification(pText, FX_COLOR_SUCCESS);
	}

	private static String styleString(String pColorString) {
		return FX_FORMAT_STRING.formatted(FX_PADDING, pColorString, FX_BACKGROUND_RADIUS);
	}

	private static int fontSize() {
		return UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
	}

	@VisibleForTesting
	final class Notification {

		private final Stage aStage;

		Notification(String pMessage, String pColorString) {
			aStage = new Stage();

			aStage.initOwner(aMainStage);
			aStage.setResizable(false);
			aStage.initStyle(StageStyle.TRANSPARENT);

			Text text = new Text(pMessage);
			text.setFont(Font.font("System", fontSize()));
			text.setFill(TEXT_COLOR);

			StackPane pane = new StackPane(text);
			pane.setStyle(styleString(pColorString));

			Scene scene = new Scene(pane);
			scene.setFill(Color.TRANSPARENT);
			aStage.setScene(scene);
		}

		/*
		 * Shows the Notification object to the screen by fading it in, showing it for a
		 * user-defined amount of time, and fading it out.
		 */
		void show(int pDurationInSeconds) {
			assert pDurationInSeconds > 0;

			aStage.show();
			aStage.getOwner().requestFocus(); // Keeps the main stage focused (and not the notifications)

			Timeline fadeInTimeline = new Timeline();
			KeyFrame fadeInKey = new KeyFrame(Duration.millis(FADE_IN_DELAY),
					new KeyValue(aStage.getScene().getRoot().opacityProperty(), 1));
			fadeInTimeline.getKeyFrames().add(fadeInKey);

			Timeline fadeOutTimeline = new Timeline();
			KeyFrame fadeOutKey = new KeyFrame(Duration.millis(FADE_OUT_DELAY),
					new KeyValue(aStage.getScene().getRoot().opacityProperty(), 0));

			fadeOutTimeline.getKeyFrames().add(fadeOutKey);
			fadeOutTimeline.setOnFinished(event -> {
				aNotifications.remove(this);
				aDeadNotifications.add(this);
			});

			Timer notificationTimer = new Timer();
			TimerTask lifespan = new TimerTask() {

				@Override
				public void run() {
					fadeOutTimeline.play();
				}
			};

			// When the fadein animation ends, we start a timer that will execute
			// the fadeout animation
			fadeInTimeline.setOnFinished(actionEvent -> notificationTimer.schedule(lifespan, 
					pDurationInSeconds * MILLISECONDS_PER_SECOND));

			// We trigger the fadein animation
			fadeInTimeline.play();
		}

		void close() {
			aStage.close();
		}

		// (x,y) is bottom left exceptionally so the client code doesn't need the height of the internal stage
		void setPosition(double pX, double pY) {
			aStage.setX(pX);
			aStage.setY(pY - aStage.getHeight());
		}

		/*
		 * Returns the height of this notification in pixels. The value can be
		 * fractional because the size of the notification is a function of the font
		 * size, which is user-settable.
		 */
		double height() {
			return aStage.getHeight();
		}
		
		@OnlyForTesting
		Stage stage() {
			return aStage;
		}
		
		private String getMessage() {
			return ((Text) aStage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getText();
		}
		
		public String toString() {
			return "[Notification \"%s\" at (%.0f, %.0f)]".formatted(getMessage(), aStage.getX(), aStage.getY());

		}
	}
}
