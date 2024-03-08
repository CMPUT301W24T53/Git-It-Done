package com.example.gidevents;

public class AttendeeProfileEditOption {
    private String optionType;
    private String currentvalue;

    public AttendeeProfileEditOption(String type, String value){
        if (value.isEmpty()){
            this.currentvalue = "None";
        }
        else{
            this.currentvalue = value;
        }
        this.optionType = type;
    }

    public String getCurrentvalue() {return this.currentvalue;}
    public String getOptionType() {return this.optionType;}
}
