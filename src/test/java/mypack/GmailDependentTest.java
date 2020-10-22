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

public class GmailDependentTest {

	public static void main(String[] args) throws Exception
	{
		//test log messages
		String blankuid = "Blank userid test passed";
		String invaliduid = "Invalid userid test passed";
		String blankpwd = "Blank password test passed";
		String invalidpwd = "Invalid password test passed";
		String validuidpwd = "Login test passed for valid credentials";
		
		//Create a folder for results
		File resultsfolder = new File("Gmaillogintestresults");
		//if folder already exists it can append folder
		resultsfolder.mkdir();
		
		SimpleDateFormat sf = new SimpleDateFormat("dd-MMM-yy-hh-mm-ss");
		Date dt=new Date();
		
		//create html file for test results(Extent Report)
		ExtentReports er = new ExtentReports(resultsfolder.getAbsolutePath()+"//gmaillogintestlog"+sf.format(dt)+".html", false);
		ExtentTest et = er.startTest("Gmail Login Test Cases Execution Results");
		
		//connect existing .xlsx file in project folder to read test criteria and test data
		File f = new File("Bookgmaillogintestcases.xlsx");
		
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
		ATUTestRecorder rec = new ATUTestRecorder(resultsfolder.getAbsolutePath(),"VideoOn"+sf.format(dt), false);
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
			driver.get("https://www.gmail.com");
			Thread.sleep(3000);
			
			//enter userid and click next
			driver.findElement(By.name("identifier")).sendKeys(userid);
			driver.findElement(By.xpath("//*[text()='Next']")).click();
			Thread.sleep(4000);
			try
			{
				//test1
				if(useridcriteria.equalsIgnoreCase("blank") &&
				driver.findElement(By.xpath("(//*[contains(text(),'Enter an email')])[2]")).isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(blankuid);
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,blankuid);
				}
				//test2
				else if(useridcriteria.equalsIgnoreCase("invalid") &&
						driver.findElement(By.xpath("//*[contains(text(),'find your Google Account')]")).isDisplayed())
				{
					sh.getRow(i).createCell(nouc).setCellValue(invaliduid);
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.PASS,invaliduid);
					System.out.println(invaliduid);
				}
				else if(useridcriteria.equalsIgnoreCase("valid") &&
						driver.findElement(By.name("password")).isDisplayed())
				{
					//test3
					//password testing
					driver.findElement(By.name("password")).sendKeys(pwd);
					driver.findElement(By.xpath("//*[text()='Next']")).click();
					Thread.sleep(6000);
					//test4
					if(pwdcriteria.equalsIgnoreCase("blank") &&
							driver.findElement(By.xpath("//span[contains(text(), 'Enter a password')]")).isDisplayed())	
					{
						sh.getRow(i).createCell(nouc).setCellValue(blankpwd);
						sh.autoSizeColumn(nouc);
						et.log(LogStatus.PASS,blankpwd);
						System.out.println(blankpwd);
					}
					//test5
					else if(pwdcriteria.equalsIgnoreCase("invalid") &&
							driver.findElement(By.xpath("//span[contains(text(),'click Forgot password to reset it.')]")).
							isDisplayed())	
					{
						sh.getRow(i).createCell(nouc).setCellValue(invalidpwd);
						sh.autoSizeColumn(nouc);
						et.log(LogStatus.PASS,invalidpwd);
						System.out.println(invalidpwd);
					}
					//test6
					else if(pwdcriteria.equalsIgnoreCase("valid")
							//&& driver.findElement(By.xpath("//*[text()='Compose']")).isDisplayed())
							&& driver.findElement(By.xpath("//*[text()='Try another way to sign in']")).isDisplayed())
					{
						sh.getRow(i).createCell(nouc).setCellValue(validuidpwd);
						sh.autoSizeColumn(nouc);
						et.log(LogStatus.PASS,validuidpwd);
						System.out.println(validuidpwd);
					} 
					//test7
					else //else for pwd
					{
						//take screenshot of failed pwd case
						File src=driver.getScreenshotAs(OutputType.FILE);
						String fname = sf.format(dt)+".png";
					
						File dest = new File(resultsfolder.getAbsolutePath()+"\\"+fname);
						FileHandler.copy(src,dest);
						sh.getRow(i).createCell(nouc).setCellValue("password test failed, pls watch "+dest.getAbsolutePath()+
																	" for further details");
						sh.autoSizeColumn(nouc);
						et.log(LogStatus.FAIL,"password test failed, pls watch "+et.addScreenCapture(dest.getAbsolutePath()));
					}
				}//end of elseif loop
				//else if()
				else //else for userid
				{
					//take screenshot of failed userid case
					File src=driver.getScreenshotAs(OutputType.FILE);
					String fname = sf.format(dt)+".png";
				
					File dest = new File(resultsfolder.getAbsolutePath()+"\\"+fname);
					FileHandler.copy(src,dest);
					sh.getRow(i).createCell(nouc).setCellValue("userid test failed, pls watch "+dest.getAbsolutePath()+
														" for further details");
					sh.autoSizeColumn(nouc);
					et.log(LogStatus.FAIL,"userid test failed, pls watch "+et.addScreenCapture(dest.getAbsolutePath()));
			
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
		File zipf = new File("Gmaillogintesults.zip");
		ZipUtil.pack(resultsfolder, zipf);
	
		//step-3: get back the excel file to project folder from results folder
		File cf1 = new File(resultsfolder.getAbsolutePath()+"\\"+f.getName());//existing
		File cf2 = new File(f.getName());
		cf1.renameTo(cf2);
	}

}
