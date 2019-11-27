package actions;

import base.Base;
import com.codeborne.selenide.*;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import io.qameta.allure.Step;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import pages.login.LoginPage;
import utils.Conditions;

import java.util.ArrayList;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.jayway.restassured.RestAssured.given;
import static utils.ScreenshotUtils.takeElementScreenshot;
import static utils.ScreenshotUtils.takeElementsScreenshot;

public class Actions {

    LoginPage loginPage = new LoginPage();
    private static BrowserMobProxy proxy = new BrowserMobProxyServer();

    String buffer;

    /**
     * ввод value во все input и textArea элементы, найденные от переданного se
     */
    public Actions setValue(SelenideElement se, String value) {
        se.shouldBe(Condition.visible);
        setValue(se, se.getText(), value);
        return this;
    }

    /**
     * удаление символов из переданного инпута se
     */
    @Step("Удаление символов из инпута")
    public Actions clear(SelenideElement se) {
        se.shouldBe(Condition.visible);
        se.clear();
        return this;
    }

    @Step("Ввести значение \"{value}\" в поле \"{name}\"")
    private void setValue(SelenideElement se, String name, String value) {
        ElementsCollection ec = se.findAll("input");
        if(ec.isEmpty()) ec = se.findAll("textarea");
        for (SelenideElement e : ec) {
            if (e.isDisplayed()) {
                e.setValue(value);
                break;
            }
        }
    }

    /**
     * Простой клик на передаваемый элемент se.
     */
    public Actions click(SelenideElement se) {
        click(se, se.getText());
        return this;
    }

    /**
     * Клик на каждый элемент из колекции ec.
     * elementsName - имя элементов во множественном числе, например, "селектор".
     */
    @Step("Нажать на все \"{elementsName}\"")
    public Actions clickOnAll(ElementsCollection ec, String elementsName) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        for (SelenideElement se : ec) {
            se.shouldBe(Condition.visible).waitUntil(Condition.enabled, 5000).click();
            wait(1.3);
        }
        return this;
    }

    /**
     * Клик на передаваемый элемент se. Этот клик используется при нажатии на элементы без текста внутри.
     * Name - имя элемента, например, "селектор".
     */
    @Step("Нажать на \"{name}\"")
    public Actions click(SelenideElement se, String name) {
        se.shouldBe(Condition.visible).waitUntil(Condition.enabled, 5000).click();
        return this;
    }

    /**
     * обычное ожидание
     */
    @Step("Ожидание {time} секунд")
    public Actions wait(double time) {
        try {
            Thread.sleep((int) time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }


    /**
     *проверка одного переданного элемента на передаваемый кондишн
     */
    public Actions check(SelenideElement se, String elementName, Condition condition) {
        check(se, elementName, condition, Conditions.getConditionName(condition));
        return this;
    }

    /**
     * проверка урла вкладки под номером tab на содержание передаваемого значения expectedString, затем переключение на первую вкладку
     */
    @Step("Проверка текущего урла на содержание \"{expectedString}\" и переключение на первую страницу")
    public Actions checkUrlAndReturn(String expectedString, int tabToClose) {
        wait(15d);
        ArrayList<String> tabs = new ArrayList<String>(getWebDriver().getWindowHandles());
        Assert.assertTrue(getWebDriver().switchTo().window(tabs.get(tabToClose-1)).getCurrentUrl().contains(expectedString),
                "Адрес не соответствует ожидаемому: "+getWebDriver().getCurrentUrl()+"!");
        wait(1d);
        getWebDriver().switchTo().window(tabs.get(0));
        return this;
    }

    @Step("Проверка текущего урла на содержание \"{expectedString}\"")
    public Actions checkUrl(String expectedString) {
        wait(2d);
        Assert.assertTrue(getWebDriver().getCurrentUrl().contains(expectedString),
                "Адрес не соответствует ожидаемому: "+getWebDriver().getCurrentUrl()+"!");
        return this;
    }

    @Step("Проверка {elementName} на {conditionName}")
    private Actions check(SelenideElement se, String elementName, Condition condition, String conditionName) {
        se.shouldBe(condition);
        return this;
    }


    /**
     * проверка переданной коллекции элементов на передаваемый кондишн
     */
    public Actions check(ElementsCollection ec, String elementsName, Condition condition) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        check(ec, elementsName, condition, Conditions.getConditionName(condition));
        return this;
    }

    /**
     * по идее, это проверка колекции ec на наличие элементов, с передаваемым состоянием condition, в количестве expectedCount
     */
    public Actions counterCheck(ElementsCollection ec, Condition condition, int expectedCount) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        int i=0;
        for (SelenideElement se : ec) {
           if (se.is(condition)) i++;
          // if (i>expectedCount) break;
        }
        Assert.assertTrue(i==expectedCount,"Количество не соответствует ожидаемому");
        return this;
    }

    @Step("Проверка {elementsName} на {conditionName}")
    private Actions check(ElementsCollection ec, String elementsName, Condition condition, String conditionName) {
        for (SelenideElement se : ec) {
            se.shouldBe(condition);
        }
        return this;
    }

    /**
     * проверка, что инпут se заполнен
     * Name - имя жлемента, например, поле номера телефона
     */
    @Step("Проверка элемента {name} на заполнение")
    public Actions checkFill(SelenideElement se, String name) {
        se.shouldBe(Condition.visible);
        Assert.assertFalse(se.is(Condition.empty),"Элемент не заполнен");
        return this;
    }

    @Step("Проверка элементов {name} на заполнение")
    public Actions checkFill(ElementsCollection ec, String name) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        for (SelenideElement se : ec) {
            Assert.assertFalse(se.is(Condition.empty),"Элемент не заполнен");
        }
        return this;
    }


    /**
     * Наведение мыши на se.
     * Name - имя элемента, например, кнопка развертки виджета
     */
    @Step("Наведение мыши на {name}")
    public Actions moveMouseTo(SelenideElement se, String name) {
        se.shouldBe(Condition.visible).hover();
        return this;
    }



    /**
     *проверка передаваемого атрибута (attributeName) элемента (se) из DOM, на содержание передаваемого значения (expectedValue)
     */
    @Step("Проверка атрибута {attributeName} на содержание \"{expectedValue}\"")
    public Actions checkAttribute(SelenideElement se, String attributeName, String expectedValue) {
        Assert.assertTrue(se.getAttribute(attributeName).contains(expectedValue), "Аргумент не соответствует ожидаемому");
        return this;
    }

    /**
     * клик на элемент, содержащий передаваемый текст
     */
    @Step("Клик на \"{value}\"")
    public Actions clickOn(ElementsCollection ec, String value) {

        ec.find(Condition.text(valueReplacement(value))).shouldBe(Condition.visible)
                .waitUntil(Condition.enabled, 5000).click();
        return this;
    }

    /**
     * клик на элемент из колекции ес с порядковым номером index
     */
    @Step("Клик на \"{index}\"-й по счету")
    public Actions clickOn(ElementsCollection ec, Integer index) {
        ec.get(index-1).waitUntil(Condition.enabled, 5000).click();
        return this;
    }

    /**
     * клик на последний элемент из колекции ес, содержащий value
     */
    @Step("Клик на последний по счету элемент коллекции, содержащий {value} ")
    public Actions clickOnLast(ElementsCollection ec, String value) {
        ec = ec.filterBy(Condition.text(value));
        ec.last().waitUntil(Condition.enabled, 5000).click();
        return this;
    }

    /**
     * клик на последний элемент из колекции ес
     */
    @Step("Клик на последний по счету элемент коллекции")
    public Actions clickOnLast(ElementsCollection ec) {
        ec.last().waitUntil(Condition.enabled, 5000).click();
        return this;
    }


    @Step("Прокрутка до \"{name}\"")
    public Actions scrollTo(SelenideElement se, String name) {
        se.scrollTo();
        return this;
    }
    /**
     * Клик на элемент из ec, содержащий( или не содержащий) value, с порядковым номером index.
     * Если элемент не должен содержать какое-то значение, то следует изменить value.
     * Например, value для не кредитной карты долже выглядеть так: "не кредитная".
     */
    @Step("Выбираем {index} по порядку продукт c \"{value}\"")
    public Actions productSelection(ElementsCollection ec, String value, int index) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (value.substring(0, 2).equals("не")) {
            value = value.substring(3);
            for (SelenideElement se : ec) {
                //se.shouldBe(Condition.exist);
                if (!se.getText().contains(valueReplacement(value))) {

                    if (index == 1) {
                        se.waitUntil(Condition.enabled, 5000).click();
                        break;
                    }

                    index--;
                }

            }
        } else {
            for (SelenideElement se : ec) {
                //se.shouldBe(Condition.exist);
                if (se.getText().contains(valueReplacement(value))) {

                    if (index == 1) {
                        se.waitUntil(Condition.enabled, 5000).click();
                        break;
                    }

                    index--;
                }

            }
        }
        return this;
    }

    /**
     * Клик на элемент из ec, содержащий( или не содержащий) firstValue, содержащий secondValue, с порядковым номером index.
     * Если элемент не должен содержать какое-то значение, то следует изменить firstValue.
     * Например, firstValue для не кредитной карты долже выглядеть так: "не кредитная". Такой способ работает только с firstValue
     */
    @Step("Выбираем {index}-й по порядку продукт c \"{firstValue}\" и с \"{secondValue}\"")
    public Actions productSelection(ElementsCollection ec, String firstValue, String secondValue, int index) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (firstValue.substring(0, 2).equals("не")) {
            firstValue = firstValue.substring(3);
                for (SelenideElement se : ec) {
               // se.shouldBe(Condition.exist);
                if (!se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue))) {

                    if (index == 1) {
                        se.waitUntil(Condition.enabled, 5000).click();
                        break;
                    }

                    index--;
                }

            }
        } else {
            for (SelenideElement se : ec) {
               // se.shouldBe(Condition.exist);
                if (se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue))) {

                    if (index == 1) {
                        se.waitUntil(Condition.enabled, 5000).click();
                        break;
                    }

                    index--;
                }

            }
        }
        return this;
    }

    /**
     *  для удобства. Меняет код валюты на символ. Например RUR = ₽
     */
    private String valueReplacement(String value) {

        switch (value) {
            case "RUR":
                value = "₽";
                break;
            case "USD":
                value = "$";
                break;
            case "EUR":
                value = "€";
                break;
           // default: value =value;
        }
        return value;
    }


    @Step("обновляем страницу")
    public Actions refresh() {
        Selenide.open(getWebDriver().getCurrentUrl());
        return this;
    }




//    @Step("Запуск прокси для глушение запроса {url}")
//    public Actions blockReq(String url) {
//        proxy.start(0);
//        proxy.blacklistRequests("http://.*\\.fbcdn.net/.*", 500);
//        proxy.blacklistRequests(url, 500);
//        return this;
//    }
//
//    @Step("Запуск прокси для глушение запроса {url}")
//    public Actions stopProxy(String url) {
//        proxy.stop();
//        return this;
//    }

    /**
     *
     * @param se  элемент, который скриншотим
     * @param elementFileName имя скриншота. Оно кодируется, например, "divValidationP2PTooltipC2СWithoutExpireDate" -
     *                        это скриншот, валидационного(Validation) тултипа(Tooltip) в функционале переводов с карты на карту(Р2Р)
     *                        с типом перевода чужая-чужая(C2С) с не заполненным(Without) сроком действия(ExpireDate) карты списания
     * @param elementName имя самого элемента для аллюра. Например, поля срока действия карты с тултипом
     */
    @Step("Проверка валидации {elementName} по скриншоту")
    public Actions checkElementWithScreenshot(SelenideElement se, String elementFileName, String elementName) {
        takeElementScreenshot(se, elementFileName);
        return this;
    }

    /**
     *
     * @param se элемент, который скриншотим
     * @param seIgnored элемент, который игнорим
     * @param elementFileName имя скриншота. Оно кодируется, например, "divValidationP2PTooltipC2СWithoutExpireDate" -
     *                        это скриншот, валидационного(Validation) тултипа(Tooltip) в функционале переводов с карты на карту(Р2Р)
     *                        с типом перевода чужая-чужая(C2С) с не заполненным(Without) сроком действия(ExpireDate) карты списания
     * @param elementName имя самого элемента для аллюра. Например, поля срока действия карты с тултипом
     */
    @Step("Проверка элемента {elementName} по скриншоту")
    public Actions checkElementWithScreenshot(SelenideElement se, SelenideElement seIgnored, String elementFileName, String elementName) {
        takeElementScreenshot(se, seIgnored,elementFileName);
        return this;
    }

    /**
     * @param ec коллекция элементов, которые скриншотим
     * @param elementsFileName имя скриншота. Оно кодируется, например, "divValidationP2PTooltipC2СWithoutExpireDate" -
     *                         это скриншот, валидационного(Validation) тултипа(Tooltip) в функционале переводов с карты на карту(Р2Р)
     *                         с типом перевода чужая-чужая(C2С) с не заполненным(Without) сроком действия(ExpireDate) карты списания
     * @param elementsName имя самого элемента для аллюра. Например, поля срока действия карты с тултипом
     */
    @Step("Проверка валидации {elementsName} по скриншоту")
    public Actions checkElementsWithScreenshot(ElementsCollection ec, String elementsFileName, String elementsName) {
        takeElementsScreenshot(ec, elementsFileName);
        return this;
    }

    /**
     *
     * @param ec коллекция элементов, которые скриншотим
     * @param seIgnored элемент, который игнорим при сравнении скриншотов. Например, введеный текст в инпут и т.п.
     * @param elementsFileName имя скриншота. Оно кодируется, например, "divValidationP2PTooltipC2СWithoutExpireDate" -
     *                         это скриншот, валидационного(Validation) тултипа(Tooltip) в функционале переводов с карты на карту(Р2Р)
     *                         с типом перевода чужая-чужая(C2С) с не заполненным(Without) сроком действия(ExpireDate) карты списания
     * @param elementsName имя самого элемента для аллюра. Например, поля срока действия карты с тултипом
     */
    @Step("Проверка валидации {elementsName} по скриншоту")
    public Actions checkElementsWithScreenshot(ElementsCollection ec, SelenideElement seIgnored, String elementsFileName, String elementsName) {
        takeElementsScreenshot(ec, seIgnored, elementsFileName);
        return this;
    }

    /**
     *
     * @param ec коллекция элементов, которые скриншотим
     * @param ecIgnored коллекция элементов, которые игнорим при сравнении скриншотов.
     * @param elementsFileName имя скриншота. Оно кодируется, например, "divValidationP2PTooltipC2СWithoutExpireDate" -
     *                         это скриншот, валидационного(Validation) тултипа(Tooltip) в функционале переводов с карты на карту(Р2Р)
     *                         с типом перевода чужая-чужая(C2С) с не заполненным(Without) сроком действия(ExpireDate) карты списания
     * @param elementsName имя самого элемента для аллюра. Например, поля срока действия карты с тултипом
     */
    @Step("Проверка валидации {elementsName} по скриншоту")
    public Actions checkElementsWithScreenshot(ElementsCollection ec,ElementsCollection ecIgnored, String elementsFileName, String elementsName) {
        takeElementsScreenshot(ec, ecIgnored, elementsFileName);
        return this;
    }

    /**
     * открывает страницу по передаваемому урлу
     * @param url
     */
    @Step("Открыть браузер на странице: {url}")
    private void openPage(String url) {
        Selenide.open(url);
    }

    /**
     * этот экшн чисто для ввода смс-ки. 5 раз пробует ввести смс-ку, пока не получится. Перед использованием поисследуй как он работет на примере готового теста
     * @param otpField элемент(инпут), в который записывается значение 1234
     * @param check элемент, который появляется в результате ошибки смс. Бывает, что 1234 считается протухшей смс-кой -_-
     * @param sendAgain элемент кнопка "отправить еще раз"
     */
    public Actions inputOtp(SelenideElement otpField, SelenideElement check, SelenideElement sendAgain) {
        check(otpField, "поля ввода смс", Condition.visible);
        wait(3d);
        setValue(otpField,"1234");
        int i =0;
        do {
            wait(2d);
            if (check.is(Condition.exist)){
                //click(sendAgain);
                sendAgain.waitUntil(Condition.enabled, 5000).click();
                wait(1d);
                setValue(otpField,"1234");
                i++;
            } else break;
        }while (i<5);
        return this;
    }

    @Step("unbind c clientId: {clientId}")
    public Actions unbind(String clientId) {

        RequestSpecification request = RestAssured.given();
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.header("Authorization", "Bearer sso_1.0_"+systemToken());


        Response response = request.post("http://whitewalkers-l.open.ru:20038/investments-summary/client/unbind?clientId="+clientId);
      //  System.out.println(response.getBody().asString());
        Boolean st = response.jsonPath().get("success");
     //   System.out.println(st);
        Assert.assertTrue(response.jsonPath().get("success").equals(true));

         return  this;
    }

    @Step("Получение системного токена")
    private String systemToken() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/x-www-form-urlencoded");
        Response response = request.relaxedHTTPSValidation().post("http://rumskapp320.open.ru/sso/oauth2/access_token?client_secret=password&grant_type=client_credentials&realm=customer&client_id=partners");
      //  System.out.println(response.getBody().asString());
        return response.jsonPath().get("access_token");

    }

    /**
     *
     * @param ec коллекция, по кторой производится поиск элемента
     * @param value искомое значение
     */
    @Step("Поиск нужного элемента")
    public Actions find(ElementsCollection ec, String value) {

        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        int i=0;
        for (SelenideElement se : ec) {

            if(se.getText().contains(valueReplacement(value))){i++; break;}

        }

        Assert.assertTrue(i==1,"Не нашел :с");
        return this;
    }

    /**
     * Происходит поиск элемента в первой коллекции и производится клик на элемент второй коллекции с тем же индексом.
     * Этот метод используется в случае, когда возникает сложность с кликом на дочерний элемент или когда элемент не является дочерним.
     * Применим лишь в случае, когда обе коллекции одинакового размера.
     * @param ec коллекция, по кторой производится поиск элемента
     * @param value значение, по которому отбирается нужный элемент
     * @param ec2 коллекция, по кторой производится клик на элемент с нужным индексом
     * @param count количество кликов. Например, нужно 2, 3, 10 таких кликов
     */
    @Step("Умный клик")
    public Actions smartClick(ElementsCollection ec, String value, ElementsCollection ec2, int count) {
       //ищем по первой колекции, кликаем по второй
        int i=0,j=0;
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (value.substring(0, 2).equals("не")) {
            value = value.substring(3);
            for (SelenideElement se : ec) {
                i++;
                if(j>=count) break;
                if(!se.getText().contains(valueReplacement(value)))
                { clickOn(ec2, i); j++;}
            }
        }
        else{
              for (SelenideElement se : ec) {
              i++;
              if(j>=count) break;
              if(se.getText().contains(valueReplacement(value)))
                    { clickOn(ec2, i); j++;}

              }
        }

        return this;
    }

    /**
     * Перегрузка смартклика. Поиск производится по двум значениям. Первое может быть с "не "
     */
    @Step("Умный клик")
    public Actions smartClick(ElementsCollection ec, String firstValue,String secondValue, ElementsCollection ec2, int count) {
       //ищем по первой колекции, кликаем по второй
        int i=0,j=0;
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (firstValue.substring(0, 2).equals("не")) {
            firstValue = firstValue.substring(3);
            for (SelenideElement se : ec) {
                i++;
                if(j>=count) break;
                if(!se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue)))
                { clickOn(ec2, i); j++;}
            }
        }
        else{
              for (SelenideElement se : ec) {
              i++;
              if(j>=count) break;
              if(se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue)))
                    { clickOn(ec2, i); j++;}

              }
        }

        return this;
    }


    @Step("клик на внутренний элемент")
    public Actions clickInside(ElementsCollection ec, String firstValue, String secondValue, String text) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (firstValue.substring(0, 2).equals("не")) {
            firstValue = firstValue.substring(3);

            for (SelenideElement se : ec) {

                if(!se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue)))
                {
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                    }

            }
        }else{
            for (SelenideElement se : ec) {

                if(se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue)))
                {
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                }

            }
        }

        return this;
    }

    @Step("клик на внутренний элемент")
    public Actions clickInside(ElementsCollection ec, String value, String text) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (value.substring(0, 2).equals("не")) {
            value = value.substring(3);

            for (SelenideElement se : ec) {

                if(!se.getText().contains(valueReplacement(value)))
                {
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                    }

            }
        }else{
            for (SelenideElement se : ec) {

                if(se.getText().contains(valueReplacement(value)))
                {
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                }

            }
        }

        return this;
    }

    @Step("клик на внутренний элемент")
    public Actions smartClickInside(ElementsCollection ec, String value, ElementsCollection ec2, int expValue, String btnText) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        int i=0;
        if (value.substring(0, 2).equals("не")) {
            value = value.substring(3);

            for (SelenideElement se : ec) {

                if(!se.getText().contains(valueReplacement(value)))
                {
                    if (Integer.parseInt(ec2.get(i).getText())>=expValue){
                        se.$(new Selectors.ByText(btnText)).waitUntil(Condition.enabled, 5000).click();
                        return this;
                    }
                }
                i++;
            }
        }else{
            for (SelenideElement se : ec) {

                if(se.getText().contains(valueReplacement(value)))
                {
                    if (Integer.parseInt(ec2.get(i).getText())>=expValue){
                        se.$(new Selectors.ByText(btnText)).waitUntil(Condition.enabled, 5000).click();
                        return this;
                    }
                }
                i++;
            }
        }

        return this;
    }

    /**
     *Перегрузка clickAndRememberInnerValue, где поиск идет по двум значениям. Смотри описание clickAndRememberInnerValue, а то мне лень)
     */
    @Step("клик на внутренний элемент c записью в {storageName} из localStorage")
    public Actions clickAndRememberInnerValue(ElementsCollection ec, String firstValue, String secondValue, String text,String soughtValue, String storageName) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (firstValue.substring(0, 2).equals("не")) {
            firstValue = firstValue.substring(3);

            for (SelenideElement se : ec) {

                if(!se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue)))
                {
                   // ((JavascriptExecutor) getWebDriver()).executeScript("localStorage.clear();");
                    ((JavascriptExecutor) getWebDriver()).executeScript(
                            "localStorage.setItem('"+storageName+"', '"+se.$(By.xpath(soughtValue)).getText()+"');");
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                    }

            }
        }else{
            for (SelenideElement se : ec) {

                if(se.getText().contains(valueReplacement(firstValue)) && se.getText().contains(valueReplacement(secondValue)))
                {
                   // ((JavascriptExecutor) getWebDriver()).executeScript("localStorage.clear();");
                    ((JavascriptExecutor) getWebDriver()).executeScript(
                            "localStorage.setItem('"+storageName+"', '"+se.$(By.xpath(soughtValue)).getText()+"');");
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                }

            }
        }

        return this;
    }

    /**
     * Находим элемент из первой коллекции по value, затем ищем от него дочерний, значение которого нужно запомнить, после кликаем на другой дочерний
     * @param ec коллекция, по кторой производится поиск элемента
     * @param value значение, по которому отбирается нужный элемент из ec. Может начинаться с "не "
     * @param text текст элемента, по которому производится клик после записи
     * @param soughtValue путь, по которому ищется дочерний элемент. Его значение записывается в localStorage
     * @param storageName называние элемента localStorage
     */
    @Step("клик на внутренний элемент c записью значения в {storageName} из localStorage")
    public Actions clickAndRememberInnerValue(ElementsCollection ec, String value, String text, String soughtValue, String storageName) {
        ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
        if (value.substring(0, 2).equals("не")) {
            value = value.substring(3);

            for (SelenideElement se : ec) {

                if(!se.getText().contains(valueReplacement(value)) )
                {
                   // ((JavascriptExecutor) getWebDriver()).executeScript("localStorage.clear();");
                    ((JavascriptExecutor) getWebDriver()).executeScript(
                            "localStorage.setItem('"+storageName+"', '"+se.$(By.xpath(soughtValue)).getText()+"');");
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                }

            }
        }else{
            for (SelenideElement se : ec) {

                if(se.getText().contains(valueReplacement(value)))
                {
                    //((JavascriptExecutor) getWebDriver()).executeScript("localStorage.clear();");
                    ((JavascriptExecutor) getWebDriver()).executeScript(
                            "localStorage.setItem('"+storageName+"', '"+se.$(By.xpath(soughtValue)).getText()+"');");
                    se.$(new Selectors.ByText(text)).waitUntil(Condition.enabled, 5000).click();
                    return this;
                }

            }
        }

        return this;
    }

    /**
     * Просматривает коллекцию ес и ищет элемент с value. Пока не найдет, кликает на следующую страницу. Кликает на первый найденный.
     * @param ec коллекция, по кторой производится поиск элемента
     * @param value значение, по которому отбирается нужный элемент из ec. Не может начинаться с "не "!
     * @param nextPage кнопка следующей страницы
     */
    @Step("постраничник")
    public Actions clickWhile(ElementsCollection ec, String value, SelenideElement nextPage) {
        boolean found = false;
        while (!found){
            ec.shouldBe(CollectionCondition.sizeGreaterThan(0));
            for (SelenideElement se : ec) {
                if(se.getText().contains(valueReplacement(value)))
                    {
                        //click(se);
                        se.waitUntil(Condition.enabled, 5000).click();
                        found =true;
                        break;
                    }

            }

            if (!found) {
                //click(nextPage);
                nextPage.waitUntil(Condition.enabled, 5000).click();
            } else {
                break;
            }
        }

        return this;
    }

    /**
     * Сделан для предодобов. Там проверяется количество определенных предодобов
     * @param se сам элемент, в нем идет поиск value
     * @param nextPage кнопка, которая переключает на следующий предодоб
     */
    @Step("постраничник")
    public Actions clickWhile(SelenideElement se, String value, SelenideElement nextPage, int count) {
        boolean found = false;
        for (int counter=1; counter<=count; counter++){
            if(se.getText().contains(valueReplacement(value)))
                {
                    found =true;
                    return this;
                }
            if (!found) {
                nextPage.waitUntil(Condition.enabled, 5000).click();
                //click(nextPage);

            }

        }

    return this;
    }

    /**
     * Проверка элемента на нужное значение
     * @param se элемент, значение которого сверяется с записью в localStorage
     * @param elementName название элемента для аллюра
     * @param storageName название элемента localStorage, со значением которого идет сравнение
     */
    @Step("проверка значения из {storageName} в localStorage")
    public Actions storageCheck(SelenideElement se, String elementName, String storageName) {

        check(se, elementName, Condition.text(
                ((JavascriptExecutor) getWebDriver()).executeScript(
                        "return localStorage.getItem('"+storageName+"');").toString()
        ));

        return this;
    }

    /**
     * Запись значения элемента в localStorage
     * @param se элемент, значение которого записывается в localStorage
     * @param elementName название элемента для аллюра
     * @param storageName название элемента localStorage, в который производится запись
     */
    @Step("запись значения {elementName} в localStorage")
    public Actions rememberValue(SelenideElement se, String elementName, String storageName) {

        ((JavascriptExecutor) getWebDriver()).executeScript(
                "localStorage.setItem('"+storageName+"', '"+se.getText()+"');");

        return this;
    }

    /**
     * Возвращение значения из определенного элемента localStorage.
     * Можно подставлять в условие поиска по продукту. Можно комбинировать:  "не "+ACTIONS.returnValue("{storageName}")
     * @param storageName название элемента localStorage
     */
    public String returnValue(String storageName) {
        return ((JavascriptExecutor) getWebDriver()).executeScript(
                "return localStorage.getItem('"+storageName+"');").toString();
    }











    @Step("Логинимся пользователем: {username}")
    public Actions login(String username) {
//        System.setProperty("Mock","true"); if(Boolean.valueOf(System.getProperty("Mock"))) mockLogin(); //если раскомментировать эту строку, то будут использоваться моки при локальном запуске
        if(Boolean.valueOf(System.getenv("Mock"))) mockLogin();
        else{
        openPage(Base.url);
        if (!getWebDriver().getCurrentUrl().contains("login")) {
            openPage(Base.url);
        }
        check(loginPage.aSocial, "ссылки альтернативного входа", Condition.exactText("или через социальные сети"))
                .check(loginPage.aRegistration, "ссылки регистрации", Condition.exactText("Регистрация"))
                .check(loginPage.aNeedPass, "ссылки восстановления пароля", Condition.exactText("Нужен пароль?"))
                .setValue(loginPage.divUsername, username)
                .setValue(loginPage.divPassword, "123").click(loginPage.btnLogin).wait(4d);

        checkBlock(username);

        if (getWebDriver().getCurrentUrl().contains("login")) {
            setValue(loginPage.divOtp, "1234").wait(5d);
        }
       int i=0;
        while (getWebDriver().getCurrentUrl().contains("login")){

            checkBlock(username);

            loginPage.aNewOtp.waitUntil(Condition.enabled, 9000);
            click(loginPage.aNewOtp, "Отправить еще раз").wait(2d).setValue(loginPage.divOtp, "1234").wait(5d);

            checkBlock(username);

            i++;
            if (i>9) throw new AssertionError("UIDM SUKA");
        }

        check(loginPage.aMain, "главное меню", Condition.visible);
        openPage(Base.urlMock);
        check(loginPage.aMain, "главное меню", Condition.visible);
        ((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wocb-sticky-header')[0].style.position = 'absolute';");
        if(loginPage.divMoneySwitcher.exists()){((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wb-currency-switcher')[0].style.visibility = 'hidden';");}
        if(loginPage.notification.exists()){((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wocb-notifications wocb-notifications_visible')[0].style.position = 'absolute';");}}
        return this;
    }


    @Step("Логинимься пользователем: {username}")
    public Actions negativeLogin(String username) {
        openPage(Base.url);
        setValue(loginPage.divUsername, username).setValue(loginPage.divPassword, "123")
                .click(loginPage.btnLogin, "Войти");
        return this;
    }


    /**
     * Запись рандомного at, что позволяет очень быстро логиниться на моках. Работает только на моках!!!
     */
    @Step("Логинимся на моки")
    private Actions mockLogin() {
        openPage(Base.url);
        Cookie at = new Cookie("at", "eyAiY3R5IjogIMrBFg2870gfAB7EPqNNQCO9jd1iviK3VCF", ".open.ru", "/", null);
        getWebDriver().manage().addCookie(at);
        openPage(Base.urlMock);
        ((JavascriptExecutor) getWebDriver()).executeScript("localStorage.setItem('hiddenSpecialOffers', '\"1130704854\":true,\"1130704855\":true');");
        openPage(Base.urlMock);
        check(loginPage.aMain, "главное меню", Condition.visible);
        ((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wocb-sticky-header')[0].style.position = 'absolute';");
        //((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wocb-notifications')[0].style.position = 'absolute';");
        //((JavascriptExecutor) getWebDriver()).executeScript("document.getElementsByClassName('wb-currency-switcher')[0].style.visibility = 'hidden';");
        return this;
    }

    private Actions checkBlock(String username) {
        if(loginPage.divBlockMsg.has(Condition.visible)){
            given().get("http://whitewalkers.open.ru/sso/unblock");
            openPage(Base.url);
            setValue(loginPage.divUsername, username)
                .setValue(loginPage.divPassword, "123").click(loginPage.btnLogin).wait(4d);
        }
        return this;
    }
}
