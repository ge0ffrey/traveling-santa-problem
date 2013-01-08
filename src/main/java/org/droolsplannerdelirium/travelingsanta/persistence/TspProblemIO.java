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

import java.io.File;

import org.drools.planner.core.solution.ProblemIO;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.machinereassignment.persistence.MachineReassignmentSolutionExporter;
import org.drools.planner.examples.machinereassignment.persistence.MachineReassignmentSolutionImporter;

public class TspProblemIO implements ProblemIO {

    private TspSolutionImporter importer = new TspSolutionImporter();
    private TspSolutionExporter exporter = new TspSolutionExporter();

    public String getFileExtension() {
        // In sync with importer.getInputFileSuffix() and exporter.getOutputFileSuffix()
        return "csv";
    }

    public Solution read(File inputSolutionFile) {
        return importer.readSolution(inputSolutionFile);
    }

    public void write(Solution solution, File outputSolutionFile) {
        exporter.writeSolution(solution, outputSolutionFile);
    }

}
