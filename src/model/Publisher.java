package model;

/**
 * Publisher
 * Class representing Publishers
 * @author Manuel Gomez IV
 */
public class Publisher {
	
	private String name;
	private int id;
	
	/**
	 * Constructor
	 * Creates a new Publisher with starter values
	 */
	public Publisher() {
		id = 0;
		name = "Unknown";
	}

	public Publisher(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
