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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;

/**
 *
 * @author Bruce Beisel
 */
public class PiGlow4JDisableTest {
    public static final void main(String args[]) {

        try {
            PiGlowLED.setGammaCorrectionMode(true);
            I2CFactory.setFactory(new I2CFactoryProviderSwing());
            PiGlow pg = PiGlow.getInstance();
            if (pg == null)
                System.exit(1);

	    PiGlowBlinker topBlinker = new PiGlowBlinker(0, 300, 0, 255, Integer.MAX_VALUE, PiGlowLED.armLEDs(PiGlowArm.TOP));
	    PiGlowBlinker leftBlinker = new PiGlowBlinker(100, 300, 0, 255, Integer.MAX_VALUE, PiGlowLED.armLEDs(PiGlowArm.LEFT));
	    PiGlowBlinker rightBlinker = new PiGlowBlinker(200, 300, 0, 255, Integer.MAX_VALUE, PiGlowLED.armLEDs(PiGlowArm.RIGHT));
            PiGlowAnimator animator = new PiGlowAnimator(pg);
            animator.addAnimation(leftBlinker);
            animator.addAnimation(rightBlinker);
            animator.addAnimation(topBlinker);
            animator.start();
	    
	    for (int i = 0; i < 100; i++) {
		topBlinker.setEnabled(false);
		Thread.sleep(2134);
		topBlinker.setEnabled(true);
		Thread.sleep(2134);
	    }

	    animator.waitForTermination(300000);
	    pg.allOff();
            System.out.println("here");


            System.out.println("Done");
        }
        catch (InterruptedException ex) {
            Logger.getLogger(PiGlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}