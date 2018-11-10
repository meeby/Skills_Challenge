package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class CSVConverter {
	private String zipPath = "gzips";
	private String decompressedPath = "files";
	private String currentPath;
	private FilenameFilter gzipFilter;
	
	public CSVConverter() {
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
	
	public boolean convertToCSV() {
		File mainFolder = new File(this.currentPath + "/" + this.zipPath);
		File[] folderList = mainFolder.listFiles();
		Arrays.sort(folderList);
		
		String outputPath = this.currentPath + "/" + this.decompressedPath;
		
		if(folderList != null) {
			for(File folder : folderList) {
				if(folder.isDirectory()) {
					File[] gzipsList = folder.listFiles(this.gzipFilter);
					
					String year = folder.getName();
					
//					System.out.print("Unzipping " + year + "...");
					
					for(File gzip : gzipsList) {
						try {
							GZIPInputStream gzipInput = new GZIPInputStream(new FileInputStream(gzip));
							InputStreamReader decoder = new InputStreamReader(gzipInput, "UTF-8");
							BufferedReader buffered = new BufferedReader(decoder);
							
							File outputFile = new File(outputPath + "/" + year + ".csv");
							outputFile.getParentFile().mkdirs();
							FileOutputStream outputFileStream = new FileOutputStream(outputFile);
							
							outputFileStream.write(getCSVHeaders().getBytes());
							
							String line = buffered.readLine();
							while(line != null) {
								String csvLine = getCSVLine(line);
								
								outputFileStream.write(csvLine.getBytes());
								line = buffered.readLine();
							}

							outputFileStream.close();
							buffered.close();
							gzipInput.close();
							
//							System.out.print(" done\n");
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return false;
						} catch (IOException e) {
							e.printStackTrace();
							return false;
						}
						
					}
				}
			}
			return true;
		}
		
		return false;
	}
	
	private String getCSVHeaders() {
		String headers = "";
		
		headers += "Year,";
		headers += "Month,";
		headers += "Day,";
		headers += "Hour,";
		headers += "Air Temperature,";
		headers += "Dew Point Temperature,";
		headers += "Sea Level Pressure,";
		headers += "Wind Direction,";
		headers += "Wind Speed Rate,";
		headers += "Sky Condition Total Coverage Code,";
		headers += "One Hour Liquid Precipitation Depth,";
		headers += "Six Hour Liquid Precipitation Depth\n";
		
		return headers;
	}
	
	private String getCSVLine(String line) {
		int dataYear = Integer.parseInt(line.substring(0, 4).trim());
		int dataMonth = Integer.parseInt(line.substring(5, 7).trim());
		int dataDay = Integer.parseInt(line.substring(8, 10).trim());
		int dataHour = Integer.parseInt(line.substring(11, 13).trim());
		float dataAirTemp = Float.parseFloat(line.substring(14, 19).trim()) / 10;
		float dataDewPoint = Float.parseFloat(line.substring(20, 25).trim()) / 10;
		float dataSeaLevelPressure = Float.parseFloat(line.substring(26, 31).trim()) / 10;
		int dataWindDirection = Integer.parseInt(line.substring(32, 37).trim());
		float dataWindSpeed = Float.parseFloat(line.substring(38, 43).trim()) / 10;
		int dataSkyCoverage = Integer.parseInt(line.substring(44, 49).trim());
		float dataOneHourLiqPrecip = Float.parseFloat(line.substring(50, 55).trim()) / 10;
		float dataSixHourLiqPrecip = Float.parseFloat(line.substring(56).trim()) / 10;
		
		String csvString = "";
		csvString += dataYear + ",";
		csvString += dataMonth + ",";
		csvString += dataDay + ",";
		csvString += dataHour + ",";
		csvString += dataAirTemp + ",";
		csvString += dataDewPoint + ",";
		csvString += dataSeaLevelPressure + ",";
		csvString += dataWindDirection + ",";
		csvString += dataWindSpeed + ",";
		csvString += dataSkyCoverage + ",";
		csvString += dataOneHourLiqPrecip + ",";
		csvString += dataSixHourLiqPrecip + "\n";
		
		return csvString;
	}
}
