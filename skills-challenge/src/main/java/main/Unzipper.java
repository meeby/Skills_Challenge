package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class Unzipper {
	private String zipPath = "gzips";
	private String decompressedPath = "files";
	private String currentPath;
	private FilenameFilter gzipFilter;
	
	public Unzipper() {
		this.currentPath = System.getProperty("user.dir");
		gzipFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				int lastPeriod = name.lastIndexOf(".");
				
				if(lastPeriod > 0) {
					String fileExtension = name.substring(lastPeriod);
					
					if(fileExtension.equals(".gz")) {
						return true;
					}
				}
				return false;
			}
			
		};
	}
	
	public boolean unzip() {
		File mainFolder = new File(this.currentPath + "/" + this.zipPath);
		File[] folderList = mainFolder.listFiles();
		
		String outputPath = this.currentPath + "/" + this.decompressedPath;
		
		if(folderList != null) {
			for(File folder : folderList) {
				if(folder.isDirectory()) {
					File[] gzipsList = folder.listFiles(this.gzipFilter);
					
					String year = folder.getName();
					
					System.out.print("Analyzing " + year);
					
					for(File gzip : gzipsList) {
						try {
							GZIPInputStream gzipInput = new GZIPInputStream(new FileInputStream(gzip));
							InputStreamReader decoder = new InputStreamReader(gzipInput, "UTF-8");
							BufferedReader buffered = new BufferedReader(decoder);
							
							File outputFile = new File(outputPath + "/" + year + ".txt");
							outputFile.getParentFile().mkdirs();
							FileOutputStream outputFileStream = new FileOutputStream(outputFile);
							
							String line = buffered.readLine();
							while(line != null) {
								outputFileStream.write(line.getBytes());
								outputFileStream.write('\n');
								line = buffered.readLine();
							}

							outputFileStream.close();
							buffered.close();
							gzipInput.close();
							
							System.out.print(" done\n");
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						}
						
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean isZipped(byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}
	
	public String getZipPath() {
		return this.zipPath;
	}
	public void setZipPath(String path) {
		this.zipPath = path;
	}
}
