/**
 * Compiles the data from the CSV files into a Tablesaw table
 * 
 * @author Mark Eby
 */

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
	private FilenameFilter csvFilter;
	private Table weatherData;
	
	private final ColumnType STRING = ColumnType.STRING;
	private final ColumnType DOUBLE = ColumnType.DOUBLE;
	private final ColumnType SKIP = ColumnType.SKIP;
	
	/**
	 * Constructor for the class.
	 * Initializes the current working directory and the CSV file filter.
	 */
	public DataCompiler() {
		this.currentPath = System.getProperty("user.dir");
		this.csvFilter = new FilenameFilter() {

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
	
	/**
	 * Goes through the individual CSV files and appends them to the Tablesaw Table
	 * 
	 * @return A Tablesaw table containing all the data found
	 */
	public Table compileData() {
		File parentFolder = new File(this.currentPath + this.filesLocation);
		
		
		File[] txtFiles = parentFolder.listFiles(this.csvFilter);
		Arrays.sort(txtFiles);
		
		// First file must be process separately because this is the easiest to do 
		// when using Tablesaw to read the CSV file.
		File firstFile = txtFiles[0];
		this.weatherData = readCSV(firstFile);
		
		for(int i = 1; i< txtFiles.length; i++) {
			this.weatherData.append(readCSV(txtFiles[i]));
		}
		return this.weatherData;
	}
	
	/**
	 * Used to generate the Tablesaw table from the CSV file contents.
	 * 
	 * @param csvFile The File object representation of the CSV file to be read.
	 * @return Tablesaw table containing the Date, Time, and Air Temperature data
	 */
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
