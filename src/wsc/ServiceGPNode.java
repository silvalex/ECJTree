package wsc;

import java.util.Arrays;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class ServiceGPNode extends GPNode {

	private static final long serialVersionUID = 1L;
	private Service service;

	public ServiceGPNode() {
		children = new GPNode[0];
	}

	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack, final GPIndividual individual, final Problem problem) {
		WSCData rd = ((WSCData) (input));
		rd.qos = Arrays.copyOf(service.qos, service.qos.length);
	}

	public void setService(Service s) {
		this.service = s;
	}

	@Override
	public String toString() {
		if (service == null)
			return "null";
		else
			return service.name;
	}

	@Override
	public int expectedChildren() {
		return 0;
	}

	@Override
	public int hashCode() {
		return service.name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ServiceGPNode) {
			ServiceGPNode o = (ServiceGPNode) other;
			return service.name.equals(o.service.name);
		}
		else
			return false;
	}

	@Override
	public ServiceGPNode clone() {
		ServiceGPNode newNode = new ServiceGPNode();
		newNode.setService(service);
		return newNode;
	}
}
