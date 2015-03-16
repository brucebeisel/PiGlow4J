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


import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;

/**
 *
 * @author Bruce Beisel
 */
public class PiGlow4JExample2 {
    public static final void main(String args[]) {
        try {
            PiGlowLED.setGammaCorrectionMode(true);
            I2CFactory.setFactory(new I2CFactoryProviderSwing());
            PiGlow pg = PiGlow.getInstance();
            if (pg == null)
                System.exit(1);

            PiGlowAnimator animator = new PiGlowAnimator(pg);
            PiGlowSequence sequence = new PiGlowSequence(5);
            sequence.addSequence(1000, PiGlowLED.colorLEDs(PiGlowColor.WHITE), 255);
            sequence.addSequence(100, PiGlowLED.colorLEDs(PiGlowColor.WHITE), 0);
            sequence.addSequence(0, PiGlowLED.colorLEDs(PiGlowColor.BLUE), 255);
            sequence.addSequence(100, PiGlowLED.colorLEDs(PiGlowColor.BLUE), 0);
            sequence.addSequence(0, PiGlowLED.colorLEDs(PiGlowColor.GREEN), 255);
            sequence.addSequence(100, PiGlowLED.colorLEDs(PiGlowColor.GREEN), 0);
            sequence.addSequence(0, PiGlowLED.colorLEDs(PiGlowColor.YELLOW), 255);
            sequence.addSequence(100, PiGlowLED.colorLEDs(PiGlowColor.YELLOW), 0);
            sequence.addSequence(0, PiGlowLED.colorLEDs(PiGlowColor.ORANGE), 255);
            sequence.addSequence(100, PiGlowLED.colorLEDs(PiGlowColor.ORANGE), 0);
            sequence.addSequence(0, PiGlowLED.colorLEDs(PiGlowColor.RED), 255);
            sequence.addSequence(100, PiGlowLED.colorLEDs(PiGlowColor.RED), 0);
            animator.addAnimation(sequence);
            animator.start();
	    animator.waitForTermination(300000);
	    pg.allOff();
            System.out.println("Done");
        }
        catch (InterruptedException e) {

        }
    }
}
