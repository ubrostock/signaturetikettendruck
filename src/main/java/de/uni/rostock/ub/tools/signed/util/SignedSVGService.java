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

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;

/**
 * SVG service, that retrieves template file from property, fills in the data
 * and renders the SVG
 * 
 * @author Robert Stephan, Rostock University Library
 *
 */
public class SignedSVGService {
    private SignedConfigService config;

    public SignedSVGService(SignedConfigService config) {
        this.config = config;
    }

    public void updateSVG(SVGDocument svg, String template, Map<String, String> texte) {
        //clear fields in SVG
        for (String key : config.findTextKeys(template)) {
            if (svg.getElementById(key) != null) {
                setTextContent(svg.getElementById(key), "");
            }
        }

        //set fields
        for (String id : texte.keySet()) {
            if (svg.getElementById(id) != null) {
                setTextContent(svg.getElementById(id), texte.get(id));
            }
        }

        //delete unfilled / empty text nodes
        for (String key : config.findTextKeys(template)) {
            if (!texte.containsKey(key) || texte.get(key).trim().length() == 0) {
                Element e = svg.getElementById(key);
                if (e != null) {
                    e.getParentNode().removeChild(e);
                }
            }
        }
    }

    public void outputSVG(SVGDocument svgDoc, Path p) {
        try (BufferedWriter ow = Files.newBufferedWriter(p)) {
            TranscoderInput input = new TranscoderInput(svgDoc);
            TranscoderOutput output = new TranscoderOutput(ow);
            Transcoder t = new SVGTranscoder();
            t.transcode(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SVGDocument loadSVGTemplate(String template) {
        SAXSVGDocumentFactory fac = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        String filename = config.getConfig().getProperty("signed.label." + template + ".templatefile");
        try (InputStream is = getClass().getResourceAsStream("/" + filename)) {
            return fac.createSVGDocument(null, is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * creates the SVG Document for the label. The property "signed.label.*.noprint"
     * contains a comma separated list of SVG element IDs, which should not be used
     * for printing (e.g. an outline necessary only for display)
     * 
     * @param template - the label template to be used
     * @param texts    - texts that should be replaced in the template
     * @param print    - true, if the SVG should be used for print, false if the SVG
     *                 should be used for display
     * @return
     */
    public SVGDocument calcSVG(String template, Map<String, String> texts, boolean print) {
        SAXSVGDocumentFactory fac = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        SVGDocument doc = null;
        String filename = config.getConfig().getProperty("signed.label." + template + ".templatefile");

        try (InputStream is = getClass().getResourceAsStream("/" + filename)) {
            doc = fac.createSVGDocument(null, is);
            updateSVG(doc, template, texts);
            if (print) {
                // delete all SVG elements with attribute class="noprint"
                // iterate from last to first, that the counter keeps valid if an element was
                // deleted
                NodeList nl = doc.getElementsByTagName("*");
                for (int i = nl.getLength() - 1; i >= 0; i--) {
                    Element e = (Element) nl.item(i);
                    if (e.hasAttribute("class") && e.getAttribute("class").contains("noprint")) {
                        e.getParentNode().removeChild(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public String retrieveLabelFromSVG(SVGDocument svg, String id) {
        Element e = svg.getElementById(id);
        if (e != null && !e.getAttribute("aria-label").isEmpty()) {
            return e.getAttribute("aria-label");
        }
        return id;
    }

    private void setTextContent(Element e, String textContent) {
        if (e == null)
            return;
        // remove children
        while (e.getFirstChild() != null) {
            e.removeChild(e.getFirstChild());
        }
        if (textContent == null) {
            e.getParentNode().removeChild(e);
        } else {
            // add child text node
            Text textNode = e.getOwnerDocument().createTextNode(textContent);
            e.appendChild(textNode);
        }
    }
}
