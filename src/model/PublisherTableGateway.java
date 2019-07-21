package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * PublisherTableGateway
 * Gateway used to interface between the MySQL DB and the Application for Publisher related queries and insertions
 * @author Manuel Gomez IV
 */
public class PublisherTableGateway {

	public Connection conn;
	private static Logger logger = LogManager.getLogger(BookTableGateway.class);

	/**
	 * Constructor
	 * Gets connection from SingletonSwitcher and sets conn
	 */
	public PublisherTableGateway() {
		conn = SingletonSwitcher.getInstance().establishDBConnection();
	}

	/**
	 * fetchPublishers
	 * @return - ArrayList of existing publishers in the DB
	 */
	public ArrayList<Publisher> fetchPublishers() {
		ArrayList<Publisher> publishers = new ArrayList<Publisher>();
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("SELECT * from Publisher");
			rs = stmt.executeQuery();
			while (rs.next()) {
				publishers.add(new Publisher(rs.getInt(1), rs.getString(2)));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return publishers;
	}

	/**
	 * getPublisherName
	 * @param id - A Publisher's ID
	 * @return - The name of the Publisher associated with that ID
	 */
	public String getPublisherName(int id) {
		String publisherName = "Unknown";
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("SELECT name from Publisher where id=?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			publisherName = rs.getString(1);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return publisherName;
	}
}
