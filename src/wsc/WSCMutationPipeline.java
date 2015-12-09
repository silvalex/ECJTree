package wsc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class WSCMutationPipeline extends BreedingPipeline {

	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		return new Parameter("wscmutationpipeline");
	}

	@Override
	public int numSources() {
		return 1;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {
		WSCInitializer init = (WSCInitializer) state.initializer;

		int n = sources[0].produce(min, max, start, subpopulation, inds, state, thread);

        if (!(sources[0] instanceof BreedingPipeline)) {
            for(int q=start;q<n+start;q++)
                inds[q] = (Individual)(inds[q].clone());
        }

        if (!(inds[start] instanceof WSCIndividual))
            // uh oh, wrong kind of individual
            state.output.fatal("WSCMutationPipeline didn't get a WSCIndividual. The offending individual is in subpopulation "
            + subpopulation + " and it's:" + inds[start]);

        // Perform mutation
        for(int q=start;q<n+start;q++) {
            WSCIndividual tree = (WSCIndividual)inds[q];
            WSCSpecies species = (WSCSpecies) tree.species;
            
            // Randomly select a node in the tree to be mutation
            List<GPNode> allNodes = new ArrayList<GPNode>();
            Queue<GPNode> queue = new LinkedList<GPNode>();
            
            queue.offer(tree.trees[0].child);
            
            while(!queue.isEmpty()) {
                GPNode current = queue.poll();
                allNodes.add(current);
                if (current.children != null) {
                    for (GPNode child : current.children)
                        allNodes.add( child );
                }
            }
            
            int selectedIndex = init.random.nextInt(allNodes.size());
            GPNode selectedNode = allNodes.get( selectedIndex );
            InOutNode ioNode = (InOutNode) selectedNode;
            
            // Generate a new tree based on the input/output information of the current node
            GPNode newNode = species.createNewTree( state, ioNode.getInputs(), ioNode.getOutputs() );
            
            // Replace the old tree with the new one
            GPNode parentNode = (GPNode) selectedNode.parent;
            if (parentNode == null) {
                tree.trees[0].child = newNode;
            }
            else {
                newNode.parent = selectedNode.parent;
                for (int i = 0; i < parentNode.children.length; i++) {
                    if (parentNode.children[i] == selectedNode) {
                        parentNode.children[i] = newNode;
                        break;
                    }
                }
            }
            
            tree.evaluated=false;
        }
        return n;
	}

}
