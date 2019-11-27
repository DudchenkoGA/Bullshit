package utils;

import com.codeborne.selenide.Condition;

public class Conditions {

    public static String getConditionName(Condition condition){
        String conditionName = null;

        if(condition.toString().contains("visible"))  conditionName = "видимость";
        else if(condition.toString().contains("exact text")) conditionName = condition.toString().replace("exact text", "соответствие тексту: ");
        else if(condition.toString().contains("exist")) conditionName = "существование";
        else if(condition.toString().contains("hidden")) conditionName = "скрытность";
        else if(condition.toString().contains("not empty")) conditionName = "заполнение";
        else if(condition.toString().contains("text")) conditionName = condition.toString().replace("text", "содержание текста: ");
        else if(condition.toString().contains("enable")) conditionName = "кликабельность";

        return conditionName;
    }


}
