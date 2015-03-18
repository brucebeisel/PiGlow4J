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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;

/**
 * This example tries to mimic the animation on the PiGlow web page at pimoroni.com
 *
 * @author Bruce Beisel
 */
public class PiGlow4JExample4 {
    public static final void main(String args[]) {

        try {
            PiGlowLED.setGammaCorrectionMode(true);
            I2CFactory.setFactory(new I2CFactoryProviderSwing());
            PiGlow piGlow = PiGlow.getInstance();
            if (piGlow == null)
                System.exit(1);

            PiGlowAnimator animator = new PiGlowAnimator(piGlow);
            PiGlowAnimation blinker = new PiGlowBlinker(0, 1200, 60, 20, 220, 10, true, false, 100, PiGlowLED.armLEDs(PiGlowArm.LEFT));
            animator.addAnimation(blinker);
            blinker = new PiGlowBlinker(600, 1200, 60, 20, 220, 10, true, false, 100, PiGlowLED.armLEDs(PiGlowArm.RIGHT));
            animator.addAnimation(blinker);
            blinker =  new PiGlowBlinker(1200, 1200, 60, 20, 220, 10, true, false, 100, PiGlowLED.armLEDs(PiGlowArm.TOP));
            animator.addAnimation(blinker);

            PiGlowSequence sequence = new PiGlowSequence(1000);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.RED), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.ORANGE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.YELLOW), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.GREEN), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.BLUE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.WHITE), 5);

            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.YELLOW), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.ORANGE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.RED), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.GREEN), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.BLUE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.WHITE), 5);

            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.WHITE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.BLUE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.GREEN), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.YELLOW), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.ORANGE), 5);
            sequence.addSequence(100, PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.RED), 5);
            animator.addAnimation(sequence);

            animator.start();
	    animator.waitForTermination(300000);
	    piGlow.allOff();
        }
        catch (InterruptedException ex) {
            Logger.getLogger(PiGlow4JExample4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
