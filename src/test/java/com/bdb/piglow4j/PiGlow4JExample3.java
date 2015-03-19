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

import com.bdb.piglow4j.sim.I2CFactoryProviderSwing;
import com.pi4j.io.i2c.I2CFactory;

/**
 *
 * @author Bruce Beisel
 */
public class PiGlow4JExample3 {
    public static final void main(String args[]) {
        //
        // Gamma correction is only recommended when using the PiGlow board, not the GUI simulator
        //
        PiGlowLED.setGammaCorrectionMode(true);
        //
        // Any arugment will force the use of the GUI simulator
        //if (args.length > 0)
            I2CFactory.setFactory(new I2CFactoryProviderSwing());

        //
        // Create the PiGlow and initialize the board
        //
        PiGlow piglow = PiGlow.getInstance();
        if (piglow == null)
            System.exit(1);

        //
        // PiGlow4J allows you to control the LEDs using two different methods: manual and animated.
        // In manual mode you make the changes you want to the LEDs and tell the board to refresh. You 
        // are also responsible for all timers/delays that are required to fit your needs.
        // In automatic mode you set up animations and add them to an animator that will control the LEDs
        // for you, including all of the timers needed.
        //

        try {
            PiGlowLED led;
            //
            // Set the intensity of the white LED on the top arm to about half intensity.
            // Note that the arms are named based on where the red LED resides
            //
            piglow.setLEDIntensity(PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.WHITE), 200);
            Thread.sleep(2000);

            //
            // Set the intensities of the three red LEDs then write to the PiGlow board.
            // Note that the calls to setIntensity() will not cause any changes to the physical LEDs,
            // that will not occur until the call to updateLEDs().
            //
            PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.RED).setIntensity(200);
            PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.RED).setIntensity(200);
            PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.RED).setIntensity(200);
            piglow.updateLEDs();
            Thread.sleep(2000);

            //
            // You can also use lambda expressions to set intensities. This example set the intensity for each
            // LED that is farther out on the arm than the green LED (yellow, orange and red in this case)
            //
            piglow.allOff();
            PiGlowLED.allLEDs().stream().filter((l2) -> l2.getColor().compareTo(PiGlowColor.GREEN) > 0).forEach((l3) -> l3.setIntensity(200));
            piglow.updateLEDs();
            Thread.sleep(2000);

            //
            // You can also control your own timing to create animations. The use of sleep is not recommended, but it is
            // acceptable in simple applications. In this example the top orange LED is flashed at a 1 Hz rate.
            //
            piglow.allOff();
            led = PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.ORANGE);
            for (int i = 0; i < 5; i++) {
                led.setIntensity(255);
                piglow.updateLEDs();
                Thread.sleep(500);
                led.setIntensity(0);
                piglow.updateLEDs();
                Thread.sleep(500);
            }


            //
            // Finally you can use the animator. The animator uses an Executor to schedule changes to the LEDs.
            // PiGlow4J provides three different animations.
            // A sequencer, that executes a sequence of changes separated by a time delay.
            // A one shot, that makes a single change to one of more LEDs
            // A blinker, that will ramp the intensity of one or more LEDs.
            // A single animator can take many animations and it will schedule the LED changes as needed.
            // The exmaple below will cause all three arms to flash each LED in sequence, starting with the white and
            // ending with the red. This pattern will repeat 5 times.
            //
            PiGlowAnimator animator = new PiGlowAnimator(piglow);
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
            piglow.allOff();
            System.out.println("Done");
        }
        catch (IOException | InterruptedException e) {

        }
    }
}