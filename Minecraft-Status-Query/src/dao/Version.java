package dao;

public class Version {
    private String name;
    private String protocol;

    public String getName() {
        return name;
    }

    public String getProtocol() {
        return protocol;
    }

	@Override
	public String toString() {
		return "Version [name=" + name + ", protocol=" + protocol + "]";
	}
       
}