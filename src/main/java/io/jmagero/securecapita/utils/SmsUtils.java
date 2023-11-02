package io.jmagero.securecapita.utils;

import com.twilio.Twilio;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;


public class SmsUtils {
    public static final String FROM_NUMBER = "+18559973074";
    public static final String SID_KEY = "AC85f5479ce05533179757d02900ca9bbe";
    public static final String TOKEN_KEY = "ad0c022bf676d4bd9b79b95f206a923d";

    public static void sendSMS(String to, String messageBody){
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }
}
