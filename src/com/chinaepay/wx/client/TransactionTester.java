/**
 * @author xinwuhen
 */
package com.chinaepay.wx.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import com.chinaepay.wx.common.CommonInfo;
import com.chinaepay.wx.common.CommonTool;
import com.chinaepay.wx.common.CommonTool.SocketConnectionManager;
import com.chinaepay.wx.control.InquiryRefundController;
import com.chinaepay.wx.entity.InquiryRefundEntity;
import com.chinaepay.wx.entity.InquiryTransactionEntity;
import com.chinaepay.wx.entity.PaymentTransactionEntity;
import com.chinaepay.wx.entity.RefundTransactionEntity;
import com.chinaepay.wx.entity.ReverseTransactionEntity;

/**
 * @author xinwuhen
 *	������Ҫ���ڲ��ԡ�
 */
public class TransactionTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		System.out.println(TransactionTester.class);
//		System.out.println(new String("Test").getClass());
		
		TransactionTester transTester = new TransactionTester();
		String strBizReq = null;
		String strBizType = null;
		
		/** ����֧������ **/
//		strBizType = CommonInfo.PAYMENT_TRANSACTION_BIZ;
//		strBizReq = transTester.getPamentTransRequest();
//		exeSocketTest(strBizType + ":" + strBizReq);
		
		/** ���Բ�ѯ���� **/
//		strBizType = CommonInfo.INQUIRY_TRANSACTION_BIZ;
//		strBizReq = transTester.getInquiryTransRequest();
//		exeSocketTest(strBizType + ":" + strBizReq);
		
		/** ���Գ������� **/
//		strBizType = CommonInfo.REVERSE_TRANSACTION_BIZ;
//		strBizReq = transTester.getReverseTransRequest();
//		exeSocketTest(strBizType + ":" + strBizReq);
		
		/** �����˿�� **/
//		strBizType = CommonInfo.REFUND_TRANSACTION_BIZ;
//		strBizReq = transTester.getRefundTransRequest();
//		exeSocketTest(strBizType + ":" + strBizReq);
		
		
		/** ���Բ�ѯ�˿ **/
		strBizType = CommonInfo.INQUIRY_REFUND_BIZ;
		strBizReq = transTester.getInquiryRefundRequest();
		exeSocketTest(strBizType + ":" + strBizReq);
		
	}
	
	/**
	 * ͨ��Socket����ҵ��ͨ�š�
	 * @param strData
	 */
	private static void exeSocketTest(String strData) {
		SocketConnectionManager socketConnMngr = CommonTool.SocketConnectionManager.getInstance();
//		socketConnMngr.openSocket("47.93.125.18", 10086);
		socketConnMngr.openSocket("127.0.0.1", 10086);
		socketConnMngr.writeData(strData);
		String strResp = socketConnMngr.readData();
		System.out.println("strResp = " + strResp);
		socketConnMngr.closeSocket();
	}
	
	/**
	 * ֧�����׶�Ӧ�������ģ���ʽ��appid=43453&mch_id=dsw342&sub_mch_id=983477232&nonce_str=aiadjsis8732487jsd8l
	 * @return
	 */
	private String getPamentTransRequest() {
		
		String strAuthCode = "135029600960750624"; // ��ά���е��û���Ȩ��
		StringBuffer sb = new StringBuffer();
		sb.append(PaymentTransactionEntity.AGENT_ID + "=" + "1r10s84408mdj0tgp6iov2c0k54jbps9");
		sb.append("&" + PaymentTransactionEntity.SUB_MCH_ID + "=" + "12152566");
		sb.append("&" + PaymentTransactionEntity.NONCE_STR + "=" + CommonTool.getRandomString(32));
		sb.append("&" + PaymentTransactionEntity.BODY + "=" + "Ipad mini  16G  ��ɫ"); // Ipad mini  16G  ��ɫ
		sb.append("&" + PaymentTransactionEntity.OUT_TRADE_NO + "=" + CommonTool.getOutTradeNo(new Date(), 18));
		sb.append("&" + PaymentTransactionEntity.TOTAL_FEE + "=" + "1");
		sb.append("&" + PaymentTransactionEntity.FEE_TYPE + "=" + "USD");
		try {
			sb.append("&" + PaymentTransactionEntity.SPBILL_CREATE_IP + "=" + CommonTool.getSpbill_Create_Ip());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		sb.append("&" + PaymentTransactionEntity.AUTH_CODE + "=" + strAuthCode);
//		sb.append("&" + PaymentTransactionEntity.APP_KEY + "=" + "024edfffae32c829b012c98a61686f3b");
		
		return sb.toString();
	}
	
	/**
	 * �����������ġ�
	 * @return
	 */
	private String getReverseTransRequest() {
		StringBuffer sb = new StringBuffer();
		sb.append(ReverseTransactionEntity.AGENT_ID + "=" + "1r10s84408mdj0tgp6iov2c0k54jbps9");
		sb.append("&" + ReverseTransactionEntity.SUB_MCH_ID + "=" + "12152566");
		sb.append("&" + ReverseTransactionEntity.OUT_TRADE_NO + "=" + "20180317162341003734102708751406"); // ����ʱ�޸Ĵ��ֶ�
		sb.append("&" + ReverseTransactionEntity.NONCE_STR + "=" + CommonTool.getRandomString(32));
//		sb.append("&" + ReverseTransactionEntity.APP_KEY + "=" + "024edfffae32c829b012c98a61686f3b");
		
		return sb.toString();
	}
	
	/**
	 * �˿������ġ�
	 * @return
	 */
	private String getRefundTransRequest() {
		StringBuffer sb = new StringBuffer();
		sb.append(RefundTransactionEntity.AGENT_ID + "=" + "1r10s84408mdj0tgp6iov2c0k54jbps9");
		sb.append("&" + RefundTransactionEntity.SUB_MCH_ID + "=" + "12152566");
		sb.append("&" + RefundTransactionEntity.NONCE_STR + "=" + CommonTool.getRandomString(32));
		sb.append("&" + RefundTransactionEntity.OUT_TRADE_NO + "=" + "20180317173710924965776167374654"); // ����ʱ�޸Ĵ��ֶ�
		sb.append("&" + RefundTransactionEntity.OUT_REFUND_NO + "=" + CommonTool.getOutRefundNo(new Date(), 18));	// ͬһ�˿��ʱ�ǵ��޸Ĵ��ֶ�Ϊ�̶�ֵ
		sb.append("&" + RefundTransactionEntity.TOTAL_FEE + "=" + "1");
		sb.append("&" + RefundTransactionEntity.REFUND_FEE + "=" + "1");
//		sb.append("&" + RefundTransactionEntity.APP_KEY + "=" + "024edfffae32c829b012c98a61686f3b");
		
		return sb.toString();
	}
	
	/**
	 * ���ɲ�ѯ��������Ĳ����б�(HashMap����).
	 * @param hmTransactionOrderCont
	 * @return
	 */
	private String getInquiryTransRequest() {
		StringBuffer sb = new StringBuffer();
		sb.append(InquiryTransactionEntity.AGENT_ID + "=" + "1r10s84408mdj0tgp6iov2c0k54jbps9");
		sb.append("&" + InquiryTransactionEntity.SUB_MCH_ID + "=" + "12152566");
		sb.append("&" + InquiryTransactionEntity.OUT_TRADE_NO + "=" + "20180317173710924965776167374654");	// ����ʱ�޸Ĵ˲���
		sb.append("&" + InquiryTransactionEntity.NONCE_STR + "=" + CommonTool.getRandomString(32));
//		sb.append("&" + InquiryTransactionEntity.APP_KEY + "=" + "024edfffae32c829b012c98a61686f3b");
		
		return sb.toString();
	}
	
	/**
	 * ��ѯ�˿��Ӧ�������ġ�
	 * @return
	 */
	private String getInquiryRefundRequest() {
		StringBuffer sb = new StringBuffer();
		sb.append(InquiryRefundEntity.AGENT_ID + "=" + "1r10s84408mdj0tgp6iov2c0k54jbps9");
		sb.append("&" + InquiryRefundEntity.SUB_MCH_ID + "=" + "12152566");
		sb.append("&" + InquiryRefundEntity.NONCE_STR + "=" + CommonTool.getRandomString(32));
		sb.append("&" + InquiryRefundEntity.OUT_TRADE_NO + "=" + "20180317173710924965776167374654");	// ����ʱ���޸Ĵ˲���
//		sb.append("&" + InquiryRefundEntity.APP_KEY + "=" + "024edfffae32c829b012c98a61686f3b");
		
		return sb.toString();
	}
}
