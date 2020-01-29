package automationFramework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AutoSubmit
{
	private final static String username = null;
	private final static String password = null;
	private static WebDriver driver;

	public static void initChromeDriver()
	{
		final String driverPath = "/home/myoiwrites/Documents/github/auto-submit/chromedriver_linux64/chromedriver";
		System.setProperty("webdriver.chrome.driver", driverPath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--window-size=1920,1200");
		driver = new ChromeDriver(options);
	}

	public static void login() throws InterruptedException
	{
		System.out.println("Login...");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		driver = new ChromeDriver();
		driver.get("https://codeforces.com/enter?back=%2F");

		// enter username
		WebElement element = driver.findElement(By.id("handleOrEmail"));
		element.sendKeys(username);

		// enter password
		element = driver.findElement(By.id("password"));
		element.sendKeys(password);
		element.sendKeys(Keys.ENTER);

		// wait until login is done
		while (driver.getCurrentUrl().compareTo("https://codeforces.com/") != 0)
		{
			Thread.sleep(1000);
		}
	}

	public static void navigateTo(String problemNumber, String problemLetter) throws InterruptedException
	{
		System.out.println("Copy-pasting codes..");
		// navigate to that problem's page
		driver.navigate().to("https://codeforces.com/problemset/submit");

		// wait until login is done
		while (driver.getCurrentUrl().compareTo("https://codeforces.com/problemset/submit") != 0)
		{
			Thread.sleep(1000);
		}

		WebElement element = driver.findElement(By.name("submittedProblemCode"));
		element.sendKeys(problemNumber + problemLetter);
	}

	public static void submit(String sourceFile) throws IOException, InterruptedException
	{
		System.out.println("Submitting codes..");
		List<String> lines = Files.readAllLines(Paths.get(sourceFile));
		String code = String.join(System.lineSeparator(), lines);
		
//		Thread.sleep(1000);
		WebElement element = driver.findElement(By.id("toggleEditorCheckbox"));
		element.click();
		
		element = driver.findElement(By.cssSelector("textarea#sourceCodeTextarea"));
		element.sendKeys(code);
		
		element = driver.findElement(By.className("submit"));
		element.click();
		
//		Thread.sleep(2000);

//		driver.get("https://codeforces.com/problemset/status");

		List<WebElement> elems = driver.findElements(By.cssSelector("table.status-frame-datatable"));
		elems = elems.get(0).findElements(By.cssSelector("tr"));
		elems = elems.get(1).findElements(By.cssSelector("td"));
		String sourceNumber = elems.get(0).getText();
		String who = elems.get(2).getText();
		String verdict = elems.get(5).getText();
		
		System.out.println(sourceNumber + ": " + who + ": " + verdict);

		if (verdict.contains("Accepted") == false)
		{
			while (verdict.contains("Running") || verdict.contains("queue"))
			{
				String tempVerdict = elems.get(5).getText();

				if (verdict.compareTo(tempVerdict) == 0)
				{
					System.out.print("..");
				}
				else
				{
					verdict = tempVerdict;	
					System.out.println("\n" + sourceNumber + ": " + who + ": " + verdict);
				}
				Thread.sleep(1500);
			} 
		}
	}

	public static void main(String[] args) throws Throwable
	{
		initChromeDriver();

		login();
		
		String problemNumber = "4";
		String problemLetter = "A";
		navigateTo(problemNumber, problemLetter);

		String sourceFile = "main.c";
		submit(sourceFile);
		driver.quit();
	}
}