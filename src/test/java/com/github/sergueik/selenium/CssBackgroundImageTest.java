package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
* The page background feature is a css business
* https://www.w3schools.com/cssref/pr_background-image.asp
*/

// Based on forum ??

public class CssBackgroundImageTest extends BaseTest {

	private String filePath = "background_image.html";
	private static final Logger log = LogManager
			.getLogger(CssBackgroundImageTest.class);
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final int x = 15;
	private static final int y = 15;
	private static final int width = 30;
	private static final int height = 25;

	static List<String> OSes = Arrays
			.asList(new String[] { "windows", "dos", "mac", "linux" });

	static Map<String, String> defaultBrowsers = new HashMap<>();
	static {
		defaultBrowsers.put("windows", "Chrome");
		defaultBrowsers.put("linux", "Firefox");
		defaultBrowsers.put("mac", "Safari");
	}

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent(filePath));
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	@Test(enabled = true)
	public void backgroundImageTest() {
		TakesScreenshot screenshot = ((TakesScreenshot) driver);

		File screenshotFile = screenshot.getScreenshotAs(OutputType.FILE);
		// Move image file to new destination
		try {
			String fullScreenImagePath = "c:\\temp\\background.jpg";

			FileUtils.copyFile(screenshotFile, new File(fullScreenImagePath));
			System.err.println("Full screenshot saved in " + fullScreenImagePath);

			URL url = new URL("file:///c:/temp/background.jpg");
			Image chunk = readFragment(url.openStream(),
					new Rectangle(x, y, width, height));
			try {
				String chunkImagePath = "c:\\temp\\chunk.png";
				if (ImageIO.write((BufferedImage) chunk, "png",
						new File(chunkImagePath))) {
					System.out.println("Chunk screenshot saved: " + chunkImagePath);
					BufferedImage a = ImageIO.read(new File(chunkImagePath));
					String compareImagePath = "c:\\temp\\expected.png";
					BufferedImage b = ImageIO.read(new File(compareImagePath));
				}
			} catch (IOException e) {
				System.err.println("(backgroundImageTest) Ignored: " + e.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	// origin:
	// http://www.java2s.com/Tutorials/Java/Graphics_How_to/Image/Read_part_of_BufferedImage.htm
	public static BufferedImage readFragment(InputStream stream, Rectangle rect)
			throws IOException {
		ImageInputStream imageStream = ImageIO.createImageInputStream(stream);
		ImageReader reader = ImageIO.getImageReaders(imageStream).next();
		ImageReadParam param = reader.getDefaultReadParam();

		param.setSourceRegion(rect);
		reader.setInput(imageStream, true, true);
		BufferedImage image = reader.read(0, param);

		reader.dispose();
		imageStream.close();

		return image;
	}

	/*
		
		a somewhat more sophisticated 
		"помогите реализовать с использованием Streams и Лямбда" - challenge 
		featuring nested loop 
		
		Set<Team> teams = new HashSet<>();
	  
	  for (Team team : scenario.getTeamsActive()) {
	    for (UserAsLearner userAsLearner : learners) {
	      if(team.getLearners().contains(userAsLearner)) {
	        teams.add(team);
	        break;
	      }
	    }
	  }
	  */
	@Test(enabled = true)
	public void browserFilterTest() {
		List<String> result = OSes.stream().filter(o -> {
			return (defaultBrowsers.containsKey(o)) ? true : false;
		}).collect(Collectors.toList());

	}

}
