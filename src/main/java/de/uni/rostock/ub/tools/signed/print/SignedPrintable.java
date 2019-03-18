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
package de.uni.rostock.ub.tools.signed.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.apache.batik.transcoder.TranscoderInput;
import org.w3c.dom.svg.SVGDocument;

/**
 * Printable object to for printing SVG objects
 * 
 * @author Robert Stephan, Rostock University Library
 */
public class SignedPrintable implements Printable {
    SVGDocument svgDoc;

    public SignedPrintable(SVGDocument svgDoc) {
        this.svgDoc = svgDoc;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /*
         * User (0,0) is typically outside the imageable area, so we must translate by
         * the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) graphics;

        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        BufferedImage img = createImage(Math.round(pageFormat.getImageableWidth() * 24));

        /* Now we perform our rendering */
        g2d.drawImage((Image) img, 0, 0, (int) Math.round(pageFormat.getImageableWidth()),
                (int) Math.round(pageFormat.getImageableHeight()), null);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    private BufferedImage createImage(float width) {

        BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();
        imageTranscoder.addTranscodingHint(BufferedImageTranscoder.KEY_WIDTH, width);
        // imageTranscoder.addTranscodingHint(BufferedImageTranscoder.KEY_HEIGHT,
        // height);
        try {
            TranscoderInput input = new TranscoderInput(svgDoc);
            imageTranscoder.transcode(input, null);
            return imageTranscoder.getBufferedImage();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
