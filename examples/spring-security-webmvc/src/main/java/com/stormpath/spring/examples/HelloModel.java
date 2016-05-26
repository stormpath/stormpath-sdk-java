package com.stormpath.spring.examples;

public class HelloModel {
    private String greeting;
    private String message;

    public HelloModel(String greeting, String message) {
        this.greeting = greeting;
        this.message = message;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
