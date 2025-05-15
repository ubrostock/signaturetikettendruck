/*
 * Copyright 2018, University Library Rostock
 * 
 * This file is part of the program "Signaturetikettendruck (Signed)".
 * https://github.com/ubrostock/signaturetikettendruck
 * 
 * "Signaturetikettendruck" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Signaturetikettendruck" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni.rostock.ub.tools.signed.cheatsheet;

import java.util.Properties;

/**
 * DrawingOptions holds settings for cheatsheet generator. It's a bean style
 * class, data is read from properties.
 * 
 * @author Robert Stephan, Rostock University Library
 */
public class DrawingOptions {
    private String unit;

    private int rows;

    private int cols;

    private double offsetX, offsetY, startX, startY;

    public DrawingOptions(Properties config) {
        unit = config.getProperty("signed.cheatsheet.unit");
        cols = Integer.parseInt(config.getProperty("signed.cheatsheet.cols"));
        rows = Integer.parseInt(config.getProperty("signed.cheatsheet.rows"));
        offsetX = Double.parseDouble(config.getProperty("signed.cheatsheet.offsetx"));
        offsetY = Double.parseDouble(config.getProperty("signed.cheatsheet.offsety"));
        startX = Double.parseDouble(config.getProperty("signed.cheatsheet.startx"));
        startY = Double.parseDouble(config.getProperty("signed.cheatsheet.starty"));
    }

    public String getUnit() {
        return unit;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }
}
