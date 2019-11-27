package utils;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.WebElementsCollection;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.cropper.indent.IndentCropper;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static base.Base.directory;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.yandex.qatools.ashot.cropper.indent.IndentFilerFactory.blur;

public class ScreenshotUtils {

    public static final Logger LOGGER = Logger.getLogger(ScreenshotUtils.class.getName());

    @Step("FullPage Screenshot")
    public static void takeScreenshotFullPage(String attachmentName) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            WebDriver webDriver = new Augmenter().augment(getWebDriver());
            WebDriver webDriver = getWebDriver();
            //Закрепление шапки для скриншотов
//            if(getWebDriver().getCurrentUrl().contains("webbank")){
//                ((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wocb-sticky-header')[0].style.position = 'absolute';");
//            }

            BufferedImage bufferedImage = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(webDriver)
                    .getImage();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            attachScreenshot(attachmentName, baos.toByteArray());
            ((JavascriptExecutor) getWebDriver()).executeScript("window.scrollTo(0,0);");
        } catch (RuntimeException ex) {
            LOGGER.severe("Невозможно сохранить скриншот: " + ex);
        }

    }



    public static void takeElementScreenshot(SelenideElement se, SelenideElement seIgnored, String elementFileName){
        String elementLocator = new GetLocator().getLocator(se);
        WebElement webElement = getWebDriver().findElement(By.xpath(elementLocator));
        String ignoredLocator = new GetLocator().getLocator(seIgnored);

        Screenshot screenshot = new AShot()
                .imageCropper(new IndentCropper()           // set custom cropper with indentation
                        .addIndentFilter(blur()))   // add filter for indented areas  //.addIgnoredElement(webElementIgnored)
                .addIgnoredElement(By.xpath(ignoredLocator))
                .coordsProvider(new MurinovCoordProvider())
                .takeScreenshot(getWebDriver(), webElement);
        saveScreenshot(elementFileName, screenshot);
        compareScreenshots(elementFileName, screenshot);
    }

    public static void takeElementScreenshot(SelenideElement se, String elementFileName){
        String elementLocator = new GetLocator().getLocator(se);
        WebElement webElement = getWebDriver().findElement(By.xpath(elementLocator));
        Screenshot screenshot = new AShot()
                .imageCropper(new IndentCropper()           // set custom cropper with indentation
                        .addIndentFilter(blur()))   // add filter for indented areas
                .coordsProvider(new MurinovCoordProvider())
                .takeScreenshot(getWebDriver(), webElement);
        saveScreenshot(elementFileName, screenshot);
        compareScreenshots(elementFileName, screenshot);
    }

    public static void takeElementsScreenshot(ElementsCollection ec, SelenideElement seIgnored,String elementFileName){
        String ignoredLocator = new GetLocator().getLocator(seIgnored);
        String collectionLocator = new GetLocator().getLocator(ec);
        List<WebElement> elements = getWebDriver().findElements(By.xpath(collectionLocator));
        Screenshot screenshot = new AShot()
                .imageCropper(new IndentCropper()           // set custom cropper with indentation
                        .addIndentFilter(blur()))   // add filter for indented areas
                .addIgnoredElement(By.xpath(ignoredLocator))
                .coordsProvider(new MurinovCoordProvider())
                .takeScreenshot(getWebDriver(), elements);
        saveScreenshot(elementFileName, screenshot);
        compareScreenshots(elementFileName, screenshot);
    }

    public static void takeElementsScreenshot(ElementsCollection ec, ElementsCollection ecIgnored,String elementFileName){
        String ignoredLocator = new GetLocator().getLocator(ecIgnored);
        String collectionLocator = new GetLocator().getLocator(ec);
        List<WebElement> elements = getWebDriver().findElements(By.xpath(collectionLocator));
        Screenshot screenshot = new AShot()
                .imageCropper(new IndentCropper()           // set custom cropper with indentation
                        .addIndentFilter(blur()))   // add filter for indented areas
                .addIgnoredElement(By.xpath(ignoredLocator))
                .coordsProvider(new MurinovCoordProvider())
                .takeScreenshot(getWebDriver(), elements);
        saveScreenshot(elementFileName, screenshot);
        compareScreenshots(elementFileName, screenshot);
    }

    public static void takeElementsScreenshot(SelenideElement se, ElementsCollection ecIgnored,String elementFileName){
        String ignoredLocator = new GetLocator().getLocator(ecIgnored);
        String collectionLocator = new GetLocator().getLocator(se);
        List<WebElement> elements = getWebDriver().findElements(By.xpath(collectionLocator));
        Screenshot screenshot = new AShot()
                .imageCropper(new IndentCropper()           // set custom cropper with indentation
                        .addIndentFilter(blur()))   // add filter for indented areas
                .addIgnoredElement(By.xpath(ignoredLocator))
                .coordsProvider(new MurinovCoordProvider())
                .takeScreenshot(getWebDriver(), elements);
        saveScreenshot(elementFileName, screenshot);
        compareScreenshots(elementFileName, screenshot);
    }

    public static void takeElementsScreenshot(ElementsCollection ec, String elementFileName){
        String collectionLocator = new GetLocator().getLocator(ec);
        List<WebElement> elements = getWebDriver().findElements(By.xpath(collectionLocator));
//        elements.add(webElement);

        Screenshot screenshot = new AShot()//.shootingStrategy(ShootingStrategies.simple())
                .imageCropper(new IndentCropper()          // set custom cropper with indentation
                        .addIndentFilter(blur()))   // add filter for indented areas
                .coordsProvider(new MurinovCoordProvider())
                .takeScreenshot(getWebDriver(), elements);
        saveScreenshot(elementFileName, screenshot);
//        Screenshot screenshot = new AShot()
//                .imageCropper(new IndentCropper()           // set custom cropper with indentation
//                        .addIndentFilter(blur()))   // add filter for indented areas
//                .coordsProvider(new WebDriverCoordsProvider())
//                .takeScreenshot(getWebDriver(), elements);
//        saveScreenshot(elementFileName, screenshot);


        compareScreenshots(elementFileName, screenshot);
    }

   static String resourcesImagesDir = directory+"\\src\\main\\resources\\screenshots\\";
   static String expectedDir = resourcesImagesDir+"\\expected\\";
   static String actualDir = resourcesImagesDir+"\\actual\\";
   static String diffDir = resourcesImagesDir+"\\diffImages\\";
   static String resultGifsDir = resourcesImagesDir+"\\gifs\\";


    private static void saveScreenshot(String elementFileName, Screenshot screenshot){
        File actualFile = new File(actualDir + elementFileName + ".png");
        try {
            ImageIO.write(screenshot.getImage(), "png", actualFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compareScreenshots(String elementFileName, Screenshot screenshotActual){
        File screenshotActualPath = new File(actualDir + elementFileName + ".png");
        File screenshotExpectedPath = new File(expectedDir + elementFileName + ".png");
        File screenshotDiffPath = new File(diffDir + elementFileName + ".png");
        File gifsDiffPath = new File(resultGifsDir + elementFileName + ".png");
        try {

            if (!screenshotExpectedPath.exists()){
                FileUtils.copyFile(screenshotActualPath, screenshotExpectedPath);
            }
            else {
                Screenshot expectedScreenshot = new Screenshot(ImageIO.read(screenshotExpectedPath));
                expectedScreenshot.setIgnoredAreas(screenshotActual.getIgnoredAreas());
                expectedScreenshot.setCoordsToCompare(screenshotActual.getCoordsToCompare());


                //ImageDiff diff = new ImageDiffer().makeDiff(expectedScreenshot, screenshotActual);
                ImageDiff diff = new ImageDiffer().makeDiff(expectedScreenshot, screenshotActual);
                BufferedImage diffImage = diff.getMarkedImage();

                ByteArrayOutputStream expectedBaos = new ByteArrayOutputStream();
                ImageIO.write(expectedScreenshot.getImage(), "png", expectedBaos);
                expectedBaos.flush();
                attachScreenshot("Ожидаемый результат", expectedBaos.toByteArray());

                ByteArrayOutputStream actualBaos = new ByteArrayOutputStream();
                ImageIO.write(screenshotActual.getImage(), "png", actualBaos);
                actualBaos.flush();
                attachScreenshot("Полученный результат", actualBaos.toByteArray());
                LOGGER.info("DIFF SIZE: "+diff.getDiffSize());
                if (diff.getDiffSize()> 35){
                    ImageIO.write(diffImage, "png", screenshotDiffPath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(diffImage, "png", baos);
                    baos.flush();
                    attachScreenshot("Разница", baos.toByteArray());

                }
                Assert.assertTrue(diff.getDiffSize()<=35, "Большая разница пикселей: " +diff.getDiffSize());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Attachment(value = "{name}", type = "image/png")
    public static byte[] attachScreenshot(String name, byte[] screenshot) {
        return screenshot;
    }
}
