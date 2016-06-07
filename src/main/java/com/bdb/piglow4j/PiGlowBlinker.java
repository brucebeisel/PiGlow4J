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
import java.util.Arrays;
import java.util.List;

/**
 * Animation that blinks one or more LEDs. The blinking can be a simple on off, or the increase in intensity can be stepped both
 * upward and downward.
 * 
 * @author Bruce Beisel
 */
public final class PiGlowBlinker extends PiGlowAnimation {
    private final int delay;
    private final int repetitionDelay;
    private final int stepInterval;    // The interval between intensity steps
    private final int lowIntensity;
    private final int highIntensity;
    private final int blinkDuration;
    private final int steps;
    private final boolean lowToHigh;
    private final boolean reverse;
    private final int repetitions;
    private final List<PiGlowLED> leds;

    private int currentStep = 0;
    private int frame;
    private int currentIntensity;
    private long nextStepTime;
    private int deltaIntensity;
    private int initialIntensity;
    private int count;

    /**
     * Constructor.
     * 
     * @param delayMillis The initial delay before the animation starts
     * @param repetitionDelayMillis The stepInterval between repetitions
     * @param blinkDuration The amount of time it takes to go from lowIntensity to highIntensity
     * @param lowIntensity The starting intensity
     * @param highIntensity The highest intensity
     * @param steps The number of steps required to go from the low to the high intensity. Note that (high - low) % step and blinkDuration % steps must equal 0
     * @param lowToHigh Whether to animate low to high or high to low
     * @param reverse Whether the animation will reverse when the high intensity is reached.
     * @param repetitions The number of times the animation will repeat
     * @param leds The list of LEDs that will be animated
     */
    public PiGlowBlinker(int delayMillis, int repetitionDelayMillis, int blinkDuration, int lowIntensity, int highIntensity, int steps, boolean lowToHigh, boolean reverse, int repetitions, List<PiGlowLED> leds) {
        this.delay = delayMillis;
        this.repetitionDelay = repetitionDelayMillis;
        this.blinkDuration = blinkDuration;
        this.stepInterval = blinkDuration / steps;
        this.lowIntensity = lowIntensity;
        this.highIntensity = highIntensity;
        this.steps = steps;
        this.lowToHigh = lowToHigh;
        this.reverse = reverse;
        this.repetitions = repetitions;
        this.leds = new ArrayList<>(leds);
	this.addManagedLEDs(leds);
    }

    /**
     * Constructor.
     * 
     * @param delayMillis The initial delay before the animation starts
     * @param blinkDuration The amount of time it takes to go from lowIntensity to highIntensity
     * @param lowIntensity The starting intensity
     * @param highIntensity The highest intensity
     * @param steps The number of steps required to go from the low to the high intensity. Note that (high - low) % step must equal 0
     * @param lowToHigh Whether to animate low to high or high to low
     * @param reverse Whether the animation will reverse when the high intensity is reached.
     * @param leds The list of LEDs that will be animated
     */
    public PiGlowBlinker(int delayMillis, int blinkDuration, int lowIntensity, int highIntensity, int steps, boolean lowToHigh, boolean reverse, List<PiGlowLED> leds) {
        this(delayMillis, 0, blinkDuration, lowIntensity, highIntensity, steps, lowToHigh, reverse, 0, leds);
    }
    /**
     * Constructor.
     * 
     * @param delayMillis The initial delay before the animation starts
     * @param repetitionDelayMillis The stepInterval between repetitions
     * @param blinkDuration The amount of time it takes to go from lowIntensity to highIntensity
     * @param lowIntensity The starting intensity
     * @param highIntensity The highest intensity
     * @param steps The number of steps required to go from the low to the high intensity. Note that (high - low) % step must equal 0
     * @param lowToHigh Whether to animate low to high or high to low
     * @param reverse Whether the animation will reverse when the high intensity is reached.
     * @param repetitions The number of times the animation will repeat
     * @param led The LED that will be animated
     */
    public PiGlowBlinker(int delayMillis, int repetitionDelayMillis, int blinkDuration, int lowIntensity, int highIntensity, int steps, boolean lowToHigh, boolean reverse, int repetitions, PiGlowLED led) {
        this(delayMillis, repetitionDelayMillis, blinkDuration, lowIntensity, highIntensity, steps, lowToHigh, reverse, repetitions, Arrays.asList(led));
    }

    /**
     * Constructor that creates a simple blinking animation for a list of LEDs.
     * 
     * @param delayMillis The initial delay before the animation starts
     * @param blinkInterval The rate at which the LEDs will blink, each low to high to low transition will take this amount of time.
     * @param lowIntensity The starting intensity
     * @param highIntensity The highest intensity
     * @param repetitions The number of times the animation will repeat
     * @param leds The LED that will be animated
     */
    public PiGlowBlinker(int delayMillis, int blinkInterval, int lowIntensity, int highIntensity, int repetitions, List<PiGlowLED> leds) {
        this(delayMillis, blinkInterval / 2, blinkInterval / 2, lowIntensity, highIntensity, 1, true, false, repetitions, leds);
    }

    /**
     * Constructor that creates a simple blinking animation for a single LED.
     * 
     * @param delayMillis The initial delay before the animation starts
     * @param blinkInterval The rate at which the LEDs will blink, each low to high to low transition will take this amount of time.
     * @param lowIntensity The starting intensity
     * @param highIntensity The highest intensity
     * @param repetitions The number of times the animation will repeat
     * @param led The LED that will be animated
     */
    public PiGlowBlinker(int delayMillis, int blinkInterval, int lowIntensity, int highIntensity, int repetitions, PiGlowLED led) {
        this(delayMillis, blinkInterval, lowIntensity, highIntensity, repetitions, Arrays.asList(led));
    }

    @Override
    public void initialize(long now) {
        nextStepTime = now + delay;
        currentStep = 0;
	count = 0;
        frame = 0;

        deltaIntensity = (highIntensity - lowIntensity) / steps;

        if (lowToHigh) {
            initialIntensity = lowIntensity;
        }
        else {
            initialIntensity = highIntensity;
            deltaIntensity = -deltaIntensity;
        }

        currentIntensity = initialIntensity;
    }

    @Override
    public long nextStepMillis(long now) {
	if (count >= repetitions)
	    return PiGlowAnimation.ANIMATION_COMPLETE;
	else
	    return nextStepTime - now;
    }

    @Override
    public void executeNextStep(long now) {
        //
        // Do nothing if the time of the next change has not been reached yet
        //
        if (now < nextStepTime)
            return;

        //
        // Set the intensities for the LEDs in this animation
        //
        leds.forEach((led) -> led.setIntensity(currentIntensity));

        //
        // Calculate the intensities and time for the next step
        //
        currentStep++;
        if (reverse) {
            if (currentStep <= steps)
                frame++;
            else
                frame--;
        }
        else
            frame++;


        //
        // Figure out if the animation cycle has finished and needs to be restarted
        //
        if ((reverse && frame == 0) || (!reverse && frame > steps)) {
            currentStep = 0;
            frame = 0;
            count++;
            nextStepTime = nextStepTime + repetitionDelay;
            currentIntensity = initialIntensity;
        }
        else {
            nextStepTime += stepInterval;
            currentIntensity = initialIntensity + (frame * deltaIntensity);
        }
    }
}