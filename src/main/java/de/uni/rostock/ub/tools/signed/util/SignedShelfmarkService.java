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
package de.uni.rostock.ub.tools.signed.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni.rostock.ub.tools.signed.model.ShelfmarkObject;

/**
 * Shelfmark service, that processes a shelfmark string and returns a map
 * containing the lines for the shelfmark label
 * handling, SVG generation and data manipulation
 * 
 * @author Robert Stephan, Rostock University Library
 *
 */
public class SignedShelfmarkService {

    private SignedConfigService config;

    public SignedShelfmarkService(SignedConfigService config) {
        this.config = config;
    }

    public Map<String, String> calcShelfmarkLabelData(ShelfmarkObject shelfmark, String template) {
        Map<String, String> labelData = new HashMap<>();
        SortedSet<String> patternKeys = config.findTextKeys(template);
        String cfgKeyRegex = "signed.label." + template + ".regex";
        Pattern p = Pattern.compile(config.getConfig().getProperty(cfgKeyRegex));
        Matcher m = p.matcher(shelfmark.toLocationAndSignatureString());
        if (m.find()) {
            for (String name : patternKeys) {
                try {
                    String s = m.group(name);
                    labelData.put(name, Objects.requireNonNullElse(s, ""));
                } catch (IllegalArgumentException | IllegalStateException e) {
                    // group with given name does not exist
                    labelData.put(name, "");
                }
            }
        }
        modifyLabels(labelData, template);
        return labelData;
    }

    /**
     * updates the label values with replacements that are defined in config.properties
     * signed.label.$template$.line.$line$.replace.$key$=$value$
     * 
     * @param labelData
     * @param template
     */
    private void modifyLabels(Map<String, String> labelData, String template) {
        for (Map.Entry<String, String> label : labelData.entrySet()) {
            Map<String, String> replacements = config.findReplacements(template, label.getKey());
            for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                label.setValue(label.getValue().replace(replacement.getKey(), replacement.getValue()));
            }
        }
    }
}
