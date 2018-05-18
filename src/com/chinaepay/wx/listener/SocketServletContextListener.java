/**
 * @author xinwuhen
 */
package com.chinaepay.wx.listener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.chinaepay.wx.common.CommonInfo;
import com.chinaepay.wx.common.CommonTool;
import com.chinaepay.wx.control.InquiryRefundController;
import com.chinaepay.wx.control.InquiryTransactionController;
import com.chinaepay.wx.control.PaymentTransactionController;
import com.chinaepay.wx.control.RefundTransactionController;
import com.chinaepay.wx.control.ReverseTransactoinController;
import com.chinaepay.wx.entity.CommunicateEntity;
import com.chinaepay.wx.entity.PaymentTransactionEntity;

/**
 * @author xinwuhen
 *
 */
public class SocketServletContextListener implements ServletContextListener {
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// ��������Socket������ػ��߳�
		ServerSocketDeamonThread serverSocktDeamonThread = new ServerSocketDeamonThread();
		new Thread(serverSocktDeamonThread).start();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	
	
	/**
	 * ����Socket�ͻ������ӵ��ػ��̡߳�
	 * @author xinwuhen
	 *
	 */
	private class ServerSocketDeamonThread implements Runnable {
		private ServerSocket srvSocket = null;
		private Socket socket = null;
		
		@Override
		public void run() {
			try {
				srvSocket = new ServerSocket(CommonInfo.SERVER_SOCKET_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (srvSocket != null) {
				while(true) {
					try {
						socket = srvSocket.accept();
						ServerSocketProcessThread sspt = new ServerSocketProcessThread(socket);
						new Thread(sspt).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	// while
			}
		}
	}
	
	/**
	 * ����Socket�ͻ������ݵĶ��������̣߳���Բ�ͬ�Ŀͻ������ӻ����ɲ����ĸ��߳��ࡣ
	 * @author xinwuhen
	 *
	 */
	private class ServerSocketProcessThread implements Runnable {
		private Socket socket = null;
		private InputStream in = null;
		private InputStreamReader isr = null;
		private BufferedReader br = null;
		private OutputStream os = null;
		private OutputStreamWriter osw =null;
		private BufferedWriter bw = null;
		
		public ServerSocketProcessThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			if (socket == null) {
				return;
			}
			
			try {
				// ���÷���˶�ȡ���ݵĳ�ʱʱ��
				socket.setSoTimeout(CommonInfo.SERVER_SOCKET_TIME_OUT);
				
				in = socket.getInputStream();
				isr = new InputStreamReader(in, "UTF-8");
				br = new BufferedReader(isr);
				int iByteData = -1;
				StringBuffer sb = new StringBuffer();
				while ((iByteData = br.read()) != -1) {
					sb.append((char) iByteData);
				}
//				socket.shutdownInput();		// �ر�Socket��InputStream.
				
				String strSocketReqCont = sb.toString();
				if (!"".equals(strSocketReqCont)) {
					System.out.println("strSocketReqCont = " + strSocketReqCont);
					
					// �����Socket�ͻ��˻�ȡ��������Ϣ
					String strProcCntrlFlag = getProcCntrlFlag(strSocketReqCont);
					String strWXReqCont = getWXRequestContent(strSocketReqCont) + getHarvestTransInfo();
					
					System.out.println("****strWXReqCont = " + strWXReqCont);
					
					if (strProcCntrlFlag != null && !"".equals(strProcCntrlFlag) && strWXReqCont != null && !"".equals(strWXReqCont)) {
						HashMap<String, String> hmWXReqCont = CommonTool.formatStrToMap(strWXReqCont);
//						HashMap<String, String> hmReturnResult = null;
						String strResponRst = null;
						switch(strProcCntrlFlag) {
							// ֧������
							case CommonInfo.PAYMENT_TRANSACTION_BIZ:
								PaymentTransactionController payTransCntrl = PaymentTransactionController.getInstance();
//								hmReturnResult = payTransCntrl.startTransactionOrder(hmWXReqCont);
								strResponRst = payTransCntrl.startTransactionOrder(hmWXReqCont);
								break;
								
							// ��������
							case CommonInfo.REVERSE_TRANSACTION_BIZ:
								ReverseTransactoinController reverseTransCntrl = ReverseTransactoinController.getInstance();
//								hmReturnResult = reverseTransCntrl.startTransactionOrder(hmWXReqCont);
								strResponRst = reverseTransCntrl.startTransactionOrder(hmWXReqCont);
								break;
								
							// �����˿�
							case CommonInfo.REFUND_TRANSACTION_BIZ:
								RefundTransactionController refundTransCntrl = RefundTransactionController.getInstance();
//								hmReturnResult = refundTransCntrl.startTransactionOrder(hmWXReqCont);
								strResponRst = refundTransCntrl.startTransactionOrder(hmWXReqCont);
								break;
								
							// ��ѯ����
							case CommonInfo.INQUIRY_TRANSACTION_BIZ:
								InquiryTransactionController inquiryTransCntrl = InquiryTransactionController.getInstance();
//								hmReturnResult = inquiryTransCntrl.startInquiryOrder(hmWXReqCont);
								strResponRst = inquiryTransCntrl.startInquiryOrder(hmWXReqCont);
								break;
								
							// ��ѯ�˿�
							case CommonInfo.INQUIRY_REFUND_BIZ:
								InquiryRefundController inquiryRefundCntrl = InquiryRefundController.getInstance();
//								hmReturnResult = inquiryRefundCntrl.startInquiryOrder(hmWXReqCont);
								strResponRst = inquiryRefundCntrl.startInquiryOrder(hmWXReqCont);
								break;
						}
						
						
//						if (hmReturnResult == null || hmReturnResult.size() == 0) {
//							return;
//						}
						
//						// ��ʽ��SocketӦ����
//						String strSocketRespCont = getSocketRespContent(hmReturnResult);
						
						// ��ͻ��˷������ݴ�����
//						if (strSocketRespCont != null) {
						if (strResponRst != null) {
							System.out.println("strResponRst = " + strResponRst);
							os = socket.getOutputStream();
							osw = new OutputStreamWriter(os, "UTF-8");
							bw = new BufferedWriter(osw);
//							bw.write(strSocketRespCont);
							bw.write(strResponRst);
							bw.flush();
							socket.shutdownOutput();	// �ر�Socket��OutputStream����ֹSocket����������ȡʱ����.
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			/*
			finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (osw != null) {
					try {
						osw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			*/
		}
	}
	
	/**
	 * ��ȡSocket�������ڵķ���˴���������Ӧ�ı�ʶ��
	 * @param strTotalCotent
	 * @return
	 */
	private String getProcCntrlFlag(String strTotalCotent) {
		if (strTotalCotent == null || strTotalCotent.equals("")) {
			return null;
		}
		
		if (!strTotalCotent.contains(":")) {
			return null;
		}
		
		return strTotalCotent.split(":")[0];
	}
	
	/**
	 * ��ȡSocket�������ڵ�ҵ�����ݡ�
	 * @param strTotalCotent
	 * @return
	 */
	private String getWXRequestContent(String strTotalCotent) {
		if (strTotalCotent == null || strTotalCotent.equals("")) {
			return null;
		}
		
		if (!strTotalCotent.contains(":")) {
			return null;
		}
		
		return strTotalCotent.split(":")[1];
	}
	
	/**
	 * ��ȡ����ģʽ���׸�ͨ����(Harvest)������صĻ�����Ϣ��
	 * @return
	 */
	private String getHarvestTransInfo() {
		String strHarTransInfo = "";
		strHarTransInfo = strHarTransInfo.concat("&" + PaymentTransactionEntity.APPID + "=" + CommonInfo.HARVEST_APP_ID);
		strHarTransInfo = strHarTransInfo.concat("&" + PaymentTransactionEntity.MCH_ID + "=" + CommonInfo.HARVEST_MCH_ID);
		strHarTransInfo = strHarTransInfo.concat("&" + PaymentTransactionEntity.APP_KEY + "=" + CommonInfo.HARVEST_KEY);
		return strHarTransInfo;
	}
	
	/**
	 * ��Socket����˴�������ɵ�Map���ͷ������ݸ�ʽ��Ϊ�ַ������͡�
	 * �ַ���ʽ:BUSINESS_PROC_RESULT=SUCCESS&out_trade_no=1217752501201407033233368018&fee_type=USD&total_fee=888&time_end=20141030133525&transaction_id=013467007045764
	 * ���У�BUSINESS_PROC_RESULT��������CommunicateEntity.
	 * @param hmReturnResult
	 * @return
	 */
	private String getSocketRespContent(HashMap<String, String> hmReturnResult) {
		if (hmReturnResult == null || hmReturnResult.size() == 0) {
			return "";
		}
		
		String strSysCommRst = hmReturnResult.get(CommunicateEntity.SYSTEM_COMM_RESULT_KEY);
		String strProcRst = hmReturnResult.get(CommunicateEntity.BUSINESS_PROC_RESULT_KEY);
		String strProcCont = hmReturnResult.get(CommunicateEntity.BUSINESS_RESPONSE_RESULT);
		if (strSysCommRst == null ||  strProcRst == null || strProcCont == null) {
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(CommunicateEntity.SYSTEM_COMM_RESULT_KEY + "=" + strSysCommRst);
		sb.append("&" + CommunicateEntity.BUSINESS_PROC_RESULT_KEY + "=" + strProcRst);
		sb.append(":" + strProcCont);
		
		return sb.toString();
	}
}
