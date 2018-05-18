/**
 * @author xinwuhen
 */
package com.chinaepay.wx.entity;

import java.util.HashMap;

/**
 * @author xinwuhen
 * ��ѯ�ඩ��ҵ�����ݡ�
 */
public class InquiryTransactionEntity extends InquiryEntity {
	private HashMap<String, String> hmInquiryTransCont = null;
	
	// ���׵���Ӧ�����ݿ������
	public static final String TBL_TRANS_ORDER = "TBL_TRANS_ORDER";
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, String> getHashInstance() {
		if (hmInquiryTransCont == null) {
			hmInquiryTransCont = new HashMap<String, String>();
		}
		return hmInquiryTransCont;
	}
}
