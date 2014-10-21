/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import java.awt.Font;
import java.awt.Color;

import net.imagej.Dataset;
import net.imagej.DatasetService;

import net.imagej.ImageJ;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.log.LogService;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.IJ;

import net.imagej.legacy.LegacyService;
import net.imagej.legacy.LegacyImageMap;


/**
 * A command that generates message and rotates 90 degress it with a Legacy PlugIn. 
 */
@Plugin(type = Command.class, headless = true )
public class LegacyTest implements Command {

    @Parameter
    private LogService log;

    @Parameter
    private DatasetService datasetService;

    @Parameter
    private LegacyService legacyService;

    @Parameter(min = "1")
    private int width = 512;

    @Parameter(min = "1")
    private int height = 1024;

    @Parameter(type = ItemIO.OUTPUT)
    public Dataset dataset;


    @Parameter
    CommandService commandService;

    @Override
    public void run() {
        //
        // Creat the dataset
        //
        final String name = "Hellow World" ;
        final long[] dims = { width, height };
        final AxisType[] axes = { Axes.X, Axes.Y };
        try{ 
            dataset = datasetService.open("sample-image.fake"); // datasetService.create( dims, name, axes);
        }
        catch(java.io.IOException ioe){
           log.error("Unable to open fake image");
           return; 
        }

        // 
        // Get the ImagePlus from the dataset
        //
        LegacyImageMap imgMap = legacyService.getImageMap();
        ImagePlus imp = imgMap.registerDataset(dataset);
        imgMap.toggleLegacyMode(true);

        // 
        // Write Hello  World on it
        //

        ImageProcessor ip = imp.getProcessor();
        ip.setFont(new Font("SansSerif", Font.PLAIN,18));
        ip.setColor(Color.GRAY);
        ip.drawString("Should be rotated 90 degress.", 90, 90);
        imp.updateAndDraw();
        legacyService.syncActiveImage();

        // 
        // Run a plugin 
        //
        IJ.runPlugIn(imp, "ij.plugin.filter.Transformer", "right");

        imgMap.toggleLegacyMode(false);
        imgMap.unregisterLegacyImage(imp);
    }

    /** Tests our command. */
    public static void main(final String... args) throws Exception {
        // Launch ImageJ as usual.
        final ImageJ ij = net.imagej.Main.launch(args);

        // Launch the "Gradient Image" command right away.
        ij.command().run(LegacyTest.class, true, "width",512,"height",1024);
    }

}
