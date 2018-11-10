/**
 * Runs the main process to generate a scatter plot with weather data.
 * Uses the FTPDownloader, CSVConverter, and DataCompiler classes.
 * 
 * @author: Mark Eby
 */

package main;

import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

public class Main {

	public static void main(String[] args) {
		FTPDownloader dataDownloader = new FTPDownloader();
		boolean downloaded = dataDownloader.getFTPData();
//		boolean downloaded = true; // enable if behind a firewall
		
		if(downloaded) {
			System.out.println("Done downloading.");
			CSVConverter unzipper = new CSVConverter();
			boolean unzipped = unzipper.convertToCSV();
			
			if(unzipped) {
				System.out.println("Done unzipping.");
				DataCompiler compiler = new DataCompiler();
				Table weatherData = compiler.compileData();
				
				if(weatherData != null) {
					System.out.println("Done compiling.");
					NumericAggregateFunction mean = getMeanFunction();
					//Generates the table with only the average Air Temperature data by month in each year 
					Table avgAirTemp = weatherData.summarize("Air Temperature", mean).by("Year", "Month");
					//Generates the scatter plot to show the data from the above table
					Plot.show(ScatterPlot.create("Average Temperature by Month of Each Year", avgAirTemp, "Month", "[Air Temperature]", "Year"));
				}
				else {
					System.out.println("Could not compile data.");
				}
			}
			else {
				System.out.println("Could not unzip downloaded files.");
			}
		}
		else {
			System.out.println("Could not download files.");
		}
	}

	// Helper method used to get the aggregate function for the mean of the column in the table
	private static NumericAggregateFunction getMeanFunction() {
		return new NumericAggregateFunction("") {

			@Override
			public Double summarize(NumberColumn column) {
				
				return column.mean();
			}
			
		};
	}
}
