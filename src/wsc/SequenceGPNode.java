package wsc;

import java.util.Arrays;
import java.util.Set;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class SequenceGPNode extends GPNode implements InOutNode {

	private static final long serialVersionUID = 1L;
	private Set<String> inputs;
	private Set<String> outputs;

	@Override
	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
		double[] qos;

		WSCData rd = ((WSCData) (input));

		children[0].eval(state, thread, input, stack, individual, problem);
		qos = Arrays.copyOf(rd.qos, rd.qos.length);
		Set<String> inputs = rd.inputs;

		children[1].eval(state, thread, input, stack, individual, problem);
		rd.qos[WSCInitializer.TIME] += qos[WSCInitializer.TIME];
		rd.qos[WSCInitializer.COST] += qos[WSCInitializer.COST];
		rd.qos[WSCInitializer.AVAILABILITY] *= qos[WSCInitializer.AVAILABILITY];
		rd.qos[WSCInitializer.RELIABILITY] *= qos[WSCInitializer.RELIABILITY];
		// The inputs should be those of the left child, but the outputs and max layer are just those of the right child (already retrieved)
		rd.inputs = inputs;
		
	    // Store input and output information in this node
        inputs = rd.inputs;
        outputs = rd.outputs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sequence(");
		for (int i = 0; i < children.length; i++) {
			GPNode child = children[i];
			if (child != null)
				builder.append(children[i].toString());
			else
				builder.append("null");
			if (i != children.length - 1){
				builder.append(",");
			}
		}
		builder.append(")");
		return builder.toString();
	}

	@Override
	public int expectedChildren() {
		return 2;
	}

	@Override
	public SequenceGPNode clone() {
		GPNode[] newChildren = new GPNode[children.length];
		for (int i = 0; i < children.length; i++) {
			newChildren[i] = (GPNode) children[i].clone();
		}
		SequenceGPNode newNode = new SequenceGPNode();
		newNode.children = newChildren;
		return newNode;
	}

    public Set< String > getInputs() {
        return inputs;
    }

    public Set< String > getOutputs() {
        return outputs;
    }
}
