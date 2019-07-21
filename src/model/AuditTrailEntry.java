package model;

import java.time.LocalDateTime;

/**
 * AuditTrailEntry
 * An object representing entries into a books audit trail
 * @author Manuel Gomez IV
 */
public class AuditTrailEntry {

	private int id;
	private LocalDateTime dateAdded;
	private String message;

	/**
	 * constructor
	 * @param id - The id of the book that this entry is associated with
	 * @param dateAdded - The date that this entry was added to the trail
	 * @param message - The message/text of the entry
	 */
	public AuditTrailEntry(int id, LocalDateTime dateAdded, String message) {
		this.id = id;
		this.dateAdded = dateAdded;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(LocalDateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return dateAdded.toLocalDate() + " " + dateAdded.toLocalTime() + " : " + message;
	}

}
