package main;

public class Main {

	public static void main(String[] args) {
//		FTPDownloader dataDownloader = new FTPDownloader();
//		boolean downloaded = dataDownloader.getFTPData();
		boolean downloaded = true;
		
		if(downloaded) {
			Unzipper unzipper = new Unzipper();
			unzipper.unzip();
		}
	}

}
