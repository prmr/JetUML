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

/**
 * An object that can be displayed by the EditorFrame spawn() method.
 */
public interface Notification
{

    /**
     * Show the Notification object to the desired coordinates.
     *
     * @param pX The target X position
     * @param pY The target Y position
     */
    void show(double pX, double pY, EditorFrame.CleanUpCallback pCleanUpCallback);

    /**
     * Move the Notification object to the desired X position.
     *
     * @param pX The target X position
     */
    void setX(double pX);

    /**
     * Move the Notification object to the desired Y position.
     *
     * @param pY The target Y position
     */
    void setY(double pY);

    /**
     * @return The height of the Notification object.
     */
    double getHeight();

}
