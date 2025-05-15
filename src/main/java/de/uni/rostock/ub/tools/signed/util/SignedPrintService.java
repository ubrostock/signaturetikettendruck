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

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JOptionPane;

import org.w3c.dom.svg.SVGDocument;

import de.uni.rostock.ub.tools.signed.print.SignedPrintable;

/**
 * Print service, starts printing the shelfmark label
 * 
 @author Robert Stephan, Rostock University Library
 *
 */
public class SignedPrintService {
    private SignedConfigService config;

    public SignedPrintService(SignedConfigService config) {
        this.config = config;
    }

    public void printShelfmarkLabel(String template, SVGDocument svg) {
        // if(imgEttikett!=null)
        PrintService psOutput = null;
        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);
        Properties printerConfig = config.getPrinterConfig();
        List<String> printers = new ArrayList<String>();
        String printerName = printerConfig.getProperty("signed.printer.name").trim();
        for (String s : printerName.split("\\,")) {
            printers.add(s.trim());
        }

        outer: for (String printer : printers) {
            for (PrintService ps : prservices) {
                if (ps.getName().contains(printer)) {
                    psOutput = ps;
                    break outer;
                }
            }
        }

        if (psOutput == null) {
            String msg = "Drucker '" + printerName + "' nicht gefunden.";
            System.err.println(msg);
            StringBuffer out = new StringBuffer();
            out.append("Folgende Drucker sind verfügbar: ");
            for (PrintService ps : prservices) {
                out.append("\n" + ps.getName() + ", ");
            }
            System.err.println(out);
            JOptionPane.showMessageDialog(null, msg + "\n\n" + out.toString(), "Druckerfehler",
                JOptionPane.ERROR_MESSAGE);
        } else {
            try {

                DocPrintJob job = psOutput.createPrintJob();
                PrintRequestAttributeSet prAttrSet = new HashPrintRequestAttributeSet();

                if ("portrait"
                    .equals(printerConfig.getProperty("signed.printer.page.orientation").trim().toLowerCase())) {
                    prAttrSet.add(OrientationRequested.PORTRAIT);
                }
                if ("landscape"
                    .equals(printerConfig.getProperty("signed.printer.page.orientation").trim().toLowerCase())) {
                    prAttrSet.add(OrientationRequested.LANDSCAPE);
                }
                // Paper; A4 borderless -> needs to be configured
                float pageWidth = Float.parseFloat(printerConfig.getProperty("signed.printer.page.width").trim());
                float pageHeight = Float.parseFloat(printerConfig.getProperty("signed.printer.page.height").trim());
                float pageBorderLeft = Float
                    .parseFloat(printerConfig.getProperty("signed.printer.page.border.left").trim());
                float pageBorderRight = Float
                    .parseFloat(printerConfig.getProperty("signed.printer.page.border.right").trim());
                float pageBorderTop = Float
                    .parseFloat(printerConfig.getProperty("signed.printer.page.border.top").trim());
                float pageBorderBottom = Float
                    .parseFloat(printerConfig.getProperty("signed.printer.page.border.bottom").trim());

                prAttrSet.add(new MediaPrintableArea(pageBorderLeft, pageBorderTop,
                    pageWidth - pageBorderLeft - pageBorderRight,
                    pageHeight - pageBorderTop - pageBorderBottom,
                    MediaPrintableArea.MM));

                if (svg != null) {
                    // working (print using buffered image):
                    Printable printable = new SignedPrintable(svg);
                    SimpleDoc printDoc = new SimpleDoc(printable, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

                    job.print(printDoc, prAttrSet);
                }
            } catch (PrintException ex) {
                ex.printStackTrace();
            }
        }

    }

}
