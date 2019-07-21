package ExcelGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.*;
import javafx.collections.ObservableList;
import model.AuthorBook;
import model.Book;
import model.Publisher;
import model.SingletonSwitcher;

/**
 * ExcelSheetGenerator The program used to generate .csv files containing publisher data
 * Extends thread so that it can be run in a seperate thread to avoid freezing/crashing the
 * 		application during generation of large publisher reports
 * @author Manuel Gomez IV
 */
public class ExcelSheetGenerator extends Thread {

	private static Logger logger = LogManager.getLogger(ExcelSheetGenerator.class);
	private File saveDir;
	private Publisher publisher;

	/**
	 * Constructor Sets value of directory to save to, and the publisher to generate
	 * a report for
	 * 
	 * @param saveDir - The dir to save to
	 * @param publisher - The publisher to generate a report for
	 */
	public ExcelSheetGenerator(File saveDir, Publisher publisher) {
		this.saveDir = saveDir;
		this.publisher = publisher;
	}

	/**
	 * run
	 * Writes all data to a StringBuilder, then to a PrintWriter to generate the file
	 */
	public void run() {
		try {
			PrintWriter pw = new PrintWriter(saveDir);
			StringBuilder sb = new StringBuilder();
			sb.append("Royalty Report\nPublisher: " + publisher.getName());
			sb.append("\nReport generated on " + LocalDateTime.now().toString().replace("T", "-"));
			sb.append("\n\nBook Title,ISBN,Author,Royalty\n");
			ArrayList<Book> bookList = new ArrayList<Book>();
			ObservableList<AuthorBook> abArr;
			double royaltyTotal;
			int i = 0;
			boolean keepRunning = true;
			while (keepRunning) {
				bookList.clear();
				bookList.addAll(
						SingletonSwitcher.getInstance().getBTGate().getBooksPublishedByWithAuthors(publisher, i));
				if (bookList.size() == 0) {
					keepRunning = false;
					break;
				}
				logger.info("Generating sheet cell block " + (i + 1));
				for (Book book : bookList) {
					sb.append(book.getTitle() + "," + book.getIsbn().toString() + ",");
					abArr = SingletonSwitcher.getInstance().getBTGate().getAuthorsForBook(book);
					royaltyTotal = 0;
					if (abArr.size() > 0) {
						for (AuthorBook ab : abArr) {
							sb.append(ab.getAuthor().toString() + "," + (float) (ab.getRoyalty() / 1000) + "%\n,,");
							royaltyTotal += ab.getRoyalty();
						}
						sb.append("Total Royalty," + (float) (royaltyTotal / 1000) + "%\n\n");
					}
				}
				i++;
			}

			pw.write(sb.toString());
			pw.close();
			logger.info("Spreadsheet generated");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
