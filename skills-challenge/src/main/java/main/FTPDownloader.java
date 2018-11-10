/**
 * This class downloads all the data files for the DFW Airport from the FTP Server.
 * Utilizes the Apache Commons Net library to connect through FTP.
 * 
 * @author: Mark Eby
 */

package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPDownloader {

	/**
	 * Wrapper method for ftpDownload to maintain ftpDownload private.
	 * 
	 * @return If the downloading was successful.
	 */
	public boolean getFTPData() {
		return ftpDownload();
	}
	
	/**
	 * Connects to the FTP Server and downloads the weather data 
	 * for the DFW Airport between the years of 1973 and 2018.
	 * 
	 * @return If the downloading was successful. 
	 */
	private boolean ftpDownload() {
		FTPClient ftpClient = new FTPClient();

		try {
			int reply;
			String server = "ftp.ncdc.noaa.gov";
			ftpClient.connect(server);
			System.out.println("Connected to " + server + ".");
			System.out.print(ftpClient.getReplyString());

			reply = ftpClient.getReplyCode();
			
			// The server refused the connection.
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				System.err.println("FTP server refused connection.");
				return false;
			}
			
			ftpClient.enterLocalPassiveMode();
	        ftpClient.login("anonymous", "");
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        
	        // The code used for the DFW Airport data.
	        String dfwAirport = "722590-03927";
	        // These years were chosen based on the "History" documentation provided
	        // Data for the DFW Airport only exists between 1973 and 2018
	        int[] yearRange = IntStream.rangeClosed(1973, 2018).toArray();
	        
	        // Generates an array of the expected filenames for the .gz files
	        String[] fileNames = new String[yearRange.length];
	        for(int i=0; i<fileNames.length; i++) {
	        	fileNames[i] = yearRange[i] + "/" + dfwAirport + "-" + yearRange[i] + ".gz";
	        }
	        
	        String workingDir = System.getProperty("user.dir");
	        
	        // Downloads each .gz file found
	        for(String filename : fileNames) {
	        	String fileLocation = workingDir + "/gzips/";
	        	File localFile = new File(fileLocation + filename);
	        	
	        	localFile.getParentFile().mkdirs();
	        	BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
	        	boolean copied = ftpClient.retrieveFile("/pub/data/noaa/isd-lite/" + filename, outputStream);
	        	
	        	outputStream.close();
	        	if(!copied) {
	        		return false;
	        	}
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
