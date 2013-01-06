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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.api.domain.variable.ValueRanges;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.droolsplannerdelirium.travelingsanta.domain.solver.VisitDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = VisitDifficultyComparator.class)
@XStreamAlias("Visit")
public class Visit extends AbstractPersistable implements Appearance {

    private City city; // the destinationCity
    
    // Planning variables: changes during planning, between score calculations.
    private Appearance previousOdd;
    private Appearance previousEven;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @PlanningVariable(chained = true)
    @ValueRanges({
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "domicileList"),
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "visitList",
                    excludeUninitializedPlanningEntity = true)})
    public Appearance getPreviousOdd() {
        return previousOdd;
    }

    public void setPreviousOdd(Appearance previousOdd) {
        this.previousOdd = previousOdd;
    }

//    @PlanningVariable(chained = true)
//    @ValueRanges({
//            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "domicileList"),
//            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "visitList",
//                    excludeUninitializedPlanningEntity = true)})
    public Appearance getPreviousEven() {
        return previousEven;
    }

    public void setPreviousEven(Appearance previousEven) {
        this.previousEven = previousEven;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getDistanceToPreviousOdd() {
        if (previousOdd == null) {
            return 0;
        }
        return getDistanceTo(previousOdd);
    }

    public int getDistanceToPreviousEven() {
        if (previousEven == null) {
            return 0;
        }
        return getDistanceTo(previousEven);
    }

    public int getDistanceTo(Appearance appearance) {
        if (appearance instanceof Visit) {
            return city.getDistance(((Visit)appearance).getCity());
        }
        return 0;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof org.droolsplannerdelirium.travelingsanta.domain.Visit) {
            org.droolsplannerdelirium.travelingsanta.domain.Visit other = (org.droolsplannerdelirium.travelingsanta.domain.Visit) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(city, other.city) // TODO performance leak: not needed?
                    .append(previousOdd, other.previousOdd) // TODO performance leak: not needed?
                    .append(previousEven, other.previousEven) // TODO performance leak: not needed?
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(city) // TODO performance leak: not needed?
                .append(previousOdd) // TODO performance leak: not needed?
                .append(previousEven) // TODO performance leak: not needed?
                .toHashCode();
    }

    @Override
    public String toString() {
        return city + "(odd after " + (previousOdd == null ? "null" : previousOdd instanceof Visit ? ((Visit) previousOdd).getCity() : "domicile")
                + ", even after " + (previousEven == null ? "null" : previousEven instanceof Visit ? ((Visit) previousEven).getCity() : "domicile") + ")";
    }

}
