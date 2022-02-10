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
package de.uni.rostock.ub.tools.signed.ui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Vector;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.w3c.dom.svg.SVGDocument;

import de.uni.rostock.ub.tools.signed.SignedApp;
import de.uni.rostock.ub.tools.signed.model.ShelfmarkObject;

/**
 * a Java AWT Frame that holds the UI
 * 
 * @author Robert Stephan, Rostock University Library
 *
 */
public class SignedFrame extends Frame {
    private static final long serialVersionUID = 1L;

    private TextField txtBarcode;
    private TextField txtSignatur;
    private TextField txtLocation;
    private TextField txtLoanindicator;
    private Choice chTemplate;
    private Checkbox cbSofortDrucken;
    private Button btnExecBarcode;
    private Button btnExecSignatur;
    private Button btnExecTemplate;
    private Button btnExecTexte;
    private Panel pnlTexts;
    private Button btnDrucken;
    private List<TextField> txtLines = new Vector<>();

    //alternative: org.apache.batik.swing.JSVGCanvas (bad display quality); 
    private SignedCanvas canvas;

    private SignedApp app;

    public SignedFrame(SignedApp app) {
        super();
        this.app = app;

        setTitle(app.getConfigService().getConfig().getProperty("signed.application.title") + " ("
            + SignedApp.retrieveVersionFromManifest() + ")");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/signed_icon.png"));
        setIconImage(icon);
        setSize(600, 450);
        setLayout(new GridBagLayout());
        MenuBar appMenuBar = new MenuBar();
        Menu mDatei = new Menu("Datei");
        appMenuBar.add(mDatei);
        MenuItem miBeenden = new MenuItem("Beenden");
        miBeenden.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mDatei.add(miBeenden);
        setMenuBar(appMenuBar);

        Label lblBarcode = new Label("Barcode:");
        add(lblBarcode, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));
        txtBarcode = new TextField("");
        String firstBarcode = app.getConfigService().getConfig().getProperty("signed.application.1stbarcode");
        if(firstBarcode!=null){
            txtBarcode.setText(firstBarcode);
        }
        add(txtBarcode, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));

        txtBarcode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    updateSignature();
                    updateTemplate();
                    updateTexte();
                    updateEtikett();
                    if (cbSofortDrucken.getState()) {
                        printEtikett();
                    }
                }

            };
        });
        txtBarcode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtBarcode.selectAll();
                }
            }
        });

        btnExecBarcode = new Button("Start");
        add(btnExecBarcode, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        btnExecBarcode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateSignature();
                updateTemplate();
                updateTexte();
                updateEtikett();
                if (cbSofortDrucken.getState()) {
                    printEtikett();
                }
            }
        });

        Label lblLocation = new Label("Standort:");
        add(lblLocation, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));

        txtLocation = new TextField();
        add(txtLocation, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        txtLocation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtLocation.selectAll();
                }
            }
        });

        txtLoanindicator = new TextField();
        txtLoanindicator.setEditable(false);
        add(txtLoanindicator, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));

        
        Label lblSignatur = new Label("Signatur:");
        add(lblSignatur, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));

        txtSignatur = new TextField();
        add(txtSignatur, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        txtSignatur.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    txtSignatur.selectAll();
                }
            }
        });

        btnExecSignatur = new Button("Start");
        add(btnExecSignatur, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        btnExecSignatur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTemplate();
                updateTexte();
                updateEtikett();
                if (cbSofortDrucken.getState()) {
                    printEtikett();
                }
            }
        });

        Label lblTemplate = new Label("Vorlage:");
        add(lblTemplate, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));
        chTemplate = new Choice();

        for (String k : app.getConfigService().getTemplateKeys().values()) {
            chTemplate.add(app.getConfigService().findTemplateName(k));
        }
        add(chTemplate, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));

        btnExecTemplate = new Button("Start");
        add(btnExecTemplate, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        btnExecTemplate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateTexte();
                updateEtikett();
                if (cbSofortDrucken.getState()) {
                    printEtikett();
                }
            }
        });

        Label lblEttiket = new Label("Etikett:");
        add(lblEttiket, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));

        Panel pnlEtikett = new Panel();
        pnlEtikett.setLayout(new GridLayout(1, 2, 8, 0));
        add(pnlEtikett, new GridBagConstraints(1, 4, 2, 3, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(8, 8, 8, 8), 0, 0));

        pnlTexts = new Panel();
        pnlTexts.setLayout(new GridBagLayout());
        pnlEtikett.add(pnlTexts);

        canvas = new SignedCanvas();
        pnlEtikett.add(canvas);

        btnExecTexte = new Button("Bild aktualisieren");
        add(btnExecTexte, new GridBagConstraints(3, 4, 1, 1, 0.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        btnExecTexte.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateEtikett();
                if (cbSofortDrucken.getState()) {
                    printEtikett();
                }
            }
        });

        btnDrucken = new Button("Drucken");
        add(btnDrucken, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.LAST_LINE_END,
                GridBagConstraints.HORIZONTAL, new Insets(8, 8, 8, 8), 0, 0));
        btnDrucken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEtikett();
                printEtikett();
            }
        });

        cbSofortDrucken = new Checkbox("Sofort Drucken");
        add(cbSofortDrucken, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));

        setVisible(true);

        System.out.println("Verfügbare Drucker:");
        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService ps : prservices) {
            System.out.println(ps.getName());

        }
        txtBarcode.selectAll();
        txtBarcode.requestFocus();
    }

    private void updateSignature() {
        try {
            txtBarcode.setText(txtBarcode.getText().toUpperCase());
            txtSignatur.setText("");
            ShelfmarkObject signObject = app.retrieveShelfmarkFromOPACByBarcode(txtBarcode.getText());
            txtSignatur.setText(signObject.getSignature());
            txtLoanindicator.setName(signObject.getLoanindicator());
            txtLoanindicator.setText(" @ " + signObject.getLoanindicator());
            String loanInfo = app.retrieveLoanInfo(signObject.getLoanindicator());
            if (loanInfo != null) {
                txtLoanindicator.setText(" @ " + signObject.getLoanindicator() + "   [" + loanInfo + "]");
            }
            txtLocation.setText(signObject.getLocation());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTemplate() {
        try {
            String template = app.getConfigService().findTemplateKey(readShelfmark());
            String[] keyArray = app.getConfigService().getTemplateKeys().keySet().toArray(new String[] {});
            for (int i = 0; i < keyArray.length; i++) {
                if (app.getConfigService().getTemplateKeys().get(keyArray[i]).equals(template)) {
                    chTemplate.select(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTexte() {
        SVGDocument svg = app.loadSVGTemplate(readTemplateKey());

        pnlTexts.removeAll();
        txtLines.clear();

        SortedMap<String, String> textKeys = app.getConfigService().findTextKeys(readTemplateKey());
        Map<String, String> labelData = app.calcShelfmarkLabelData(readShelfmark(), readTemplateKey());

        int pos = 0;
        for (String id : textKeys.values()) {
            if (!id.equals("_ignore")) {

                Label lbl = new Label(app.retrieveLabelFromSVG(svg, id) + ":");
                pnlTexts.add(lbl, new GridBagConstraints(0, ++pos, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                        GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
                TextField txt = new TextField(labelData.get(id));
                txt.setName(id);
                pnlTexts.add(txt, new GridBagConstraints(1, pos, 1, 1, 1.0, 0.0, GridBagConstraints.FIRST_LINE_START,
                        GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
                txtLines.add(txt);
            }
        }
        doLayout();
        pnlTexts.doLayout();
    }

    private void printEtikett() {
        app.printShelfmarkLabel(readTemplateKey(), readLabelData());
        txtBarcode.selectAll();
        txtBarcode.requestFocus();
    }

    private Map<String, String> readLabelData() {
        Map<String, String> data = new HashMap<>();
        for (TextField tf : txtLines) {
            data.put(tf.getName(), tf.getText().trim());
        }
        return data;
    }

    private ShelfmarkObject readShelfmark() {
        ShelfmarkObject sgn = new ShelfmarkObject();
        sgn.setLocation(txtLocation.getText().trim());
        sgn.setSignature(txtSignatur.getText().trim());
        sgn.setLoanindicator(txtLoanindicator.getName());

        return sgn;
    }

    private String readTemplateKey() {
        for (String key : app.getConfigService().getTemplateKeys().values()) {
            if (chTemplate.getSelectedItem().equals(app.getConfigService().findTemplateName(key))) {
                return key;
            }
        }
        return null;
    }

    private void updateEtikett() {
        SVGDocument svgdoc = app.calcSVG(readTemplateKey(), readLabelData(), false);
        canvas.setDocument(svgdoc);
        canvas.validate();
        canvas.repaint();
        pnlTexts.validate();
        btnDrucken.requestFocus();
    }
}
