/*
 * Copyright (C) 2016 Bruce Beisel
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
 * Animation that will fire a sequence of changes with specified delays between the changes.
 *
 * @author Bruce Beisel
 */
public final class PiGlowSequence extends PiGlowAnimation {
    private static final class Sequence {
        public long startDelay;
        public List<PiGlowLED> leds;
        public int intensity;
        public Sequence(long startDelay, List<PiGlowLED> leds, int intensity) {
            this.startDelay = startDelay;
            this.leds = new ArrayList<>(leds);
            this.intensity = intensity;
        }
    }
    private long currentDelay;
    private final List<Sequence> sequences;
    private long nextStepTime;
    private long initialTime;
    private int stepIndex;
    private final int repetitions;
    private int count;

    /**
     * Constructor.
     * 
     * @param repetitions The number of times this animation will run
     */
    public PiGlowSequence(int repetitions) {
        currentDelay = 0;
        sequences = new ArrayList<>();
        this.repetitions = repetitions;
    }

    /**
     * Add a sequence step with multiple LEDs at the same intensity.
     * 
     * @param milliGap The time gap between the previous sequence and this one
     * @param leds The LEDs that will be changed
     * @param intensity  The intensity that the LEDs will be set to
     */
    public void addSequence(long milliGap, List<PiGlowLED> leds, int intensity) {
        currentDelay += milliGap;
        sequences.add(new Sequence(currentDelay, leds, intensity));
	this.addManagedLEDs(leds);
    }

    /**
     * Add a sequence step with a single LED.
     * 
     * @param milliGap The time gap between the previous sequence and this one
     * @param led The LED that will be changed
     * @param intensity  The intensity that the LEDs will be set to
     */
    public void addSequence(long milliGap, PiGlowLED led, int intensity) {
        addSequence(milliGap, Arrays.asList(led), intensity);
    }

    @Override
    public void initialize(long now) {
        nextStepTime = now + sequences.get(0).startDelay;
        initialTime = now;
        count = 0;
    }

    @Override
    public long nextStepMillis(long now) {
        if (stepIndex >= sequences.size())
            return PiGlowAnimation.ANIMATION_COMPLETE;
        else
            return nextStepTime - now;
    }

    @Override
    public void executeNextStep(long now) {
        if (now < nextStepTime)
            return;

        long delay = sequences.get(stepIndex).startDelay;

        for (int i = stepIndex; i < sequences.size() && sequences.get(i).startDelay == delay; i++) {
            stepIndex++;
            for (PiGlowLED led : sequences.get(i).leds)
                led.setIntensity(sequences.get(i).intensity);
        }

        if (stepIndex < sequences.size())
            nextStepTime = initialTime + sequences.get(stepIndex).startDelay;
        else {
            count++;
            if (count < repetitions) {
                initialTime = now;
                stepIndex = 0;
                nextStepTime = now + sequences.get(0).startDelay;
            }
        }
    }
}