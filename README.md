# Selenium Java BDD Enterprise-Level Automation Framework

An enterprise-grade UI test automation framework built using **Selenium WebDriver**, **Java**, **Cucumber (BDD)**, and the **Page Object Model (POM)** design pattern. The framework supports **cross-browser execution**, **parallel test runs**, and generates detailed **Extent Reports**, making it suitable for integration into CI/CD pipelines.

---

## Application Under Test

This framework automates test scenarios against [practice.expandtesting.com](https://practice.expandtesting.com) — a public practice site offering a range of real-world automation scenarios, including:

- **Login workflows** — including multi-factor authentication and OAuth examples
- **Password reset flows** — including email verification and security question flows
- **Form validation** — including dynamic form fields and validation rules
- **Date pickers and calendars** — including time zone handling and scheduling
- **Drag-and-drop interactions** — including sortable lists and file uploads
- **Shadow DOM elements** — including nested shadow DOM and style encapsulation
- **JavaScript dialogs and popups** — including custom modal windows and alert handling
- **A modern React-based web application**

---

## Tech Stack

| Category | Tool / Technology |
|---|---|
| Language | Java |
| Browser Automation | Selenium WebDriver |
| BDD Framework | Cucumber |
| Test Runner / Orchestration | TestNG |
| Build Tool | Maven |
| Design Pattern | Page Object Model (POM) |
| Reporting | Extent Reports |
| Cross-Browser Support | Chrome, Firefox, Edge |
| Execution | Sequential (local) & Parallel (cross-browser) |

---

## ✅ Prerequisites

Before running this project locally, ensure you have:

- **Java JDK** (8 or higher) installed and `JAVA_HOME` configured
- **Maven** installed (or use the bundled Maven Wrapper — see below)
- An IDE such as **Eclipse** or IntelliJ IDEA
- **Chrome**, **Firefox**, and **Edge** browsers installed on your machine

> This project uses the **Maven Wrapper** (`.mvn/`), so a separate Maven installation isn't strictly required — `mvnw` will download the correct Maven version automatically.

---

## Project Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/sunilb9767/Selenium_Java_BDD_EnterpriseLevel_Automation_Framework.git
   cd Selenium_Java_BDD_EnterpriseLevel_Automation_Framework
   ```

2. **Set up the configuration file:**

   This repo intentionally does **not** commit the real config file (to avoid leaking environment-specific values). Copy the template and fill in your own values:
   ```bash
   cp config.properties.template config.properties
   ```
   Update `config.properties` with your environment details, e.g.:
   ```properties
   baseUrl=https://practice.expandtesting.com
   browser=chrome
   ```

3. **Install dependencies:**
   ```bash
   mvn clean install
   ```

---

## Running the Tests

This framework supports two execution modes:

### 1. Local execution (single browser) — via TestRunner class
Run the JUnit/TestNG `TestRunner` class directly from your IDE, or via Maven:
```bash
mvn test
```

### 2. Cross-browser execution (parallel) — via `testng.xml`
Cross-browser and parallel runs are configured through `testng.xml`, which executes the suite across **Chrome**, **Firefox**, and **Edge**:
```bash
mvn test -DsuiteXmlFile=testng.xml
```

> Browser selection and thread/parallel settings can be adjusted inside `testng.xml`.

---

## Test Reports

After execution, detailed **Extent Reports** are generated, providing step-by-step results, pass/fail status, and execution timestamps for each scenario.

> Reports are generated per run and are not committed to source control (see `.gitignore`) — open the generated HTML report locally after each run to view results.

---

## Project Structure

```
Selenium_Java_BDD_EnterpriseLevel_Automation_Framework/
├── .mvn/
│   ├── jvm.config
│   └── maven.config
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/
│   │   │   │   └── BasePage.java
│   │   │   ├── config/
│   │   │   │   └── ConfigReader.java
│   │   │   ├── constants/
│   │   │   │   ├── FrameworkConstants.java
│   │   │   │   ├── MessageConstants.java
│   │   │   │   └── PageUrlConstants.java
│   │   │   ├── driver/
│   │   │   │   ├── BrowserContext.java
│   │   │   │   ├── DriverFactory.java
│   │   │   │   └── DriverManager.java
│   │   │   ├── pages/
│   │   │   │   ├── LoginPage.java
│   │   │   │   ├── RegisterPage.java
│   │   │   │   └── SecurePage.java
│   │   │   └── utils/
│   │   │       ├── ScreenshotUtil.java
│   │   │       └── WaitUtils.java
│   │   └── resources/
│   │       ├── config/
│   │       │   └── config.properties
│   │       ├── environments/
│   │       │   ├── prod.properties
│   │       │   ├── qa.properties
│   │       │   └── staging.properties
│   │       └── log4j2.xml
│   └── test/
│       ├── java/
│       │   ├── hooks/
│       │   │   └── Hooks.java
│       │   ├── runners/
│       │   │   ├── RerunRunner.java
│       │   │   └── TestRunner.java
│       │   └── stepdefs/
│       │       └── LoginSteps.java
│       └── resources/
│           ├── features/
│           │   └── login.feature
│           └── extent.properties
├── .gitignore
├── pom.xml
└── testng.xml
---

## Key Framework Features

- **Page Object Model (POM)** for maintainable, reusable page interactions
- **Cross-browser testing** across Chrome, Firefox, and Edge
- **Parallel execution** configured via `testng.xml`
- **BDD-style scenarios** using Gherkin syntax (Cucumber)
- **Extent Reports** for clear, detailed test execution reporting
- Designed for future **CI/CD integration**

---


## 👤 Author

**Sunil B**
GitHub: [@sunilb9767](https://github.com/sunilb9767)
