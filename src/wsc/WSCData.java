package wsc;

import ec.gp.*;

public class WSCData extends GPData {

	private static final long serialVersionUID = 1L;
	public double[] qos;

	public void copyTo(final GPData gpd) {
		WSCData wscd = (WSCData) gpd;
		wscd.qos = qos;
	}
}
