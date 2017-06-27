package com.BrandCatalog;

/**
 * Hello world!
 *
 */

import java.io.File;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.BrandCatalog.action.BrandActions;
import com.BrandCatalog.parser.TopBrandParser;
import com.BrandCatalog.utils.BrandDFileHandling;
import com.BrandCatalog.utils.MailConfiguration;
import static com.BrandCatalog.utils.Constants.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import com.amazonaws.services.s3.model.PutObjectRequest;

public class TopBrand {

	WebDriver driver;
	TopBrandParser topBrandParser = new TopBrandParser();
	BrandActions brandActions = new BrandActions();
	BrandDFileHandling brandFileHandling = new BrandDFileHandling();
	String USERHOME = System.getProperty("user.dir") + "/";

	MailConfiguration mailConfiguration = new MailConfiguration();
	private static final Logger logger = LoggerFactory.getLogger(TopBrand.class);

	public void executeUrlSeries(String seriesFileName) throws Exception {

		try {
			List<String> seriesPartList = new ArrayList<>();

			parseUrlSeries(seriesFileName, seriesPartList);
			brandFileHandling.createCSVFile();
			for (String url : seriesPartList) {
				driver.get(url);
				boolean enabled = true;
				for (int i = 2; i <= 29; i++) {
					enabled = brandActions.getUrlLink(driver, i);
					if (enabled) {
						topBrandParser.jsoupParser(driver);
						brandFileHandling.addBrandDetail(topBrandParser.getBrandListDetail());
					}
				}
				upLoaadReport(BrandDFileHandling.fileName);
			}
		} catch (Exception e) {
			logger.error("Exception:", e);

		} finally {
			brandFileHandling.close();
			driver.quit();
		}
	}

	private void parseUrlSeries(String seriesFilePath, List<String> urlList) throws Exception {
		URL seriesFileURL = TopBrand.class.getResource(seriesFilePath);
		URI seriesFileURI = seriesFileURL == null ? null : seriesFileURL.toURI();
		Path seriesFileAbsolutePath = seriesFileURI != null ? Paths.get(seriesFileURI) : Paths.get(seriesFilePath);

		List<String> seriesLines = Files.readAllLines(seriesFileAbsolutePath);
		for (String seriesLine : seriesLines) {
			seriesLine = seriesLine.trim();
			if (seriesLine.startsWith("#") || seriesLine.isEmpty()) {
				continue;
			}
			urlList.addAll(Arrays.asList(parseLine(seriesLine)));

		}

	}

	public void upLoaadReport(String fileName) throws IOException {

		AmazonS3 s3 = new AmazonS3Client();
		String bucketName = "boomerang-qa-report";
		s3.putObject(new PutObjectRequest(bucketName, fileName, new File(USERHOME + fileName))
				.withCannedAcl(CannedAccessControlList.PublicRead));

	}

	private String[] parseLine(String line) {
		String[] urlSplit = null;
		if (line.contains(","))
			urlSplit = line.split(",");
		else
			urlSplit = new String[] { line };
		return urlSplit;
	}

	public WebDriver getBrowser(String browser, String os) throws IOException {
		String userDir = System.getProperty("user.dir");
		if (browser.toLowerCase().equals(CHROME) && os.toLowerCase().contains(MAC)) {
			System.setProperty(CHROME_DRIVER_PROPS, userDir + CHROME_DRIVER_PATH_MAC);
			driver = new ChromeDriver(new ChromeOptions());
		} else if (browser.toLowerCase().equals(CHROME) && os.toLowerCase().equals(WINDOWS)) {
			System.setProperty(CHROME_DRIVER_PROPS, userDir + CHROME_DRIVER_PATH_WINDOWS);
			driver = new ChromeDriver(new ChromeOptions());
		} else if (browser.toLowerCase().equals(CHROME) && os.toLowerCase().contains("linux")) {
                        System.setProperty(CHROME_DRIVER_PROPS, userDir + CHROME_DRIVER_PATH_MAC);
                        driver = new ChromeDriver(new ChromeOptions());
		}else {
			driver = new FirefoxDriver(DesiredCapabilities.firefox());
		}
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
		return driver;
	}

	public static void main(String[] args) throws Exception {
		TopBrand topBrandDetail = new TopBrand();
		topBrandDetail.getBrowser("chrome", System.getProperty("os.name"));
		topBrandDetail.executeUrlSeries(args[0]);
	}

}
