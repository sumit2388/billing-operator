package net.one97.billing.halotal.protocol.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.one97.billing.core.factory.BeanFactory;


public class HalotalJsonManagementWrapper {
	
	private static final Logger logger = LoggerFactory.getLogger(HalotalJsonManagementWrapper.class);
	
	private RestClient restClient;	
	
		public void setrestClient(RestClient restClient) {
		this.restClient = restClient;
	}

		
		public  String	createJsonData(String msisdn,String pricePoint, String url , String gw_PRO ,String charging_SER ,
				String gw_CMD ,String charging_SUB, String charging_CATE, String charging_ITEM, String charging_SUB_CP, String charging_CONT,
				String charging_TYPE,String charging_source,String charging_IMEI,String product){
		
		logger.info(" Inside class HalotalJsonManagementWrapper , method createJsonData  ");
		logger.info(" parameters :: msisdn :"+msisdn+" , pricePoint :"+pricePoint +" , url : "+url+" , gw_PRO : "+ gw_PRO+" , charging_SER :"+charging_SER+
				" , gw_CMD : "+gw_CMD+ " charging_SUB : "+charging_SUB+ " charging_CATE : "+charging_CATE+ " charging_ITEM : "+charging_ITEM+ " charging_SUB_CP : "+charging_SUB_CP+ 
				" charging_CONT : "+charging_CONT+ " charging_TYPE : "+charging_TYPE+ " charging_source : "+charging_source+" charging_IMEI : "+charging_IMEI+" product : "+product);	
		
		String req = UUID.randomUUID().toString();	
		logger.info(" Inside class HalotalJsonManagementWrapper , method createJsonData  :: req"+req);
		//String source="CLIENT";    //WAP
		
		 String response="";
		 try {
			  response=	sendRequestToMps(charging_SUB, charging_CATE,charging_ITEM, charging_SUB_CP, 
					charging_CONT,pricePoint,req,msisdn,charging_source,gw_PRO,charging_SER,gw_CMD,url,charging_IMEI,product);
			  logger.info(msisdn+ " Response value = "+response);
		} catch (Exception e) {
			logger.error(msisdn+"Exception ",e);
		}

		return response;
		
		
	}
		
		public  String  sendRequestToMps(String charging_SUB,String charging_CATE,String charging_ITEM,String  charging_SUB_CP, String charging_CONT
				,String pricePoint,String req,String msisdn,String source,String gw_PRO,String charging_SER,String gw_CMD,String url,String charging_IMEI,String product)
	            throws Exception {
			 logger.info(" Inside sendRequestToMps");
	        String url_request_spamsms = makeMpsRequestUrl( charging_SUB, charging_CATE,charging_ITEM,   charging_SUB_CP, charging_CONT,  pricePoint, req, msisdn, source,
	        		gw_PRO, charging_SER, gw_CMD,url,charging_IMEI,product); 
	        
	        logger.info(" url_request_spamsms = ");
	        
	        String response = restClient.serviceCall(url_request_spamsms,msisdn);
	        logger.info("msisdn "+msisdn+" response recieved");
	        if(response != null){
	        String data_complete_spamsms = decodeMpsResponse(response, charging_SER,product);
	        logger.info(" data_complete_spamsms = ");
	        
	        return analyseCodeReturn(data_complete_spamsms,msisdn);
	        }
	        else{
	        	return "error";
	        }
	    }
		
		
		private String makeMpsRequestUrl(String sub,String cate,String item,  String sub_cp,String CONT, String PRICE,String REQ,String mobile,String source,
				String pro,String ser,String cmd,String url,String IMEI,String product) 
	            throws NoSuchAlgorithmException, Exception {
			 logger.info("inside makeMpsRequestUrl ");
	        String url_wap_mobile = url;
	        String dir=  BeanFactory.getProperty("halotal.keys.dir.path");
	        logger.info("dir path "+dir);	
	        
	        dir  = StringUtils.replace(dir, "{PRODUCT}", product);
	       	        
	        logger.info("======> check encryption key at folder :" + dir);
	        String publicKeyViettel = getKeyFile(dir + "PublicKeyVT.pem");
	        logger.info("publicKeyViettel path "+dir+ "PublicKeyVT.pem");
	        
	        String privateKeyCP = getKeyFile(dir + "PrivateKeyCP.pem");
	        logger.info("privateKeyCP path "+dir+ "PrivateKeyCP.pem");
	        
	        String publicKeyCP = getKeyFile(dir + "PublicKeyCP.pem");
	        logger.info("publicKeyCP path "+dir+ "PublicKeyCP.pem");
	        
	        String keyAES = AESKeyGen();
	        StringBuffer input = new StringBuffer();
	        input.append("SUB=").append(sub)
		            .append("&CATE=").append(cate)
		            .append("&ITEM=").append(item)
		            .append("&SUB_CP=").append(sub_cp)
		            .append("&CONT=").append(CONT)
		            .append("&PRICE=").append(PRICE)
		            .append("&REQ=").append(REQ)
		            .append("&MOBILE=").append(mobile)
		            .append("&SOURCE=").append(source) //
		            .append("&IMEI=").append(IMEI)
		            .append("&TYPE=TYPE")	               
		            ;
	        if (cate != null && !cate.isEmpty()) {
	            input.append("&CATE=").append(cate);
	        } else {
	            input.append("&CATE=");
	        }
	        logger.info("==============>>>>> BUILD DATA TO SEND TO MPS<<===============");
	        logger.info("step 1: data input  -------->" + input);

	        String input_with_key_AES = "value=" + encryptAES(input.toString(), keyAES) + "&key=" + keyAES;
	        logger.info("step 2: decode AES for input ------->" );
	   //     logger.info("step 2: decode AES for input ------->" + input_with_key_AES);

	        String data_encrypted_RSA = (encryptRSA(input_with_key_AES, publicKeyViettel));
	        logger.info("step 3: input add RSA for input --->" );
	    //    logger.info("step 3: input add RSA for input --->" + data_encrypted_RSA);

	        String signature = URLEncoder.encode(createMsgSignature(data_encrypted_RSA, privateKeyCP),CharEncoding.UTF_8);
	        logger.info("step 4: create signature for authentication ---> ");
	   //     logger.info("step 4: create signature for authentication ---> " + signature);

	        boolean kq = verifyMsgSignature(URLDecoder.decode(signature,CharEncoding.UTF_8), publicKeyCP, data_encrypted_RSA);
	        logger.info("step 5: authentication signature ----------> " );
	     //   logger.info("step 5: authentication signature ----------> " + kq);

	        String url_request = url_wap_mobile + "PRO=" + pro + "&SER=" + ser + "&SUB=" + sub + "&CMD="
	                + cmd + "&DATA=" + URLEncoder.encode(data_encrypted_RSA,CharEncoding.UTF_8) + "&SIG=" + signature;
	        return url_request;
	    }
		
		//step 2
		 private  String encryptAES(String data, String key) throws Exception {
		        String dataEncrypted = new String();
		        try {
		            Cipher aesCipher = Cipher.getInstance("AES");
		            byte[] raw = hexToBytes(key);
		            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		            aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		            byte[] byteDataToEncrypt = data.getBytes();
		            byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
		            dataEncrypted = DatatypeConverter.printBase64Binary(byteCipherText);
		            return dataEncrypted;
		        } catch (Exception ex) {
		        	logger.error(ex.getMessage());
		        }
		        return dataEncrypted;
		    }

		 private  byte[] hexToBytes(String hex) {
		        return hexToBytes(hex.toCharArray());
		    }

		  private  byte[] hexToBytes(char[] hex) {
		        int length = hex.length / 2;
		        byte[] raw = new byte[length];
		        for (int i = 0; i < length; i++) {
		            int high = Character.digit(hex[i * 2], 16);
		            int low = Character.digit(hex[i * 2 + 1], 16);
		            int value = (high << 4) | low;
		            if (value > 127) {
		                value -= 256;
		            }
		            raw[i] = (byte) value;
		        }
		        return raw;
		    }
		  
		//step 3
		
		    private  String encryptRSA(String toEncrypt, String strPublicKey) {
		        RSAKeyParameters rsaPbKey = getBCPublicKeyFromString(strPublicKey);
		        if (rsaPbKey == null) {
		        	logger.info("RSAPublicKey == null");
		            return null;
		        }
		        try {
		            AsymmetricBlockCipher theEngine = new RSAEngine();
		            theEngine = new PKCS1Encoding(theEngine);
		            theEngine.init(true, rsaPbKey);
		            return new String(Base64.encode(theEngine.processBlock(toEncrypt.getBytes(), 0, toEncrypt.getBytes().length)));
		        } catch (InvalidCipherTextException ex) {
		        	logger.error("encryptRSA error ", ex);
		        }
		        return null;
		    }
		    
		    private  RSAKeyParameters getBCPublicKeyFromString(String strPublicKey) {
		        try {
		            PublicKey prvKey = getPublicKeyFromString(strPublicKey);
		            KeyFactory keyFac = KeyFactory.getInstance("RSA");
		            RSAPublicKeySpec pkSpec = keyFac.getKeySpec(prvKey, RSAPublicKeySpec.class);
		            RSAKeyParameters pub = new RSAKeyParameters(false, pkSpec.getModulus(), pkSpec.getPublicExponent());
		            return pub;
		        } catch (Exception e) {
		        	logger.info("Exception : " + e);
		            return null;
		        }
		    }
		  
		    //step 4
		    
		    private  String createMsgSignature(String data, String strPrivateKey) {
		        String encryptData = "";
		        try {
		            PrivateKey privateKey = getPrivateKeyFromString(strPrivateKey);
		            java.security.Signature s = java.security.Signature.getInstance("SHA1withRSA");
		            s.initSign(privateKey);
		            s.update(data.getBytes());
		            byte[] signature = s.sign();
		            encryptData = new String(Base64.encode(signature));                             //Encrypt data
		        } catch (Exception e) {
		        	logger.error("createMsgSignature", e);
		        }
		        return encryptData;
		    }
		    
		    private  PrivateKey getPrivateKeyFromString(String key) throws Exception {
		        PrivateKey privateKey = null;
		        try {
		            KeyPair pemPair;
		           PEMReader reader = new PEMReader(new StringReader(key), null, "SunRsaSign");
		                pemPair = (KeyPair) reader.readObject();           

		            privateKey = (PrivateKey) pemPair.getPrivate();
		            reader.close();
		        } catch (Exception e) {
		        	logger.info("error getPrivateKeyFromString " + e);
		        }
		        return privateKey;

		    }
		    
		  
		 private  String AESKeyGen() throws NoSuchAlgorithmException {
		        try {
		            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		            keyGen.init(128, new SecureRandom());
		            SecretKey secretKey = keyGen.generateKey();
		            return byteToHex(secretKey.getEncoded());
		        } catch (NoSuchAlgorithmException noSuchAlgo) {
		        	logger.error(noSuchAlgo.getMessage());
		        }
		        return null;
		    }

		    private  String byteToHex(byte[] data) {
		        StringBuilder buf = new StringBuilder();
		        for (int i = 0; i < data.length; i++) {
		            int halfbyte = (data[i] >>> 4) & 0x0F;
		            int two_halfs = 0;
		            do {
		                if ((0 <= halfbyte) && (halfbyte <= 9)) {
		                    buf.append((char) ('0' + halfbyte));
		                } else {
		                    buf.append((char) ('a' + (halfbyte - 10)));
		                }
		                halfbyte = data[i] & 0x0F;
		            } while (two_halfs++ < 1);
		        }
		        return buf.toString();
		    }
		    


		   
		   

		   

		    private  PublicKey getPublicKeyFromString(String key) throws Exception {
		        PublicKey publicKey = null;
		        try {
		           PEMReader reader = new PEMReader(new StringReader(key), null, "SunRsaSign");
		                publicKey = (PublicKey) reader.readObject();
		            
		                reader.close();
		        } catch (Exception e) {
		        	logger.error("error getPublicKeyFromString ", e);
		        }
		        return publicKey;
		    }

		    

		    

		   

		    private  boolean verifyMsgSignature(String encodeText, String strPublicKey, String input) {

		        try {
		            PublicKey publicKey = getPublicKeyFromString(strPublicKey);
		            byte[] base64Bytes = Base64.decode(encodeText);                                 // decode base64
		            java.security.Signature sig = java.security.Signature.getInstance("SHA1WithRSA");
		            sig.initVerify(publicKey);
		            sig.update(input.getBytes());

		            return sig.verify(base64Bytes);
		        } catch (Exception e) {
		        	logger.error("verifyMsgSignature", e);
		        }
		        return false;
		    }
		    
		    private  final Map<String, String> KEY_MPS = new HashMap<String, String>();
		    
		    private  String getKeyFile(String filePath) {
		        String value = KEY_MPS.get(filePath);
		        if (value != null) {
		            return value;
		        }
		        
		        File file = new File(filePath);
		        StringBuilder contents = new StringBuilder();
		        BufferedReader reader = null;

		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String text = null;
		            while ((text = reader.readLine()) != null) {
		                contents.append(text)
		                        .append(System.getProperty(
		                                        "line.separator"));
		            }
		        } catch (IOException e) {
		        	logger.error("getKeyFile", e);
		        } finally {
		            try {
		                if (reader != null) {
		                    reader.close();
		                }
		            } catch (IOException e) {
		            	logger.error("getKeyFile", e);
		            }
		            
		            value = contents.toString();
		            if (!value.isEmpty()) {
		                KEY_MPS.put(filePath, value);
		            }
		        }

		        return value;
		    }

		    private  String decryptRSA(String toDecrypt, String strPrivateKey) {

		        RSAPrivateCrtKeyParameters rsaPrKey = getBCPrivateKeyFromString(strPrivateKey);
		        if (rsaPrKey == null) {
		        	logger.info("RSAPrivateKey == null");
		            return null;
		        }

		        try {
		            AsymmetricBlockCipher theEngine = new RSAEngine();
		            theEngine = new PKCS1Encoding(theEngine);
		            theEngine.init(false, rsaPrKey);
		            return new String(theEngine.processBlock(Base64.decode(toDecrypt), 0, Base64.decode(toDecrypt).length));
		        } catch (Exception ex) {
		        	logger.error("decryptRSA", ex);
		        }
		        return null;
		    }

		    private  RSAPrivateCrtKeyParameters getBCPrivateKeyFromString(String strPrivateKey) {
		        try {
		            PrivateKey prvKey = getPrivateKeyFromString(strPrivateKey);
		            KeyFactory keyFac = KeyFactory.getInstance("RSA");
		            RSAPrivateCrtKeySpec pkSpec = keyFac.getKeySpec(prvKey, RSAPrivateCrtKeySpec.class);
		            RSAPrivateCrtKeyParameters priv = new RSAPrivateCrtKeyParameters(pkSpec.getModulus(),
		                    pkSpec.getPublicExponent(), pkSpec.getPrivateExponent(), pkSpec.getPrimeP(),
		                    pkSpec.getPrimeQ(), pkSpec.getPrimeExponentP(), pkSpec.getPrimeExponentQ(),
		                    pkSpec.getCrtCoefficient());
		            return priv;
		        } catch (Exception e) {
		            return null;
		        }
		    }

		    private  String decryptAES(String dataEncrypt, String key) throws Exception {
		        String dataDecrypted = new String();
		        try {
		            Cipher aesCipher = Cipher.getInstance("AES");
		            byte[] raw = hexToBytes(key);
		            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		            byte[] decordedValue = DatatypeConverter.parseBase64Binary(dataEncrypt);
		            aesCipher.init(Cipher.DECRYPT_MODE, skeySpec, aesCipher.getParameters());
		            byte[] byteDecryptedText = aesCipher.doFinal(decordedValue);
		            dataDecrypted = new String(byteDecryptedText);
		            return dataDecrypted;
		        } catch (Exception ex) {
		        	logger.error(ex.getMessage());
		        }
		        return dataDecrypted;
		    }

		    
		    public  String decodeMpsResponse(BufferedReader br, String subServiceName) throws Exception {
		     
		       String dir=  BeanFactory.getProperty("halotal.keys.dir.path");
		       logger.info("dir path = "+dir);
		       
		       String publicKeyViettel = getKeyFile(dir + "PublicKeyVT.pem");
		       logger.info("publicKeyViettel path = "+dir + "PublicKeyVT.pem");
		       
		       String privateKeyCP = getKeyFile(dir + "PrivateKeyCP.pem");
		       logger.info("privateKeyCP path = "+dir + "PrivateKeyCP.pem");

		       //String strTemp = "DATA=C+jb1u4p0saaaUP5NCJYQ/aDB85FQsbv3IghLJ9sMDxx5WRQpsv8KegOlsnqzn5prFriPeYYuJbwgNPS57x3s2iVt95TQPcHA6ESUM9qAuZVRhUAMtUhIVsLx+dCeBtAFOChPlNk7m9tlOWgaDccRRVUCvaXNLdbB0SH/0Gm0RVA0xgVHGIcgrnV6dZEj6VXTy9nGxRNp3t7/g41YGxeIOoZ+ZhXF/kUshxlussqhvFpxg4R9O939vO8kNRb9rvlQIOHhembV3cnVYRb3VDPQQIBQeRjKSHYeGilpYGNf9S5VglvneMnFhZ4ZeppZ1OFrJVzIc2FuSRyshWxscwrH2v1xZ7vakyjJq0hdhmFwsJ0bi6tKsoSPfRM5CzTAumOsfYZPbSqcYiuCbPbtyru07MR+Ve1RQU1qRNw/7m4LZVObdv7M2FCY54yoOFJGqImvn+mb5II4TvRhs0IOHYfJO30IXik+2nnHach78yIY7u6PIKv6HocqEyXpFnj4D1iwX8y4Hj4VvwauYODff0gVbXsbDXqML7nXMysKWJKAF91k02HpzERzu1AvwiAjK25RaGkynb3Xge1egSVSaS7XRD2v02GftLhSFOnXy6IoaPQhjXQa11x55Rvj1f83LKMDaKZOqS6ARaT+tyTsygqZ2LHMMwfeCRo9dFLjoDsypU=&SIG=fPs3wCN4VE7d61sgILL6IgVaD7oHq82YEjutbvJY3Ky9nVMeiJO4YaTZ3lH3iGd3mU9PNZpBVlKLLl4A5g59E52bDmWMhMA6JuSgGNNU%2Fnkt307sgFlIT10OuD83AgmNZJGDgC2qo0jR0U9BhAQ4sk3Abz%2BLFNEvlD9fdc%2FS2lz%2FuSxZk9JDmkINOw424Wmm%2BWemCzIWu99gqzHyRe4x1dJoPGTHmVeNCXpcS7hCyxj9BW%2BTr1Y7Pn0orFIwDE%2Bz2RKGxYjNdRiX7mJJx6iVh6bz0s84jN7tlPnBjxT4mk52B3cVjbZJNQ19pzg32RJkHTdnZ87Ds30EbN%2F5zOMdMEiSn840UGNteFXV%2F76TgNDmcki82qp%2BDAMt9L7%2Bc9GWwc9iEfcbiDBIgTtIP4jQBtKBubINb6MhraqDUXoMoMiZbDbF7lyXrcQbdUfHBsFhtFxa1Ry%2B4Cu%2FO9rGuSVqcE3uBBgM50EM%2FH%2FRzs1SWuQIlu1DemwoSftO7nP92IEE8z9FBlEmMSWdtiSP4%2BlhOkycFadFfDkiwuE1%2Fq4XNTmX%2BwOmla5W7vaOGIRFu5Jt%2Fg%2F453qIvo3W0yQCxe1kzKh8KIso%2BK8b5%2BxuEkie871Luoa9ten4N%2FWymKB3eGxk2%2FEGQ%2FTZQXXT2uC4oJwfoxrCPwKrQbnu9zhsZM12%2BYo%3D";
		       
		       String strTemp = "";
		       
		       logger.info("================>>>DECODE DATA RETURN FROM MPS<<===============");
		       while (null != (strTemp = br.readLine())) {
		           if (strTemp.length() > 200) {
		               String[] s = strTemp.split("&");
		               String dataN = s[0].substring(5);
		               String sigN = s[1].substring(4);
		               logger.info("step add: sigN = " + sigN);
		               boolean verify_return = verifyMsgSignature(URLDecoder.decode(sigN,CharEncoding.UTF_8), publicKeyViettel, dataN);
		               logger.info("  step 7: data receive from mps dataN = -----> " + dataN);
		               logger.info("  step 8: check signuature ---------> " + verify_return);
		               
		               String data_decrypt_RSAN = decryptRSA(dataN, privateKeyCP);
		               logger.info("data_decrypt_RSAN = "+data_decrypt_RSAN);
		               
		               String value_return = data_decrypt_RSAN.split("&")[0].substring(6);
		               logger.info("value_return = "+value_return);
		               
		               String key_return = data_decrypt_RSAN.split("&")[1].substring(4);
		               logger.info("key_return = "+key_return);
		               
		               String data_complete = decryptAES(value_return, key_return);
		               logger.info("data_complete = "+data_complete);
		               
		               return data_complete;
		           } else {
		        	   logger.error(strTemp);
		           }
		       }
		       return "";
		   }
		    
		     public  String analyseCodeReturn(String input,String msisdn) {
		    	 logger.info("   step 9: decode data return from mps ----> " + input);
		        String value = "unknown" ;
		        String[] a = input.split("&");
		        for (String a1 : a) {
		            if (a1.split("=")[0].equals("RES")) {
		                String key = a1.split("=")[1];
		                logger.info(" response key "+ key);
		                logger.info(msisdn + " step 10: analyse RESPONSE code return ---> " + a1 + "==>  "+ ResponseCode.getdiscription(key));
		              //  value = ResponseCode.getdiscription(key);		
		                value=key;
		                break;
		            }
		        }
		        return value;
		    }
		     
		     
		     
		     public  String decodeMpsResponse(String  response, String subServiceName,String product) throws Exception {
			     
			       String dir=  BeanFactory.getProperty("halotal.keys.dir.path");
			       logger.info("dir path = "+dir);
			       
			       dir  = StringUtils.replace(dir, "{PRODUCT}", product);
			 
			       logger.info("======> check decryption key at folder :" + dir);
			       String publicKeyViettel = getKeyFile(dir + "PublicKeyVT.pem");
			       logger.info("publicKeyViettel path = "+dir + "PublicKeyVT.pem");
			       
			       String privateKeyCP = getKeyFile(dir + "PrivateKeyCP.pem");
			       logger.info("privateKeyCP path = "+dir + "PrivateKeyCP.pem");

			       //String strTemp = "DATA=C+jb1u4p0saaaUP5NCJYQ/aDB85FQsbv3IghLJ9sMDxx5WRQpsv8KegOlsnqzn5prFriPeYYuJbwgNPS57x3s2iVt95TQPcHA6ESUM9qAuZVRhUAMtUhIVsLx+dCeBtAFOChPlNk7m9tlOWgaDccRRVUCvaXNLdbB0SH/0Gm0RVA0xgVHGIcgrnV6dZEj6VXTy9nGxRNp3t7/g41YGxeIOoZ+ZhXF/kUshxlussqhvFpxg4R9O939vO8kNRb9rvlQIOHhembV3cnVYRb3VDPQQIBQeRjKSHYeGilpYGNf9S5VglvneMnFhZ4ZeppZ1OFrJVzIc2FuSRyshWxscwrH2v1xZ7vakyjJq0hdhmFwsJ0bi6tKsoSPfRM5CzTAumOsfYZPbSqcYiuCbPbtyru07MR+Ve1RQU1qRNw/7m4LZVObdv7M2FCY54yoOFJGqImvn+mb5II4TvRhs0IOHYfJO30IXik+2nnHach78yIY7u6PIKv6HocqEyXpFnj4D1iwX8y4Hj4VvwauYODff0gVbXsbDXqML7nXMysKWJKAF91k02HpzERzu1AvwiAjK25RaGkynb3Xge1egSVSaS7XRD2v02GftLhSFOnXy6IoaPQhjXQa11x55Rvj1f83LKMDaKZOqS6ARaT+tyTsygqZ2LHMMwfeCRo9dFLjoDsypU=&SIG=fPs3wCN4VE7d61sgILL6IgVaD7oHq82YEjutbvJY3Ky9nVMeiJO4YaTZ3lH3iGd3mU9PNZpBVlKLLl4A5g59E52bDmWMhMA6JuSgGNNU%2Fnkt307sgFlIT10OuD83AgmNZJGDgC2qo0jR0U9BhAQ4sk3Abz%2BLFNEvlD9fdc%2FS2lz%2FuSxZk9JDmkINOw424Wmm%2BWemCzIWu99gqzHyRe4x1dJoPGTHmVeNCXpcS7hCyxj9BW%2BTr1Y7Pn0orFIwDE%2Bz2RKGxYjNdRiX7mJJx6iVh6bz0s84jN7tlPnBjxT4mk52B3cVjbZJNQ19pzg32RJkHTdnZ87Ds30EbN%2F5zOMdMEiSn840UGNteFXV%2F76TgNDmcki82qp%2BDAMt9L7%2Bc9GWwc9iEfcbiDBIgTtIP4jQBtKBubINb6MhraqDUXoMoMiZbDbF7lyXrcQbdUfHBsFhtFxa1Ry%2B4Cu%2FO9rGuSVqcE3uBBgM50EM%2FH%2FRzs1SWuQIlu1DemwoSftO7nP92IEE8z9FBlEmMSWdtiSP4%2BlhOkycFadFfDkiwuE1%2Fq4XNTmX%2BwOmla5W7vaOGIRFu5Jt%2Fg%2F453qIvo3W0yQCxe1kzKh8KIso%2BK8b5%2BxuEkie871Luoa9ten4N%2FWymKB3eGxk2%2FEGQ%2FTZQXXT2uC4oJwfoxrCPwKrQbnu9zhsZM12%2BYo%3D";
			       
			       String strTemp = response;
			       
			       logger.info("================>>>DECODE DATA RETURN FROM MPS<<===============");
			 //      while (null != (strTemp = br.readLine())) {
			           if (strTemp.length() > 200) {
			               String[] s = strTemp.split("&");
			               String dataN = s[0].substring(5);
			               String sigN = s[1].substring(4);
			               logger.info("step add: sigN = " + sigN);
			               boolean verify_return = verifyMsgSignature(URLDecoder.decode(sigN,CharEncoding.UTF_8), publicKeyViettel, dataN);
			               logger.info("  step 7: data receive from mps dataN = -----> " );
			          //     logger.info("  step 8: check signuature ---------> " + verify_return);
			               
			               String data_decrypt_RSAN = decryptRSA(dataN, privateKeyCP);
			               logger.info("data_decrypt_RSAN  ");
			               
			               String value_return = data_decrypt_RSAN.split("&")[0].substring(6);
			               logger.info("value_return  ");
			               
			               String key_return = data_decrypt_RSAN.split("&")[1].substring(4);
			               logger.info("key_return = ");
			               
			               String data_complete = decryptAES(value_return, key_return);
			               logger.info("data_complete = ");
			               
			               return data_complete;
			           } else {
			        	   logger.error(strTemp);
			           }
			//       }
			       return "";
			   }
		    
		    
		    
		    public void checkLinkCP(String url){
		        
		    }
		    


		    public static void main(String[] args) {
		    	HalotalJsonManagementWrapper aa = new HalotalJsonManagementWrapper();
		    	Scanner sc = new Scanner(System.in);
		    	String path = sc.next();
		    	System.out.println(path);
		    	try {
		    		aa.encryptRSA("abc",path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	
}
