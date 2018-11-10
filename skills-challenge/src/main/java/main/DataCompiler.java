package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class DataCompiler {
	private String currentPath;
	private String filesLocation = "/files";
	private FilenameFilter txtFilter;
	private Table weatherData;
	
	private final ColumnType STRING = ColumnType.STRING;
	private final ColumnType DOUBLE = ColumnType.DOUBLE;
	private final ColumnType SKIP = ColumnType.SKIP;
	
	public DataCompiler() {
		this.currentPath = System.getProperty("user.dir");
		txtFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				int lastPeriod = name.lastIndexOf(".");
				
				if(lastPeriod > 0) {
					String fileExtension = name.substring(lastPeriod);
					
					if(fileExtension.equals(".csv")) {
						return true;
					}
				}
				return false;
			}
			
		};
	}
	
	public Table compileData() {
		File parentFolder = new File(this.currentPath + this.filesLocation);
		
		
		File[] txtFiles = parentFolder.listFiles(txtFilter);
		Arrays.sort(txtFiles);
		
		File firstFile = txtFiles[0];
		this.weatherData = readCSV(firstFile);
		
		for(int i = 1; i< txtFiles.length; i++) {
			this.weatherData.append(readCSV(txtFiles[i]));
		}
		return this.weatherData;
	}
	
	private Table readCSV(File csvFile) {
		
		ColumnType[] types = {DOUBLE, DOUBLE, DOUBLE, STRING, DOUBLE, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP, SKIP};
		
		try {
			return Table.read().csv(CsvReadOptions
					.builder(csvFile.getAbsolutePath())
					.columnTypes(types));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
