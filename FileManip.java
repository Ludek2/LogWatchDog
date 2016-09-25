import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FileManip {
	
	public FileManip(){};
	
	public static ArrayList<File> searchForSpecificFiles(File root, String filter)
	{
		ArrayList<File> filteredFiles= new ArrayList<File>();
		if(root != null){  //just for safety   

			if(root.isDirectory())
			{
				for(File file : root.listFiles()){
	            
					if(file.isFile() && file.getName().contains(filter))
					{
						filteredFiles.add(file);
					}
				}
			}
		}
		return filteredFiles;
	}
	
	
	public static File latestFileModifDate(ArrayList<File> files){
		
				File latestFileModif=files.get(0);    //potencial bug if ArrayList is empty
			
			
				for (File file : files) {
					if(file.lastModified()>latestFileModif.lastModified())latestFileModif=file;
				}
			 
				return latestFileModif;
	}
	
	public static ArrayList<File> FileModifLaterThan(Date dateToCompare, ArrayList<File> files){
		ArrayList<File> newFiles= new ArrayList<File>();
		for(File file: files){	
			if(dateToCompare.compareTo(new Date(file.lastModified())) < 0){
				newFiles.add(file);
			}
		}
		return newFiles;
	}
}
