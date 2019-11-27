package base;

import actions.Actions;
import com.codeborne.selenide.Configuration;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import dp.GetUsers;
import org.apache.commons.io.FileUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.header.HeaderBlock;
import pages.history.HistoryPage;
import pages.investments.InvestmentPage;
import pages.login.LoginPage;
import pages.payments.PaymentsPage;
import pages.products.*;
import pages.profile.ProfilePage;
import pages.transfers.TransferPage;
import testConfig.TestConfig;
import testListeners.TestListeners;
import utils.ScreenshotUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.given;


/**
 * Created by Rogov_AVL on 07.03.2017.
 */
@Listeners(TestListeners.class)
public class Base {

    public static final Logger LOGGER = Logger.getLogger("TEST LOGGER");
    public ScreenshotUtils screenshotUtils = new ScreenshotUtils();
    public static String svetogor = "svetogor";
    public static String user = "kneradovsky";
    public static String andytensinUK = "614";
    public static String nepomnyaUK = "681319";
    public static String nickkvasovBroker = "414602";
    public static String url;
    public static String urlMock;
    public static String directory = new File("").getAbsolutePath();
    public static String at;

    protected final LoginPage LOGIN_PAGE  = new LoginPage();
    protected final BeltCards BELT_CARDS = new BeltCards();
    protected final BeltOffers BELT_OFFERS =  new BeltOffers();
    protected final PaymentsPage PAYMENTS_PAGE = new PaymentsPage();
    protected final InvestmentPage INVESTMENTS_PAGE = new InvestmentPage();
    protected final TransferPage TRANSFER_PAGE = new TransferPage();
    protected final BeltAccounts BELT_ACCOUNTS = new BeltAccounts();
    protected final BeltDeposits BELT_DEPOSITS = new BeltDeposits();
    protected final BeltCredits BELT_CREDITS = new BeltCredits();
    protected final HeaderBlock HEADER_BLOCK = new HeaderBlock();
    protected final ProfilePage PROFILE_PAGE = new ProfilePage();
    protected final HistoryPage HISTORY_PAGE = new HistoryPage();
    protected Actions ACTIONS;


    @BeforeSuite(description = "Установка тестовой конфигурации")
    public void setConfiguration() throws IOException {
        TestConfig testConfiguration = new TestConfig();
        url = testConfiguration.getUrl();
        urlMock = testConfiguration.getUrlMock();
        Configuration.browser = "testConfig.SelenoidWebDriverProvider";
        Configuration.timeout = 63000;
        Configuration.collectionsTimeout = 63000;
        Configuration.browserSize = "1920x1080";

        given().get("http://whitewalkers.open.ru/sso/unblock");

//        Configuration.browser = "chrome"; whitewalkers.open.ru/sso/unblock
//        Configuration.remote = "http://rumskapt225:4419/wd/hub";
    }

    @BeforeMethod(description = "Вывод сообщения о начале теста", alwaysRun = true)
    public void initSteps(Method method) {
//        Configuration.browser = "testConfig.SelenoidWebDriverProvider";
//        Configuration.timeout = 63000;
//        Configuration.browserSize = "1920x1080";
        ACTIONS = new Actions();
//        if(System.getenv("Url").contains("whitewalkers"))given().get("http://whitewalkers.open.ru/sso/unblock");
        LOGGER.info("Test \"" + method.getName() + "\" STARTED");
    }


    @AfterMethod(description = "Вывод результата теста")
    public void finishSteps(Method method, ITestResult iTestResult) {
        switch (iTestResult.getStatus()) {
            case 1:
                LOGGER.info("Test \"" + method.getName() + "\" SUCCESS");
                break;
            case 2:
                LOGGER.info("Test \"" + method.getName() + "\" FAILED");
                break;
        }
    }
    @AfterSuite(description = "Информация о тестовом окружении")
    public void environmentInfo(){
        File source = new File(directory + "\\src\\main\\resources\\environment.properties");
        File dest = new File(directory + "\\target\\allure-results\\environment.properties");
       // File dest = new File(directory + "\\build\\allure-results\\environment.properties");
        try {
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public static void setAt(String at) {
        Base.at = at;
    }
}

