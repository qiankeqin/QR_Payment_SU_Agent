/**
 * @author xinwuhen
 */
package com.chinaepay.wx.common;

/**
 * @author xinwuhen
 * ������Ҫ�洢�̶������ò�����Ϣ���磺΢��֧��URL��JDBC���������������˺�ID��
 */
public class CommonInfo {
	/** Socket�������ò���  **/
	// Socket��������򿪵ļ����˿�
	public static final int SERVER_SOCKET_PORT = 10086;
	// Ϊÿһ��Socket���������õĳ�ʱʱ��, ����������ڴ�ʱ�������ȡ�����ͻ��˵���Ϣ������׳�����java.net.SocketTimeoutException
	public static final int SERVER_SOCKET_TIME_OUT = 5000;
	// ͬ�����ڿͻ��˵�Socket����Ҳ��Ҫ����ͬ���Ĳ������Ա�ȷ����һ��ʱ����δ�յ�����˵ķ�����Ϣʱ���׳�ͬ�������⡣
	public static final int CLIENT_SOCKET_TIME_OUT = 15000;
	
	/** Socket���������ڱ�ʶ�������ҵ��ı�ʶ  **/
	// ֧������
	public static final String PAYMENT_TRANSACTION_BIZ = "PaymentTransaction";
	// ��������
	public static final String REVERSE_TRANSACTION_BIZ = "ReverseTransactoin";
	// �����˿�
	public static final String REFUND_TRANSACTION_BIZ = "RefundTransaction";
	// ��ѯ����
	public static final String INQUIRY_TRANSACTION_BIZ = "InquiryTransactionOrder";
	// ��ѯ�˿�
	public static final String INQUIRY_REFUND_BIZ = "InquiryRefundOrder";
	
	// �׸�ͨ����(Harvest)������صĻ�����Ϣ(����Ѷ����)
	// �����˺�ID
	public static final String HARVEST_APP_ID = "wxb91c84b6c4d2e07b";
	// �̻���
	public static final String HARVEST_MCH_ID = "1900014621";
	// ��Կ
	public static final String HARVEST_KEY = "024edfffae32c829b012c98a61686f3b";
	
	
	/** ���ݿ���� **/
	// �̻���Ϣ��
	public static final String TBL_MCH_INFO = "tbl_mch_info";
	// �̻��뽻�׵�������
	public static final String TBL_MCH_INFO_TRANS_ORDER = "tbl_mch_info_trans_order";
	// ���׵���Ϣ��
	public static final String TBL_TRANS_ORDER = "tbl_trans_order";
	// ��������Ϣ��
	public static final String TBL_TRANS_ORDER_REVERSE_ORDER = "tbl_trans_order_reverse_order";
	// ���׵����˿������
	public static final String TBL_TRANS_ORDER_REFUND_ORDER = "tbl_trans_order_refund_order";
	// �˿��Ϣ��
	public static final String TBL_REFUND_ORDER = "tbl_refund_order";
	
	/** ���״�����Ϣ��ʾ **/
	// δ�ύ�κζ���
	public static final String strNonAnyOrder = "0000";
	// �������ʹ���
	public static final String strOrderTypeErr = "0001";
	// ������������
	public static final String strOrderArgsErr = "0002";
	// �����ʼ�������ݵ����ݿ�ʧ��
	public static final String strInsertInitalOrderErr = "0003";
	// ΢�Ŷ˺�̨ϵͳ���ִ���
	public static final String strWxBackendSystemErr = "0004";
	// ΢�Ŷ˷���Ӧ����ϢΪ��
	public static final String strWxResponseInfoIsEmperty = "0005";
	// ����΢�Ŷ˵ķ���Ӧ����Ϣ����
	public static final String strAnaylizeWxResponseErr = "0006";
	// ���¶�����������ݿ�ʧ��
	public static final String strUpdateOrderResultErr = "0007";
	// �����̻��Ķ���Ӧ����ϢΪ��
	public static final String strResponseInfoIsEmpertyForCommercial = "0008";
}
