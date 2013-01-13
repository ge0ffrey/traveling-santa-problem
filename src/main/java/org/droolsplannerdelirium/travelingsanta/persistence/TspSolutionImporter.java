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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.droolsplannerdelirium.travelingsanta.domain.Appearance;
import org.droolsplannerdelirium.travelingsanta.domain.City;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

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
        private boolean solutionInitialized = false;

        public Solution readSolution() throws IOException {
            travelingSalesmanTour = new TravelingSalesmanTour();
            travelingSalesmanTour.setId(0L);
            readHeaders();
            readCityList();
            createVisitList();
            initializeVisitList();
            return travelingSalesmanTour;
        }

        private void readHeaders() throws IOException {
            travelingSalesmanTour.setName(readStringValue("id,x,y"));
            cityListSize = 150000;
        }

        private void readCityList() throws IOException {
            List<City> cityList = new ArrayList<City>(cityListSize);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                if (line.equals("path1,path2")) {
                    solutionInitialized = true;
                    break;
                }
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
            List<Visit> visitList = new ArrayList<Visit>(cityList.size());
            Domicile domicile = new Domicile();
            domicile.setId(-1L);
            domicileList.add(domicile);
            int index = 0;
            for (City city : cityList) {
                Visit visit = new Visit();
                visit.setId(city.getId());
                visit.setCity(city);
                // Notice that we leave the PlanningVariable properties on null
                visitList.add(visit);
                index++;
            }
            travelingSalesmanTour.setDomicileList(domicileList);
            travelingSalesmanTour.setVisitList(visitList);
        }

        private void initializeVisitList() throws IOException {
            if (!solutionInitialized) {
                return;
            }
            Domicile domicile = travelingSalesmanTour.getDomicileList().get(0);
            List<Visit> visitList = travelingSalesmanTour.getVisitList();
            Map<Long, Visit> idMap = new HashMap<Long, Visit>(visitList.size());
            for (Visit visit : visitList) {
                idMap.put(visit.getCity().getId(), visit);
            }
            Map<Integer, Visit> oddVisitMap = new HashMap<Integer, Visit>(visitList.size());
            Map<Visit, Integer> oddIndexMap = new HashMap<Visit, Integer>(visitList.size());
            Map<Integer, Visit> evenVisitMap = new HashMap<Integer, Visit>(visitList.size());
            Map<Visit, Integer> evenIndexMap = new HashMap<Visit, Integer>(visitList.size());
            for (int index = 0; ; index++) {
                String line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                String[] lineTokens = splitBy(line, ",", ",", 2, 2, false, false);
                if (!lineTokens[0].equals("null")) {
                    long oddId = Long.parseLong(lineTokens[0]);
                    if (oddId < 0L || oddId >= visitList.size()) {
                        throw new IllegalArgumentException("Invalid oddId (" + oddId + ").");
                    }
                    Visit oddVisit = idMap.get(oddId);
                    if (oddVisit == null) {
                        throw new IllegalArgumentException("Invalid oddVisit (" + oddVisit
                                + ") for oddId (" + oddId + ").");
                    }
                    oddVisitMap.put(index, oddVisit);
                    oddIndexMap.put(oddVisit, index);
                }
                if (!lineTokens[1].equals("null")) {
                    long evenId = Long.parseLong(lineTokens[1]);
                    if (evenId < 0L || evenId >= visitList.size()) {
                        throw new IllegalArgumentException("Invalid evenId (" + evenId + ").");
                    }
                    Visit evenVisit = idMap.get(evenId);
                    if (evenVisit == null) {
                        throw new IllegalArgumentException("Invalid evenVisit (" + evenVisit
                                + ") for evenId (" + evenId + ").");
                    }
                    evenVisitMap.put(index, evenVisit);
                    evenIndexMap.put(evenVisit, index);
                }
            }
            for (Visit visit : visitList) {
                int oddIndex = oddIndexMap.get(visit);
                visit.setPreviousOdd(oddIndex == 0 ? domicile : oddVisitMap.get(oddIndex - 1));
                int evenIndex = evenIndexMap.get(visit);
                visit.setPreviousEven(evenIndex == 0 ? domicile : evenVisitMap.get(evenIndex - 1));
            }
        }

    }

}
