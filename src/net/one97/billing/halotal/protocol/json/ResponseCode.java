package net.one97.billing.halotal.protocol.json;

import java.util.HashMap;
import java.util.Map;

public enum ResponseCode {
	
	RESPONSE1("0", "Transaction successful"),RESPONSE2("1", "The phone number not found"),RESPONSE3("4", "The client’s IP is not in the IP Pools"),RESPONSE4("11", "Missing parameter"),
	RESPONSE5("13", "Missing amount field"),RESPONSE6("14", "Missing cp_request_id field"),RESPONSE7("15", "Missing value field"),RESPONSE8("16", "Missing AES key"),
	RESPONSE9("17", "Missing name item"),RESPONSE10("18", "Missing category field"),RESPONSE11("22", "Invalid CP code"),RESPONSE12("23", "Invalid payment"),
	RESPONSE13("24", "Confirmation transaction not found"),RESPONSE14("25", "Invalid CP Request Id"),RESPONSE15("503", "MPS system error"),RESPONSE16("101", "Operating error"),
	RESPONSE17("102", "Service registration failed"),RESPONSE18("103", "Pay by mobile balance failed"),RESPONSE19("104", "Service cancellation failed"),RESPONSE20("201", "Invalid signature"),
	RESPONSE21("202", "Error on payment"),RESPONSE22("207", "Register same charge package of service"),RESPONSE23("401", "Not enough money"),RESPONSE24("402", "Service has not been registered"),
	RESPONSE25("403", "Subscriber empty"),RESPONSE26("404", "The phone number does not exist"),RESPONSE27("405", "The owner of the mobile number was changed"),RESPONSE28("406", "Mobile information not found"),
	RESPONSE29("407", "CP is not allow to execute: Invalid or missing SUB, SER, CMD parameters"),RESPONSE30("408", "Service has been registered"),RESPONSE31("409", "The subscriber is blocked 2 ways"),RESPONSE32("410", "Is not Viettel subscriber"),
	RESPONSE33("411", "Subscriber was been cancel service"),RESPONSE34("412", "Subscriber have not used this service"),RESPONSE35("413", "Can’t get the money amount to payment: parameters SUB, SER, CMD, CATEGORY (with the payment commands: CHARGE/DOWNLOAD) are not passed completely or are invalid"),
	RESPONSE36("414", "Subscriber has cancelled service but it still appear in charging cycle"),RESPONSE37("415", "Invalid OTP code"),
	RESPONSE38("416", "Incorrect OTP code/ OTP code expired"),RESPONSE39("417", "USSD session time out"),RESPONSE40("440", "System error"),
	RESPONSE41("501", "Phone number has not been registered"),RESPONSE42("202", "Incorrect password"),RESPONSE43("203", "MPS account does not exist"),
	RESPONSE44("204", "CP account existed but its service has not been used"),RESPONSE45("205", "CP account already existed in the MPS system");
	

    private final String code;
    private final String description;
    private static Map<String, String> mMap;

    private ResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }

    public static String getdiscription(String code) {
        if (mMap == null) {
            initializeMapping();
        }
        if (mMap.containsKey(code)) {
            return mMap.get(code);
        }
        return null;
    }

    private static void initializeMapping() {
        mMap = new HashMap<String, String>();
        for (ResponseCode s : ResponseCode.values()) {
            mMap.put(s.code, s.description);
        }
    }
}

