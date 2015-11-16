package dao;

import java.util.List;

public class Players {

	private int max;
	private int online;
	private List<Player> sample;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public List<Player> getSample() {
		return sample;
	}

	public void setSample(List<Player> sample) {
		this.sample = sample;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Players [max=").append(max).append(", online=")
				.append(online).append("]");
		return builder.toString();
	}

}
