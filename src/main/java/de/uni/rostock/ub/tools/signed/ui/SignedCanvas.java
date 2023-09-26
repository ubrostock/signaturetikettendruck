/*
 * Copyright 2022, University Library Rostock
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
package de.uni.rostock.ub.tools.signed.ui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.w3c.dom.svg.SVGDocument;

/**
 * Canvas which holds and diplays the SVG image in an AWT user interface.
 * 
 * org.apache.batik.swing.JSVGCanvas was used before,
 * but the display quality was bad.
 * The quality was improved by creating a larger Buffered Image from SVG
 * and reduce the size for display afterwards.
 */
import de.uni.rostock.ub.tools.signed.print.BufferedImageTranscoder;

public class SignedCanvas extends Canvas {
    private static final long serialVersionUID = 1L;

    SVGDocument svgdoc;

    public void paint(Graphics g) {
        if (svgdoc != null) {
            BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();
            imageTranscoder.addTranscodingHint(BufferedImageTranscoder.KEY_WIDTH, (float) getWidth() * 4);
            TranscoderInput input = new TranscoderInput(svgdoc);

            try {
                imageTranscoder.transcode(input, null);
            } catch (TranscoderException e) {
                e.printStackTrace();
            }

            BufferedImage bi = imageTranscoder.getBufferedImage();
            if ((float) bi.getWidth() / this.getWidth() >= (float) bi.getHeight() / this.getHeight()) {
                g.drawImage(bi, 0, 0, this.getWidth(), Math.round((float) this.getWidth() * bi.getHeight() / bi.getWidth()),
                    this);
            } else {
                g.drawImage(bi, 0, 0, Math.round((float) this.getHeight() * bi.getWidth() / bi.getHeight()), this.getHeight(),
                    this);
            }
        }
    }

    public void setDocument(SVGDocument svgdoc) {
        this.svgdoc = svgdoc;
    }

}
