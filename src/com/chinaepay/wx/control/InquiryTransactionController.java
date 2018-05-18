/**
 * @author xinwuhen
 */
package com.chinaepay.wx.control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.chinaepay.wx.common.CommonInfo;
import com.chinaepay.wx.common.CommonTool;
import com.chinaepay.wx.common.MysqlConnectionPool;
import com.chinaepay.wx.dao.TransOrderDAO;
import com.chinaepay.wx.entity.InquiryTransactionEntity;
import com.chinaepay.wx.entity.PaymentTransactionEntity;

/**
 * @author xinwuhen
 *
 */
public class InquiryTransactionController extends InquiryController {
	private static InquiryTransactionController inquiryOrderCntrl = null;
	
	private static final String INQUIRY_TRANSACTION_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	
	/**
	 * ��ȡ�����Ψһʵ����
	 * @return
	 */
	public static InquiryTransactionController getInstance() {
		if (inquiryOrderCntrl == null) {
			inquiryOrderCntrl = new InquiryTransactionController();
		}
		return inquiryOrderCntrl;
	}
	
	
	/**
	 * ���ظ��̻��ı��ĸ�ʽ�����£�
	 * HashMap�ĸ�ʽ��
	 * [key]: BUSINESS_PROC_RESULT 		[value]: SUCCESS �� [΢�Ŷ���Ĵ�����]
	 * [key]: BUSINESS_RESPONSE_RESULT 	[value]: out_trade_no=1217752501201407033233368018&fee_type=USD&total_fee=888&time_end=20141030133525&transaction_id=013467007045764
	 */
//	public HashMap<String, String> startInquiryOrder(HashMap<String, String> hmInquiryOrderCont) {
	public String startInquiryOrder(HashMap<String, String> hmInquiryOrderCont) {
//		// У���ѯ�ඩ���Ĳ���
//		boolean blnValOrderArgs = blnValdOrderArgs(hmInquiryOrderCont);
//		if (!blnValOrderArgs) {
//			return orgnizeResponseInfo(InquiryTransactionEntity.SUCCESS, InquiryTransactionEntity.PARAM_ERROR, new String[] {hmInquiryOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmInquiryOrderCont.get(InquiryTransactionEntity.OUT_TRADE_NO), "", "", "", ""});
//		}
//		
//		// У��������Ƿ���ڲ���Ч
//		boolean blnValidAgnt = validateAgent(hmInquiryOrderCont.get(PaymentTransactionEntity.AGENT_ID));
//		
//		if (!blnValidAgnt) {
//			return orgnizeResponseInfo(PaymentTransactionEntity.SUCCESS, PaymentTransactionEntity.SYSTEMERROR, new String[] {hmInquiryOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmInquiryOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), 
//					hmInquiryOrderCont.get(PaymentTransactionEntity.FEE_TYPE), hmInquiryOrderCont.get(PaymentTransactionEntity.TOTAL_FEE),
//					CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
//		}
//		
//		// ���������̼��ն��̻�
//		refferAgentAndSubMerchant(hmInquiryOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmInquiryOrderCont.get(PaymentTransactionEntity.SUB_MCH_ID));
		
		// ��΢�ź�̨���н��׶Խӣ���ȡ΢�ŵ�ʵʱӦ����, ������΢�Ŷ�Ӧ�������в�ͬ��ҵ����
		String strWXResponseResult = this.sendReqAndGetResp(INQUIRY_TRANSACTION_ORDER_URL, hmInquiryOrderCont, CommonTool.getDefaultHttpClient());
		System.out.println(">>>>strWXResponseResult" + strWXResponseResult);
		// ����XML��������Map��
		HashMap<String, String> hmWXRespResult = null;
		try {
			hmWXRespResult = new ParsingWXResponseXML().getMapBaseWXRespResult(strWXResponseResult);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
//			return orgnizeResponseInfo(InquiryTransactionEntity.SUCCESS, InquiryTransactionEntity.SYSTEMERROR, new String[] {hmInquiryOrderCont.get(PaymentTransactionEntity.AGENT_ID), hmInquiryOrderCont.get(InquiryTransactionEntity.OUT_TRADE_NO), 
//												hmInquiryOrderCont.get(InquiryTransactionEntity.FEE_TYPE), hmInquiryOrderCont.get(InquiryTransactionEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
		}
		
		// У��΢�Ŷ˷��ص�Ӧ����Ϣ
		String strInquiryReturnCode = hmWXRespResult.get(InquiryTransactionEntity.RETURN_CODE);
		String strInquiryResultCode = hmWXRespResult.get(InquiryTransactionEntity.RESULT_CODE);
		String strInquiryErrCode = hmWXRespResult.get(InquiryTransactionEntity.ERR_CODE);
		String strTradeState = hmWXRespResult.get(InquiryTransactionEntity.TRADE_STATE);
		
		
//		String strSysCommRst = null;
//		String strProcResult = null;
//		String[] strRespResult = null;
//		if ((strInquiryReturnCode != null && strInquiryResultCode != null && strTradeState != null)
//				&&
//			(strInquiryReturnCode.equals(InquiryTransactionEntity.SUCCESS) && strInquiryResultCode.equals(InquiryTransactionEntity.SUCCESS) && strTradeState.equals(InquiryTransactionEntity.SUCCESS))) {
//			// ���·��ظ��̻���Ӧ����
//			strSysCommRst = InquiryTransactionEntity.SUCCESS;
//			strProcResult = strTradeState;
//		} else {
//			// �ж�ͨ�ż�ϵͳ״̬(������ͨ����·�Ƿ������Լ������Ƿ���ڵ�)
//			if (strInquiryReturnCode != null && strInquiryReturnCode.equals(InquiryTransactionEntity.SUCCESS) && strInquiryResultCode != null && strInquiryResultCode.equals(InquiryTransactionEntity.SUCCESS)) {
//				strSysCommRst = InquiryTransactionEntity.SUCCESS;
//			} else {
//				if (strInquiryErrCode != null && !"".equals(strInquiryErrCode)) {
//					strSysCommRst = strInquiryErrCode;
//				} else {
//					strSysCommRst = InquiryTransactionEntity.SYSTEMERROR;
//				}
//			}
//			
//			// �ж϶�������״̬
//			if (strTradeState != null && !"".equals(strTradeState)) {
//				strProcResult = strTradeState;
//			}  else {
//				strProcResult = InquiryTransactionEntity.SYSTEMERROR;
//			}
//		}
		
		// ���¶���״̬�����׶�����
		boolean blnUpdateOrder = updateOrderInfoToTbl(hmWXRespResult);
//		if (!blnUpdateOrder) {
//			strSysCommRst = InquiryTransactionEntity.SUCCESS;
//			strProcResult = InquiryTransactionEntity.SYSTEMERROR;
//		}
		
//		Map<String, String> mapRespCommInfo = getRespCommcialInfo(hmInquiryOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), CommonInfo.TBL_TRANS_ORDER);
//		strRespResult = new String[] {hmInquiryOrderCont.get(PaymentTransactionEntity.AGENT_ID), mapRespCommInfo.get(InquiryTransactionEntity.OUT_TRADE_NO), 
//								mapRespCommInfo.get(InquiryTransactionEntity.FEE_TYPE), mapRespCommInfo.get(InquiryTransactionEntity.TOTAL_FEE),
//								mapRespCommInfo.get(InquiryTransactionEntity.TIME_END), mapRespCommInfo.get(InquiryTransactionEntity.TRANSACTION_ID)};
//		
//		return orgnizeResponseInfo(strSysCommRst, strProcResult, strRespResult);
		
		return strWXResponseResult;
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
			}
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
