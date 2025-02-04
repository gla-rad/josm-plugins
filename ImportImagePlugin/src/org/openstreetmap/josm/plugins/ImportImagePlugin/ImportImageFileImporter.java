// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.ImportImagePlugin;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.io.importexport.FileImporter;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.plugins.ImportImagePlugin.ImageLayer.LayerCreationCanceledException;
import org.openstreetmap.josm.tools.Logging;

/**
 * Class to open georeferenced image with standard file open dialog 
 */
public class ImportImageFileImporter extends FileImporter {
    
    public ImportImageFileImporter() {
        super(new ExtensionFileFilter("tiff,tif,jpg,jpeg,bmp,png", "jpg", 
                "Georeferenced image file [by ImportImage plugin] (*.jpg, *.jpeg, *.tif, *.tiff, *.png, *.bmp)"));
    }

    @Override
    public boolean isBatchImporter() {
        return true;
    }

    @Override
    public double getPriority() {
        return -3;
    }

    @Override
    public void importData(List<File> files, ProgressMonitor progressMonitor) {
        if (null == files) return;

        for (File file: files) {
            if (file.isDirectory()) continue;
            ImageLayer layer;
            Logging.info("ImportImageFileImporter: File chosen: {0}", file);
            try {
                layer = new ImageLayer(file);
            } catch (LayerCreationCanceledException e) {
                Logging.trace(e);
                // if user decides that layer should not be created just return.
                continue;
            } catch (Exception e) {
                Logging.error("ImportImageFileImporter: Error while creating image layer: \n{0}", e.getMessage());
                Logging.error(e);
                GuiHelper.runInEDT(() ->
                    JOptionPane.showMessageDialog(MainApplication.getMainFrame(), tr("Error while creating image layer: {0}", e.getCause())));
                continue;
            }

            MainApplication.getLayerManager().addLayer(layer);
        }
    }
}
