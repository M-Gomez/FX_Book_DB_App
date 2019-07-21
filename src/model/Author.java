package model;

import java.time.LocalDate;

/**
 * Author
 * Class representing Authors
 * @author Manuel Gomez IV
 */
public class Author {
	
	private int id;
	private String fName, lName, gender, webSite;
	private LocalDate dateOfBirth;

	/**
	 * Constructor
	 * Paramless constructor used to ceate a blank author OBJ
	 */
	public Author() {
		fName = "";
		lName = "";
		gender = "";
		webSite = "";
		dateOfBirth = LocalDate.MIN;
	}

	/**
	 * Constructor
	 * Used to create an author OBJ with passed args
	 * @param id - The Author's ID
	 * @param fName - The Author's first name
	 * @param lName - The Author's last name
	 * @param dob - The Author's date of birth
	 * @param gender - The Author's gender
	 * @param webSite - The Author's website URL
	 */
	public Author(int id, String fName, String lName, LocalDate dob, String gender, String webSite) {
		this.id = id;
		this.fName = fName;
		this.lName = lName;
		this.dateOfBirth = dob;
		this.gender = gender;
		if (webSite == null) {
			this.webSite = "";
		} else {
			this.webSite = webSite;
		}
	}

	/**
	 * Constructor
	 * Used to create an author OBJ by cloning values from an existing Author OBJ
	 * @param author
	 */
	public Author(Author author) {
		this.fName = author.fName;
		this.lName = author.lName;
		this.gender = author.gender;
		this.webSite = author.webSite;
		this.dateOfBirth = author.dateOfBirth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public String toString() {
		return fName + " " + lName;

	}
}
