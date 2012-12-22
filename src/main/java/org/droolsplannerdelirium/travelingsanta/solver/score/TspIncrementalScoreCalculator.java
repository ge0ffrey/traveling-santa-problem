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

import org.drools.planner.core.score.buildin.hardandsoft.DefaultHardAndSoftScore;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.score.buildin.hardandsoftlong.DefaultHardAndSoftLongScore;
import org.drools.planner.core.score.buildin.hardmediumsoft.DefaultHardMediumSoftScore;
import org.drools.planner.core.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;
import org.drools.planner.core.score.buildin.simple.SimpleScore;
import org.drools.planner.core.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.droolsplannerdelirium.travelingsanta.domain.Appearance;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

public class TspIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<TravelingSalesmanTour> {

    private Domicile domicile;

    private int hardScore;
    private int oddScore;
    private int evenScore;

    public void resetWorkingSolution(TravelingSalesmanTour tour) {
        if (tour.getDomicileList().size() != 1) {
            throw new UnsupportedOperationException(
                    "The domicileList (" + tour.getDomicileList() + ") should be a singleton.");
        }
        domicile = tour.getDomicileList().get(0);
        hardScore = 0;
        oddScore = 0;
        evenScore = 0;
        for (Visit visit : tour.getVisitList()) {
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
            // HACK: This counts too much, but the insert/retracts balance each other out
            oddScore += domicile.getCity().getDistance(previousOdd.getCity());
            oddScore -= domicile.getCity().getDistance(visit.getCity());
        }
        Appearance previousEven = visit.getPreviousEven();
        if (previousEven != null) {
            evenScore -= visit.getDistanceToPreviousEven();
            // HACK: This counts too much, but the insert/retracts balance each other out
            evenScore += domicile.getCity().getDistance(previousEven.getCity());
            evenScore -= domicile.getCity().getDistance(visit.getCity());
        }
    }

    private void retract(Visit visit) {
        Appearance previousOdd = visit.getPreviousOdd();
        if (previousOdd != null) {
            oddScore += visit.getDistanceToPreviousOdd();
            // HACK: This counts too much, but the insert/retracts balance each other out
            oddScore -= domicile.getCity().getDistance(previousOdd.getCity());
            oddScore += domicile.getCity().getDistance(visit.getCity());
        }
        Appearance previousEven = visit.getPreviousEven();
        if (previousEven != null) {
            evenScore += visit.getDistanceToPreviousEven();
            // HACK: This counts too much, but the insert/retracts balance each other out
            evenScore -= domicile.getCity().getDistance(previousEven.getCity());
            evenScore += domicile.getCity().getDistance(visit.getCity());
        }
    }

    public HardMediumSoftScore calculateScore() {
        int mediumScore = Math.min(oddScore, evenScore);
        int softScore = Math.max(oddScore, evenScore);
        return DefaultHardMediumSoftScore.valueOf(hardScore, mediumScore, softScore);
    }

}
