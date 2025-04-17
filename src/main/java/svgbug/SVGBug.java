package svgbug;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        try (InputStream is = getClass().getResourceAsStream("svgbug_tspan.svg");
            OutputStream ous = Files.newOutputStream(Paths.get("C:\\temp\\svgbug_tspan.png"))) {
            TranscoderInput ti = new TranscoderInput(is);
            TranscoderOutput to = new TranscoderOutput(ous);

            PNGTranscoder conv = new PNGTranscoder();
            conv.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 250f);
            conv.transcode(ti, to);
        }
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
