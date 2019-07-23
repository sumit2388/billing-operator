package net.one97.billing.halotal.commons;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private static final Logger logger = LoggerFactory
			.getLogger(Utils.class);
	public static HashMap<String, String> getHashMapOfValues(String chargingCode) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (chargingCode != null) {
			String[] userparams = chargingCode.split(",");
			System.out.println("Charging code params :" + userparams);
//			logger.info("Charging code params :" + userparams);
			for (String paramvalue : userparams) {
				String[] keyValue = paramvalue.split("=");
				if (keyValue.length > 1) {
					hashMap.put(keyValue[0], keyValue[1]);
				}
			}
		} else
			return null;
		return hashMap;
	}
}
