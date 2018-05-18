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
import com.chinaepay.wx.entity.InquiryRefundEntity;
import com.chinaepay.wx.entity.PaymentTransactionEntity;

/**
 * @author xinwuhen
 *
 */
public class InquiryRefundController extends InquiryController {
	private static InquiryRefundController inquiryRefundContrl = null;
	
	private static final String INQUIRY_REFUND_ORDER_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
	
	/**
	 * ��ȡ�����Ψһʵ����
	 * @return
	 */
	public static InquiryRefundController getInstance() {
		if (inquiryRefundContrl == null) {
			inquiryRefundContrl = new InquiryRefundController();
		}
		return inquiryRefundContrl;
	}

	@Override
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
//			return orgnizeResponseInfo(InquiryRefundEntity.SUCCESS, InquiryRefundEntity.PARAM_ERROR, new String[] {hmInquiryOrderCont.get(InquiryRefundEntity.AGENT_ID), hmInquiryOrderCont.get(InquiryRefundEntity.OUT_TRADE_NO), "", "", "", ""});
//		}
//		
//		// У��������Ƿ���ڲ���Ч
//		boolean blnValidAgnt = validateAgent(hmInquiryOrderCont.get(InquiryRefundEntity.AGENT_ID));
//		
//		if (!blnValidAgnt) {
//			return orgnizeResponseInfo(PaymentTransactionEntity.SUCCESS, InquiryRefundEntity.SYSTEMERROR, new String[] {hmInquiryOrderCont.get(InquiryRefundEntity.AGENT_ID), hmInquiryOrderCont.get(InquiryRefundEntity.OUT_TRADE_NO), 
//					hmInquiryOrderCont.get(InquiryRefundEntity.FEE_TYPE), hmInquiryOrderCont.get(InquiryRefundEntity.TOTAL_FEE),
//					CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), ""});
//		}
		
		// ��΢�ź�̨���н��׶Խӣ���ȡ΢�ŵ�ʵʱӦ����, ������΢�Ŷ�Ӧ�������в�ͬ��ҵ����
		String strWXResponseResult = this.sendReqAndGetResp(INQUIRY_REFUND_ORDER_URL, hmInquiryOrderCont, CommonTool.getDefaultHttpClient());
//		System.out.println(">>>>strWXResponseResult" + strWXResponseResult);
		// ����XML��������Map��
		HashMap<String, String> hmWXRespResult = null;
		try {
			hmWXRespResult = new ParsingWXResponseXML().getMapBaseWXRespResult(strWXResponseResult);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
//			return orgnizeResponseInfo(InquiryRefundEntity.SUCCESS, InquiryRefundEntity.SYSTEMERROR, new String[] {hmInquiryOrderCont.get(InquiryRefundEntity.AGENT_ID), hmInquiryOrderCont.get(InquiryRefundEntity.OUT_TRADE_NO), 
//												hmInquiryOrderCont.get(InquiryRefundEntity.FEE_TYPE), hmInquiryOrderCont.get(InquiryRefundEntity.TOTAL_FEE),
//												CommonTool.getFormatDate(new Date(), "yyyyMMddHHmmss"), hmInquiryOrderCont.get(InquiryRefundEntity.TRANSACTION_ID)});
		}
		
//		// У��΢�Ŷ˷��ص�Ӧ����Ϣ
//		String strInquiryReturnCode = hmWXRespResult.get(InquiryRefundEntity.RETURN_CODE);
//		String strInquiryResultCode = hmWXRespResult.get(InquiryRefundEntity.RESULT_CODE);
//		String strInquiryErrCode = hmWXRespResult.get(InquiryRefundEntity.ERR_CODE);
//		String strRefundResult = getValBaseExtKey(hmWXRespResult, InquiryRefundEntity.REFUND_STATUS);
//		
//		String strSysCommRst = null;
//		String strProcResult = null;
//		String[] strRespResult = null;
//		if ((strInquiryReturnCode != null && strInquiryResultCode != null && strRefundResult != null)
//				&&
//			(strInquiryReturnCode.equals(InquiryRefundEntity.SUCCESS) && strInquiryResultCode.equals(InquiryRefundEntity.SUCCESS) && strRefundResult.equals(InquiryRefundEntity.SUCCESS))) {
//			// ���·��ظ��̻���Ӧ����
//			strSysCommRst = InquiryRefundEntity.SUCCESS;
//			strProcResult = strRefundResult;
//		} else {
//			// �ж�ͨ�ż�ϵͳ״̬(������ͨ����·�Ƿ������Լ������Ƿ���ڵ�)
//			if (strInquiryReturnCode != null && strInquiryReturnCode.equals(InquiryRefundEntity.SUCCESS) && strInquiryResultCode != null && strInquiryResultCode.equals(InquiryRefundEntity.SUCCESS)) {
//				strSysCommRst = InquiryRefundEntity.SUCCESS;
//			} else {
//				if (strInquiryErrCode != null && !"".equals(strInquiryErrCode)) {
//					strSysCommRst = strInquiryErrCode;
//				} else {
//					strSysCommRst = InquiryRefundEntity.SYSTEMERROR;
//				}
//			}
//			
//			// �ж϶�������״̬
//			if (strRefundResult != null && !"".equals(strRefundResult)) {
//				strProcResult = strRefundResult;
//			}  else {
//				strProcResult = InquiryRefundEntity.SYSTEMERROR;
//			}
//		}
		
		// ���¶���״̬�����׶�����
		boolean blnUpdateOrder = updateOrderInfoToTbl(hmWXRespResult);
//		if (!blnUpdateOrder) {
//			strSysCommRst = InquiryRefundEntity.SUCCESS;
//			strProcResult = InquiryRefundEntity.SYSTEMERROR;
//		}
//		
//		Map<String, String> mapRespCommInfo = getRespCommcialInfo(hmInquiryOrderCont.get(PaymentTransactionEntity.OUT_TRADE_NO), CommonInfo.TBL_REFUND_ORDER);
//		strRespResult = new String[] {hmInquiryOrderCont.get(InquiryRefundEntity.AGENT_ID), mapRespCommInfo.get(InquiryRefundEntity.OUT_TRADE_NO), 
//										mapRespCommInfo.get(InquiryRefundEntity.FEE_TYPE), mapRespCommInfo.get(InquiryRefundEntity.TOTAL_FEE),
//										mapRespCommInfo.get(InquiryRefundEntity.TIME_END), mapRespCommInfo.get(InquiryRefundEntity.TRANSACTION_ID)};
//		
//		return orgnizeResponseInfo(strSysCommRst, strProcResult, strRespResult);
		
		return strWXResponseResult; 
	}

	@Override
	public boolean updateOrderInfoToTbl(HashMap<String, String> mapOrderInfo) {
		boolean blnUpdateRst = false;
		Connection conn = null;
		PreparedStatement preStat = null;
		String strFinalUpSql = "update " + CommonInfo.TBL_REFUND_ORDER 
								+ " set refund_status='" + castNullToBlank(getValBaseExtKey(mapOrderInfo, InquiryRefundEntity.REFUND_STATUS)) 
								+ "', refund_channel='" + castNullToBlank(getValBaseExtKey(mapOrderInfo, InquiryRefundEntity.REFUND_CHANNEL)) 
								+ "', refund_success_time='" + castNullToBlank(getValBaseExtKey(mapOrderInfo, InquiryRefundEntity.REFUND_SUCCESS_TIME)) 
								+ "', refund_recv_accout='" + castNullToBlank(getValBaseExtKey(mapOrderInfo, InquiryRefundEntity.REFUND_RECV_ACCOUT)) 
								+ "', rate='" + castNullToBlank(mapOrderInfo.get(InquiryRefundEntity.RATE)) 
								+ "' where out_refund_no='" + castNullToBlank(getValBaseExtKey(mapOrderInfo, InquiryRefundEntity.OUT_REFUND_NO))
								+ "' and out_trade_no='" + castNullToBlank(mapOrderInfo.get(InquiryRefundEntity.OUT_TRADE_NO)) + "';";
		System.out.println("strFinalUpSql = " + strFinalUpSql);
		try {
			conn = MysqlConnectionPool.getInstance().getConnection(false);
			preStat = conn.prepareStatement(strFinalUpSql);
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
	
	/**
	 * ��ѯ�˿ʱ�����΢�ŷ��صĴ����±�($n���磺refund_success_time_$n)���ֶ�ȡֵʱ�������е����⴦��
	 * @param map
	 * @param strKey
	 * @return
	 */
	private String getValBaseExtKey(HashMap<String, String> map, String strKey) {
		if (map == null || strKey == null || "".equals(strKey)) {
			return "";
		}
		
		String strVal = "";
		
		// �˿�ʱ΢���������һ�ν���������������˿��, Ĭ��Ϊ��50
		final int MAX_EXT_NUM = 50;
		String strFullKey = null;
		for (int i = 0; i < MAX_EXT_NUM; i++) {
			strFullKey = strKey + "_" + i;
			if (map.containsKey(strFullKey)) {
				strVal = map.get(strFullKey);
				break;
			}
		}
		
		return strVal;
	}
	
	/**
	 * ��Null�ַ���ת��Ϊ������
	 * @param strSrc
	 * @return
	 */
	private String castNullToBlank(String strSrc) {
		return strSrc == null ? "" : strSrc;
	}

}
