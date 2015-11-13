package dao;

public class StatusResponse {

	private boolean online = false;
	private String description;
	private Player players;
	private Version version;
	private String favicon;
	private int time;
	private int size;

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getDescription() {
		return description;
	}

	public Player getPlayers() {
		return players;
	}

	public Version getVersion() {
		return version;
	}

	public String getFavicon() {
		return favicon;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "StatusResponse [description=" + description + ", players="
				+ players + ", version=" + version + ", favicon=" + favicon
				+ ", time=" + time + "]";
	}

}