package wsc;

import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.simple.*;

public class WSC extends GPProblem implements SimpleProblemForm {

	private static final long serialVersionUID = 1L;

	public void setup(final EvolutionState state, final Parameter base) {
		// very important, remember this
		super.setup(state, base);

		// verify our input is the right class (or subclasses from it)
		if (!(input instanceof WSCData))
			state.output.fatal("GPData class must subclass from "
					+ WSCData.class, base.push(P_DATA), null);
	}

	public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum) {
		if (!ind.evaluated) {
			WSCInitializer init = (WSCInitializer) state.initializer;
			WSCData input = (WSCData) (this.input);

			GPIndividual gpInd = (GPIndividual) ind;
			gpInd.trees[0].child.eval(state, threadnum, input, stack, ((GPIndividual) ind), this);
			double[] qos = input.qos;


			double fitness = calculateFitness(qos[WSCInitializer.AVAILABILITY], qos[WSCInitializer.RELIABILITY], qos[WSCInitializer.TIME], qos[WSCInitializer.COST], init);

			// the fitness better be SimpleFitness!
			SimpleFitness f = ((SimpleFitness) ind.fitness);
			f.setFitness(state, fitness, false);
			//f.setStandardizedFitness(state, fitness);
			ind.evaluated = true;
		}
	}

	private double calculateFitness(double a, double r, double t, double c, WSCInitializer init) {
		a = normaliseAvailability(a, init);
		r = normaliseReliability(r, init);
		t = normaliseTime(t, init);
		c = normaliseCost(c, init);

		double fitness = ((init.w1 * a) + (init.w2 * r) + (init.w3 * t) + (init.w4 * c));
		return fitness;
	}

	private double normaliseAvailability(double availability, WSCInitializer init) {
		if (init.maxAvailability - init.minAvailability == 0.0)
			return 1.0;
		else
			return (availability - init.minAvailability)/(init.maxAvailability - init.minAvailability);
	}

	private double normaliseReliability(double reliability, WSCInitializer init) {
		if (init.maxReliability - init.minReliability == 0.0)
			return 1.0;
		else
			return (reliability - init.minReliability)/(init.maxReliability - init.minReliability);
	}

	private double normaliseTime(double time, WSCInitializer init) {
		// If the time happens to go beyond the normalisation bound, set it to the normalisation bound
		if (time > init.maxTime)
			time = init.maxTime;

		if (init.maxTime - init.minTime == 0.0)
			return 1.0;
		else
			return (init.maxTime - time)/(init.maxTime - init.minTime);
	}

	private double normaliseCost(double cost, WSCInitializer init) {
		// If the cost happens to go beyond the normalisation bound, set it to the normalisation bound
		if (cost > init.maxCost)
			cost = init.maxCost;

		if (init.maxCost - init.minCost == 0.0)
			return 1.0;
		else
			return (init.maxCost - cost)/(init.maxCost - init.minCost);
	}
}