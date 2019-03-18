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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

import de.uni.rostock.ub.tools.signed.SignedApp;
import de.uni.rostock.ub.tools.signed.model.ShelfmarkObject;

/** Cheatsheet generator is a utility which generates a PDF (A4 format) containing multiple
 *  shelfmark labels.
 *  It can be used to test the result of the regex'es for shelfmark line splitting.
 *  Input is a list of barcodes, configured via property.
 *  
 * @author Robert Stephan, Rostock University Library
 *
 */
public class CheatSheetGenerator {
    SAXSVGDocumentFactory fac = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());

    private SignedApp app = new SignedApp();

    /**
     * @param args
     */
    public static void main(String[] args) {
        CheatSheetGenerator app = new CheatSheetGenerator();
        app.run();

    }

    public void run() {
        Properties config = app.getConfigService().getConfig();
        // Get a DOMImplementation.
        for (Object keyO : config.keySet()) {
            String key = keyO.toString();
            if (key.startsWith("signed.cheatsheet.barcodes.")) {
                SVGDocument doc = null;
                InputStream is = null;
                try {
                    try {
                        is = getClass()
                                .getResourceAsStream("/" + config.getProperty("signed.cheatsheet.templatefile").trim());

                        doc = fac.createSVGDocument(null, is);
                        // Finally, stream out SVG to the standard output using UTF-8 encoding.
                        DrawingOptions drawOpts = new DrawingOptions(config);

                        String name = key.substring(key.lastIndexOf(".") + 1);
                        String[] data = config.getProperty(key).split(",");
                        for (int i = 0; i < data.length; i++) {
                            String barcode = data[i].trim();
                            if (barcode.length() > 0) {
                                drawEttikett(doc, i, barcode, drawOpts);
                            }
                        }

                        // Writer out = new OutputStreamWriter(System.out, "UTF-8");
                        Writer out = new OutputStreamWriter(new FileOutputStream(new File(
                                new File(config.getProperty("signed.cheatsheet.outputdirectory")), name + ".svg")),
                                "UTF-8");

                        TranscoderInput input = new TranscoderInput(doc);
                        TranscoderOutput output = new TranscoderOutput(out);
                        Transcoder t = new SVGTranscoder();
                        t.transcode(input, output);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    private void drawEttikett(SVGDocument doc, int pos, String barcode, DrawingOptions drawOpts) {
        try {
            ShelfmarkObject shelfmark = app.retrieveShelfmarkFromOPACByBarcode(barcode);
            String template = app.getConfigService().findTemplateKey(shelfmark);
            InputStream is = null;

            try {
                String filename = app.getConfigService().getConfig()
                        .getProperty("signed.label." + template + ".templatefile");
                is = getClass().getResourceAsStream("/" + filename);

                SVGDocument docEtti = app.calcSVG(template, app.calcShelfmarkLabelData(shelfmark, template), false);

                SVGElement elem = docEtti.getRootElement();
                double x = drawOpts.getStartX() + (pos % drawOpts.getCols() * drawOpts.getOffsetX());
                double y = drawOpts.getStartY() + (pos / drawOpts.getCols() * drawOpts.getOffsetY());

                elem.setAttribute("x", Double.toString(x) + drawOpts.getUnit());
                elem.setAttribute("y", Double.toString(y + 8) + drawOpts.getUnit());

                doc.getRootElement().appendChild(doc.adoptNode(elem));

                Element textBarcode = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
                textBarcode.appendChild(doc.createTextNode(barcode));
                textBarcode.setAttribute("x", Double.toString(x) + drawOpts.getUnit());
                textBarcode.setAttribute("y", Double.toString(y) + drawOpts.getUnit());
                textBarcode.setAttribute("style", "fill:black;font-family:Arial;font-size:12px;");
                doc.getRootElement().appendChild(textBarcode);

                Element textLocation = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
                textLocation.appendChild(doc.createTextNode("!" + shelfmark.getLocation() + "!"));
                textLocation.setAttribute("x", Double.toString(x) + drawOpts.getUnit());
                textLocation.setAttribute("y", Double.toString(y + 3.5) + drawOpts.getUnit());
                textLocation.setAttribute("style",
                        "fill:black;font-family:Arial;font-weight:bold; font-stretch: condensed;font-size:12px;");
                doc.getRootElement().appendChild(textLocation);

                Element textSignature = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
                textSignature.appendChild(doc.createTextNode(shelfmark.getSignature()));
                textSignature.setAttribute("x", Double.toString(x) + drawOpts.getUnit());
                textSignature.setAttribute("y", Double.toString(y + 7) + drawOpts.getUnit());
                textSignature.setAttribute("style",
                        "fill:black;font-family:Arial;font-weight:bold; font-stretch: condensed;font-size:12px;");
                doc.getRootElement().appendChild(textSignature);

            } catch (Exception e) {

            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {

        }

    }
}
