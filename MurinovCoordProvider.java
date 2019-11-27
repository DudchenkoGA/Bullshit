package utils;

import org.openqa.selenium.*;
import ru.yandex.qatools.ashot.coordinates.Coords;
import ru.yandex.qatools.ashot.coordinates.CoordsProvider;

public class MurinovCoordProvider extends CoordsProvider {

    public MurinovCoordProvider(){
    }

    public Coords ofElement(WebDriver driver, WebElement element) {
//        Point point = element.getLocation();
        int elementX = getElementLocationXByJS(driver, element);
        int elementY = getElementLocationYByJS(driver, element);

        Dimension dimension = element.getSize();
        return new Coords(elementX,elementY, dimension.getWidth(), dimension.getHeight());
    }

    public static int getElementLocationXByJS(WebDriver wd, WebElement element) {
        String elementXStr = (String) ((JavascriptExecutor) wd).executeScript(
                "return arguments[0].getBoundingClientRect().left.toString();", element);
        elementXStr.replace(",", ".");
        if(elementXStr.contains(".")) {
            elementXStr = elementXStr.substring(0, elementXStr.indexOf("."));
        }
        int result = 0;
        try{
            result = Integer.parseInt(elementXStr);
        } catch (Exception e){
        }
        if(result != 0) {
            return result;
        } else {
            throw new IllegalStateException("Не удалось обнаружить левую границу элемента");
        }
    }



    public static int getElementLocationYByJS(WebDriver wd, WebElement element) {
        String elementXStr = (String) ((JavascriptExecutor) wd).executeScript(
                "return arguments[0].getBoundingClientRect().top.toString();", element);
        elementXStr.replace(",", ".");
        if(elementXStr.contains(".")) {
            elementXStr = elementXStr.substring(0, elementXStr.indexOf("."));
        }
        int result = 0;
        try{
            result = Integer.parseInt(elementXStr);
        } catch (Exception e){
        }
        if(result != 0) {
            return result;
        } else {
            throw new IllegalStateException("Не удалось обнаружить верхнюю границу элемента");
        }
    }

}
