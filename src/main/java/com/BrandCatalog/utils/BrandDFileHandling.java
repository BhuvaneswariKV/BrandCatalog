package com.BrandCatalog.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrandDFileHandling {
	public static String fileName = "TopBrandDetail.csv";
	String FileHeader = "Category,Sub Category,Top Brand,Brand Name,Brand Count";
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	FileWriter brandDetails;

	private static final Logger logger = LoggerFactory.getLogger(BrandDFileHandling.class);

	public void createCSVFile() {
		try {
			brandDetails = new FileWriter(fileName);
			logger.info(" file created");
			addHeader();
		} catch (IOException e) {
			logger.error("Exception", e);
		}
	}

	public void addHeader() {
		try {
			brandDetails.append(FileHeader);
			brandDetails.append(NEW_LINE_SEPARATOR);

		} catch (IOException e) {
			logger.error("Exception", e);
		}
	}

	public void addBrandDetail(List<ArrayList<String>> listDetails) {
		try {
			for (List<String> brandDetail : listDetails) {
				for (String data : brandDetail) {
					brandDetails.append(data);
					brandDetails.append(COMMA_DELIMITER);
				}
				brandDetails.append(NEW_LINE_SEPARATOR);
			}

		} catch (IOException e) {
			logger.error("Exception:", e);
		}

	}

	public void close() {
		try {
			brandDetails.close();
		} catch (IOException e) {
			logger.error(" Exception in File Action", e);
		}
	}

}
