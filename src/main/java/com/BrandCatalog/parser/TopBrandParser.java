package com.BrandCatalog.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class TopBrandParser {
	String CATEGORY = "#center>*:nth-child(1)>*:nth-child(1)";
	String SUBCATEGORY = "#center>*:nth-child(1)>*:nth-child(4)";
	String TOPBRAND = ".a-color-state";
	String BRANDLIST = ".s-see-all-indexbar-column>li";
	String BRANDNAME = ".refinementLink";
	String BRANDCOUNT = ".narrowValue";
	String PAGENAVIGATIONLINK = "#center>*:nth-child(2)>div>div>div>.pagnLink";

	Document document;

	public void jsoupParser(WebDriver webDriver) {
		String javascript = "return arguments[0].innerHTML";
		String pageSource = (String) ((JavascriptExecutor) webDriver).executeScript(javascript,
				webDriver.findElement(By.tagName("html")));
		document = Jsoup.parse(pageSource);
	}

	public Elements parseElements(String cssSelector) {
		return document.select(cssSelector);
	}

	public int getElementsCount(String cssSelector) {
		return document.select(cssSelector).size();
	}

	public String parseContent(String cssSelector) {
		return document.select(cssSelector).text();
	}

	public int getPageNavigationInUrl() {
		return parseElements(PAGENAVIGATIONLINK).size();
	}

	public List<ArrayList<String>> getBrandListDetail() {
		ArrayList<ArrayList<String>> brandListDetail = new ArrayList<>();

		String category = parseContent(CATEGORY);
		String subCategory = parseContent(SUBCATEGORY);
		String topBrand = isTopBrand();

		Elements brandDetailElements = parseElements(BRANDLIST);

		Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
		for (Element brandDetailElement : brandDetailElements) {
			ArrayList<String> brandDetails = new ArrayList<>();

			brandDetails.add(category.replace(",", " "));
			brandDetails.add(subCategory.replace(",", " "));
			brandDetails.add(topBrand);
			brandDetails.add(brandDetailElement.select(BRANDNAME).text().replaceAll(",", " "));

			Matcher matcher = pattern.matcher(brandDetailElement.select(BRANDCOUNT).text());
			if (matcher.find())
				brandDetails.add(matcher.group(1).replaceAll(",", ""));
			brandListDetail.add(brandDetails);
		}
		return brandListDetail;

	}

	public String isTopBrand() {
		return "Top Brands".equals(parseContent(TOPBRAND)) ? "YES" : "NO";
	}

}
