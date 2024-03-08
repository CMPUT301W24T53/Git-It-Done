package com.example.gidevents;

/**
 * Class for each option an attendee profile can have
 */
public class AttendeeProfileEditOption {
    private String optionType;
    private String currentvalue;

    /**
     * contructor assignes both the option type and its value
     * @param type type of option
     * @param value value of option
     */
    public AttendeeProfileEditOption(String type, String value){
        if (value.isEmpty()){
            this.currentvalue = "None";
        }
        else{
            this.currentvalue = value;
        }
        this.optionType = type;
    }

    /**
     * Returns value of this option
     * @return value
     */
    public String getCurrentvalue() {return this.currentvalue;}

    /**
     * Returns the type of this option
     * @return type
     */
    public String getOptionType() {return this.optionType;}
}
