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

import java.util.Objects;

/**
 * ShelfmarkObject is a Java bean that contains informations for shelfmarks
 * the location string, the shelfmark, the loan indicator 
 * 
 * @author Robert Stephan, Rostock University Library
 *
 */
public record ShelfmarkObject(String location, String signature, String loanindicator) {

    public ShelfmarkObject(String location, String signature, String loanindicator) {
        this.location = Objects.requireNonNullElse(location, "");
        this.signature = Objects.requireNonNullElse(signature, "");
        this.loanindicator = Objects.requireNonNullElse(loanindicator, "");
    }

    public String toLocationAndSignatureString() {
        return "!" + location + "!" + signature;
    }
}
