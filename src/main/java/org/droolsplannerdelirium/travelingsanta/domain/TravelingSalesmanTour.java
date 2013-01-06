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

package org.droolsplannerdelirium.travelingsanta.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.api.domain.solution.PlanningSolution;
import org.drools.planner.core.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

@PlanningSolution
@XStreamAlias("TravelingSalesmanTour")
public class TravelingSalesmanTour extends AbstractPersistable implements Solution<HardMediumSoftScore> {

    private String name;
    private List<City> cityList;
    private List<org.droolsplannerdelirium.travelingsanta.domain.Domicile> domicileList;

    private List<Visit> visitList;

    private HardMediumSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public List<org.droolsplannerdelirium.travelingsanta.domain.Domicile> getDomicileList() {
        return domicileList;
    }

    public void setDomicileList(List<Domicile> domicileList) {
        this.domicileList = domicileList;
    }

    @PlanningEntityCollectionProperty
    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour)) {
            return false;
        } else {
            org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour other = (org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour) o;
            if (visitList.size() != other.visitList.size()) {
                return false;
            }
            for (Iterator<Visit> it = visitList.iterator(), otherIt = other.visitList.iterator(); it.hasNext();) {
                Visit visit = it.next();
                Visit otherVisit = otherIt.next();
                // Notice: we don't use equals()
                if (!visit.solutionEquals(otherVisit)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Visit visit : visitList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(visit.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
