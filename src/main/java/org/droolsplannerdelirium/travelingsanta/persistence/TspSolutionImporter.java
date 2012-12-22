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

package org.droolsplannerdelirium.travelingsanta.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.droolsplannerdelirium.travelingsanta.domain.City;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;
import org.droolsplannerdelirium.travelingsanta.persistence.TspDaoImpl;

@SuppressWarnings("unused")
public class TspSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".csv";

    public static void main(String[] args) {
        new org.droolsplannerdelirium.travelingsanta.persistence.TspSolutionImporter().convertAll();
    }

    public TspSolutionImporter() {
        super(new TspDaoImpl());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new TravelingSalesmanTourInputBuilder();
    }

    public class TravelingSalesmanTourInputBuilder extends TxtInputBuilder {

        private TravelingSalesmanTour travelingSalesmanTour;

        private int cityListSize;

        public Solution readSolution() throws IOException {
            travelingSalesmanTour = new TravelingSalesmanTour();
            travelingSalesmanTour.setId(0L);
            readHeaders();
            readCityList();
            createVisitList();
            return travelingSalesmanTour;
        }

        private void readHeaders() throws IOException {
            travelingSalesmanTour.setName(readStringValue("id,x,y"));
            cityListSize = 150000;
        }

        private void readCityList() throws IOException {
            List<City> cityList = new ArrayList<City>(cityListSize);
            for (int i = 0; i < cityListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBy(line, ",", ",", 3, 3, false, false);
                City city = new City();
                // 149945,3197,2073
                city.setId(Long.parseLong(lineTokens[0]));
                city.setLatitude(Integer.parseInt(lineTokens[2]));
                city.setLongitude(Integer.parseInt(lineTokens[1]));
                cityList.add(city);
            }
            travelingSalesmanTour.setCityList(cityList);
        }

        private void createVisitList() {
            List<City> cityList = travelingSalesmanTour.getCityList();
            int domicileListSize = 1;
            List<Domicile> domicileList = new ArrayList<Domicile>(domicileListSize);
            List<Visit> visitList = new ArrayList<Visit>(cityList.size() - domicileListSize);
            int count = 0;
            for (City city : cityList) {
                if (count < domicileListSize) {
                    Domicile domicile = new Domicile();
                    domicile.setId(city.getId());
                    domicile.setCity(city);
                    domicileList.add(domicile);
                } else {
                    Visit visit = new Visit();
                    visit.setId(city.getId());
                    visit.setCity(city);
                    // Notice that we leave the PlanningVariable properties on null
                    visitList.add(visit);
                }
                count++;
            }
            travelingSalesmanTour.setDomicileList(domicileList);
            travelingSalesmanTour.setVisitList(visitList);
        }

    }

}
