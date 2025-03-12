package com.megacitycab.utils;
import com.twilio.Twilio;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.net.URI;
import java.math.BigDecimal;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


public class SmsUtil {

    public static final String ACCOUNT_SID = "AC4f9526f07018dee8b6cbf28eba32a132";
    public static final String AUTH_TOKEN = "[AuthToken]";
    public static final String TWILIO_PHONE_NUMBER = "+19133649341";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static boolean sendSms(String recipientPhoneNumber, String messageBody) {
        try {
            Message.creator(
                    new com.twilio.type.PhoneNumber(recipientPhoneNumber),
                    new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
                    messageBody
            ).create();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
