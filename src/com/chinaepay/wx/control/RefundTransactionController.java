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
import com.chinaepay.wx.dao.MchInfoDAO;
import com.chinaepay.wx.dao.RefundOrderDAO;
import com.chinaepay.wx.dao.TransOrderDAO;
import com.chinaepay.wx.dao.TransOrderRefundOrderDAO;
import com.chinaepay.wx.entity.PaymentTransactionEntity;
import com.chinaepay.wx.entity.RefundTransactionEntity;
import com.chinaepay.wx.entity.TransactionEntity;

/**
 * @author xinwuhen
 *	������ɶ������˿������
 */
public class RefundTransactionController extends TransactionController {
	private static RefundTransactionController refundOrderCntrl = null;
	
	private static final String STR_REFUND_ORDER_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	
	/**
	 * ��ȡ�����Ψһʵ����
	 * @return
	 */
	public static RefundTransactionController getInstance() {
		if (refundOrderCntrl == null) {
			refundOrderCntrl = new RefundTransactionController();
		}
		
		return refundOrderCntrl;
	}

	@Override
	/**
	 * �˷��������̻��ĶԽӽӿڣ������ڳ��˴������е��׸�ͨ��΢�ź�̨��ҵ���߼��⣬��Ҫ���ظ����̻����׸�֮ͨ��Լ���ı��ġ����У����ظ��̻��ı��ĸ�ʽ�����£�
	 * HashMap�ĸ�ʽ��
	 * [key]: BUSINESS_PROC_RESULT 		[value]: SUCCESS �� [΢�Ŷ���Ĵ�����]
	 * [key]: BUSINESS_RESPONSE_RESULT 	[value]: out_trade_no=1217752501201407033233368018&fee_type=USD&total_fee=888&time_end=20141030133525&transaction_id=013467007045764
	 */
//	public HashMap<String, String> startTransactionOrder(HashMap<String, String> hmTransactionOrderCont) {
	public String startTransactionOrder(HashMap<String, String> hmTransactionOrderCont) {
//		// ֧����������У��
//		boolean blnValOrderArgs = blnValdOrderArgs(hmTransactionOrderCont);
//		if (!blnValOrderArgs) {
//			return orgnizeResponseInfo(RefundTransactionEntity.SUCCESS, RefundTransactionEntity.PARAM_ERROR, new String[] {hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID), RefundTransactionEntity.OUT_TRADE_NO, "", "", "", ""});
//		}
//		
//		// У��������Ƿ���ڲ���Ч
//		boolean blnValidAgnt = validateAgent(hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID));
//		
//		if (!blnValidAgnt) {
//			return orgnizeResponseInfo(RefundTransactionEntity.SUCCESS, RefundTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(RefundTransactionEntity.OUT_TRADE_NO), 
//					hmTransactionOrderCont.get(RefundTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(RefundTransactionEntity.TOTAL_FEE),
//					CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
//		}		
		
		// ��ʼ������Ϣ�־û���DB
		hmTransactionOrderCont.put(RefundTransactionEntity.TRANS_TIME, CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"));
		boolean blnPersistOrderInfo = insertOrderInfoToTbl(hmTransactionOrderCont);
		System.out.println("blnPersistOrderInfo = " + blnPersistOrderInfo);
		hmTransactionOrderCont.remove(RefundTransactionEntity.TRANS_TIME);
//		if (!blnPersistOrderInfo) {
//			return orgnizeResponseInfo(RefundTransactionEntity.SUCCESS, RefundTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(RefundTransactionEntity.OUT_TRADE_NO), 
//												hmTransactionOrderCont.get(RefundTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(RefundTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), hmTransactionOrderCont.get(RefundTransactionEntity.TRANSACTION_ID)});
//		}
		
		// ��΢�ź�̨���н��׶Խӣ���ȡ΢�ŵ�ʵʱӦ����, ������΢�Ŷ�Ӧ�������в�ͬ��ҵ����
		CloseableHttpClient httpclient = CommonTool.getCertHttpClient(TransactionEntity.SSL_CERT_PASSWORD);
		String strWXResponseResult = this.sendReqAndGetResp(STR_REFUND_ORDER_URL, hmTransactionOrderCont, httpclient);
//		System.out.println("strWXResponseResult = " + strWXResponseResult);
		// ����XML��������Map��
		HashMap<String, String> hmWXRespResult = null;
		try {
			hmWXRespResult = new ParsingWXResponseXML().getMapBaseWXRespResult(strWXResponseResult);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
//			return orgnizeResponseInfo(RefundTransactionEntity.SUCCESS, RefundTransactionEntity.SYSTEMERROR, new String[] {hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID), hmTransactionOrderCont.get(RefundTransactionEntity.OUT_TRADE_NO), 
//												hmTransactionOrderCont.get(RefundTransactionEntity.FEE_TYPE), hmTransactionOrderCont.get(RefundTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
		}
		
		String strReturnCode = hmWXRespResult.get(RefundTransactionEntity.RETURN_CODE);
		String strResultCode = hmWXRespResult.get(RefundTransactionEntity.RESULT_CODE);
		String strErrCode = hmWXRespResult.get(RefundTransactionEntity.ERR_CODE);
		
//		String strSysCommRst = null;
//		String strRespCommcialRst = null;
//		String[] strRespCommcialInfo = null;
		
		
		HashMap<String, String> newClonedMap = CommonTool.getCloneMap(hmWXRespResult);
		// �˿�����ʧ��
		if (strReturnCode == null || strResultCode == null
				|| !strReturnCode.equals(RefundTransactionEntity.SUCCESS) || !strResultCode.equals(RefundTransactionEntity.SUCCESS)) {
//			// �ж�ͨ�ż�ϵͳ״̬
//			strSysCommRst = (strReturnCode == null || "".equals(strReturnCode)) ? RefundTransactionEntity.SYSTEMERROR : strReturnCode;
//			
//			// ����֧��ҵ�����ʧ��
//			strRespCommcialRst = (strErrCode == null || "".equals(strErrCode)) ? RefundTransactionEntity.SYSTEMERROR : strErrCode;
		} else { // �˿�����ɹ�
//			// ���÷��ظ��̻��˵�Ӧ����Ϣ
//			strSysCommRst = RefundTransactionEntity.SUCCESS;
//			strRespCommcialRst = RefundTransactionEntity.SUCCESS;
			
			// ���½��׵�״̬Ϊ��ת���˿
			newClonedMap.put(PaymentTransactionEntity.TRADE_STATE, PaymentTransactionEntity.REFUND);
		}
		
		boolean blnUpdateOrderRst = updateOrderInfoToTbl(newClonedMap);
		
//		Map<String, String> mapRespCommInfo = getRespCommcialInfo(hmTransactionOrderCont.get(RefundTransactionEntity.OUT_TRADE_NO), CommonInfo.TBL_REFUND_ORDER);
//		strRespCommcialInfo = new String[] {hmTransactionOrderCont.get(RefundTransactionEntity.AGENT_ID), mapRespCommInfo.get(RefundTransactionEntity.OUT_TRADE_NO), 
//								mapRespCommInfo.get(RefundTransactionEntity.FEE_TYPE), mapRespCommInfo.get(RefundTransactionEntity.TOTAL_FEE),
//								mapRespCommInfo.get(RefundTransactionEntity.TIME_END), mapRespCommInfo.get(RefundTransactionEntity.TRANSACTION_ID)};
//		
//		// ���ر��ν��׵Ĵ��������̻��ˣ����̻��˸��ݽ��׽���������Ĳ�ѯ����
//		return orgnizeResponseInfo(strSysCommRst, strRespCommcialRst, strRespCommcialInfo);
		
		return strWXResponseResult;
	}
	
	@Override
	public boolean insertOrderInfoToTbl(Map<String, String> mapOrderInfo) {
		if (mapOrderInfo == null || mapOrderInfo.size() == 0) {
			return false;
		}
		
		// ����MchInfoDAO����
		MchInfoDAO mchInfoDao = (MchInfoDAO) loadMapInfoToDAO(mapOrderInfo, MchInfoDAO.class);
		// ����MchInfoTransOrderDAO����
		// MchInfoTransOrderDAO mchInfoTransOrderDao = (MchInfoTransOrderDAO) loadMapInfoToDAO(mapOrderInfo, MchInfoTransOrderDAO.class);
		// ����TransOrderDAO����
		TransOrderDAO transOrderDao = (TransOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderDAO.class);
		// ����ͻ��˷�����������ݵ����ݿ��
		if (mchInfoDao == null || /** mchInfoTransOrderDao == null || **/ transOrderDao == null) {
			System.out.println("����DAO�������");
			return false;
		}
		
		boolean blnReturenRst = false;
		
		Connection conn = null;
		PreparedStatement preStat = null;
		ResultSet rs = null;
		try {
			conn = MysqlConnectionPool.getInstance().getConnection(false);
			
			// �ж����ݿ����Ƿ�����̻��š����̻��š��̻������ŵ���Ϣ
			// �ж�tbl_mch_info�����̻��š����̻����Ƿ����
			String strSimInqSql = getSimpleInquirySqlFromDAO(mchInfoDao, CommonInfo.TBL_MCH_INFO);
			String strMchInfoWhereArgs = " where mch_id='" + mchInfoDao.getMch_id() + "' and sub_mch_id='" + mchInfoDao.getSub_mch_id() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strMchInfoWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strMchInfoWhereArgs));
			rs = preStat.executeQuery();
			// tbl_mch_info�����޴��̻��������̻���
			if (!rs.next()) {	
				blnReturenRst = false;
				return blnReturenRst;
			}
				
			// �ж�tbl_trans_order�����̻��������Ƿ����
			strSimInqSql = getSimpleInquirySqlFromDAO(transOrderDao, CommonInfo.TBL_TRANS_ORDER);
			String strTransOrderWhereArgs = " where out_trade_no ='" + transOrderDao.getOut_trade_no() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strTransOrderWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strTransOrderWhereArgs));
			rs = preStat.executeQuery();
			// tbl_trans_order�����޴��̻�������
			if (!rs.next()) {	
				blnReturenRst = false;
				return blnReturenRst;
			}
			
			
			// �ж�tbl_trans_order_refund_order���Ƿ���ڶ�Ӧ�ļ�¼
			TransOrderRefundOrderDAO tranRefundOrderDAO = (TransOrderRefundOrderDAO) loadMapInfoToDAO(mapOrderInfo, TransOrderRefundOrderDAO.class);
			strSimInqSql = getSimpleInquirySqlFromDAO(tranRefundOrderDAO, CommonInfo.TBL_TRANS_ORDER_REFUND_ORDER);
			String strTransRefundOrderWhereArgs = " where out_trade_no ='" + tranRefundOrderDAO.getOut_trade_no() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strTransRefundOrderWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strTransRefundOrderWhereArgs));
			rs = preStat.executeQuery();
			if (!rs.next()) {
				// �������ݵ�tbl_trans_order_refund_order��
				String strSqlTransRefundOrderDao = getInsertSqlFromDAO(tranRefundOrderDAO, CommonInfo.TBL_TRANS_ORDER_REFUND_ORDER);
				System.out.println("strSqlTransRefundOrderDao = " + strSqlTransRefundOrderDao);
				preStat = conn.prepareStatement(strSqlTransRefundOrderDao);
				int iRowsTransRefundOrder = preStat.executeUpdate();
				System.out.println("iRowsTransRefundOrder = " + iRowsTransRefundOrder);
			}
			
			// �ж�tbl_refund_order���Ƿ����֧��������Ӧ���˿��¼
			RefundOrderDAO refundOrderDAO = (RefundOrderDAO) loadMapInfoToDAO(mapOrderInfo, RefundOrderDAO.class);
			strSimInqSql = getSimpleInquirySqlFromDAO(refundOrderDAO, CommonInfo.TBL_REFUND_ORDER);
			String strRefundOrderWhereArgs = " where out_trade_no ='" + refundOrderDAO.getOut_trade_no() + "';";
			preStat = conn.prepareStatement(strSimInqSql + strRefundOrderWhereArgs);
			System.out.println("strInqSql = " + (strSimInqSql + strRefundOrderWhereArgs));
			rs = preStat.executeQuery();
			
			if (rs.next()) { // ���ڸ��̻������ŵļ�¼
				String strRefundId = rs.getString("refund_id");
				if (strRefundId != null && !"".equals(strRefundId)) { // ����΢�Ŷ˻�δ�����˿��refund_id�������˿����뻹δ��΢�Ŷ˳ɹ����ܣ�
					blnReturenRst = false;
					return blnReturenRst;
				} else {
					// ɾ���ɵļ�¼
					strSimInqSql = "delete from " + CommonInfo.TBL_REFUND_ORDER + " where out_refund_no='" + refundOrderDAO.getOut_refund_no() + "' and out_trade_no='" + refundOrderDAO.getOut_trade_no() + "';" ;
					preStat = conn.prepareStatement(strSimInqSql);
					System.out.println("strInqSql = " + strSimInqSql);
					int iDelRows = preStat.executeUpdate();
				}
			}
			
			// �������ݵ�tbl_refund_order�� 
			String strSqlRefundOrderDao = getInsertSqlFromDAO(refundOrderDAO, CommonInfo.TBL_REFUND_ORDER);
			System.out.println("strSqlRefundOrderDao = " + strSqlRefundOrderDao);
			preStat = conn.prepareStatement(strSqlRefundOrderDao);
			int iRowsRefundOrder = preStat.executeUpdate();
			System.out.println("iRowsRefundOrder = " + iRowsRefundOrder);
			
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
											+ "' where out_trade_no='" + mapOrderInfo.get(RefundTransactionEntity.OUT_TRADE_NO) + "';";
				System.out.println("updateSql = " + strTransOrderSql);
				preStat = conn.prepareStatement(strTransOrderSql);
				int iUpdatedRows = preStat.executeUpdate();
			}
						
			// ����tbl_refund_order���ڵĶ�������״̬
			RefundOrderDAO refundOrderDAO = (RefundOrderDAO) loadMapInfoToDAO(mapOrderInfo, RefundOrderDAO.class);
			// ����ͻ��˷�����������ݵ����ݿ��
			if (refundOrderDAO == null) {
				System.out.println("����DAO�������");
				return false;
			}
			
			String strSimpleUpdateSql = getSimpleUpdateSqlFromDAO(refundOrderDAO, CommonInfo.TBL_REFUND_ORDER);
			if (strSimpleUpdateSql != null && !"".equals(strSimpleUpdateSql)) {
				String strRefundOrderWhereArgs = " where out_refund_no='" + refundOrderDAO.getOut_refund_no() + "' and out_trade_no='" + refundOrderDAO.getOut_trade_no() + "';";
				System.out.println("updateSql = " + (strSimpleUpdateSql + strRefundOrderWhereArgs));
				preStat = conn.prepareStatement(strSimpleUpdateSql + strRefundOrderWhereArgs);
				int iUpdatedRows = preStat.executeUpdate();
			}
			
			// �ύ����
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
