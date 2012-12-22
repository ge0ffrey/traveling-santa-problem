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

package org.droolsplannerdelirium.travelingsanta.app;

import org.drools.planner.config.XmlSolverFactory;
import org.drools.planner.core.Solver;
import org.drools.planner.examples.common.app.CommonApp;
import org.drools.planner.examples.common.persistence.AbstractSolutionImporter;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.droolsplannerdelirium.travelingsanta.persistence.TspDaoImpl;
import org.droolsplannerdelirium.travelingsanta.persistence.TspSolutionImporter;
import org.droolsplannerdelirium.travelingsanta.swingui.TspPanel;

public class TspApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/droolsplannerdelirium/travelingsanta/solver/tspSolverConfig.xml";

    public static void main(String[] args) {
        fixateLookAndFeel();
        new org.droolsplannerdelirium.travelingsanta.app.TspApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverFactory solverFactory = new XmlSolverFactory();
        solverFactory.configure(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new TspPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TspDaoImpl();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new TspSolutionImporter();
    }

}
