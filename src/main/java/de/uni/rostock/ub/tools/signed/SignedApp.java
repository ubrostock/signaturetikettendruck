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
package de.uni.rostock.ub.tools.signed;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.w3c.dom.svg.SVGDocument;

import de.uni.rostock.ub.tools.signed.cheatsheet.CheatSheetGenerator;
import de.uni.rostock.ub.tools.signed.model.ShelfmarkObject;
import de.uni.rostock.ub.tools.signed.ui.SignedFrame;
import de.uni.rostock.ub.tools.signed.util.SignedCatalogService;
import de.uni.rostock.ub.tools.signed.util.SignedConfigService;
import de.uni.rostock.ub.tools.signed.util.SignedPrintService;
import de.uni.rostock.ub.tools.signed.util.SignedSVGService;
import de.uni.rostock.ub.tools.signed.util.SignedShelfmarkService;

/**
 * Signaturettikettendruck (Signed)
 * 
 * a Java based application for printing shelfmark labels.
 * It uses Java Regular expression to split shelfmarks into multiple lines.
 * The layout of the label is configured via SVG files.
 * Printing is done via Java Printing Service.
 * 
 * 
 * @author Robert Stephan, Rostock University Library
 * 
 */
public class SignedApp {

    private SignedConfigService configService;

    private SignedPrintService printService;

    private SignedSVGService svgService;

    private SignedCatalogService catalogService;

    private SignedShelfmarkService labelService;

    public SignedApp() {
        configService = new SignedConfigService();
        printService = new SignedPrintService(configService);
        svgService = new SignedSVGService(configService);
        catalogService = new SignedCatalogService(configService);
        labelService = new SignedShelfmarkService(configService);
    }

    public static void main(String[] args) {
        for (String s : args) {
            if (s.equals("spicker")) {
                CheatSheetGenerator.main(args);
                return;
            }
        }
        SignedApp app = new SignedApp();
        app.run();
    }

    public void run() {
        new SignedFrame(this);
    }

    public SignedConfigService getConfigService() {
        return configService;
    }

    public String retrieveLoanInfo(String loanIndicator) {
        return Objects.requireNonNullElse(
            configService.getConfig().getProperty("signed.loanindicator." + loanIndicator), "");
    }

    public void printShelfmarkLabel(String template, Map<String, String> labelData) {
        printService.printShelfmarkLabel(template, svgService.calcSVG(template, labelData, true));
    }

    public String retrieveLabelFromSVG(SVGDocument svgDoc, String key) {
        return svgService.retrieveLabelFromSVG(svgDoc, key);
    }

    public ShelfmarkObject retrieveShelfmarkFromOPACByBarcode(String barcode) {
        try {
            return catalogService.retrieveShelfmarkObjectFromOPACWithBarcode(barcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SVGDocument calcSVG(String template, Map<String, String> labelData, boolean print) {
        return svgService.calcSVG(template, labelData, print);
    }

    public SVGDocument loadSVGTemplate(String template) {
        return svgService.loadSVGTemplate(template);
    }

    public Map<String, String> calcShelfmarkLabelData(ShelfmarkObject shelfmark, String template) {
        return labelService.calcShelfmarkLabelData(shelfmark, template);
    }

    public static String retrieveBuildInfosFromManifest(boolean addCommitInfos) {
        try {
            Enumeration<URL> resources = SignedApp.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                Manifest manifest = new Manifest(url.openStream());
                Attributes attributes = manifest.getMainAttributes();
                if ("signaturetikettendruck".equals(attributes.getValue("Implementation-Artifact-ID"))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(attributes.getValue("Implementation-Title"))
                        .append(" ").append(attributes.getValue("Implementation-Version"));
                    if (addCommitInfos) {
                        sb.append(" [SCM: \"").append(attributes.getValue("SCM-Branch"))
                            .append("\" \"").append(attributes.getValue("SCM-Commit"))
                            .append("\" \"" + attributes.getValue("SCM-Time")).append("\"]");
                    }
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to read manifest entry");
            e.printStackTrace();
        }
        return "Signaturetikettendruck";
    }

    public static String retrieveVersionFromManifest() {
        try {
            Enumeration<URL> resources = SignedApp.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                Manifest manifest = new Manifest(url.openStream());
                Attributes attributes = manifest.getMainAttributes();
                if ("signaturetikettendruck".equals(attributes.getValue("Implementation-Artifact-ID"))) {
                    return attributes.getValue("Implementation-Version");

                }
            }
        } catch (IOException e) {
            System.err.println("Unable to read manifest entry");
            e.printStackTrace();
        }
        return "";
    }
}
