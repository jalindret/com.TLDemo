# UI Test Automation Project (Selenium + TestNG + Maven)

## 1. Overview
Maven-based UI test automation framework using:
- Selenium WebDriver
- TestNG
- Page Object Model (POM)
- Selectors - PageFactory using @FindBy
- Reporting: Surefire (default)
- Utility helpers for waits, safe actions, time validation

## 2. Tech Stack
Language: Java 17+  
Build: Maven  
Runner: TestNG  
UI: Selenium WebDriver  
Reporting: Surefire
Design: Page Object Model  
Assertions: TestNG Assert  
Logging: TestNG Reporter.log  

## 3. Prerequisites
1. Java (java --version)
2. Maven (mvn --version)
3. update ~/.zshrc with paths for java, maven 
4. Browser installed (Chrome/Firefox)
5. (Optional) Allure CLI (brew install allure)
6. macOS terminal access

## 4. Project Structure
project-root/  
├─ pom.xml  
├─ README.md  
├─ target/ (generated)  
├─ src  
│  ├─ main/java/org/exp/base/BaseClass.java  
│  ├─ main/java/org/exp/pages/*pages
│  ├─ main/java/org/exp/utils/Helper.java  
│  └─ test/java/org/exp/tests/*tests  
└─ (generated reports)  
   ├─ target/surefire-reports/  

## 5. Build & Run
mvn clean test  
Single test: mvn -Dtest=TestOne test   

## 6. Reporting
Surefire: target/surefire-reports/  
HTML summary:
mvn test surefire-report:report-only  
open target/site/surefire-report.html  

Allure/ extent with listeres can be implemented for a much better reporting capability
missing because of lack of time

## 7. Page Object Model
- @FindBy locators
- Public action + assertion methods
- Centralized waits in Helper

## 8. Assertions & Logging
Assert: org.testng.Assert  
Logging: Reporter.log(msg, true) -> console + surefire XML reporter-output

## 9. Adding a New Test
1. Create/extend page object in pages package
2. Add test class under tests package
4. Run mvn test

## 10. New Page Template
    public class NewPage extends Helper {
        @FindBy(id = "someId")
        private WebElement sample;
        public NewPage(WebDriver driver) {
            super(driver, BaseClass.getExplicitWaitSeconds());
            PageFactory.initElements(driver, this);
        }
        public void doAction() {
            waitForVisible(sample).click();
        }
    }

## 11. Configuration
Dependencies: pom.xml
Waits: BaseClass / Helper  
Extent listener: test class or testng.xml  

## 12. Common Commands
Clean + test: mvn clean test  
Compile only: mvn clean compile

