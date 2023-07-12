package com.example.demo;

import java.io.Serializable;

public class Move implements Serializable {
    private String buttonId;
    private String symbol;

    public Move(String buttonId, String symbol) {
        this.buttonId = buttonId;
        this.symbol = symbol;
    }

    public String getButtonId() {
        return buttonId;
    }

    public String getSymbol() {
        return symbol;
    }
}
