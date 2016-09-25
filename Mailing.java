

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;


public class Mailing
{
    
	ArrayList<String> mailAddresses= new ArrayList<String>();
	
	public Mailing(){
		
	}
	
	public ArrayList<String> getMailAddresses() {
		return mailAddresses;
	}


	public void setMailAddresses(ArrayList<String> mailAddresses) {
		this.mailAddresses = mailAddresses;
	}


	public static ArrayList<String> getMailAddrsesFromFile(File file){
		ArrayList<String> mailAddresses= new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(file.getPath())))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				mailAddresses.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
			//logger.severe(e.toString());
		}
		return mailAddresses;
	}
	
	public void sendNotification(ArrayList<String> mailAddresses,String Subject, String text) throws EmailException{
		Email email = new SimpleEmail();
	    email.setSmtpPort(587);
	    email.setAuthenticator(new DefaultAuthenticator("yourmail@domain.com","mail_possword"));
	    email.setStartTLSEnabled(true);
	    email.setDebug(false);
	    email.setHostName("smtp_server_address");
	    email.setFrom("yourmail@domain.com");
	    email.setSubject(Subject);
	    email.setMsg(text);
	    for (String adrs : mailAddresses)email.addTo(adrs);
	    //email.addTo("andrius.tamkus@gmail.com");
	    email.send();
	}
	
	public void sendFiles(ArrayList<String> mailAddresses,ArrayList<File> files, String Subject, String text) throws EmailException{
		
		EmailAttachment attachment = new EmailAttachment();
		
		
		MultiPartEmail email = new MultiPartEmail();
	    email.setSmtpPort(587);
	    email.setAuthenticator(new DefaultAuthenticator("yourmail@domain.com","mail_possword"));
	    email.setStartTLSEnabled(true);
	    email.setDebug(false);
	    email.setHostName("smtp_server_address");
	    email.setFrom("yourmail@domain.com");
	    email.setSubject(Subject);
	    email.setMsg(text);
	    for (String adrs : mailAddresses)email.addTo(adrs);
	    
	    for (File file : files){
	    	attachment.setPath(file.getPath());
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription(file.getName());
			attachment.setName(file.getName());
	    	email.attach(attachment);	
	    }
	    
	    
	    email.send();
	}
}