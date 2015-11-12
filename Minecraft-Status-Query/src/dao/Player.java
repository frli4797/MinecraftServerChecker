package dao;

public class Player {
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

	@Override
	public String toString() {
		return "Player [name=" + name + ", id=" + id + "]";
	}

}
