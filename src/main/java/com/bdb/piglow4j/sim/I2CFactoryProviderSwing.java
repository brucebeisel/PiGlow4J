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
package com.bdb.piglow4j.sim;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactoryProvider;

/**
 * I2CFactoryProvider that creates the I2CBus that create the PiGlow simulator GUI.
 *
 * @author Bruce Beisel
 */
public final class I2CFactoryProviderSwing implements I2CFactoryProvider {

    /**
     * Return an I2CBus for a given address.
     * 
     * @param i The address which is ignored
     * @return The I2CBus for the simulator
     * @throws IOException Never thrown, but required
     */
    @Override
    public I2CBus getBus(int i) throws IOException {
        return new I2CBusSwing();
    }
}