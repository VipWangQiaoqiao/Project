package net.oschina.app.improve.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpEventOptions implements Serializable {
    public static final int FORM_TYPE_TEXT = 0;
    public static final int FORM_TYPE_TEXT_AREA = 1;
    public static final int FORM_TYPE_SELECT = 2;
    public static final int FORM_TYPE_CHECK_BOX = 3;
    public static final int FORM_TYPE_RADIO = 4;
    public static final int FORM_TYPE_EMAIL = 5;
    public static final int FORM_TYPE_DATE = 6;
    public static final int FORM_TYPE_MOBILE = 7;
    public static final int FORM_TYPE_NUMBER = 8;
    public static final int FORM_TYPE_URL = 9;

    private String key;
    private String value;//用户输入的参数
    private List<String> selectList;//用户多选的参数
    private int keyType;
    private int formType;
    private String label;
    private String option;
    private String optionStatus;
    private String defaultValue;
    private boolean required;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public List<String> getSelectList() {
        return selectList;
    }

    public void setSelectList(List<String> selectList) {
        this.selectList = selectList;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public int getFormType() {
        return formType;
    }

    public void setFormType(int formType) {
        this.formType = formType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOptionStatus() {
        return optionStatus;
    }

    public void setOptionStatus(String optionStatus) {
        this.optionStatus = optionStatus;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
