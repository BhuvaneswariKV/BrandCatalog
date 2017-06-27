package com.BrandCatalog.action;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrandActions {
	private static final Logger logger = LoggerFactory.getLogger(BrandActions.class);

	public boolean getUrlLink(WebDriver driver, int linkIndex) {
		try {
			WebElement element = driver.findElement(By.cssSelector(
					"#center>*:nth-child(2)>div>div>.a-row.a-spacing-none.pagn>*:nth-child(" + linkIndex + ")"));
			if (!element.getAttribute("class").equals("pagnDisabled")) {
				logger.info(" Navigate to:" + element.getText());
				element.click();
				return true;
			} else
				return false;
		} catch (Exception e) {
			logger.error("Exception", e);
			return false;
		}

	}

}
