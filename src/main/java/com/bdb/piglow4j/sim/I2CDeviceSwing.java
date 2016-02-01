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

import com.bdb.piglow4j.PiGlowLED;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.pi4j.io.i2c.I2CDevice;

/**
 * A mock I2CDevice for creating a GUI to simulate the PiGlow.
 *
 * @author Bruce Beisel
 */
public final class I2CDeviceSwing implements I2CDevice {
    private PiGlowGUI gui;
    private static final Logger logger = Logger.getLogger(I2CDeviceSwing.class.getName());

    /**
     * Constructor.
     */
    public I2CDeviceSwing() {
        try {
            //
            // Create the GUI that simulates the PiGlow
            //
            gui = new PiGlowGUI();
            PiGlowLED.setGammaCorrectionMode(false);
            SwingUtilities.invokeAndWait(()->gui.createElements());
        }
        catch (InvocationTargetException | InterruptedException e) {
            logger.severe("Failed to create PiGlow simulator GUI");
        }
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @param b Not used
     * 
     * @throws IOException Never thrown
     */
    @Override
    public void write(byte b) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @param bytes Not used
     * @param offset Not used
     * @param size Not used
     * 
     * @throws IOException Never thrown
     */
    @Override
    public void write(byte[] bytes, int offset, int size) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Write a single byte to the PiGlow board simulator.
     * 
     * @param address The address to write
     * @param b The byte to write
     * @throws IOException Never thrown by the simulator
     */
    @Override
    public void write(int address, byte b) throws IOException {
        byte buffer[] = {b};
        gui.processBytes(address, buffer, 1);
    }

    /**
     * Write an array of bytes to the PiGlow board simulator.
     * 
     * @param address The address to write
     * @param bytes The array of bytes to write
     * @param offset The offset within the array to start writing
     * @param size The size of the array
     * @throws IOException Never thrown by the simulator
     */
    @Override
    public void write(int address, byte[] bytes, int offset, int size) throws IOException {
        //
        // Copy the bytes to a local buffer and pass them on to the GUI
        //
        byte buffer[] = Arrays.copyOfRange(bytes, offset, size - offset);
        gui.processBytes(address, buffer, size);
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @return Never returns
     * 
     * @throws IOException Not thrown
     */
    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @param bytes Not used
     * @param i Not used
     * @param i1 Not used
     * @return Never returns
     * @throws IOException Never thrown
     */
    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @param i Not used
     * @return Never returns
     * @throws IOException  Never thrown
     */
    @Override
    public int read(int i) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @param i Not used
     * @param bytes Not used
     * @param i1 Not used
     * @param i2 Not used
     * @return Never returns
     * @throws IOException  Never thrown
     */
    @Override
    public int read(int i, byte[] bytes, int i1, int i2) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not used by the PiGlow simulator.
     * 
     * @param i Not used
     * @param bytes Not used
     * @param i1 Not used
     * @param bytes1 Not used
     * @param i2 Not used
     * @param i3 Not used
     * @return Never returns
     * @throws IOException  Never thrown
     */
    @Override
    public int read(byte[] bytes, int i, int i1, byte[] bytes1, int i2, int i3) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }
}