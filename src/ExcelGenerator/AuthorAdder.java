package ExcelGenerator;

import java.util.Random;
import model.AuditTableGateway;
import model.Author;
import model.Book;
import model.BookTableGateway;
import model.PublisherTableGateway;

/**
 * AuthorAdder
 * This is an independent class that adds random authors to some book entries for testing
 * @author Manuel Gomez IV
 */
public class AuthorAdder {
	
	/**
	 * main
	 * contains the logic to randomly add authors to book and insert records into the DB
	 */
	public static void main(String[] args) {
		Random r = new Random();
		BookTableGateway btg = new BookTableGateway();
		PublisherTableGateway ptg = new PublisherTableGateway();
		AuditTableGateway atg = new AuditTableGateway();
		btg.setPTGate(ptg);
		btg.setATGate(atg);
		Book book;
		for (int i = 1; i < 500; i++) {
			int authorsToAdd = r.nextInt(3) + 1;
			int pSwitch = r.nextInt(10 - authorsToAdd);
			int authorID;
			Author a;
			for (int j = 0; j < authorsToAdd; j++) {
				book = new Book();
				book.setId(i);
				a = new Author();
				switch (pSwitch) {
				case 0:
					authorID = 1;
					break;
				case 1:
					authorID = 2;
					break;
				case 2:
					authorID = 3;
					break;
				case 3:
					authorID = 4;
					break;
				case 4:
					authorID = 5;
					break;
				case 5:
					authorID = 6;
					break;
				case 6:
					authorID = 7;
					break;
				case 7:
					authorID = 8;
					break;
				case 8:
					authorID = 10;
					break;
				case 9:
					authorID = 12;
					break;
				default:
					authorID = 1;
					break;
				}
				a.setId(authorID);
				btg.addAuthorToBook(a, book, .02f);
				pSwitch++;
			}
		}
	}

}
