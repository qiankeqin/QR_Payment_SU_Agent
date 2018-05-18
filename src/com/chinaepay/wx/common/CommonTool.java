/**
 * @author xinwuhen
 */
package com.chinaepay.wx.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.chinaepay.wx.entity.CommunicateEntity;

/**
 * @author xinwuhen ���׹�������������ļ��㹤���ࡣ
 */
public class CommonTool {

	/**
	 * ���ɹ̶�λ���������������
	 * 
	 * @return
	 */
	public static String getRandomString(int intLength) {
		char[] ch = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
				'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		char[] chNew = new char[intLength];
		int iChLengh = ch.length;
		for (int i = 0; i < chNew.length; i++) {
			int index = (int) (iChLengh * Math.random());
			chNew[i] = ch[index];
		}

		return String.valueOf(chNew);
	}

	/**
	 * �����̻������š�
	 * 
	 * @param date
	 * @param intExtLength ����Ӧ<=18����Ϊ΢��Ҫ�󶩵����ܳ���Ӧ������32λ����dateת���ĳ���Ϊ14λ��
	 * @return
	 */
	public static String getOutTradeNo(Date date, int intExtLength) {
		if (date == null) {
			return null;
		}

		String strPrefix = getFormatDate(date, "yyyyMMddHHmmss");

		char[] ch = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
		char[] chNew = new char[intExtLength];
		int iChLengh = ch.length;
		for (int i = 0; i < chNew.length; i++) {
			int index = (int) (iChLengh * Math.random());
			chNew[i] = ch[index];
		}
		String strSufix = String.valueOf(chNew);

		return strPrefix.concat(strSufix);
	}
	
	/**
	 * �����̻��˿�ţ��˴�Ĭ��Ϊ32λ��
	 * @param date
	 * @param intExtLength ����Ӧ<=18����Ϊ΢��Ҫ�󶩵����ܳ���Ӧ������32λ����dateת���ĳ���Ϊ14λ��
	 * @return
	 */
	public static String getOutRefundNo(Date date, int intExtLength) {
		return getOutTradeNo(date, intExtLength);
	}

	/**
	 * ���ݸ�ʽ�ַ��������ڽ��и�ʽ����
	 * 
	 * @param date
	 * @param strFormat
	 * @return
	 */
	public static String getFormatDate(Date date, String strFormat) {
		DateFormat sdf = new SimpleDateFormat(strFormat);
		return sdf.format(date);
	}

	/**
	 * ���ݸ�ʽ��ȡx��ǰ/��x��ǰ/���x��ǰ/������ڡ�
	 * 
	 * @param date
	 * @param strFormat
	 * @param intPreYear
	 * @param intPreMonth
	 * @param strPreDay
	 * @return
	 */
	public static String getPreOrSuffFormatDate(Date date, String strFormat, int intPreYear, int intPreMonth,
			int intPreDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, intPreYear);// x��ǰ/���ʱ��
		calendar.add(Calendar.MONTH, intPreMonth);// x����ǰ/���ʱ��
		calendar.add(Calendar.DAY_OF_MONTH, intPreDay);// x��ǰ/���ʱ��
		Date newDate = calendar.getTime();// ��ȡx��ǰ/���ʱ�䣬����x����ǰ/���ʱ��, ����x��ǰ/���ʱ��
		return getFormatDate(newDate, strFormat);
	}

	/**
	 * ��ȡ��ǰ�ն��豸��IP��ַ��
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getSpbill_Create_Ip() throws UnknownHostException {
		InetAddress res = InetAddress.getLocalHost();
		return res.getHostAddress();
	}

	/**
	 * �ڷ���΢�ź�̨���д���ǰ�����ɶ�����ǩ����
	 * 
	 * @param orderEntity
	 * @param strKey
	 * @return
	 */
	public static String getEntitySign(Map<String, String> hmOrderCont) {
		String[] strContKeys = hmOrderCont.keySet().toArray(new String[0]);
		Arrays.sort(strContKeys);
		StringBuffer sb = new StringBuffer();
		for (String K : strContKeys) {
			String V = hmOrderCont.get(K);
			if (K != null && !"".equals(K) && !K.equals(CommunicateEntity.SIGN) && !K.equals(CommunicateEntity.APP_KEY) && !K.equals(CommunicateEntity.AGENT_ID)
					&& V != null && !"".equals(V)) {
				sb.append(K.concat("=").concat(V).concat("&"));
			}
		}

		sb.append(getCorrectKey(CommunicateEntity.APP_KEY) + "=" + hmOrderCont.get(CommunicateEntity.APP_KEY));

		return new MD5Util().MD5(sb.toString(), "UTF-8").toUpperCase();
	}
	
	private static String getCorrectKey(String strFullKey) {
		if (strFullKey == null) {
			return "";
		}
		
		String strResp = "";
		if (strFullKey.contains("_")) {
			strResp = strFullKey.split("_")[1];
		}
		return strResp;
	}

	/**
	 * ��ȡһ���µ�clone��Map.
	 * 
	 * @param hmWXRespResult
	 * @return
	 */
	public static HashMap<String, String> getCloneMap(HashMap<String, String> hmWXRespResult) {
		if (hmWXRespResult == null) {
			return null;
		}

		HashMap<String, String> newMap = new HashMap<String, String>();
		String[] strKeys = hmWXRespResult.keySet().toArray(new String[0]);
		for (String strKey : strKeys) {
			if (strKey != null) {
				newMap.put(strKey, hmWXRespResult.get(strKey));
			}
		}

		return newMap;
	}
	
	/**
	 * �ϲ�����Map�����ݡ�
	 * @param sourceMap
	 * @param appendMap
	 * @return
	 */
	public static HashMap<String, String> getAppendMap(HashMap<String, String> sourceMap, Map<String, String> appendMap) {
		if (sourceMap == null || appendMap == null) {
			return null;
		}
		
		String[] strKeys = appendMap.keySet().toArray(new String[0]);
		for (String strKey : strKeys) {
			if (strKey != null) {
				sourceMap.put(strKey, appendMap.get(strKey));
			}
		}

		return sourceMap;
	}

	/**
	 * ��ʽ���ͻ��˵�Socket�����ַ���ΪMap.
	 * 
	 * @param strSocketRequest
	 * @return
	 */
	public static HashMap<String, String> formatStrToMap(String strSocketRequest) {
		if (strSocketRequest == null) {
			return null;
		}

		HashMap<String, String> hmOrderCont = new HashMap<String, String>();
		String[] strBig = strSocketRequest.split("&");
		for (String strTemp : strBig) {
			String[] strSmall = strTemp.split("=");
			hmOrderCont.put(strSmall[0], strSmall[1]);
		}

		return hmOrderCont;
	}

	/**
	 * ���ݲ�ͬ�Ĳ���ϵͳ��ʽ���ļ��洢·����
	 * 
	 * @param strFileSeparator
	 * @return
	 */
	public static String getWebAppAbsolutPath(Class clazz, String strFileSeparator) {
		String strOSWebAppPath = null;
		// WebApp�ľ���·��
		String strWebAppPath = CommonTool.urlDecodeUTF8(clazz.getClassLoader().getResource("/").getPath().replaceAll("/WEB-INF/classes/", ""));
		System.out.println(strWebAppPath);
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("win")) { // Windows����ϵͳ
			strOSWebAppPath = strWebAppPath.replaceAll("\\/", "\\" + strFileSeparator);
		} else { // Linux��Unix����ϵͳ
			strOSWebAppPath = strWebAppPath; // ��·�������滻����
		}

		return strOSWebAppPath;
	}

	/**
	 * ���ز���SSl֤���httpClient.
	 * 
	 * @return
	 */
	public static CloseableHttpClient getDefaultHttpClient() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		return httpclient;
	}

	/**
	 * ������ҪSSL֤���httpClient.
	 * 
	 * @param strCertPassword
	 * @return
	 */
	public static CloseableHttpClient getCertHttpClient(String strCertPassword) {
		if (strCertPassword == null || "".equals(strCertPassword)) {
			System.out.println("֤������Ϊ�գ�");
			return null;
		}

		CloseableHttpClient httpclient = null;
		/**
		 * ע��PKCS12֤�� �Ǵ�΢���̻�ƽ̨-���˻�����-�� API��ȫ �����ص�
		 */
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("PKCS12");
			String strCertFile = CommonTool.getWebAppAbsolutPath(CommonTool.class, System.getProperty("file.separator")) + "/conf/apiclient_cert.p12";
			System.out.println("strCertFile = " + strCertFile);
			FileInputStream instream = new FileInputStream(new File(strCertFile));// P12�ļ�Ŀ¼
			try {
				keyStore.load(instream, strCertPassword.toCharArray());
			} finally {
				instream.close();
			}

			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, strCertPassword.toCharArray()).build();
			// Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyStoreException | KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException
				| CertificateException | IOException e) {
			e.printStackTrace();
		}

		return httpclient;
	}
	
	/**
	 * ת��URL�ڵĲ���ΪUTF-8��ʽ��
	 * 
	 * @param str
	 * @return
	 */
	public static String urlDecodeUTF8(String str) {
		if (str == null) {
			return "";
		}

		try {
			return URLDecoder.decode(str.trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * ת��Float��Double��Ϊ2λС������ַ�����
	 * @return
	 */
	public static String formatNumToDoublePoints(double dblData) {
		return String.format("%.2f", dblData);
	}

	public static class MD5Util {
		private final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

		/**
		 * MD5�����㷨��
		 * 
		 * @param sourceStr
		 * @return
		 */
		public String MD5(String sourceStr, String charsetName) {

			if (sourceStr == null || sourceStr.equals("")) {
				return null;
			}

			String resultString = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				if (charsetName == null || "".equals(charsetName)) {
					resultString = byteArrayToHexString(md.digest(sourceStr.getBytes()));
				} else {
					resultString = byteArrayToHexString(md.digest(sourceStr.getBytes(charsetName)));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

			return resultString;
		}

		private String byteArrayToHexString(byte b[]) {
			StringBuffer resultSb = new StringBuffer();
			for (int i = 0; i < b.length; i++)
				resultSb.append(byteToHexString(b[i]));

			return resultSb.toString();
		}

		private String byteToHexString(byte b) {
			int n = b;
			if (n < 0)
				n += 256;
			int d1 = n / 16;
			int d2 = n % 16;
			return hexDigits[d1] + hexDigits[d2];
		}
	}
	
	/**
	 * Socket�������࣬���ڻ�ȡSocket������Զ˹ܵ�������ݡ��ӶԶ˹ܵ���ȡ���ݡ�
	 * @author xinwuhen
	 */
	public static class SocketConnectionManager {
		private static SocketConnectionManager socketConnMngr = null;
		private Socket socket = null;
		private OutputStream os = null;
		private OutputStreamWriter osw = null;
		private BufferedWriter bw = null;
		private InputStream in = null;
		private InputStreamReader isr = null;
		private BufferedReader br = null;
		
		private SocketConnectionManager() {
			super();
		}
		
		/**
		 * ��ȡSocket������ʵ����
		 * @return
		 */
		public static SocketConnectionManager getInstance() {
			if (socketConnMngr == null) {
				socketConnMngr = new SocketConnectionManager();
			}
			return socketConnMngr;
		}
		
		/**
		 * ��ȡ����Socket����
		 * @param strHost
		 * @param iPort
		 * @return
		 */
		public Socket openSocket(String strHost, int iPort) {
			try {
				if (socket == null || socket.isClosed()) {
					socket = new Socket(strHost, iPort);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return socket;
		}
		
		/**
		 * �ر�Socket�Լ���򿪵��������/��������
		 */
		public void closeSocket() {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * ͨ������ܵ�����Զ�������ݡ�
		 * @param strData
		 */
		public void writeData(String strData) {
			if (strData == null || "".equals(strData)) {
				return;
			}
			
			if (socket != null && !socket.isClosed()) {
				try {
					os = socket.getOutputStream();
					osw = new OutputStreamWriter(os, "UTF-8");
					bw = new BufferedWriter(osw);
					bw.write(strData);
					bw.flush();
					socket.shutdownOutput();
				} catch (IOException e) {
					e.printStackTrace();
					
					// �ر�Socket���ӣ��Լ����е����/������
					this.closeSocket();
				}
			}
		}
		
		/**
		 * ͨ������ܵ����ӶԶ˶�ȡ���ݡ�
		 * @return
		 */
		public String readData() {
			StringBuffer sb = new StringBuffer();
			if (socket != null && !socket.isClosed()) {
				try {
					socket.setSoTimeout(CommonInfo.CLIENT_SOCKET_TIME_OUT);
					in = socket.getInputStream();
					isr = new InputStreamReader(in, "UTF-8");
					br = new BufferedReader(isr);
					int iByteData = -1;
					while ((iByteData = br.read()) != -1) {
						sb.append((char) iByteData);
					}
				} catch (IOException e) {
					e.printStackTrace();
					
					// �ر�Socket���ӣ��Լ����е����/������
					this.closeSocket();
				}
			}
			return sb.toString();
		}
	}
}
