/*
 * Copyright 2013 JBoss Inc
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.droolsplannerdelirium.travelingsanta.domain.Appearance;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

public class TspSolutionExporter extends AbstractTxtSolutionExporter {

    public static void main(String[] args) {
        new TspSolutionExporter().convertAll();
    }

    public TspSolutionExporter() {
        super(new TspDaoImpl());
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new TspOutputBuilder();
    }

    public class TspOutputBuilder extends TxtOutputBuilder {

        private TravelingSalesmanTour travelingSalesmanTour;

        public void setSolution(Solution solution) {
            travelingSalesmanTour = (TravelingSalesmanTour) solution;
        }

        public void writeSolution() throws IOException {
            bufferedWriter.write("path1,path2");
            bufferedWriter.newLine();

            List<Domicile> domicileList = travelingSalesmanTour.getDomicileList();
            if (domicileList.size() != 1) {
                throw new IllegalStateException();
            }
            Domicile domicile = domicileList.get(0);
            List<Visit> visitList = travelingSalesmanTour.getVisitList();
            Map<Appearance, Visit> oddMap = new HashMap<Appearance, Visit>(visitList.size());
            Map<Appearance, Visit> evenMap = new HashMap<Appearance, Visit>(visitList.size());
            for (Visit visit : visitList) {
                oddMap.put(visit.getPreviousOdd(), visit);
                evenMap.put(visit.getPreviousEven(), visit);
            }
            Map<Visit, Integer> oddIndexMap = buildIndexMap(domicile, visitList, oddMap);
            Map<Visit, Integer> evenIndexMap = buildIndexMap(domicile, visitList, evenMap);
            int count = 0;
            for (Visit visit : visitList) {
                Integer oddIndex = oddIndexMap.get(visit);
                Integer evenIndex = evenIndexMap.get(visit);
                if (oddIndex == null || evenIndex == null) {
                    logger.warn("The visit ({}) should has no oddIndex ({}) and evenIndex ({}).",
                            visit, oddIndex, evenIndex);
                }
                bufferedWriter.write(oddIndex + "," + evenIndex);
                bufferedWriter.newLine();
                count++;
            }
            if (count != 150000) {
                throw new IllegalStateException("The count (" + count + ") should be 150000.");
            }
        }

        private Map<Visit, Integer> buildIndexMap(Domicile domicile, List<Visit> visitList,
                Map<Appearance, Visit> map) {
            Map<Visit, Integer> indexMap = new HashMap<Visit, Integer>(visitList.size());
            Visit nextVisit = map.get(domicile);
            int index = 1;
            while (nextVisit != null) {
                indexMap.put(nextVisit, index);
                nextVisit = map.get(nextVisit);
                index++;
            }
            return indexMap;
        }

    }

}
