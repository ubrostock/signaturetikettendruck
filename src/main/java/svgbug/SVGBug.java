package svgbug;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 * This class presents a bug in SVG rendering.
 * Empty <tspan> elements lead to unwanted line breaks of previous lines
 *
 * @author Robert Stephan, Rostock University Library
 *
 */
public class SVGBug {

    public void run() throws Exception {
        TranscoderInput ti = new TranscoderInput(getClass().getResourceAsStream("svgbug_tspan.svg"));
        TranscoderOutput to = new TranscoderOutput(new FileOutputStream(new File("C:\\temp\\svgbug_tspan.png")));

        PNGTranscoder conv = new PNGTranscoder();
        conv.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 250f);
        conv.transcode(ti, to);

    }

    public static void main(String[] args) {
        SVGBug test = new SVGBug();
        try {
            test.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
