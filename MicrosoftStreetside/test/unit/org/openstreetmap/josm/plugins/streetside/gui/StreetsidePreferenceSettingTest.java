// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.streetside.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openstreetmap.josm.plugins.streetside.utils.TestUtil.getPrivateFieldValue;

import java.awt.GraphicsEnvironment;
import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.data.preferences.BooleanProperty;
import org.openstreetmap.josm.data.preferences.StringProperty;
import org.openstreetmap.josm.gui.preferences.PreferenceTabbedPane;
import org.openstreetmap.josm.plugins.streetside.io.download.StreetsideDownloader.DOWNLOAD_MODE;
import org.openstreetmap.josm.testutils.annotations.Main;

@Main
@Disabled
class StreetsidePreferenceSettingTest {
    // TODO: repair broken unit test from Mapillary
    @Test
    void testAddGui() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        PreferenceTabbedPane tabs = new PreferenceTabbedPane();
        tabs.buildGui();
        int displayTabs = tabs.getDisplayPreference().getTabPane().getTabCount();
        StreetsidePreferenceSetting setting = new StreetsidePreferenceSetting();
        setting.addGui(tabs);
        assertEquals(displayTabs + 1, tabs.getDisplayPreference().getTabPane().getTabCount());
        assertEquals(tabs.getDisplayPreference(), setting.getTabPreferenceSetting(tabs));
    }

    @Test
    void testIsExpert() {
        Assertions.assertFalse(new StreetsidePreferenceSetting().isExpert());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testOk() {
        StreetsidePreferenceSetting settings = new StreetsidePreferenceSetting();

        // Initialize the properties with some arbitrary value to make sure they are not unset
        new StringProperty("streetside.display-hour", "default").put("arbitrary");
        new StringProperty("streetside.format-24", "default").put("arbitrary");
        new StringProperty("streetside.move-to-picture", "default").put("arbitrary");
        new StringProperty("streetside.hover-enabled", "default").put("arbitrary");
        new StringProperty("streetside.download-mode", "default").put("arbitrary");
        new StringProperty("streetside.prefetch-image-count", "default").put("arbitrary");

        // Test checkboxes
        settings.ok();
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "displayHour"),
                "streetside.display-hour");
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "format24"),
                "streetside.format-24");
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "moveTo"),
                "streetside.move-to-picture");
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "hoverEnabled"),
                "streetside.hover-enabled");
        assertEquals(
                String.valueOf(
                        ((SpinnerNumberModel) getPrivateFieldValue(settings, "preFetchSize")).getNumber().intValue()),
                new StringProperty("streetside.prefetch-image-count", "default").get());

        // Toggle state of the checkboxes
        toggleCheckbox((JCheckBox) getPrivateFieldValue(settings, "displayHour"));
        toggleCheckbox((JCheckBox) getPrivateFieldValue(settings, "format24"));
        toggleCheckbox((JCheckBox) getPrivateFieldValue(settings, "moveTo"));
        toggleCheckbox((JCheckBox) getPrivateFieldValue(settings, "hoverEnabled"));
        ((SpinnerNumberModel) getPrivateFieldValue(settings, "preFetchSize")).setValue(73);

        // Test the second state of the checkboxes
        settings.ok();
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "displayHour"),
                "streetside.display-hour");
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "format24"),
                "streetside.format-24");
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "moveTo"),
                "streetside.move-to-picture");
        assertPropertyMatchesCheckboxSelection((JCheckBox) getPrivateFieldValue(settings, "hoverEnabled"),
                "streetside.hover-enabled");
        assertEquals(
                String.valueOf(
                        ((SpinnerNumberModel) getPrivateFieldValue(settings, "preFetchSize")).getNumber().intValue()),
                new StringProperty("streetside.prefetch-image-count", "default").get());

        // Test combobox
        for (int i = 0; i < ((JComboBox<String>) getPrivateFieldValue(settings, "downloadModeComboBox"))
                .getItemCount(); i++) {
            ((JComboBox<String>) getPrivateFieldValue(settings, "downloadModeComboBox")).setSelectedIndex(i);
            settings.ok();
            assertEquals(new StringProperty("streetside.download-mode", "default").get(),
                    DOWNLOAD_MODE.fromLabel(Objects.requireNonNull(((JComboBox<String>) getPrivateFieldValue(settings, "downloadModeComboBox"))
                            .getSelectedItem()).toString()).getPrefId());
        }
    }

    /**
     * Checks, if a certain {@link BooleanProperty} (identified by the {@code propName} attribute)
     * matches the selected-state of the given {@link JCheckBox}
     * @param cb the {@link JCheckBox}, which should be checked against the {@link BooleanProperty}
     * @param propName the name of the property against which the selected-state of the given {@link JCheckBox} should be checked
     */
    private static void assertPropertyMatchesCheckboxSelection(JCheckBox cb, String propName) {
        assertEquals(cb.isSelected(), new BooleanProperty(propName, !cb.isSelected()).get());
    }

    private static void toggleCheckbox(JCheckBox jcb) {
        jcb.setSelected(!jcb.isSelected());
    }

}
