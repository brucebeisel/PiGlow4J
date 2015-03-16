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
package com.bdb.piglow4j.sim;

import com.bdb.piglow4j.PiGlow;
import com.bdb.piglow4j.PiGlowArm;
import com.bdb.piglow4j.PiGlowColor;
import com.bdb.piglow4j.PiGlowLED;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The main Swing component that displays the PiGlow image and updates the simulated LEDs.
 * 
 * @author Bruce Beisel
 */
@SuppressWarnings("serial")
public final class PiGlowJComponent extends JPanel {
    private final LedInfo leds[] = new LedInfo[PiGlow.PIGLOW_LED_COUNT];
    private final Color whites[] = new Color[256];
    private final Color blues[] = new Color[256];
    private final Color yellows[] = new Color[256];
    private final Color greens[] = new Color[256];
    private final Color oranges[] = new Color[256];
    private final Color reds[] = new Color[256];
    private final ImageIcon background;
    private static final Logger logger = Logger.getLogger(PiGlowJComponent.class.getName());

    /**
     * Class the hold the information about each simulated LED.
     */
    private class LedInfo {
        public PiGlowArm arm;
        public PiGlowColor color;
        public int intensity;
        public Color[] colors;
        public JLabel label;
        public LedInfo(PiGlowArm arm, PiGlowColor color, int x, int y, Color[] colors) {
            this.arm = arm;
            this.color = color;
            this.colors = colors;
            label = new JLabel();
            label.setOpaque(true);
            label.setSize(25, 25);
            label.setPreferredSize(new Dimension(25, 25));
            label.setLocation(x, y);
            label.setBackground(colors[0]);
        }
    }

    /**
     * Constructor.
     * 
     * @param image the PiGlow board image
     */
    public PiGlowJComponent(ImageIcon image) {
        setLayout(null);
        setBackground(Color.BLACK);
        background = image;

        //
        // Build the 256 intensities for each LED color
        //
        buildColors(whites, Color.WHITE);
        buildColors(blues, Color.BLUE);
        buildColors(yellows, Color.YELLOW);
        buildColors(greens, Color.GREEN);
        buildColors(oranges, Color.ORANGE);
        buildColors(reds, Color.RED);

        //
        // Create the LEDs for the top LED arm
        //
        LedInfo info = new LedInfo(PiGlowArm.TOP, PiGlowColor.RED, 225, 220, reds);
        leds[PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.RED).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.ORANGE, 173, 255, oranges);
        leds[PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.ORANGE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.YELLOW, 138, 305, yellows);
        leds[PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.YELLOW).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.GREEN, 135, 360, greens);
        leds[PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.GREEN).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.BLUE, 155, 415, blues);
        leds[PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.BLUE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.TOP, PiGlowColor.WHITE, 205, 432, whites);
        leds[PiGlowLED.findLED(PiGlowArm.TOP, PiGlowColor.WHITE).getAddress() - 1] = info;

        //
        // Create the LEDs for the left LED arm
        //
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.RED, 50, 500, reds);
        leds[PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.RED).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.ORANGE, 115, 520, oranges);
        leds[PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.ORANGE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.YELLOW, 175, 525, yellows);
        leds[PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.YELLOW).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.GREEN, 230, 505, greens);
        leds[PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.GREEN).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.BLUE, 265, 460, blues);
        leds[PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.BLUE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.LEFT, PiGlowColor.WHITE, 260, 400, whites);
        leds[PiGlowLED.findLED(PiGlowArm.LEFT, PiGlowColor.WHITE).getAddress() - 1] = info;

        //
        // Create the LEDs for the right LED arm
        //
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.RED, 385, 504, reds);
        leds[PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.RED).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.ORANGE, 380, 445, oranges);
        leds[PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.ORANGE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.YELLOW, 350, 395, yellows);
        leds[PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.YELLOW).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.GREEN, 310, 355, greens);
        leds[PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.GREEN).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.BLUE, 255, 340, blues);
        leds[PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.BLUE).getAddress() - 1] = info;
        info = new LedInfo(PiGlowArm.RIGHT, PiGlowColor.WHITE, 202, 372, whites);
        leds[PiGlowLED.findLED(PiGlowArm.RIGHT, PiGlowColor.WHITE).getAddress() - 1] = info;

        //
        // Add the LED JLabels
        //
        for (LedInfo led : leds)
            add(led.label);

        setPreferredSize(new Dimension(background.getIconWidth(), background.getIconHeight()));
    }

    private void buildColors(Color[] colors, Color baseColor) {
        //
        // Multiply each red, green and blue portion by the 0 - 255 ratio, then create a new color with the results
        //
        for (int i = 0; i <= PiGlowLED.MAX_INTENSITY; i++) {
            float ratio = i / (float)PiGlowLED.MAX_INTENSITY;
            if (ratio > 1.0F)
                ratio = 1.0F;

            int red = (int)(baseColor.getRed() * ratio);
            int green = (int)(baseColor.getGreen() * ratio);
            int blue = (int)(baseColor.getBlue() * ratio);
            colors[i] = new Color(red, green, blue);
        }
    }

    /**
     * Set the intensities for all of the simulated LEDs.
     * 
     * @param intensities The intensities
     */
    public void setIntensities(int[] intensities) {
        logger.fine("Receiving intensities");
        for (int i = 0; i < leds.length; i++) {
            leds[i].intensity = intensities[i];
        }
    }

    /**
     * Commit the new intensity values for all of the LEDs.
     */
    public void commit() {
        logger.fine("Committing");
        //
        // Set the background color of each LED JLabel
        //
        for (LedInfo led : leds)
            led.label.setBackground(led.colors[led.intensity]);
    }

    /**
     * Force the PiGlow board image to be drawn.
     * 
     * @param g The graphics used to draw the image
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background.getImage(), 0, 0, null);
    }
}