package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.IntStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPDownloader {

	public boolean getFTPData() {
		return ftpDownload();
	}

	private boolean ftpDownload() {
		FTPClient ftpClient = new FTPClient();

		try {
			int reply;
			String server = "ftp.ncdc.noaa.gov";
			ftpClient.connect(server);
			System.out.println("Connected to " + server + ".");
			System.out.print(ftpClient.getReplyString());

			reply = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				System.err.println("FTP server refused connection.");
				return false;
			}
			
			ftpClient.enterLocalPassiveMode();
	        ftpClient.login("anonymous", "");
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        
	        String dfwAirport = "722590-03927";
	        int[] yearRange = IntStream.rangeClosed(1973, 2018).toArray();
	        
	        String[] fileNames = new String[yearRange.length];
	        for(int i=0; i<fileNames.length; i++) {
	        	fileNames[i] = yearRange[i]+"/"+dfwAirport+"-"+yearRange[i]+".gz";
	        }
	        String workingDir = System.getProperty("user.dir");
	        
	        for(String filename : fileNames) {
	        	String fileLocation = workingDir+"/gzips/";
	        	File localFile = new File(fileLocation+filename);
	        	
	        	localFile.getParentFile().mkdirs();
	        	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
	        	boolean copied = ftpClient.retrieveFile("/pub/data/noaa/isd-lite/"+filename, outputStream);
	        	
	        	if(copied) {
	        		System.out.println(filename+" copied");
	        	}
	        	else {
	        		System.out.println(filename+" not copied");
	        	}
	        	outputStream.close();
	        }

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException ioe) {
					System.out.println("Could not disconnect!");
				}
			}
		}
		
		return true;
	}
}
