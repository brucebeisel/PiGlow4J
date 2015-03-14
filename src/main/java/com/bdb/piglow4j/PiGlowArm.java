/*
 * Copyright (C) 2015 Bruce Beisel
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.piglow4j;

/**
 * The different PiGlow arms of LEDs.
 * 
 * @author Bruce
 */
public enum PiGlowArm {
    /**
     * The arm of LEDs whose red LED is at the top of the PiGlow board.
     */
    TOP,
    /**
     * The arm of LEDs whose red LED is on the right of the PiGlow board.
     */
    RIGHT,
    /**
     * The arm of LEDs whose red LED is on the left of the PiGlow board.
     */
    LEFT
}
