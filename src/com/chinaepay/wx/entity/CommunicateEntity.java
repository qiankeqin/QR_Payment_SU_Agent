/**
 * @author xinwuhen
 */
package com.chinaepay.wx.entity;

/**
 * @author xinwuhen
 *
 */
public class CommunicateEntity {
	/** ���³��������̻����׸�֮ͨ��Ľӿڶ��� **/
	// ϵͳ�������
	public static final String SYSTEM_COMM_RESULT_KEY = "SYSTEM_COMM_RESULT";	// ��ֵΪ��SUCCESS ��  FAIL
	// ҵ�����Ƿ�ɹ��ı�ʶ��ֵΪ: SUCCESS �� [΢���ṩ�Ĵ�����]
	public static final String BUSINESS_PROC_RESULT_KEY = "BUSINESS_PROC_RESULT";
	// ��ʶÿ�ν��׻��ѯ�������ý���ķ��ر��ĵ�
	public static final String BUSINESS_RESPONSE_RESULT = "BUSINESS_RESPONSE_RESULT";
	
	/** ���³�����ʶ�������еĲ����� **/
	// ������ID
	public static final String AGENT_ID = "agent_id";
	// �����˺�ID
	public static final String APPID = "appid";
	// �̻���
	public static final String MCH_ID = "mch_id";
	// ���̻���
	public static final String SUB_MCH_ID = "sub_mch_id";
	// ΢�Ŷ�����
	public static final String TRANSACTION_ID = "transaction_id";
	// �̻�������
	public static final String OUT_TRADE_NO = "out_trade_no";
	// �̻��˿��
	public static final String OUT_REFUND_NO = "out_refund_no";
	// ����ַ���
	public static final String NONCE_STR = "nonce_str";
	// ǩ��
	public static final String SIGN = "sign";
	// ��Ʒ����	
	public static final String BODY = "body";
	// �̻���KEY
	public static final String APP_KEY = "app_key";
	// ��۽��
	public static final String TOTAL_FEE = "total_fee";
	// �˿���
	public static final String REFUND_FEE = "refund_fee";
	// ��۱���
	public static final String FEE_TYPE = "fee_type";
	// �ն�IP
	public static final String SPBILL_CREATE_IP = "spbill_create_ip";
	// ��Ȩ��
	public static final String AUTH_CODE = "auth_code";
	// �û���ʶ
	public static final String OPEN_ID = "openid";
	// ��������
	public static final String TRADE_TYPE = "trade_type";
	// ��������
	public static final String BANK_TYPE = "bank_type";
	// �ֽ�֧������	
	public static final String CASH_FEE_TYPE = "cash_fee_type";
	// �ֽ�֧�����	
	public static final String CASH_FEE = "cash_fee";
	// ����	
	public static final String RATE = "rate";
	// �˿�״̬
	public static final String REFUND_STATUS = "refund_status";
	// �˿�����
	public static final String REFUND_CHANNEL = "refund_channel";
	// �˿������˻�
	public static final String REFUND_RECV_ACCOUT = "refund_recv_accout";
	
	/** ���³�����ʶӦ�����еĲ����� **/
	// ����״̬�룺���ֶ���ͨ�ű�ʶ���ǽ��ױ�ʶ�������Ƿ�ɹ���Ҫ�鿴result_code���ж�
	public static final String RETURN_CODE = "return_code";
	// ҵ����	
	public static final String RESULT_CODE = "result_code";
	// ͨ�Ż�ҵ���׽�����ɹ� �磺֧���ɹ�
	public static final String SUCCESS = "SUCCESS";
	// �������
	public static final String ERR_CODE = "err_code";
	// ͨ�Ż�ҵ���׽����ʧ��
	public static final String FAIL = "FAIL";
	// ���׷���ʱ��
	public static final String TRANS_TIME = "trans_time";
	// ���ʱ��
	public static final String TIME_END = "time_end";
	// �˿�ɹ�ʱ��
	public static final String REFUND_SUCCESS_TIME = "refund_success_time";
	// ����״̬	
	public static final String  TRADE_STATE = "trade_state";
	// �ӿڷ��ش���
	public static final String SYSTEMERROR = "SYSTEMERROR";
	// �û�֧���У���Ҫ��������
	public static final String USERPAYING = "USERPAYING";
	// ����ϵͳ�쳣
	public static final String BANKERROR = "BANKERROR";
	// ��������
	public static final String PARAM_ERROR = "PARAM_ERROR";
	// ת���˿�
	public static final String REFUND = "REFUND";
	// δ֧��
	public static final String NOTPAY = "NOTPAY";
	// �ѹر�
	public static final String CLOSED = "CLOSED";
	// �ѳ���(ˢ��֧��)
	public static final String REVOKED = "REVOKED";
	// ֧��ʧ��(����ԭ�������з���ʧ��)
	public static final String PAYERROR = "PAYERROR";
	// �˽��׶����Ų�����
	public static final String ORDERNOTEXIST = "ORDERNOTEXIST";
	// �Ƿ��ص�	
	public static final String RECALL = "recall";
}
