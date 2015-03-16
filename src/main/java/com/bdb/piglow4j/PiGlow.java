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

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.system.SystemInfo;

/**
 *
 * @author Bruce
 */
public final class PiGlow {
    /**
     * The number of LEDs that are on the PiGlow
     */
    public static final int PIGLOW_LED_COUNT = 18;
    private static final int ENABLE_OUTPUT_ADDR = 0x0;
    private static final byte ENABLE_OUTPUT = 0x1;
    private static final int FIRST_LED_ADDR = 0x1;
    private static final int ENABLE_TOP_ARM_ADDR = 0x13;
    private static final int ENABLE_LEFT_ARM_ADDR = 0x14;
    private static final int ENABLE_RIGHT_ARM_ADDR = 0x15;
    private static final int COMMIT_ADDR = 0x16;
    private static final int I2C_ADDR = 0x54;
    private static final byte VALUE = (byte)0xFF;
    private static final byte ALL_OFF[] = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    private I2CBus bus;
    private I2CDevice device;
    private final byte[] intensities;
    private static final PiGlow instance;
    private static boolean initialized;
    private static final Logger logger = Logger.getLogger(PiGlow.class.getName());

    static {
        initialized = false;
        instance = new PiGlow();
    }

    /**
     * Singleton factory method to get the PiGlow.
     * 
     * @return The PiGlow singleton
     */
    public static PiGlow getInstance() {
        if (!initialized && !instance.initialize())
            return null;
        else {
            initialized = true;
            return instance;
        }
    }

    /**
     * Constructor.
     */
    private PiGlow() {
        intensities = new byte[PIGLOW_LED_COUNT];
    }

    /**
     * This method fixes a bug in pi4j that does not report the board type correctly for RPi 2 Rev B.
     * This method can be delete when the pi4j code is fixed.
     * 
     * @return The board type
     */
    private SystemInfo.BoardType getBoardType() {
        try {
            String revision = SystemInfo.getRevision();
            long irevision = Long.parseLong(revision, 16);
            long scheme = (irevision >> 23) & 0x1;
            long ram = (irevision >> 20) & 0x7;
            long manufacturer = (irevision >> 16) & 0xF;
            long processor = (irevision >> 12) & 0xF;
            long type = (irevision >> 4) & 0xFF;
            long rev = irevision & 0xF;

            logger.fine(String.format("Board Revision: Scheme: %d RAM: %d Manufacturer %d Processor: %d Type: %d Revision: %d",
                                      scheme, ram, manufacturer, processor, type, rev));
            if (scheme == 0)
                return SystemInfo.getBoardType();
            else if (type == 4)
                return SystemInfo.BoardType.Model2B_Rev1;
            else
                return SystemInfo.BoardType.UNKNOWN;
        }
        catch (IOException | InterruptedException ex) {
            logger.severe("Failed to determine Raspberry Pi board type");
            return SystemInfo.BoardType.UNKNOWN;
        }
    }

    /**
     * Initialize the PiGlow interface.
     * 
     * @return True of the PiGlow initialized successfully
     */
    private boolean initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(()->allOff()));
        SystemInfo.BoardType boardType = getBoardType();
        int busNumber = I2CBus.BUS_1;
        switch (boardType) {
            case ModelA_Rev1:
            case ModelB_Rev1:
            case ModelB_Rev2:
                busNumber = I2CBus.BUS_0;
                break;

            case ModelA_Plus_Rev1:
            case ModelB_Plus_Rev1:
            case Model2B_Rev1:
                busNumber = I2CBus.BUS_1;
                break;
        }

        try {
            bus = I2CFactory.getInstance(busNumber);
            device = bus.getDevice(I2C_ADDR);

            device.write(ENABLE_OUTPUT_ADDR, ENABLE_OUTPUT);
            device.write(ENABLE_TOP_ARM_ADDR, VALUE);
            device.write(ENABLE_LEFT_ARM_ADDR, VALUE);
            device.write(ENABLE_RIGHT_ARM_ADDR, VALUE);
            return true;
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize the PiGlow", e);
            return false;
        }
    }

    /**
     * Commit the changes to the PiGlow.
     * 
     * @throws IOException Error writing to the I2C device
     */
    public void commit() throws IOException {
        device.write(COMMIT_ADDR, VALUE);
    }

    /**
     * Set the intensity of a single LED.
     * 
     * @param led The LED whose intensity is to be changed
     * @param intensity the new intensity
     * 
     * @throws IOException Failed to write to the Raspberry Pi I2C
     * @throws IllegalArgumentException The intensity is out of range
     */
    public void setLEDIntensity(PiGlowLED led, int intensity) throws IOException, IllegalArgumentException {
        led.setIntensity(intensity);
        device.write(led.getAddress(), (byte)intensity);
        commit();
    }

    /**
     * Write the new LED intensities to the PiGlow.
     * 
     * @throws IOException Failed to write to the Raspberry Pi I2C
     */
    public void updateLEDs() throws IOException {
        logger.fine("Updating the LED intensities");
        PiGlowLED.allLEDs().forEach((led) -> intensities[led.getAddress() - FIRST_LED_ADDR] = (byte)led.getIntensity());

        device.write(FIRST_LED_ADDR, intensities, 0, intensities.length);
        commit();
    }

    /**
     * Turn off all of the LEDs
     */
    public void allOff() {
        logger.fine("Turning all off");
        try {
            device.write(FIRST_LED_ADDR, ALL_OFF, 0, ALL_OFF.length);
            commit();
	    PiGlowLED.allLEDs().forEach((led) -> led.setIntensity(0));
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception turning off all LEDs", ex);
        }
    }
}