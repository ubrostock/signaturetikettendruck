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
package de.uni.rostock.ub.tools.signed.model;

/**
 * ShelfmarkObject is a Java bean that contains informations for shelfmarks
 * the location string, the shelfmark, the loan indicator 
 * 
 * @author Robert Stephan, Rostock University Library
 *
 */
public class ShelfmarkObject {
    private String signature = "";
    private String location = "";
    private String loanindicator = "";

    public ShelfmarkObject() {

    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLoanindicator() {
        return loanindicator;
    }

    public void setLoanindicator(String loanindicator) {
        this.loanindicator = loanindicator;
    }

    public String toLocationAndSignatureString() {
        return "!" + location + "!" + signature;
    }
}
