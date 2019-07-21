package model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * JSONReader
 * Reads in /config/config.json to provide any necessary config info
 * Objects are stored as private static fields with accessors for easy access 
 * @author Manuel Gomez IV
 */
public class JSONReader {

	private static JSONObject jsonObj;
	private static Logger logger = LogManager.getLogger(BookTableGateway.class);
	private static String db_address, username, password;

	/**
	 * start
	 * Parses the config.json file and stores data statically
	 */
	public static void start() {
		JSONParser parser = new JSONParser();
		try {
			jsonObj = (JSONObject) parser
					.parse(new FileReader("c:/Users/Manny_G/eclipse-workspace/finalproject/config/config.json"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		db_address = (String) jsonObj.get("DB_Address");
		username = (String) jsonObj.get("Username");
		password = (String) jsonObj.get("Password");
	}

	public static String getDb_address() {
		return db_address;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

}
