package com.wicare.wistorm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encrypt 加密库
 * 
 * @author c
 * @date 2015-10-9
 */
public class WEncrypt {

	/**
	 * MD5加密加密方式一
	 * 
	 * @param string
	 *            原始内容
	 * @return md5 16进制字符串
	 * 
	 *         public final static String getMD5(String input) { try {
	 *         MessageDigest md = MessageDigest.getInstance("MD5"); byte[]
	 *         messageDigest = md.digest(input.getBytes()); BigInteger number =
	 *         new BigInteger(1, messageDigest); String hashtext =
	 *         number.toString(16); // Now we need to zero pad it if you
	 *         actually want the full 32 // chars. while (hashtext.length() <
	 *         32) { hashtext = "0" + hashtext; } return hashtext; } catch
	 *         (NoSuchAlgorithmException e) { e.printStackTrace(); } return
	 *         input;
	 * 
	 *         }
	 */
	/**
	 * 
	 * MD5加密加密方式二 *
	 * 
	 * @param input
	 *            原始内容
	 * @return md5 16进制字符串
	 */
	public final static String MD5(String input) {
		byte[] source;
		try {
			// Get byte according by specified coding.
			source = input.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			source = input.getBytes();
		}
		String result = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			// The result should be one 128 integer
			byte temp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = temp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			result = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * md5File 文件MD5加密
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件加密后十六进制字符串
	 */
	public static String md5File(String filePath) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(filePath);

			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			;
			byte[] mdbytes = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
