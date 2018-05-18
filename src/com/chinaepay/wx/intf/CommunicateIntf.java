/**
 * @author xinwuhen
 */
package com.chinaepay.wx.intf;

import java.util.HashMap;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author xinwuhen
 *
 */
public interface CommunicateIntf {
	/**
	 * У�齻�ײ�����
	 * @param transOrderInfo
	 * @return
	 */
	public boolean blnValdOrderArgs(HashMap<String, String> hmOrderCont);
	
	/**
	 * ��΢�ź�̨����ҵ�������Ĳ���ȡ�������ݡ�
	 * @param transOrderInfo
	 * @return String ���صı������ݡ�
	 */
	public String sendReqAndGetResp(String strURL, HashMap<String, String> hmOrderCont, CloseableHttpClient httpclient);
	
}
