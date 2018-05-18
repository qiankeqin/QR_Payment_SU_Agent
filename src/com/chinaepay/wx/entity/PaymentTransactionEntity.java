/**
 * @author xinwuhen
 */
package com.chinaepay.wx.entity;

import java.util.HashMap;

/**
 * @author xinwuhen
 *	�������ڴ洢֧���ඩ���Ľ������ݡ�
 */
public class PaymentTransactionEntity extends TransactionEntity {
	private HashMap<String, String> hmPaymentTransCont = null;
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, String> getHashInstance() {
		if (hmPaymentTransCont == null) {
			hmPaymentTransCont = new HashMap<String, String>();
		}
		return hmPaymentTransCont;
	}
}
