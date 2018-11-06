package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.IntStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPConnection {

	public FTPConnection() {
		this.ftpConnect();
	}

	private boolean ftpConnect() {
		FTPClient ftpClient = new FTPClient();

		boolean error = false;
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
//	        String fileLocation = workingDir+"/gzips";
//	        File testFile = new File(fileLocation);
	        System.out.println(workingDir);
//	        System.out.println(testFile.exists());
	        
	        for(String filename : fileNames) {
	        	System.out.println(filename);
	        	String fileLocation = workingDir+"/gzips/";
	        	File localFile = new File(fileLocation+filename);
	        	System.out.println(localFile.getAbsolutePath());
	        	localFile.getParentFile().mkdirs();
	        	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
	        	boolean copied = ftpClient.retrieveFile("/pub/data/noaa/isd-lite/"+filename, outputStream);
	        	System.out.println(ftpClient.getReplyString());
	        	
	        	if(copied) {
	        		System.out.println(filename+" copied");
	        	}
	        	else {
	        		System.out.println(filename+" not copied");
	        	}
	        	outputStream.close();
	        }

		} catch (IOException e) {
			error = true;
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
			System.exit(error ? 1 : 0);
		}
		
		return true;
	}
}
