import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.mail.EmailException;

public class LogWatchDog{
	
	
	public static void main(String[] args) {
		System.out.println("Program has started");
		
		Executor executor = Executors.newFixedThreadPool(3);
		
		int threadID =0;
		String path;
		String mailSubject= null;
		String mailText = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader("MailText.txt")))
		{
				mailSubject=br.readLine();
				mailText=br.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
			//logger.severe(e.toString());
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader("Paths.txt")))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				path=sCurrentLine;
				executor.execute( new OneWatching( path, threadID, mailSubject, mailText ));
				threadID++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			//logger.severe(e.toString());
		}
		
		
		/*
		int threadID =0;
		String path="C://Test";
		executor.execute( new OneWatching( path, threadID ));
		threadID++;
		String path2="C://Test2";
		executor.execute( new OneWatching( path2, threadID ));
		threadID++;
		*/
		
		//new Thread(new LogWatchDog()).start();
	}
}

class OneWatching implements Runnable {
  
  String path;
  int threadID;
  String mailSubject;
  String mailText;
  
  OneWatching ( String path, int threadID, String mailSubject, String mailText ) {
    this.path = path;
    this.threadID=threadID;
    this.mailSubject=mailSubject;
    this.mailText=mailText+"\n\n File path:"+path;
  }
  
	public void run() {
        // ------------- log log log log ------------------------
		Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  
	    try {  
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("LogFile"+threadID+".log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("Thread executed for the path: " +path);  
	    } catch (SecurityException e) {  
	        e.printStackTrace();
	        logger.severe(e.toString());
	    } catch (IOException e) {  
	        e.printStackTrace();  
	        logger.severe(e.toString());
	    } 
	    // --------------- the end of the log ----------------
	    
	    
	    File mailAddrsestxt = new File("Mail-Addresses.txt");
		Mailing mail = new Mailing();
	    mail.setMailAddresses(Mailing.getMailAddrsesFromFile(mailAddrsestxt));
	   
	    
	    String root=this.path;
		String filter="alarm";
		File file=new File(root);
		ArrayList<File> filteredFiles= new ArrayList<File>();
		ArrayList<File> newFiles= new ArrayList<File>();
		File latestFileModif;
		String logText;
		
		////for first execution in a PC only!!!
		filteredFiles=FileManip.searchForSpecificFiles(file, filter);
		Date prevLatestModifDate=new Date(1); 
	    
		
		//read prevLatestModifdate from the file 
		try (BufferedReader br = new BufferedReader(new FileReader("Data"+this.threadID+".dat")))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				prevLatestModifDate = new Date(Long.parseLong(sCurrentLine));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe(e.toString());
		}
		
		while(true){
			
			filteredFiles=FileManip.searchForSpecificFiles(file, filter);
        	
        	if(!filteredFiles.isEmpty()){
        		latestFileModif = FileManip.latestFileModifDate(filteredFiles);
        		Date latestModifDate= new Date(latestFileModif.lastModified());
        		if(prevLatestModifDate.before(latestModifDate)){
        			newFiles = FileManip.FileModifLaterThan(prevLatestModifDate, filteredFiles);
        			
        			logText="New file(s) found:";
    				for(File file1 : newFiles)logText+=" "+file1.getName();
    				logger.info(logText);
    			    
    				prevLatestModifDate=latestModifDate;
        			// safe long latestFileModif to the file
        			PrintWriter writer;
        			try {
        				writer = new PrintWriter("Data"+this.threadID+".dat", "UTF-8");
        				writer.println(Long.toString(latestFileModif.lastModified()));
        				writer.close();
        			} catch (FileNotFoundException | UnsupportedEncodingException ex) {
        				// TODO Auto-generated catch block
        				ex.printStackTrace();
        				logger.severe(ex.toString());
        			}
    			
        			
    			
        			try {
        				mail.sendFiles(mail.getMailAddresses(), newFiles, this.mailSubject, this.mailText);
        				logText="Mail sent with the files:";
        				for(File file1 : newFiles)logText+=" "+file1.getName();
        				logger.info(logText);
        			} catch (EmailException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        				logger.severe(e.toString());
        			}
        		}
    		
        		try {
        			Thread.sleep(300000); // 5min == 300000ms
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			logger.severe(e.toString());
        		}
        	}
		}
    }
}
