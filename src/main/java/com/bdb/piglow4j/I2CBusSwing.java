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

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

/**
 * A mock I2CDevice for creating a GUI the simulates the PiGlow.
 *
 * @author Bruce Beisel
 */
public class I2CBusSwing implements I2CBus {

    @Override
    public I2CDevice getDevice(int addr) throws IOException {
        if (addr == 0x54)
            return new I2CDeviceSwing();
        else
            return null;
    }

    @Override
    public String getFileName() {
        return "No File";
    }

    @Override
    public int getFileDescriptor() {
        return 0;
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
