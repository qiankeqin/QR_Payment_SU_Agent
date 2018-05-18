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

import org.apache.http.impl.client.CloseableHttpClient;
import org.xml.sax.SAXException;

import com.chinaepay.wx.common.CommonInfo;
import com.chinaepay.wx.common.CommonTool;
import com.chinaepay.wx.common.MysqlConnectionPool;
import com.chinaepay.wx.dao.TransOrderDAO;
import com.chinaepay.wx.dao.TransOrderReverseOrderDAO;
import com.chinaepay.wx.entity.PaymentTransactionEntity;
import com.chinaepay.wx.entity.RefundTransactionEntity;
import com.chinaepay.wx.entity.ReverseTransactionEntity;
import com.chinaepay.wx.entity.TransactionEntity;

/**
 * @author xinwuhen
 *	������ɶ����ĳ���������
 */
public class ReverseTransactoinController extends TransactionController {
private static ReverseTransactoinController reverseTransController = null;
	
	private static final String STR_REVERSE_ORDER_URL = "https://api.mch.weixin.qq.com/secapi/pay/reverse";
	
	/**
	 * ��ȡ�����Ψһʵ����
	 * @return
	 */
	public static ReverseTransactoinController getInstance() {
		if (reverseTransController == null) {
			reverseTransController = new ReverseTransactoinController();
		}
		
		return reverseTransController;
	}
	
	/**
	 * ����������ӿڡ�
	 * HashMap�ĸ�ʽ��
	 * [key]: BUSINESS_PROC_RESULT 		[value]: SUCCESS �� [΢�Ŷ���Ĵ�����]
	 * [key]: BUSINESS_RESPONSE_RESULT 	[value]: out_trade_no=1217752501201407033233368018&fee_type=USD&total_fee=888&time_end=20141030133525&transaction_id=013467007045764
	 */
	@Override
//	public HashMap<String, String> startTransactionOrder(HashMap<String, String> hmTransactionOrderCont) {
	public String startTransactionOrder(HashMap<String, String> hmTransactionOrderCont) {
//		// У�鶩���������
//		boolean blnValOrderArgs = blnValdOrderArgs(hmTransactionOrderCont);
//		if (!blnValOrderArgs) {
//			return orgnizeResponseInfo(ReverseTransactionEntity.SUCCESS, ReverseTransactionEntity.PARAM_ERROR, new String[] {hmTransactionOrderCont.get(ReverseTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(ReverseTransactionEntity.OUT_TRADE_NO), "", "", "", ""});
//		}
//		
//		// У��������Ƿ���ڲ���Ч
//		boolean blnValidAgnt = validateAgent(hmTransactionOrderCont.get(ReverseTransactionEntity.AGENT_ID));
//		
//		if (!blnValidAgnt) {
//			return orgnizeResponseInfo(ReverseTransactionEntity.SUCCESS, ReverseTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(ReverseTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(ReverseTransactionEntity.OUT_TRADE_NO), 
//					hmTransactionOrderCont.get(ReverseTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(ReverseTransactionEntity.TOTAL_FEE),
//					CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
//		}
		
		// ��������Ϣ���
		hmTransactionOrderCont.put(ReverseTransactionEntity.TRANS_TIME, CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"));
		boolean blnPersistOrderInfo = insertOrderInfoToTbl(hmTransactionOrderCont);
		hmTransactionOrderCont.remove(ReverseTransactionEntity.TRANS_TIME);
//		if (!blnPersistOrderInfo) {
//			return orgnizeResponseInfo(ReverseTransactionEntity.SUCCESS, ReverseTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(ReverseTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(ReverseTransactionEntity.OUT_TRADE_NO), 
//												hmTransactionOrderCont.get(ReverseTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(ReverseTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), hmTransactionOrderCont.get(ReverseTransactionEntity.TRANSACTION_ID)});
//		}
		
		// ���ͳ��������뵽΢�Ŷ˲���ȡӦ����Ϣ
		HashMap<String, String> hmWXRespResult = null;
		String strReturnCode = null;
		String strResultCode = null;
		String strRecall = null;
		String strWXResponseResult = null;
		int iTotalTimes = 3; // ��ʶ��΢�Ŷ�Ҫ�����·��𳷵�����ʱ��������෢��Ĵ���
		try {
			for (int i = 0; i < iTotalTimes; i++) {
				CloseableHttpClient httpclient = CommonTool.getCertHttpClient(TransactionEntity.SSL_CERT_PASSWORD);
				strWXResponseResult = this.sendReqAndGetResp(STR_REVERSE_ORDER_URL, hmTransactionOrderCont, httpclient);
//				System.out.println("*strWXResponseResult = " + strWXResponseResult);
				// ����XML��������Map��
				hmWXRespResult = new ParsingWXResponseXML().getMapBaseWXRespResult(strWXResponseResult);
				
				strReturnCode = hmWXRespResult.get(ReverseTransactionEntity.RETURN_CODE);
				strResultCode = hmWXRespResult.get(ReverseTransactionEntity.RESULT_CODE);
				strRecall = hmWXRespResult.get(ReverseTransactionEntity.RECALL);
				
				// ����Ҫ�ص�΢�Ŷ˵ĳ�������
				if (strRecall != null && strRecall.equals("N") || 
						(strReturnCode != null && strReturnCode.equals(ReverseTransactionEntity.SUCCESS) && strResultCode != null && strResultCode.equals(ReverseTransactionEntity.SUCCESS))) {
					break;
				}
				
				// ��ͣ500����
				try {
					Thread.sleep(1*500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
//			return orgnizeResponseInfo(ReverseTransactionEntity.SUCCESS, ReverseTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(ReverseTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(ReverseTransactionEntity.OUT_TRADE_NO), 
//												hmTransactionOrderCont.get(ReverseTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(ReverseTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), hmTransactionOrderCont.get(ReverseTransactionEntity.TRANSACTION_ID)});
		}
		
		
//		String strSysCommRst = null;
//		String strRespCommcialRst = null;
//		String[] strRespCommcialInfo = null;
		
		HashMap<String, String> respClonedMap = CommonTool.getCloneMap(hmWXRespResult);
		HashMap<String, String> newClonedMap = CommonTool.getAppendMap(respClonedMap, hmTransactionOrderCont);
		
		
		if (strReturnCode != null && strReturnCode.equals(ReverseTransactionEntity.SUCCESS) 
				&& strResultCode != null && strResultCode.equals(ReverseTransactionEntity.SUCCESS)) {	 // �����ɹ�
//			strSysCommRst = ReverseTransactionEntity.SUCCESS;
//			strRespCommcialRst = ReverseTransactionEntity.SUCCESS;
			
			// ���³�����������׶�����(�����ɹ�)
			newClonedMap.put(PaymentTransactionEntity.TRADE_STATE, ReverseTransactionEntity.REVOKED);
			
		} else {	// ����ʧ��
//			strSysCommRst = (strReturnCode == null || "".equals(strReturnCode)) ? ReverseTransactionEntity.FAIL : strReturnCode;
//			strRespCommcialRst = (strResultCode == null || "".equals(strResultCode)) ? ReverseTransactionEntity.SYSTEMERROR : strResultCode;
		}
		
//		Map<String, String> mapRespCommInfo = getRespCommcialInfo(hmTransactionOrderCont.get(RefundTransactionEntity.OUT_TRADE_NO), CommonInfo.TBL_TRANS_ORDER);
//		strRespCommcialInfo = new String[] {hmTransactionOrderCont.get(ReverseTransactionEntity.AGENT_ID), mapRespCommInfo.get(ReverseTransactionEntity.OUT_TRADE_NO), 
//								mapRespCommInfo.get(ReverseTransactionEntity.FEE_TYPE), mapRespCommInfo.get(ReverseTransactionEntity.TOTAL_FEE),
//								mapRespCommInfo.get(ReverseTransactionEntity.TIME_END), mapRespCommInfo.get(ReverseTransactionEntity.TRANSACTION_ID)};
		
		if (newClonedMap != null) {
			newClonedMap.put(ReverseTransactionEntity.TIME_END, CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"));
			boolean blnUpdateOrderRst = updateOrderInfoToTbl(newClonedMap);
		}
		
		// ���ر��γ����Ĵ��������̻���
//		return orgnizeResponseInfo(strSysCommRst, strRespCommcialRst, strRespCommcialInfo);
		return strWXResponseResult;
	}

	/* (non-Javadoc)
	 * @see com.chinaepay.wx.control.TransactionController#insertOrderInfoToTbl(java.util.Map)
	 */
	@Override
	public boolean insertOrderInfoToTbl(Map<String, String> mapOrderInfo) {
		if (mapOrderInfo == null || mapOrderInfo.size() == 0) {
			return false;
		}
		
		boolean blnReturenRst = false;
		
		// ����TransOrderDAO����
		TransOrderDAO transOrderDao = (TransOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderDAO.class);
		if (transOrderDao == null) {
			System.out.println("����DAO�������");
			return false;
		}
		
		Connection conn = null;
		PreparedStatement preStat = null;
		ResultSet rs = null;
		
		try {
			// �ж�tbl_trans_order�����Ƿ��д��̻�������
			String strSimInqSql = getSimpleInquirySqlFromDAO(transOrderDao, CommonInfo.TBL_TRANS_ORDER);
			String strWhereArgs = " where out_trade_no='" + transOrderDao.getOut_trade_no() + "';";
			
			conn = MysqlConnectionPool.getInstance().getConnection(false);
			preStat = conn.prepareStatement(strSimInqSql + strWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strWhereArgs));
			rs = preStat.executeQuery();
			if (rs != null && rs.next()) {	// tbl_trans_order_reverse_order����û�е�ǰwhere������ѯ���ļ�¼
				// ����TransOrderReverseOrderDAO���󣬲����¸ö����е�����
				TransOrderReverseOrderDAO tranOrderReveOrderDAO = (TransOrderReverseOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderReverseOrderDAO.class);
				if (tranOrderReveOrderDAO == null) {
					System.out.println("����DAO�������");
					return false;
				}
				
				
				// �ж����ݿ�����Ϣ�Ƿ��ظ�
				// �ж�tbl_trans_order_reverse_order��
				strSimInqSql = getSimpleInquirySqlFromDAO(tranOrderReveOrderDAO, CommonInfo.TBL_TRANS_ORDER_REVERSE_ORDER);
				strWhereArgs = " where out_trade_no='" + tranOrderReveOrderDAO.getOut_trade_no() + "';";
				preStat = conn.prepareStatement(strSimInqSql + strWhereArgs);
				System.out.println("strInqSql = " + (strSimInqSql + strWhereArgs));
				rs = preStat.executeQuery();
				if (rs != null && !rs.next()) {	// tbl_trans_order_reverse_order����û�е�ǰwhere������ѯ���ļ�¼
					// �������ݵ�tbl_trans_order_reverse_order��
					String strSqlTransReverseOrderDao = getInsertSqlFromDAO(tranOrderReveOrderDAO, CommonInfo.TBL_TRANS_ORDER_REVERSE_ORDER);
					System.out.println("strSqlTransReverseOrderDao = " + strSqlTransReverseOrderDao);
					preStat = conn.prepareStatement(strSqlTransReverseOrderDao);
					int iRowsTransReverse = preStat.executeUpdate();
					System.out.println("iRowsTransReverse = " + iRowsTransReverse);
				}
				
				// �ύ��������
				conn.commit();
				blnReturenRst = true;
			}
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
		if (mapOrderInfo == null) {
			return false;
		}
		
		boolean blnUpdateRst = false;
		Connection conn = null;
		PreparedStatement preStat = null;
		
		try {
			conn = MysqlConnectionPool.getInstance().getConnection(false);
			
			// ����tbl_trans_order���ڵĶ�������״̬
			String strTradeStat = mapOrderInfo.get(PaymentTransactionEntity.TRADE_STATE);
			if (strTradeStat != null && !"".equals(strTradeStat)) {
				String strTransOrderSql = "update " + CommonInfo.TBL_TRANS_ORDER 
											+ " set trade_state='" + strTradeStat 
											+ "' where out_trade_no='" + mapOrderInfo.get(ReverseTransactionEntity.OUT_TRADE_NO) + "';";
				
				System.out.println("updateSql = " + strTransOrderSql);
				preStat = conn.prepareStatement(strTransOrderSql);
				int iUpdatedRows = preStat.executeUpdate();
			}
			
			// ����tbl_trans_order_reverse_order���ڵ���Ϣ
			TransOrderReverseOrderDAO transReverseOrderDao = (TransOrderReverseOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderReverseOrderDAO.class);
			// ����ͻ��˷�����������ݵ����ݿ��
			if (transReverseOrderDao == null) {
				System.out.println("����DAO�������");
				return false;
			}
			
			String strSimpleUpdateSql = getSimpleUpdateSqlFromDAO(transReverseOrderDao, CommonInfo.TBL_TRANS_ORDER_REVERSE_ORDER);
			String strTransOrderWhereArgs = " where out_trade_no='" + transReverseOrderDao.getOut_trade_no() + "'";
			System.out.println("updateSql = " + (strSimpleUpdateSql + strTransOrderWhereArgs));
			preStat = conn.prepareStatement(strSimpleUpdateSql + strTransOrderWhereArgs);
			int iUpdatedRows = preStat.executeUpdate();
			
			conn.commit();
			blnUpdateRst = true;
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

}
