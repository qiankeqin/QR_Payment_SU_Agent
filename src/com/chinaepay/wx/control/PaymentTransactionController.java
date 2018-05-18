/**
 * @author xinwuhen
 */
package com.chinaepay.wx.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.chinaepay.wx.common.CommonInfo;
import com.chinaepay.wx.common.CommonTool;
import com.chinaepay.wx.common.MysqlConnectionPool;
import com.chinaepay.wx.control.CommunicateController.ParsingWXResponseXML;
import com.chinaepay.wx.dao.MchInfoDAO;
import com.chinaepay.wx.dao.MchInfoTransOrderDAO;
import com.chinaepay.wx.dao.TransOrderDAO;
import com.chinaepay.wx.entity.InquiryTransactionEntity;
import com.chinaepay.wx.entity.PaymentTransactionEntity;
import com.chinaepay.wx.entity.RefundTransactionEntity;

/**
 * @author xinwuhen
 *	֧�����͵Ķ��������ࡣ
 */
public class PaymentTransactionController extends TransactionController {
	private static PaymentTransactionController paymentOrderCntrl = null;
	
	private static final String STR_PAYMENT_ORDER_URL = "https://api.mch.weixin.qq.com/pay/micropay";
	
	/**
	 * ��ȡ�����Ψһʵ����
	 * @return
	 */
	public static PaymentTransactionController getInstance() {
		if (paymentOrderCntrl == null) {
			paymentOrderCntrl = new PaymentTransactionController();
		}
		
		return paymentOrderCntrl;
	}
	
	
	/**
	 * �˷��������̻��ĶԽӽӿڣ������ڳ��˴������е��׸�ͨ��΢�ź�̨��ҵ���߼��⣬��Ҫ���ظ����̻����׸�֮ͨ��Լ���ı��ġ����У����ظ��̻��ı��ĸ�ʽ�����£�
	 * HashMap�ĸ�ʽ��
	 * [key]: BUSINESS_PROC_RESULT 		[value]: SUCCESS �� [΢�Ŷ���Ĵ�����]
	 * [key]: BUSINESS_RESPONSE_RESULT 	[value]: out_trade_no=1217752501201407033233368018&fee_type=USD&total_fee=888&time_end=20141030133525&transaction_id=013467007045764
	 */
//	public HashMap<String, String> startTransactionOrder(HashMap<String, String> hmTransactionOrderCont) {
	public String startTransactionOrder(HashMap<String, String> hmTransactionOrderCont) {
		// ֧����������У��
//		boolean blnValOrderArgs = blnValdOrderArgs(hmTransactionOrderCont);
//		if (!blnValOrderArgs) {
//			return orgnizeResponseInfo(PaymentTransactionEntity.SUCCESS, PaymentTransactionEntity.PARAM_ERROR, new String[] {hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), "", "", "", ""});
//		}
//		
//		// У��������Ƿ���ڲ���Ч
//		boolean blnValidAgnt = validateAgent(hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID));
//		
//		if (!blnValidAgnt) {
//			return orgnizeResponseInfo(PaymentTransactionEntity.SUCCESS, PaymentTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), 
//					hmTransactionOrderCont.get(PaymentTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(PaymentTransactionEntity.TOTAL_FEE),
//					CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
//		}
		
		// ���������̼��ն��̻�
		refferAgentAndSubMerchant(hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(PaymentTransactionEntity.SUB_MCH_ID));
		
		// ��ʼ������Ϣ�־û���DB
		hmTransactionOrderCont.put(PaymentTransactionEntity.TRANS_TIME, CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"));
		boolean blnPersistOrderInfo = insertOrderInfoToTbl(hmTransactionOrderCont);
		hmTransactionOrderCont.remove(PaymentTransactionEntity.TRANS_TIME);
//		if (!blnPersistOrderInfo) {
//			return orgnizeResponseInfo(PaymentTransactionEntity.SUCCESS, PaymentTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), 
//												hmTransactionOrderCont.get(PaymentTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(PaymentTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
//		}
		
		// ��΢�ź�̨���н��׶Խӣ���ȡ΢�ŵ�ʵʱӦ����, ������΢�Ŷ�Ӧ�������в�ͬ��ҵ����
		String strWXResponseResult = this.sendReqAndGetResp(STR_PAYMENT_ORDER_URL, hmTransactionOrderCont, CommonTool.getDefaultHttpClient());
		System.out.println("++++++++++++++strWXResponseResult = " + strWXResponseResult);
		// У�齻�׽�����ݴ�ȷ���Ƿ���Ҫ���á���ѯ������API���򡾳���������API�������������һ�εĽ���Ӧ����Ϣ(������΢�ź�̨)
		// ����XML��������Map��
		HashMap<String, String> hmWXRespResult = null;
		try {
			hmWXRespResult = new ParsingWXResponseXML().getMapBaseWXRespResult(strWXResponseResult);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
//			return orgnizeResponseInfo(PaymentTransactionEntity.SUCCESS, PaymentTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), 
//												hmTransactionOrderCont.get(PaymentTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(PaymentTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
		}
		
		
		String strReturnCode = hmWXRespResult.get(PaymentTransactionEntity.RETURN_CODE);
		String strResultCode = hmWXRespResult.get(PaymentTransactionEntity.RESULT_CODE);
		String strErrCode = hmWXRespResult.get(PaymentTransactionEntity.ERR_CODE);
		
//		String strSysCommRst = null;
//		String strRespCommcialRst = null;
//		String[] strRespCommcialInfo = null;
		
		// ����ʧ��
		if (strReturnCode == null || strResultCode == null
				|| !strReturnCode.equals(PaymentTransactionEntity.SUCCESS) || !strResultCode.equals(PaymentTransactionEntity.SUCCESS)) {
			// �ж�ͨ�ż�ϵͳ״̬
//			strSysCommRst = (strReturnCode == null || "".equals(strReturnCode)) ? PaymentTransactionEntity.SYSTEMERROR : strReturnCode;
			
			// ����֧��ҵ�����ʧ��
//			strRespCommcialRst = (strErrCode == null || "".equals(strErrCode)) ? PaymentTransactionEntity.SYSTEMERROR : strErrCode;
			
			// ���½��׽�������ݿ�(����ʧ��)
			HashMap<String, String> newClonedMap = CommonTool.getCloneMap(hmWXRespResult);
			if (strErrCode != null && !"".equals(strErrCode)) {
				newClonedMap.put(PaymentTransactionEntity.TRADE_STATE, strErrCode);
			} else {
				newClonedMap.put(PaymentTransactionEntity.TRADE_STATE, PaymentTransactionEntity.SYSTEMERROR);
			}
			newClonedMap.put(PaymentTransactionEntity.OUT_TRADE_NO, hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO));
			boolean blnUpdateOrderRst = updateOrderInfoToTbl(newClonedMap);
//			if (!blnUpdateOrderRst) {
//				strRespCommcialRst = PaymentTransactionEntity.SYSTEMERROR;
//			}
			
			// �û�����֧����״̬ʱ��������һ�̲߳�ѯ3��
			if (strErrCode != null && strErrCode.equals(PaymentTransactionEntity.USERPAYING)) {
				// ÿ5���ѯһ�Σ�������ѯ1�ְ��ӣ�ȷ���û��Ƿ�֧���ɹ�
				ValidatePaymentResultThread vprt = new ValidatePaymentResultThread(hmTransactionOrderCont, 18, 10 * 1000);	
				new Thread(vprt).start();
			}
			
		} else { // ���׳ɹ�
			// ���÷��ظ��̻��˵�Ӧ����Ϣ
//			strSysCommRst = PaymentTransactionEntity.SUCCESS;
//			strRespCommcialRst = PaymentTransactionEntity.SUCCESS;

			// ���½��׽�������ݿ�(���׳ɹ�)
			HashMap<String, String> newClonedMap = CommonTool.getCloneMap(hmWXRespResult);
			newClonedMap.put(PaymentTransactionEntity.OUT_TRADE_NO, hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO));
			newClonedMap.put(PaymentTransactionEntity.TRADE_STATE, PaymentTransactionEntity.SUCCESS);
			boolean blnUpdateOrderRst = updateOrderInfoToTbl(newClonedMap);
//			if (!blnUpdateOrderRst) {
//				strRespCommcialRst = PaymentTransactionEntity.SYSTEMERROR;
//			}
		}
		
//		Map<String, String> mapRespCommInfo = getRespCommcialInfo(hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), CommonInfo.TBL_TRANS_ORDER);
//		strRespCommcialInfo = new String[] {hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID), mapRespCommInfo.get(PaymentTransactionEntity.OUT_TRADE_NO), 
//								mapRespCommInfo.get(PaymentTransactionEntity.FEE_TYPE), mapRespCommInfo.get(PaymentTransactionEntity.TOTAL_FEE),
//								mapRespCommInfo.get(PaymentTransactionEntity.TIME_END), mapRespCommInfo.get(PaymentTransactionEntity.TRANSACTION_ID)};
		
		// ���ر��ν��׵Ĵ��������̻��ˣ����̻��˸��ݽ��׽���������Ĳ�ѯ����
//		return orgnizeResponseInfo(strSysCommRst, strRespCommcialRst, strRespCommcialInfo);
		return strWXResponseResult;
	}
	
	/**
	 * ��ʼ�����׵���Ϣ�����ݿ⡣
	 * @param strTblName
	 * @param hmDataValues
	 * @return
	 */
	public boolean insertOrderInfoToTbl(Map<String, String> mapOrderInfo) {
		if (mapOrderInfo == null || mapOrderInfo.size() == 0) {
			return false;
		}
		
		// ����MchInfoDAO���󣬲����¸ö����е�����
		MchInfoDAO mchInfoDao = (MchInfoDAO) loadMapInfoToDAO(mapOrderInfo, MchInfoDAO.class);
		// ����MchInfoTransOrderDAO���󣬲����¸ö����е�����
		MchInfoTransOrderDAO mchInfoTransOrderDao = (MchInfoTransOrderDAO) loadMapInfoToDAO(mapOrderInfo, MchInfoTransOrderDAO.class);
		// ����TransOrderDAO���󣬲����¸ö����е�����
		TransOrderDAO transOrderDao = (TransOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderDAO.class);
		
		// ����ͻ��˷�����������ݵ����ݿ��
		if (mchInfoDao == null || mchInfoTransOrderDao == null || transOrderDao == null) {
			System.out.println("����DAO�������");
			return false;
		}
		
		
		boolean blnReturenRst = false;
		
		Connection conn = null;
		PreparedStatement preStat = null;
		ResultSet rs = null;
		try {
			conn = MysqlConnectionPool.getInstance().getConnection(false);
			
			// �ж����ݿ�����Ϣ�Ƿ��ظ�
			// �ж�tbl_mch_info��
			String strSimInqSql = getSimpleInquirySqlFromDAO(mchInfoDao, CommonInfo.TBL_MCH_INFO);
			String strMchInfoWhereArgs = " where mch_id='" + mchInfoDao.getMch_id() + "' and sub_mch_id='" + mchInfoDao.getSub_mch_id() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strMchInfoWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strMchInfoWhereArgs));
			rs = preStat.executeQuery();
			if (rs != null && !rs.next()) {	// tbl_mch_info����û�е�ǰwhere������ѯ���ļ�¼
				// �������ݵ�tbl_mch_info��
				String strSqlMchInfoDao = getInsertSqlFromDAO(mchInfoDao, CommonInfo.TBL_MCH_INFO);
				System.out.println("strSqlMchInfoDao = " + strSqlMchInfoDao);
				preStat = conn.prepareStatement(strSqlMchInfoDao);
				int iRowsMchInfo = preStat.executeUpdate();
				System.out.println("iRowsMchInfo = " + iRowsMchInfo);
			}
			
			// �ж�tbl_mch_info_trans_order��
			strSimInqSql = getSimpleInquirySqlFromDAO(mchInfoTransOrderDao, CommonInfo.TBL_MCH_INFO_TRANS_ORDER);
			String strMchInfoTransOrderWhereArgs = " where mch_id='" + mchInfoTransOrderDao.getMch_id() + "' and sub_mch_id='" 
													+ mchInfoTransOrderDao.getSub_mch_id() + "' and out_trade_no ='" 
													+ mchInfoTransOrderDao.getOut_trade_no() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strMchInfoTransOrderWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strMchInfoWhereArgs));
			rs = preStat.executeQuery();
			if (rs != null && !rs.next()) {	// tbl_mch_info_trans_order����û�е�ǰwhere������ѯ���ļ�¼
				// �������ݵ�tbl_mch_info_trans_order��
				String strSqlMchInfoTransOrderDao = getInsertSqlFromDAO(mchInfoTransOrderDao, CommonInfo.TBL_MCH_INFO_TRANS_ORDER);
				System.out.println("strSqlMchInfoTransOrderDao = " + strSqlMchInfoTransOrderDao);
				preStat = conn.prepareStatement(strSqlMchInfoTransOrderDao);
				int iRowsMchInfoTransOrder = preStat.executeUpdate();
				System.out.println("iRowsMchInfoTransOrder = " + iRowsMchInfoTransOrder);
			}
			
			// �ж�tbl_trans_order��
			strSimInqSql = getSimpleInquirySqlFromDAO(transOrderDao, CommonInfo.TBL_TRANS_ORDER);
			String strTransOrderWhereArgs = " where out_trade_no ='" + transOrderDao.getOut_trade_no() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strTransOrderWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strTransOrderWhereArgs));
			rs = preStat.executeQuery();
			if (rs != null && !rs.next()) {	// tbl_trans_order����û�е�ǰwhere������ѯ���ļ�¼
				// �������ݵ�tbl_trans_order��
				String strSqlTransOrderDao = getInsertSqlFromDAO(transOrderDao, CommonInfo.TBL_TRANS_ORDER);
				System.out.println("strSqlTransorderDao = " + strSqlTransOrderDao);
				preStat = conn.prepareStatement(strSqlTransOrderDao);
				int iRowsTransOrder = preStat.executeUpdate();
				System.out.println("iRowsTransOrder = " + iRowsTransOrder);
			}
			
			// �ύ��������
			conn.commit();
			blnReturenRst = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			// ִ��Rollback����
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			blnReturenRst = false;
			
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			
			if (preStat != null) {
				try {
					preStat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (conn != null) {
				MysqlConnectionPool.getInstance().releaseConnection(conn);
			}
		}
		
		return blnReturenRst;
	}
	
	@Override
	public boolean updateOrderInfoToTbl(HashMap<String, String> mapOrderInfo) {
		// ����TransOrderDAO���󣬲����¸ö����е�����
		TransOrderDAO transOrderDao = (TransOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderDAO.class);
				
		// ����ͻ��˷�����������ݵ����ݿ��
		if (transOrderDao == null) {
			System.out.println("����DAO�������");
			return false;
		}
		
		boolean blnUpdateRst = false;
		Connection conn = null;
		PreparedStatement preStat = null;
		try {
			String strSimpleUpdateSql = getSimpleUpdateSqlFromDAO(transOrderDao, CommonInfo.TBL_TRANS_ORDER);
			if (strSimpleUpdateSql != null && !"".equals(strSimpleUpdateSql)) {
				conn = MysqlConnectionPool.getInstance().getConnection(false);
				String strTransOrderWhereArgs = " where out_trade_no='" + transOrderDao.getOut_trade_no() + "'";
				System.out.println("updateSql = " + (strSimpleUpdateSql + strTransOrderWhereArgs));
				preStat = conn.prepareStatement(strSimpleUpdateSql + strTransOrderWhereArgs);
				int iUpdatedRows = preStat.executeUpdate();
				conn.commit();
				blnUpdateRst = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			blnUpdateRst = false;
		} finally {
			if (preStat != null) {
				try {
					preStat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (conn != null) {
				MysqlConnectionPool.getInstance().releaseConnection(conn);
			}
		}
		
		return blnUpdateRst;
	}
	
	/**
	 * У��֧���Ƿ�ɹ��Ķ����̡߳�
	 * @author xinwuhen
	 */
	private class ValidatePaymentResultThread implements Runnable {
		private HashMap<String, String> hmTransactionOrderCont = null;
		// У���������
		private int iMaxTimes = 0;
		// ÿ�μ���ļ��ʱ��(����)
		private long lMiniScnds = 0;
		
		public ValidatePaymentResultThread(HashMap<String, String> hmTransactionOrderCont, int iMaxTimes, long lMiniScnds) {
			this.hmTransactionOrderCont = hmTransactionOrderCont;
			this.iMaxTimes = iMaxTimes;
			this.lMiniScnds = lMiniScnds;
		}
		
		@Override
		public void run() {
			if (hmTransactionOrderCont != null && hmTransactionOrderCont.size() > 0) {
				StringBuffer sb = new StringBuffer();
				sb.append(InquiryTransactionEntity.AGENT_ID + "=" + hmTransactionOrderCont.get(PaymentTransactionEntity.AGENT_ID));
				sb.append("&" + InquiryTransactionEntity.APPID + "=" + hmTransactionOrderCont.get(PaymentTransactionEntity.APPID));
				sb.append("&" + InquiryTransactionEntity.MCH_ID + "=" + hmTransactionOrderCont.get(PaymentTransactionEntity.MCH_ID));
				sb.append("&" + InquiryTransactionEntity.SUB_MCH_ID + "=" + hmTransactionOrderCont.get(PaymentTransactionEntity.SUB_MCH_ID));
				sb.append("&" + InquiryTransactionEntity.OUT_TRADE_NO + "=" + hmTransactionOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO));	// ����ʱ�޸Ĵ˲���
				sb.append("&" + InquiryTransactionEntity.NONCE_STR + "=" + CommonTool.getRandomString(32));
				sb.append("&" + InquiryTransactionEntity.APP_KEY + "=" + hmTransactionOrderCont.get(PaymentTransactionEntity.APP_KEY));
				String strInquiryTransSign = CommonTool.getEntitySign(CommonTool.formatStrToMap(sb.toString()));
				sb.append("&" + InquiryTransactionEntity.SIGN + "=" + strInquiryTransSign);
				
				HashMap<String, String> hmInquiryOrder = CommonTool.formatStrToMap(sb.toString());
				
				InquiryTransactionController inquiryTransCntrl = InquiryTransactionController.getInstance();
				for (int i = 0; i < iMaxTimes; i++) {
					System.out.println("--->i = " + (i+1));

					// ��ѯ����֧�����
//					HashMap<String, String> hmInquiryRst = inquiryTransCntrl.startInquiryOrder(hmInquiryOrder);
					String strResponRst = inquiryTransCntrl.startInquiryOrder(hmInquiryOrder);
//					if (hmInquiryRst != null && hmInquiryRst.size() > 0) {
//						String strCommuRst = hmInquiryRst.get(InquiryTransactionEntity.SYSTEM_COMM_RESULT_KEY);
//						String strBizRst = hmInquiryRst.get(InquiryTransactionEntity.BUSINESS_PROC_RESULT_KEY);
//						
//						if (strCommuRst != null && strCommuRst.equals(PaymentTransactionEntity.SUCCESS) 
//								&& strBizRst != null && strBizRst.equals(PaymentTransactionEntity.SUCCESS)) {	// ֧���ɹ�
//							// �����ڲ�ѯ���׵�ʱ���ڲ�ѯ�����ڲ��Ѿ��������ݿ��(tbl_trans_order)�Ľ���״̬(trade_state)���£��ʴ˴�������Ҫ�������ݿ⣬ֱ������ѭ������
//							break;
//						}
//					}
					// ����XML��������Map��
					HashMap<String, String> hmWXRespResult = null;
					try {
						hmWXRespResult = new ParsingWXResponseXML().getMapBaseWXRespResult(strResponRst);
					} catch (ParserConfigurationException | IOException | SAXException e) {
						e.printStackTrace();
//						return orgnizeResponseInfo(RefundTransactionEntity.SUCCESS, RefundTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(RefundTransactionEntity.OUT_TRADE_NO), 
//															hmTransactionOrderCont.get(RefundTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(RefundTransactionEntity.TOTAL_FEE),
//															CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
					}
					
					String strReturnCode = hmWXRespResult.get(InquiryTransactionEntity.RETURN_CODE);
					String strResultCode = hmWXRespResult.get(InquiryTransactionEntity.RESULT_CODE);
					String strTradeStatus = hmWXRespResult.get(InquiryTransactionEntity.TRADE_STATE);
					if (strReturnCode != null && strResultCode != null && strTradeStatus != null
							&& strReturnCode.equals(InquiryTransactionEntity.SUCCESS) && strResultCode.equals(InquiryTransactionEntity.SUCCESS)
							&& strTradeStatus.equals(InquiryTransactionEntity.SUCCESS)) {
						break;
					}
					
					
					try {
						Thread.sleep(lMiniScnds);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
