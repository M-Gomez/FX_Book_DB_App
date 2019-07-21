package ExcelGenerator;

import java.util.ArrayList;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.AuditTableGateway;
import model.Book;
import model.BookTableGateway;
import model.Publisher;
import model.PublisherTableGateway;

/**
 * BookGenerator
 * Generates random Book records to populate the DB
 * @author Manuel Gomez IV
 */
public class BookGenerator {

	private static Logger logger = LogManager.getLogger(BookGenerator.class);

	/**
	 * BookGenerator
	 * Generates random Book entries and enters them into the DB for testing
	 * Randomly generates titles based on 4 different string arrays, to make titles somewhat logical
	 */
	public static void main(String[] args) {
		String[] sA1 = {"old", "cold", "new", "cool", "scary", "scared", "worrisome", "very big", "small", "powerful", "imaginative", "hungry", "sleepy", "tired", "aggresive", "handsome", "ambitious", "brave", "calm", "eager", "clean", "chubby", "colossal", "gigantic", "little", "gentle", "religious", "grumpy", "embarrassed", "bewildered"};
		String[] sA2 = {"catarpillar", "basketball player", "police man", "doctor", "waiter", "ducky", "puppy", "psychic", "kitty cat", "badger", "otter", "basilisk", "platypus", "martian", "italian man", "chef", "veterinarian", "student", "qa tester", "sysadmin", "project manager", "soccer player", "vegitarian", "eel", "fish", "baby", "water chestnut", "apple tree", "president", "mechanic", "salesman", "twitch streamer", "quarterback", "halfback", "coach", "jealous"};
		String[] sA3 = {"favorite", "epic", "awesome", "amazing", "gorgeous", "worst", "unique", "weird", "funny", "magic", "itchy", "silly", "wonderful", "magnificent", "nice", "jolly", "unsightly", "miniature", "microscopic", "massive", "best", "loltyler1.com discount code alpha"};
		String[] sA4 = {"bodyguard", "plates", "game", "stuffed animal", "stopwatch", "pillow", "victory royale", "xbox controller", "wallet", "chair", "racecar bed", "rocket", "water bed", "closet", "water bottle", "jetski", "headset", "printer", "hard drive", "cologne", "deoderant", "robe", "diamond pickaxe", "iron sword", "stone shovel", "usb flash drive"};
		ArrayList<Book> bookArr = new ArrayList<Book>();
		Random r = new Random();
		BookTableGateway btg = new BookTableGateway();
		PublisherTableGateway ptg = new PublisherTableGateway();
		AuditTableGateway atg = new AuditTableGateway();
		btg.setPTGate(ptg);
		btg.setATGate(atg);
		String isbn;
		int isbnInt = 501242;
		for(int i = 0; i < 100;i++) {
			bookArr.clear();
		for(int j = 0; j < 1100;j++) {
			String name = "The " + sA1[r.nextInt(sA1.length)] + " " + sA2[r.nextInt(sA2.length)] + " and his " + sA3[r.nextInt(sA3.length)] + " " + sA4[r.nextInt(sA4.length)];
			int yearPublished = r.nextInt(1900) + 117;
			String summary = "A book that exists";
			int pSwitch = r.nextInt(7);
			int publisherID;
			switch(pSwitch) {
			case 0:
				publisherID = 123;
				break;
			case 1:
				publisherID = 124;
				break;
			case 2:
				publisherID = 125;
				break;
			case 3:
				publisherID = 126;
				break;
			case 4:
				publisherID = 128;
				break;
			case 5:
				publisherID = 129;
				break;
			case 6:
				publisherID = 127;
				break;
				default:
				publisherID = 123;
				break;
			}
			isbn = String.valueOf(isbnInt);
			isbnInt++;
			bookArr.add(new Book(0, yearPublished, name, summary, isbn, null, null, new Publisher(publisherID, null)));
			logger.info("generated " + name);
		}
		btg.insertBookArray(bookArr);
		}
		
	}

}
