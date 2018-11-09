package main;

import java.io.File;
import java.util.zip.GZIPInputStream;

public class Unzipper {
	private String zipPath = "gzips";
	private String decompressedPath = "files";
	private String currentPath;
	
	public Unzipper() {
		this.currentPath = System.getProperty("user.dir");
	}
	
	public boolean unzip() {
		File mainFolder = new File(this.currentPath + this.zipPath);
		File[] folderList = mainFolder.listFiles();
		
		if(folderList != null) {
			for(File folder : folderList) {
				File[] gzipsList = folder.listFiles();
				
				for(File gzip : gzipsList) {
					System.out.println(gzip.getName());
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
