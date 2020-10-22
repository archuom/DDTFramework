package mypack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.zeroturnaround.zip.ZipUtil;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import atu.testrecorder.ATUTestRecorder;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Way2smsIndependentTest {

	public static void main(String[] args) throws Exception
	{
		//test log messages
		String validuidpwd = "Login test passed for valid credentials";
		String validuidinvalidpwd = "Valid uid but invalid pwd test passed";
		String validuidblankpwd = "valid uid and blank pwd test passed";
		String wronguidvalidpwd = "wrong uid valid pwd test passed";
		String invaliduidvalidpwd = "Invalid userid  valid pwd test passed";
		String blankuidvalidpwd = "Blank uid and valid pwd test passed";
		
		
		//Create a folder for results
		File resultsfolder = new File("way2smslogintestresults");
		//if folder already exists it can append folder
		resultsfolder.mkdir();
		
		SimpleDateFormat sf = new SimpleDateFormat("dd-MMM-yy-hh-mm-ss");
		Date dt=new Date();
		
		//create html file for test results(Extent Report)
		ExtentReports er = new ExtentReports(resultsfolder.getAbsolutePath()+"//way2smslogintestlog"+sf.format(dt)+".html", false);
		ExtentTest et = er.startTest("way2sms Login Test Cases Execution Results");
		
		//connect existing .xlsx file in project folder to read test criteria and test data
		File f = new File("way2smstestcases.xlsx");
		
		//Take read permission on that file
		FileInputStream fi = new FileInputStream(f);
		
		//access as a excel file
		Workbook wb = WorkbookFactory.create(fi);
		
		Sheet sh = wb.getSheet("sheet1");//existing
		int nour = sh.getPhysicalNumberOfRows();
		
		//create results column header by adding date to column name
		int nouc = sh.getRow(0).getLastCellNum();
		
		String cname= "Results on"+ sf.format(dt);
		sh.getRow(0).createCell(nouc).setCellValue(cname);
		sh.autoSizeColumn(nouc);
		
		//start video recording
		ATUTestRecorder rec = new ATUTestRecorder(resultsfolder.getAbsolutePath(),"Way2smsVideoOn"+sf.format(dt), false);
		rec.start();
		
		//get data from 2nd row(index=1) onwards in sheet1
		//1st row (index=0) has column names(input1, input2, output)
		for(int i=1;i<nour;i++)
		{
			DataFormatter df = new DataFormatter();
			String userid = df.formatCellValue(sh.getRow(i).getCell(0));
			String useridcriteria = df.formatCellValue(sh.getRow(i).getCell(1));
			String pwd = df.formatCellValue(sh.getRow(i).getCell(2));
			String pwdcriteria =  df.formatCellValue(sh.getRow(i).getCell(3));
			
			//open browser and launch site
			WebDriverManager.chromedriver().setup();
			System.setProperty("webdriver.chrome.silentOutput", "true");
			ChromeDriver driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.get("https://www.way2sms.com/");
			Thread.sleep(3000);
			
			//enter userid and click next
			driver.findElement(By.id("mobileNo")).sendKeys(userid);
			driver.findElement(By.name("password")).sendKeys(pwd);
			driver.findElement(By.xpath("(//button[contains(text(),'Login')])[1]")).click();
			Thread.sleep(3000);
			try
			{
				//test1
				if(useridcriteria.equalsIgnoreCase("valid") &&
					pwdcriteria.equalsIgnoreCase("valid") &&
					driver.findElement(By.xpath("//span[text()='Send SMS']")).isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(validuidpwd);
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,validuidpwd);
					System.out.println("test-1"+validuidpwd );
				}
				//test2
				else if(useridcriteria.equalsIgnoreCase("valid") &&
						pwdcriteria.equalsIgnoreCase("invalid") &&
						driver.findElement(By.xpath("//*[contains(text(),'Incorrect number or password! Try Again.')]")).
						isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(validuidinvalidpwd);
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,validuidinvalidpwd );
					System.out.println("test-2"+validuidinvalidpwd );
				}
				//TEST3
				else if(useridcriteria.equalsIgnoreCase("valid") &&
						pwdcriteria.equalsIgnoreCase("blank") &&
						driver.findElement(By.xpath("(//b[text()='Enter password'])[2]")).isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(validuidblankpwd);
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,validuidblankpwd);
					System.out.println("test-3"+validuidblankpwd);
				}
				//test4
				else if(useridcriteria.equalsIgnoreCase("wrong_size") &&
						//pwdcriteria.equalsIgnoreCase("valid") &&
						driver.findElement(By.xpath("//b[text()='Enter valid mobile number']")).
						isDisplayed())	
				{
					sh.getRow(i).createCell(nouc).setCellValue(wronguidvalidpwd );
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,wronguidvalidpwd );
					System.out.println("test 4"+wronguidvalidpwd );
				}
				//test5
				else if(useridcriteria.equalsIgnoreCase("wrong_series") &&
						driver.findElement(By.xpath("//b[text()='Invalid mobile number']")).
						isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(wronguidvalidpwd );
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,wronguidvalidpwd );
					System.out.println("test-5"+wronguidvalidpwd );
				}
				//test 6
				else if(useridcriteria.equalsIgnoreCase("blank") &&
						driver.findElement(By.xpath("//b[text()='Enter your mobile number']")).
						isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(blankuidvalidpwd );
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,blankuidvalidpwd );
					System.out.println("test-6"+blankuidvalidpwd );
				}
				
				//test7
				else if(useridcriteria.equalsIgnoreCase("invalid") &&
						//pwdcriteria.equalsIgnoreCase("valid") &&
						driver.findElement(By.xpath("//b[text()='Your mobile number is not register with us']")).
						isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(invaliduidvalidpwd );
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,invaliduidvalidpwd );
					System.out.println("test-7"+invaliduidvalidpwd );
				}
				else
				{
					//take screenshot of failed case
					File src=driver.getScreenshotAs(OutputType.FILE);
					String fname = sf.format(dt)+".png";
					
					File dest = new File(resultsfolder.getAbsolutePath()+"\\"+fname);
					FileHandler.copy(src,dest);
					sh.getRow(i).createCell(nouc).setCellValue("login test failed, pls watch "+dest.getAbsolutePath()+
																	" for further details");
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.FAIL,"login test failed, pls watch "+et.addScreenCapture(dest.getAbsolutePath()));
				}
				//close site to continue further test iterations
				driver.close();
			}//end of try
			catch (Exception e)
			{
				//take screenshot of any raised exception
				File src=driver.getScreenshotAs(OutputType.FILE);
				String fname = sf.format(dt)+".png";
			
				File dest = new File(resultsfolder.getAbsolutePath()+"\\"+fname);
				FileHandler.copy(src,dest);
				sh.getRow(i).createCell(nouc).setCellValue(e.getMessage());
				sh.autoSizeColumn(nouc);
				et.log(LogStatus.FAIL,e.getMessage()+et.addScreenCapture(dest.getAbsolutePath()));
				//close site to continue further test iterations after exception 
				driver.close();
			}
		}
		//stop recording
		rec.stop();
	
		//Take write permission on that excel file to save changes
		FileOutputStream fo = new FileOutputStream(f);
		wb.write(fo);
		wb.close();
		fi.close();
		fo.close();
	
		//save results file
		er.endTest(et);
		er.flush();
	
		//step-1: send excel file into results folder
		//copy of results folder
		File rf = new File(resultsfolder.getAbsolutePath()+"\\"+f.getName());
		f.renameTo(rf);
	
		//step-2: Zip that folder
		File zipf = new File("way2smslogintestresults.zip");
		ZipUtil.pack(resultsfolder, zipf);
	
		//step-3: get back the excel file to project folder from results folder
		File cf1 = new File(resultsfolder.getAbsolutePath()+"\\"+f.getName());//existing
		File cf2 = new File(f.getName());
		cf1.renameTo(cf2);
	}

}
