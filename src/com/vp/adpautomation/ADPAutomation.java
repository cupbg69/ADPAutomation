package com.vp.adpautomation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

public class ADPAutomation 
{

	Configuration config;
	WebDriver driver;
	
	public void Start() throws Exception
	{
		Setup();
		LogIn();
		GoToMyTimecard();
		AddRelevantDatesToSheet();
		FillWithHours();
		FillEarningCode();
		FillCustomer();
		FillProject();
	}

	private void Setup() throws Exception
	{
		config = new Configuration();
		driver = new FirefoxDriver();
	}

	private void LogIn() throws Exception
	{
		String username = URLEncoder.encode(config.getUsername().trim(), "UTF-8");
		String password = URLEncoder.encode(config.getPassword().trim(), "UTF-8");
		String fullUri = "https://"+username+":"+password+"@agateway.adp.com/siteminderagent/nocert/1443711288/smgetcred.scc?TYPE=16777217&REALM=-SM-Portal%20Access%20[10%3a54%3a48%3a300647719625]&SMAUTHREASON=0&METHOD=GET&SMAGENTNAME=-SM-QdVwmC1faj529vBDBOtxR15H%2fK8MBiENd3QyUrIPQXSIkgmi9lcz5g8QNvXKtpxz&TARGET=-SM-https%3a%2f%2fportal%2eadp%2ecom%2fwps%2fmyportal%2fsitemap%2fEmployee%2fHome%2fWelcome";
		driver.get(fullUri);
	}
	
	private void GoToMyTimecard()
	{
		driver.get("https://portal.adp.com/wps/myportal/sitemap/Employee/TimeAttendance/MyTimecard");

		//press my timecard button
		FluentWait fluentWait = new FluentWait<>(driver); 
		fluentWait.withTimeout(30, TimeUnit.SECONDS);
		fluentWait.pollingEvery(200, TimeUnit.MILLISECONDS);
		fluentWait.ignoring(NoSuchElementException.class);
		
		WebElement frame = (WebElement)fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id("IFRM")));
		driver.switchTo().frame(frame);
		
		
		WebElement timeCardButton = (WebElement)fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id("UI4_ctBody_UCTodaysActivities_btnTimeSheet")));
		
		timeCardButton.click();
	}
	
	private void AddRelevantDatesToSheet() throws Exception
	{
		FluentWait fluentWait = new FluentWait<>(driver); 
		fluentWait.withTimeout(4, TimeUnit.SECONDS);
		fluentWait.pollingEvery(200, TimeUnit.MILLISECONDS);
		fluentWait.ignoring(NoSuchElementException.class);

		try
		{
			WebElement addDatesButton = (WebElement)fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id("hypAddDates")));
			addDatesButton.click();			
		}
		catch (Exception e) //Above element doesn't exist
		{
			WebElement preferencesMenu = (WebElement)fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id("hypPreferences")));
			preferencesMenu.click();			
			WebElement hideUnscheduledDaysItem = (WebElement)fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id("menuItem14")));
			hideUnscheduledDaysItem.click();	
			WebElement addDatesButton = (WebElement)fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id("hypAddDates")));
			addDatesButton.click();	
		}
		
		//Change control to new browser child window
		 String BaseWindow = driver.getWindowHandle();
         Set<String> handles = driver.getWindowHandles();
         String newWindow = null;
         for (String currentHandle : handles)
         {
             if (!currentHandle.equals(BaseWindow))
             {
                 newWindow = currentHandle;
                 break;
             }
         }
         
         if (newWindow != null)
        	 driver.switchTo().window(newWindow);
         else 
        	 throw new Exception("Dates Windows hasn't been opened. Check if browser is blocking popups");
		
		 List<WebElement>	
		 datesTableMatches = driver.findElements(By.xpath("//a[contains(text(), 'Mon')]"));
		 datesTableMatches.addAll(driver.findElements(By.xpath("//a[contains(text(), 'Tue')]")));
		 datesTableMatches.addAll(driver.findElements(By.xpath("//a[contains(text(), 'Wed')]")));
		 datesTableMatches.addAll(driver.findElements(By.xpath("//a[contains(text(), 'Thu')]")));
		 datesTableMatches.addAll(driver.findElements(By.xpath("//a[contains(text(), 'Fri')]")));
		 
		 for(WebElement we : datesTableMatches)
		 {
			 WebElement parentTr = we.findElement(By.xpath("../.."));
			 WebElement SiblingTD = parentTr.findElement(By.cssSelector("td.hC"));
			 WebElement addDateCheckBox = SiblingTD.findElement(By.cssSelector("input"));
			 if (!addDateCheckBox.isSelected()) //If its not already selected
				 addDateCheckBox.click();		 
		 }
		
		 WebElement SubmitDesiredDatesButton = driver.findElement(By.id("btnSubmit"));
		 SubmitDesiredDatesButton.click();
		 
		 //Go back to the first window
		 driver.switchTo().window(BaseWindow);
		 driver.switchTo().frame(driver.findElement(By.id("IFRM")));
	}
	
	public void FillWithHours() throws InterruptedException
	{
		String hours = config.getHoursPerDay();
		
		FluentWait fluentWait = new FluentWait<>(driver); 
		fluentWait.withTimeout(4, TimeUnit.SECONDS);
		fluentWait.pollingEvery(200, TimeUnit.MILLISECONDS);
		fluentWait.ignoring(NoSuchElementException.class);
		
		//(List<WebElement>) fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[id^='TOTALHOURS']")));
		//driver.findElements(By.cssSelector("a[id^='TOTALHOURS']"));
		List<WebElement> HoursInput = (List<WebElement>) fluentWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a[id^='TOTALHOURS']")));


		for(WebElement e : HoursInput)
		{
			e.click(); //Due to a browser script, this will put focus in a textbox
			Thread.sleep(50);
			WebElement input = driver.findElement(By.cssSelector("#EditLayer input"));
			if(input.getAttribute("value").trim().equals(""))//We only input if there isn't already text	
				input.sendKeys(hours);
		
		}
	}
	
	public void FillEarningCode() throws InterruptedException
	{
		String earningCode = config.getEarningsCode();
		
		List<WebElement> HoursInput = driver.findElements(By.cssSelector("a[id^='31']"));

		for(WebElement e : HoursInput)
		{
			e.click(); //Due to a browser script, this will put focus in a textbox
			Thread.sleep(100);
			WebElement input = driver.findElement(By.cssSelector("#EditLayer input"));
			if(input.getAttribute("value").trim().equals(""))//We only input if there isn't already text	
				input.sendKeys(earningCode);
		}
	}
	
	public void FillCustomer() throws InterruptedException
	{
		String customer = config.getCustomer();
		
		List<WebElement> HoursInput = driver.findElements(By.cssSelector("a[id^='17']"));

		for(WebElement e : HoursInput)
		{
			e.click(); //Due to a browser script, this will put focus in a textbox
			Thread.sleep(100);
			WebElement input = driver.findElement(By.cssSelector("#EditLayer input"));
			if(input.getAttribute("value").trim().equals(""))//We only input if there isn't already text	
				input.sendKeys(customer);
		}
	}
		
	public void FillProject() throws InterruptedException
	{
		String project = config.getProjectName();
		
		List<WebElement> HoursInput = driver.findElements(By.cssSelector("a[id^='18']"));

		for(WebElement e : HoursInput)
		{
			e.click(); //Due to a browser script, this will put focus in a textbox
			Thread.sleep(100);
			WebElement input = driver.findElement(By.cssSelector("#EditLayer input"));
			if(input.getAttribute("value").trim().equals("")) //We only input if there isn't already text	
				input.sendKeys(project);
		}
	}
	
	
	
	

}
