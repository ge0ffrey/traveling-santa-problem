/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.droolsplannerdelirium.travelingsanta.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.droolsplannerdelirium.travelingsanta.domain.City;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

/**
 * TODO this code is highly unoptimized
 */
public class TspWorldPanel extends JPanel {

    private static final int TEXT_SIZE = 8;

    private final TspPanel tspPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    private ImageIcon europaBackground;

    public TspWorldPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                TravelingSalesmanTour travelingSalesmanTour = org.droolsplannerdelirium.travelingsanta.swingui.TspWorldPanel.this.tspPanel.getTravelingSalesmanTour();
                if (travelingSalesmanTour != null) {
                    resetPanel(travelingSalesmanTour);
                }
            }
        });
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                if (translator != null) {
//                    double longitude = translator.translateXToLongitude(e.getX());
//                    double latitude = translator.translateYToLatitude(e.getY());
//                    org.droolsplannerdelirium.travelingsanta.swingui.TspWorldPanel.this.tspPanel.insertCityAndVisit(longitude, latitude);
//                }
//            }
//        });
        europaBackground = new ImageIcon(getClass().getResource(
                "/org/droolsplannerdelirium/travelingsanta/swingui/europaBackground.png"));
    }

    public void resetPanel(TravelingSalesmanTour travelingSalesmanTour) {
        translator = new LatitudeLongitudeTranslator();
        for (City city : travelingSalesmanTour.getCityList()) {
            translator.addCoordinates(city.getLatitude(), city.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics g = createCanvas(width, height);
        String tourName = travelingSalesmanTour.getName();
        if (tourName.startsWith("europe")) {
            g.drawImage(europaBackground.getImage(), 0, 0, translator.getImageWidth(), translator.getImageHeight(), this);
        }
        g.setFont(g.getFont().deriveFont((float) TEXT_SIZE));
        g.setColor(TangoColors.PLUM_2);
        for (Visit visit : travelingSalesmanTour.getVisitList()) {
            City city = visit.getCity();
            int x = translator.translateLongitudeToX(city.getLongitude());
            int y = translator.translateLatitudeToY(city.getLatitude());
            g.fillRect(x, y, 1, 1);
            if (city.getName() != null) {
                g.drawString(city.getName(), x + 3, y - 3);
            }
        }
        g.setColor(TangoColors.BUTTER_1);
        for (Visit visit : travelingSalesmanTour.getVisitList()) {
            if (visit.getPreviousOdd() != null && visit.getPreviousOdd() instanceof Visit) {
                City previousCity = ((Visit) visit.getPreviousOdd()).getCity();
                int previousX = translator.translateLongitudeToX(previousCity.getLongitude());
                int previousY = translator.translateLatitudeToY(previousCity.getLatitude());
                City city = visit.getCity();
                int x = translator.translateLongitudeToX(city.getLongitude());
                int y = translator.translateLatitudeToY(city.getLatitude());
                g.drawLine(previousX, previousY, x, y);
            }
        }
        // Draw all CHAMELEON before drawing any PLUM
        g.setColor(TangoColors.PLUM_1);
        for (Visit visit : travelingSalesmanTour.getVisitList()) {
            if (visit.getPreviousEven() != null && visit.getPreviousEven() instanceof Visit) {
                City previousCity = ((Visit) visit.getPreviousEven()).getCity();
                int previousX = translator.translateLongitudeToX(previousCity.getLongitude());
                int previousY = translator.translateLatitudeToY(previousCity.getLatitude());
                City city = visit.getCity();
                int x = translator.translateLongitudeToX(city.getLongitude());
                int y = translator.translateLatitudeToY(city.getLatitude());
                g.drawLine(previousX, previousY, x, y);
            }
        }
        repaint();
    }

    public void updatePanel(TravelingSalesmanTour travelingSalesmanTour) {
        resetPanel(travelingSalesmanTour);
    }

    private Graphics createCanvas(double width, double height) {
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        return g;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }

}
