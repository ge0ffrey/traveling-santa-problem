package org.droolsplannerdelirium.travelingsanta.solver.move;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.move.factory.MoveIteratorFactory;
import org.drools.planner.core.heuristic.selector.move.generic.SwapMove;
import org.drools.planner.core.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.drools.planner.core.heuristic.selector.move.generic.chained.ChainedSwapMove;
import org.drools.planner.core.move.CompositeMove;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.score.director.incremental.IncrementalScoreDirector;
import org.droolsplannerdelirium.travelingsanta.domain.Appearance;
import org.droolsplannerdelirium.travelingsanta.domain.Domicile;
import org.droolsplannerdelirium.travelingsanta.domain.TravelingSalesmanTour;
import org.droolsplannerdelirium.travelingsanta.domain.Visit;
import org.droolsplannerdelirium.travelingsanta.solver.score.TspIncrementalScoreCalculator;

// TODO this is more hack than code :)
public class NearChangeMoveIteratorFactory implements MoveIteratorFactory {

//    private static final int NEAR_COUNT_MAX = 100;

    private boolean init = false;
    private TravelingSalesmanTour tour;
    private List<Visit> visitList;
    private PlanningEntityDescriptor entityDescriptor;
    private PlanningVariableDescriptor oddVariableDescriptor;
    private PlanningVariableDescriptor evenVariableDescriptor;
    private TspIncrementalScoreCalculator scoreCalculator;
    private Domicile domicile;

    public long getSize(ScoreDirector scoreDirector) {
//        TravelingSalesmanTour tour = (TravelingSalesmanTour) scoreDirector.getWorkingSolution();
//        return tour.getVisitList().size() * 2 * NEAR_COUNT_MAX;
        return -1L;
    }

    public Iterator<Move> createOriginalMoveIterator(ScoreDirector scoreDirector) {
        throw new UnsupportedOperationException();
    }

    public Iterator<Move> createRandomMoveIterator(ScoreDirector scoreDirector, final Random workingRandom) {
        init(scoreDirector);
        final Visit srcVisit = visitList.get(workingRandom.nextInt(visitList.size()));
        double distance = 400.0;
        List<Visit> nearVisitList = scoreCalculator.findNearVisitList(srcVisit, distance);
        while (nearVisitList.size() < 100) {
            distance *= 2;
            nearVisitList = scoreCalculator.findNearVisitList(srcVisit, distance);
        }
        final Iterator<Visit> nearVisitIterator = nearVisitList.iterator();
        return new Iterator<Move>() {

            private int moveTypeIndex = 0;
            private Visit nearVisit = null;

            public boolean hasNext() {
                if (moveTypeIndex == 0) {
                    return nearVisitIterator.hasNext();
                } else {
                    return true;
                }
            }

            public Move next() {
                Move move;
                switch (moveTypeIndex) {
                    case 0:
                        nearVisit = nearVisitIterator.next();
                        move = new ChainedChangeMove(srcVisit, oddVariableDescriptor, nearVisit);
                        break;
                    case 1:
                        move = new ChainedChangeMove(srcVisit, evenVariableDescriptor, nearVisit);
                        break;
                    case 2:
                        move = new CompositeMove(Arrays.<Move>asList(
                                new ChainedChangeMove(srcVisit, oddVariableDescriptor, nearVisit),
                                new ChainedChangeMove(srcVisit, evenVariableDescriptor, nearVisit)));
                        break;
                    case 3:
                        move = new ChainedSwapMove(entityDescriptor.getPlanningVariableDescriptors(),
                                srcVisit, nearVisit);
                        break;
                    default:
                        throw new IllegalStateException("Invalid moveTypeIndex (" + moveTypeIndex + ")");
                }
                moveTypeIndex = (moveTypeIndex + 1) % 4;
                return move;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private void init(ScoreDirector scoreDirector) {
        if (!init) {
            init = true;
            tour = (TravelingSalesmanTour) scoreDirector.getWorkingSolution();
            visitList = tour.getVisitList();
            entityDescriptor = scoreDirector.getSolutionDescriptor().getPlanningEntityDescriptor(Visit.class);
            oddVariableDescriptor = entityDescriptor.getPlanningVariableDescriptor("previousOdd");
            evenVariableDescriptor = entityDescriptor.getPlanningVariableDescriptor("previousEven");
            scoreCalculator = (TspIncrementalScoreCalculator) ((IncrementalScoreDirector) scoreDirector).getIncrementalScoreCalculator();
            domicile = tour.getDomicileList().get(0);
        }
    }

//    private Move createMove(Random workingRandom) {
//        Visit visit = visitList.get(workingRandom.nextInt(visitList.size()));
//        String variableName = workingRandom.nextBoolean() ? "previousOdd" : "previousEven";
//        Appearance previousAppearance;
//        previousAppearance = scoreCalculator.findNear(visit, workingRandom.nextInt(NEAR_COUNT_MAX) + 1);
////        int random = workingRandom.nextInt(NEAR_COUNT_MAX + 1);
////        if (random == NEAR_COUNT_MAX) {
////            previousAppearance = domicile;
////        } else {
////            previousAppearance = scoreCalculator.findNear(visit, random + 1);
////        }
//        return new ChainedChangeMove(visit, entityDescriptor.getPlanningVariableDescriptor(variableName), previousAppearance);
//    }

}
