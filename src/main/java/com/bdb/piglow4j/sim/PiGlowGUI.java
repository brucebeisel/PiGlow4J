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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Main class for the PiGlow simulator GUI.
 *
 * @author Bruce
 */
public final class PiGlowGUI {
    private boolean on = false;
    private boolean topArmOn = false;
    private boolean leftArmOn = false;
    private boolean rightArmOn = false;
    private final int intensities[] = new int[PiGlow.PIGLOW_LED_COUNT];
    private PiGlowJComponent component;
    private final static Logger logger = Logger.getLogger(PiGlowGUI.class.getName());

    /**
     * Create the elements of the PiGlow simulator GUI
     */
    public void createElements() {
        try {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            InputStream is = ClassLoader.class.getResourceAsStream("/piglow.jpg");
            BufferedImage bufferedImage = ImageIO.read(is);
            component = new PiGlowJComponent(new ImageIcon(bufferedImage));
            frame.add(component);
            frame.pack();
            frame.setVisible(true);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Process the bytes that were written to the I2C device.
     * 
     * @param address The address to which the bytes were written
     * @param buffer The buffer of bytes
     * @param length The number of bytes in the buffer
     */
    public void processBytes(int address, byte buffer[], int length) {
        //
        // Turn on the PiGlow board
        //
        if (address == 0x0 && buffer[0] == 0x1) {
            on = true;
            logger.info("PiGlow is ON");
            return;
        }

        //
        // If the board is not on, then any other command is ignored
        //
        if (!on) {
            logger.info("Ignoring bytes because board is OFF");
            return;
        }

        if (address == 0x13 && ((int)buffer[0] & 0xFF) == 0xFF) {
            logger.info("Turning on TOP arm");
            topArmOn = true;
        }
        else if (address == 0x14 && ((int)buffer[0] & 0xFF) == 0xFF) {
            logger.info("Turning on LEFT arm");
            leftArmOn = true;
        }
        else if (address == 0x15 && ((int)buffer[0] & 0xFF) == 0xFF) {
            logger.info("Turning on RIGHT arm");
            rightArmOn = true;
        }
        else if (address == 0x16 && ((int)buffer[0] & 0xFF) == 0xFF) {
            logger.fine("Committing");
            component.commit();
        }
        else if (address >= 0x1 && address <= 0x12) {
            logger.fine("Getting intensities");
            for (int i = 0; address + i <= 0x12 && i < length; i++)
                intensities[address + i - 1] = ((int)buffer[i] & 0xFF);

            component.setIntensities(intensities);
        }
        else
            logger.log(Level.WARNING, "Received bytes for unknown address: {0}", address);
    }
}
