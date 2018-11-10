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
					Table avgAirTemp = weatherData.summarize("Air Temperature", mean).by("Year", "Month");
					
					System.out.println(avgAirTemp.printAll());
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

	private static NumericAggregateFunction getMeanFunction() {
		return new NumericAggregateFunction("") {

			@Override
			public Double summarize(NumberColumn column) {
				
				return column.mean();
			}
			
		};
	}
}
