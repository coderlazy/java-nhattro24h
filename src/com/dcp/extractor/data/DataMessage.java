/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

/**
 *
 * @author LazyCode
 */
public class DataMessage {

    private String ruleName;
    private String value;

    public DataMessage() {
    }

    public DataMessage(String ruleName, String value) {
        setRuleName(ruleName);
        setValue(ruleName);
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
