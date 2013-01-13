/*
 * Copyright 2012 JBoss Inc
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

package org.droolsplannerdelirium.travelingsanta.solver.score;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.drools.planner.core.score.buildin.hardmediumsoft.DefaultHardMediumSoftScore;
import org.drools.planner.core.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.drools.planner.core.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.droolsplannerdelirium.travelingsanta.domain.Appearance;
import org.droolsplannerdelirium.travelingsanta.domain.City;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

public class TspIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<TravelingSalesmanTour> {

    private int hardScore;
    private double oddScore;
    private double evenScore;

    private NavigableMap<Double, NavigableMap<Double, Visit>> coordinateMap = null;

    public void resetWorkingSolution(TravelingSalesmanTour tour) {
        hardScore = 0;
        oddScore = 0.0;
        evenScore = 0.0;
        coordinateMap = new TreeMap<Double, NavigableMap<Double, Visit>>();
        for (Visit visit : tour.getVisitList()) {
            City city = visit.getCity();
            NavigableMap<Double, Visit> subCoordinateMap = coordinateMap.get(city.getLatitude());
            if (subCoordinateMap == null) {
                subCoordinateMap = new TreeMap<Double, Visit>();
                coordinateMap.put(city.getLatitude(), subCoordinateMap);
            }
            Visit previousValue = subCoordinateMap.put(city.getLongitude(), visit);
            if (previousValue != null) {
                throw new IllegalStateException("Invalid previousValue (" + previousValue + ").");
            }
            insert(visit);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insert((Visit) entity);
    }

    public void beforeAllVariablesChanged(Object entity) {
        retract((Visit) entity);
    }

    public void afterAllVariablesChanged(Object entity) {
        insert((Visit) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Visit) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((Visit) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((Visit) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Visit visit) {
        Appearance previousOdd = visit.getPreviousOdd();
        if (previousOdd != null) {
            oddScore -= visit.getDistanceToPreviousOdd();
        }
        Appearance previousEven = visit.getPreviousEven();
        if (previousEven != null) {
            evenScore -= visit.getDistanceToPreviousEven();
            // Do not share edges
            if (previousOdd == previousEven) {
                hardScore--;
            }
            // Do not share reverse edges
            if (previousOdd instanceof Visit && ((Visit) previousOdd).getPreviousEven() == visit) {
                hardScore--;
            }
            if (previousEven instanceof Visit && ((Visit) previousEven).getPreviousOdd() == visit) {
                hardScore--;
            }
        }
    }

    private void retract(Visit visit) {
        Appearance previousOdd = visit.getPreviousOdd();
        if (previousOdd != null) {
            oddScore += visit.getDistanceToPreviousOdd();
        }
        Appearance previousEven = visit.getPreviousEven();
        if (previousEven != null) {
            evenScore += visit.getDistanceToPreviousEven();
            // Do not share edges
            if (previousOdd == previousEven) {
                hardScore++;
            }
            // Do not share reverse edges
            if (previousOdd instanceof Visit && ((Visit) previousOdd).getPreviousEven() == visit) {
                hardScore++;
            }
            if (previousEven instanceof Visit && ((Visit) previousEven).getPreviousOdd() == visit) {
                hardScore++;
            }
        }
    }

    public HardMediumSoftScore calculateScore() {
        double mediumScore = Math.min(oddScore, evenScore);
        double softScore = Math.max(oddScore, evenScore);
        // TODO HACK
        return DefaultHardMediumSoftScore.valueOf(hardScore, (int) (mediumScore * 100.0), (int) (softScore * 100.0));
        // return DefaultHardMediumSoftScore.valueOf(hardScore, mediumScore, softScore);
    }

    public List<Visit> findNearVisitList(Visit srcVisit, double distance) {
        City srcCity = srcVisit.getCity();
        NavigableMap<Double, NavigableMap<Double, Visit>> partMap = coordinateMap
                .headMap(srcCity.getLatitude() + distance, true).tailMap(srcCity.getLatitude() - distance, true);
        List<Visit> nearVisitList = new ArrayList<Visit>(partMap.size() * 10 + 20);
        for (NavigableMap<Double, Visit> subMap : partMap.values()) {
            NavigableMap<Double, Visit> partSubMap = subMap
                    .headMap(srcCity.getLongitude() + distance, true).tailMap(srcCity.getLongitude() - distance, true);
            for (Visit visit : partSubMap.values()) {
                if (visit != srcVisit) {
                    nearVisitList.add(visit);
                }
            }
        }
        return nearVisitList;
    }

//    public Visit findNear(Visit srcVisit, int nearCount) {
//        City srcCity = srcVisit.getCity();
//        NavigableMap<Double, Visit> subCoordinateMap = coordinateMap.get(srcCity.getLatitude());
//
//    }

}
