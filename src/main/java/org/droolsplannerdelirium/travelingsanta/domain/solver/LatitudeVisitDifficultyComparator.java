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

package org.droolsplannerdelirium.travelingsanta.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;

public class LatitudeVisitDifficultyComparator implements Comparator<Visit>, Serializable {

    public int compare(Visit a, Visit b) {
        return new CompareToBuilder()
                // TODO experiment with (aLatitude - bLatitude) % 10
                .append(a.getCity().getLatitude(), b.getCity().getLatitude())
                .append(a.getCity().getLongitude(), b.getCity().getLongitude())
                .append(a.getId(), b.getId())
                .toComparison();
    }

}