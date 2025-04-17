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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import de.uni.rostock.ub.tools.signed.model.ShelfmarkObject;

/**
 * Configuration service, that reads from properties and creates application specific utility objects
 * for templates, template names, ...
 * 
 * @author Robert Stephan, Rostock University Library
 *
 */
public class SignedConfigService {

    private Properties config = new Properties();

    private Properties printerConfig = new Properties();

    private SortedMap<String, String> templateKeys = new TreeMap<String, String>();

    public SignedConfigService() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            getClass().getResourceAsStream("/signed_cfg.properties"), StandardCharsets.ISO_8859_1))) {
            config.load(br);
        } catch (IOException | NullPointerException e) {
            StringBuffer msg = new StringBuffer("Prüfen Sie, ob die Datei signed_cfg.properties existiert!");
            if (e.getMessage() != null) {
                msg.append("\n").append(e.getMessage());
            }
            System.err.println(msg.toString());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, msg.toString(), "Fehler in der Konfiguration",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            getClass().getResourceAsStream("/signed_printer.properties"), StandardCharsets.ISO_8859_1))) {
            printerConfig.load(br);
        } catch (IOException | NullPointerException e) {
            StringBuffer msg = new StringBuffer("Prüfen Sie, ob die Datei signed_printer.properties existiert!");
            if (e.getMessage() != null) {
                msg.append("\n").append(e.getMessage());
            }
            System.err.println(msg.toString());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, msg.toString(), "Fehler in der Konfiguration",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // init template keys

        for (Object o : config.keySet()) {
            String key = o.toString();
            if (key.startsWith("signed.template.pattern.")) {
                key = key.substring("signed.template.pattern.".length());
                String[] s = key.split("\\.");
                templateKeys.put(s[0], s[1]);
            }
        }

    }

    /**
     * 
     * @return application properties
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * 
     * @return application properties for printer configuration
     */
    public Properties getPrinterConfig() {
        return printerConfig;
    }

    public SortedMap<String, String> findTextKeys(String template) {
        SortedMap<String, String> textKeys = new TreeMap<String, String>();
        String cfgKeyRegex = "signed.label." + template + ".regex";
        if (config.keySet().contains(cfgKeyRegex)) {
            String regex = config.getProperty(cfgKeyRegex);

            Matcher m = Pattern.compile("\\([?]<([a-zA-Z][a-zA-Z0-9]+)>").matcher(regex);
            int count = 0;
            while (m.find()) {
                textKeys.put(String.format("%04d", ++count), m.group(1));
            }
        } else {
            // old variant
            String patternBase = "signed.label." + template + ".pattern.";
            for (Object o : config.keySet()) {
                String key = o.toString();
                if (key.startsWith(patternBase)) {
                    key = key.substring(patternBase.length());
                    String[] s = key.split("\\.");
                    textKeys.put(s[0], s[1]);
                }
            }
        }
        return textKeys;
    }

    /**
     * 
     * @param shelfmark
     * @param templateKeys
     * @return
     */
    public String findTemplateKey(ShelfmarkObject shelfmark) {
        System.out.println("Shelfmark: " + shelfmark.toLocationAndSignatureString());

        for (String k : templateKeys.keySet()) {
            String pattern = config.getProperty("signed.template.pattern." + k + "." + templateKeys.get(k));
            if (shelfmark.toLocationAndSignatureString().matches(pattern)) {
                System.out.println("Template: " + templateKeys.get(k));
                return templateKeys.get(k);
            }
        }
        return null;
    }

    public String findTemplateName(String templateKey) {
        return config.getProperty("signed.label." + templateKey + ".name").trim();
    }

    public SortedMap<String, String> getTemplateKeys() {
        return templateKeys;
    }

}
