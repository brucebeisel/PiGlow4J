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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;

/**
 *
 * @author Bruce Beisel
 */
public class PiGlow4JExample1 {
    public static final void main(String args[]) {

        try {
            PiGlowLED.setGammaCorrectionMode(true);
            //I2CFactory.setFactory(new I2CFactoryProviderSwing());
            PiGlow pg = PiGlow.getInstance();
            if (pg == null)
                System.exit(1);

            PiGlowBlinker leftBlinker =  new PiGlowBlinker(100, 0, 1500, 0, 255, 5, true, true, 2, PiGlowLED.armLEDs(PiGlowArm.LEFT));
            PiGlowBlinker rightBlinker = new PiGlowBlinker(  0, 0, 1500, 0, 255, 5, true, true, 2, PiGlowLED.armLEDs(PiGlowArm.RIGHT));
            PiGlowBlinker topBlinker =   new PiGlowBlinker(200, 0, 1500, 0, 255, 5, true, true, 2, PiGlowLED.armLEDs(PiGlowArm.TOP));
            PiGlowAnimator animator = new PiGlowAnimator(pg);
            animator.addAnimation(leftBlinker);
            animator.addAnimation(rightBlinker);
            animator.addAnimation(topBlinker);
            animator.start();
	    leftBlinker.setEnabled(false);
	    animator.waitForTermination(300000);
	    pg.allOff();
            System.out.println("here");

            animator = new PiGlowAnimator(pg);
            PiGlowBlinker blueBlinker = new PiGlowBlinker(0, 1000, 0, 255, 5, PiGlowLED.colorLEDs(PiGlowColor.BLUE));
            animator.addAnimation(blueBlinker);
            animator.start();
	    animator.waitForTermination(300000);
            Thread.sleep(2000);
	    pg.allOff();
            System.out.println("here 2");

            animator = new PiGlowAnimator(pg);
            PiGlowAnimation animation;
            animation = new PiGlowOneShot(0, 255, PiGlowLED.armLEDs(PiGlowArm.TOP));
            animator.addAnimation(animation);
            animation = new PiGlowOneShot(500, 0, PiGlowLED.armLEDs(PiGlowArm.TOP));
            animator.addAnimation(animation);
            animation = new PiGlowOneShot(500, 255, PiGlowLED.armLEDs(PiGlowArm.LEFT));
            animator.addAnimation(animation);
            animation = new PiGlowOneShot(1000, 0, PiGlowLED.armLEDs(PiGlowArm.LEFT));
            animator.addAnimation(animation);
            animation = new PiGlowOneShot(1000, 255, PiGlowLED.armLEDs(PiGlowArm.RIGHT));
            animator.addAnimation(animation);
            animation = new PiGlowOneShot(1500, 0, PiGlowLED.armLEDs(PiGlowArm.RIGHT));
            animator.addAnimation(animation);
            animator.start();
	    animator.waitForTermination(300000);

	    PiGlowLED.allLEDs().forEach((led) -> led.setIntensity(255));
            pg.updateLEDs();

            System.out.println("Done");
        }
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(PiGlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
