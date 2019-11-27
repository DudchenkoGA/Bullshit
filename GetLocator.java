package utils;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openqa.selenium.WebElement;

public class GetLocator {
   public String getLocator(SelenideElement element) {
        try {
            Object proxyOrigin = FieldUtils.readField(element, "h", true);
            Object locator = FieldUtils.readField(proxyOrigin, "webElementSource", true);
            Object criteria = FieldUtils.readField(locator, "criteria", true);
            Object xpathExpression = FieldUtils.readField(criteria, "xpathExpression", true);
            if (xpathExpression != null) {
                return xpathExpression.toString();
            }
        } catch (IllegalAccessException ignored) {
        }
        return "[unknown]";
    }

    public String getLocator(ElementsCollection ec) {
        try {
            SelenideElement element =ec.get(0);
            Object h = FieldUtils.readField(element, "h", true);
            Object webElementSource = FieldUtils.readField(h, "webElementSource", true);
            Object collection = FieldUtils.readField(webElementSource, "collection", true);
            Object selector = FieldUtils.readField(collection, "selector", true);
            Object xpathExpression = FieldUtils.readField(selector, "xpathExpression", true);
            if (xpathExpression != null) {
                return xpathExpression.toString();
            }
        } catch (IllegalAccessException ignored) {
        }
        return "[unknown]";
    }
}
