package com.kerbybit.chattriggers.core.variables;

public class CTBoolean implements Variable {

    private Boolean value;

    public CTBoolean(Boolean value) {
        this.value = value;
    }

    public CTBoolean(String toParse) {
    	//TODO: PARSE TO VALUE
	}

    public Boolean get() {
        return value;
    }

    public void set(Boolean newVal) {
        value = newVal;
    }

    @Override
    public void call(String operation, String args) {
        switch (operation) {

        }
    }

    @Override
    public String getStringValue() {
        return value.toString();
    }
}
