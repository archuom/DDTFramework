package mypack;

import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DatePickerProg {

	public static void main(String[] args) throws Exception
	{
		// Get expected date from keyboard
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Day");
		int expecteddate = Integer.parseInt(sc.nextLine());
		System.out.println("Enter month name");
		String expectedmonth = sc.nextLine();
		System.out.println("Enter Year");
		int expectedyear = Integer.parseInt(sc.nextLine());
		sc.close();
		
		//launch site
		WebDriverManager.chromedriver().setup();
		System.setProperty("webdriver.chrome.silentOutput","true");
		ChromeDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		
		driver.get("https://jqueryui.com/datepicker");
		//Thread.sleep(3000);

		//operate on datepicker
		//use below  code to scroll to date picker calendar
		driver.switchTo().frame(0);
		WebElement e = driver.findElement(By.id("datepicker"));
		driver.executeScript("arguments[0].scrollIntoView();",e);
		e.click();
		//year testing
		while(2>1)//infinite loop for year
		{
			String temp1 = driver.findElement(By.xpath("//*[@class='ui-datepicker-year']")).getText();
			int actualyear = Integer.parseInt(temp1);
			if(expectedyear<actualyear)
			{
				driver.findElement(By.xpath("//*[text()='Prev']")).click();
				//Thread.sleep(1000);
				
			}
			else if(expectedyear>actualyear)
			{
				driver.findElement(By.xpath("//*[text()='Next']")).click();
				//Thread.sleep(1000);
				
			}
			else //expectedyear == actualyear
			{
				//month testing(come to January
				while(2>1)//infinite loop for month
				{
					String temp2 = driver.findElement(By.xpath("//*[@class='ui-datepicker-month']")).getText();
					String actualmonth = temp2.toLowerCase();
					if(!actualmonth.equalsIgnoreCase("january"))
					{
						driver.findElement(By.xpath("//*[text()='Prev']")).click();
						//Thread.sleep(1000);
						
					}
					else
					{
						break;
					}
				}
				//month (come to expected month from january)
				while(2>1)//infinite loop 
				{
					String temp3 = driver.findElement(By.xpath("//*[@class='ui-datepicker-month']")).getText();
					String actualmonth = temp3.toLowerCase();
					if(!actualmonth.equalsIgnoreCase(expectedmonth))
					{
						driver.findElement(By.xpath("//*[text()='Next']")).click();
						//Thread.sleep(1000);
						
					}
					else
					{
						break;
					}
				}
				break;
			}
		}
		driver.findElement(By.xpath("//a[text()='"+expecteddate+"']")).click();
		//driver.close();
		
	}

}
