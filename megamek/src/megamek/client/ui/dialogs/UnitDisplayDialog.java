/*
 * Copyright (c) 2022 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import megamek.client.ui.Messages;
import megamek.client.ui.swing.ClientGUI;
import megamek.client.ui.swing.GUIPreferences;
import megamek.client.ui.swing.unitDisplay.UnitDisplay;
import megamek.client.ui.swing.util.UIUtil;
import megamek.common.Entity;

public class UnitDisplayDialog extends JDialog {
    static private UnitDisplayDialog dialogInstance = null;
    static private UnitDisplay unitDisplay = null;
    
    //region Variable Declarations
    private final ClientGUI clientGUI;
    private static final GUIPreferences GUIP = GUIPreferences.getInstance();
    //endregion Variable Declarations

    public static void showEntity(final JFrame frame, Entity entity, final boolean newInstance) {
        UnitDisplayDialog localDialogInstance = null;
        UnitDisplay localUnitDisplay = null;
        if (!newInstance) {
            localDialogInstance = dialogInstance;
            localUnitDisplay = unitDisplay;
        }
        if (localDialogInstance == null || unitDisplay == null) {
            localDialogInstance = new UnitDisplayDialog(frame, null);
            localUnitDisplay = new UnitDisplay(null, null);
            localDialogInstance.add(localUnitDisplay, BorderLayout.CENTER);
            localDialogInstance.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            if (!newInstance) {
                dialogInstance = localDialogInstance;
                unitDisplay = localUnitDisplay;
            }
        }
        localUnitDisplay.displayEntity(entity);
        localDialogInstance.setVisible(true);
    }

    //region Constructors
    public UnitDisplayDialog(final JFrame frame, final ClientGUI clientGUI) {
        super(frame, "", false);
        this.clientGUI = clientGUI;
        this.setTitle(Messages.getString("ClientGUI.MekDisplay"));

        if (GUIP.getUnitDisplayStartTabbed()) {
            this.setLocation(GUIP.getUnitDisplayPosX(), GUIP.getUnitDisplayPosY());
            this.setSize(GUIP.getUnitDisplaySizeWidth(), GUIP.getUnitDisplaySizeHeight());
        }
        else {
            this.setLocation(GUIP.getUnitDisplayNontabbedPosX(), GUIP.getUnitDisplayNontabbedPosY());
            this.setSize(GUIP.getUnitDisplayNonTabbedSizeWidth(), GUIP.getUnitDisplayNonTabbedSizeHeight());
        }

        UIUtil.updateWindowBounds(this);
        this.setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                GUIP.setUnitDisplayEnabled(false);
            }
        });

    }
    //endregion Constructors

    public void saveSettings() {
        if ((getSize().width * getSize().height) > 0) {
            if (GUIP.getUnitDisplayStartTabbed()) {
                GUIP.setUnitDisplayPosX(getLocation().x);
                GUIP.setUnitDisplayPosY(getLocation().y);
                GUIP.setUnitDisplaySizeWidth(getSize().width);
                GUIP.setUnitDisplaySizeHeight(getSize().height);
            } else {
                GUIP.setUnitDisplayNontabbedPosX(getLocation().x);
                GUIP.setUnitDisplayNontabbedPosY(getLocation().y);
                GUIP.setUnitDisplayNonTabbedSizeWidth(getSize().width);
                GUIP.setUnitDisplayNonTabbedSizeHeight(getSize().height);
                if (clientGUI != null) {
                    clientGUI.getUnitDisplay().saveSplitterLoc();
                }
            }
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if ((e.getID() == WindowEvent.WINDOW_DEACTIVATED) || (e.getID() == WindowEvent.WINDOW_CLOSING)) {
            saveSettings();
        }
    }

    /**
     * In addition to the default Dialog processKeyEvent, this method
     * dispatches a KeyEvent to the client gui.
     * This enables all the gui hotkeys.
     */
    @Override
    protected void processKeyEvent(KeyEvent evt) {
        if (clientGUI != null) {
            evt.setSource(clientGUI);
            clientGUI.getMenuBar().dispatchEvent(evt);
            // Make the source be the ClientGUI and not the dialog
            // This prevents a ClassCastException in ToolTipManager
            clientGUI.getCurrentPanel().dispatchEvent(evt);
        }
        if (!evt.isConsumed()) {
            super.processKeyEvent(evt);
        }
    }
}
