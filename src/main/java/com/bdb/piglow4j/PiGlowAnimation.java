/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.piglow4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for all animations that are controlled by a <code>PiGlowAnimator</code>.
 *
 * @author Bruce Beisel
 */
public abstract class PiGlowAnimation {
    /**
     * Used as a return from a callback to indicate that the animation should stop
     */
    public static final long ANIMATION_COMPLETE = -1;
    private boolean isEnabled = true;
    private final List<PiGlowLED> managedLEDs = new ArrayList<>(18);

    /**
     * Returns whether this animation is currently enabled.
     * 
     * @return True if enabled
     */
    public final boolean isEnabled() {
	return isEnabled;
    }

    /**
     * Enable or disable an animation.
     * 
     * @param enabled True if the animation is to be enabled
     */
    public final void setEnabled(boolean enabled) {
	isEnabled = enabled;

	//
	// If the animation is being disabled then turn off all of the LEDs in this animation.
	// Note that this is only effective if the derived animation registers its LEDs with
	// this base class.
	//
	if (!enabled)
	    managedLEDs.forEach((led) -> led.setIntensity(0));
    }

    /**
     * Add LEDs to the animation which manages a list to turns off the LEDs when the animation is disabled.
     * 
     * @param leds LEDs to add to the list of managed LEDs
     */
    protected final void addManagedLEDs(List<PiGlowLED> leds) {
	if (managedLEDs.isEmpty())
	    managedLEDs.addAll(leds);
	else {
            leds.stream().filter((led) -> (!managedLEDs.contains(led))).forEach((led) -> {
                managedLEDs.add(led);
            });
	}
    }

    /**
     * Initialize any counters or times that are needed to track the animation.
     * 
     * @param now The current time used to initialize the timing of this animation
     */
    public abstract void initialize(long now);

    /**
     * How many milliseconds to wait before the next step of the animation must run.
     * 
     * @param now The current time used to calculate time when the next change to this animation
     * @return The time when the next change needs to occur for this animation or ANIMATION_COMPLETE if the animation is complete
     */
    public abstract long nextStepMillis(long now);

    /**
     * Change the LEDs if the current time is equal to or past the next step time.
     * 
     * @param now The current time
     */
    public abstract void executeNextStep(long now);
}