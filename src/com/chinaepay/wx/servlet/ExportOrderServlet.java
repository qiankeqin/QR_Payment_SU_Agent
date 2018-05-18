/**
 * @author xinwuhen
 */
package com.chinaepay.wx.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinaepay.wx.common.CommonTool;

public class ExportOrderServlet extends InquiryOrderServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
			String mch_id = CommonTool.urlDecodeUTF8(request.getParameter("mch_id"));
			String sub_mch_id = CommonTool.urlDecodeUTF8(request.getParameter("sub_mch_id"));
			String orderType = CommonTool.urlDecodeUTF8(request.getParameter("orderType"));
			String orderStat = CommonTool.urlDecodeUTF8(request.getParameter("orderStat"));
			String transStartTime = CommonTool.urlDecodeUTF8(request.getParameter("transStartTime"));
			String transEndTime = CommonTool.urlDecodeUTF8(request.getParameter("transEndTime"));
			String exportPath = CommonTool.urlDecodeUTF8(request.getParameter("exportPath"));
			String exportFile = CommonTool.urlDecodeUTF8(request.getParameter("exportFile"));
		
			List<Map<String, String>> listInquiryRst = super.getOrderInquiryResult(mch_id, sub_mch_id, orderType, orderStat, transStartTime, transEndTime);
			
			// ���ɵ����ļ�
			this.generateExportFile(listInquiryRst, exportPath, exportFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doPosst(HttpServletRequest request, HttpServletResponse response) {
		this.doGet(request, response);
	}
	
	/**
	 * �������ݿ��ѯ��������ɹ��������ļ���ѹ������д洢��
	 * @param listInquiryRst
	 * @param exportPath	ѹ���ļ����洢��·��
	 * @param exportFileName	ѹ���ļ���(*.zip)
	 * @return
	 * @throws Exception 
	 */
	private boolean generateExportFile(List<Map<String, String>> listInquiryRst, String exportPath, String exportFileName) throws SQLException {
		if (listInquiryRst == null) {
			throw new SQLException("���κβ�ѯ���...");
		}
		
		if (exportPath == null || exportPath.equals("") || exportFileName == null || exportFileName.equals("")) {
			System.out.println("�ļ�·�����ļ���Ϊ��...");
			return false;
		}
		
        // �ж�.txt�ļ��Ƿ����"."�ַ�
        if (!exportFileName.contains(".")) {
        	System.out.println("ѹ���ļ���ȱʧ'.'�ַ�!");
			return false;
        }
        
        String SYSTEM_PATH_CHARACTOR = System.getProperty("file.separator");
        // ��ȡWebApp��Ŀ¼�������ݲ�ͬ�Ĳ���ϵͳ��Windows/Linux/Unix����ʽ��
        String strOSWebAppPath = CommonTool.getWebAppAbsolutPath(this.getClass(), SYSTEM_PATH_CHARACTOR);
        
        // �ж��ļ��洢��Ŀ¼�Ƿ����
 		File zipDir = new File(strOSWebAppPath.concat(SYSTEM_PATH_CHARACTOR).concat(exportPath));  
		if (!zipDir.exists() || !zipDir.isDirectory()) {  
		     zipDir.mkdirs();
		}
        
		// ��ȡ.txt��.zip�ļ���ŵĹ�ͬĿ¼
		String strTxtAndZipFilePath = strOSWebAppPath.concat(SYSTEM_PATH_CHARACTOR).concat(exportPath);
		
        // ������д��.txt�ļ�
		File txtFile = null;
		try {
			// �ļ���(*.txt��ʽ���ַ���.��ǰ�ߵĲ���)
	    	String strPrefixFileName = exportFileName.split("\\.")[0];
	    	String strTxtFileFullName = strTxtAndZipFilePath.concat(SYSTEM_PATH_CHARACTOR).concat(strPrefixFileName).concat(".txt");
			txtFile = getTxtFile(strTxtFileFullName, listInquiryRst);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
         
		// ����.zip�ļ�
		if (txtFile == null) {
			System.out.println("�����ı��ļ�.txtΪ��...");
			return false;
		}
		
		String strZipFileFullName = strTxtAndZipFilePath.concat(SYSTEM_PATH_CHARACTOR).concat(exportFileName);
		File zipFile = new File(strZipFileFullName);
		boolean blnCastRst = castTxtToZipFile(txtFile, zipFile);
		
		// ɾ���ɵ�.txt��.zip�ļ�
		this.deleteOldDirAndFiles(zipDir, txtFile, zipFile);
    	
		return blnCastRst;
	}
	
	
	
	/**
	 * ����ѯ�������д�뵽.txt�ļ���
	 * @param strOSWebAppPath
	 * @param strFileSeparator
	 * @param exportPath
	 * @param exportFile
	 * @param listInquiryRst
	 * @return
	 * @throws IOException 
	 */
	private File getTxtFile(String strTxtFileFullName, List<Map<String, String>> listInquiryRst) throws IOException {
		
    	File txtFile = new File(strTxtFileFullName);
        	
    	// ������д��.txt�ļ�
    	FileOutputStream fos = null;
    	// ��ȡ���з���Ϊ����Ӧ�ͻ��˾������ΪWindows����������ļ����з���Windows��ʽ
    	String strEnterForWin = "\r\n";	// Linux���з���\r   Mac���з���\n
    	String strFirstLine = "���|�̻���|���̻���|��������|����״̬|�̻�������|΢�Ŷ�����|���׽���ʱ��|���|��۱���|����" + strEnterForWin;
    	try {
    		fos = new FileOutputStream(txtFile);
			fos.write(strFirstLine.getBytes("UTF-8"));
			if (listInquiryRst != null) {
				for (int i = 0; i < listInquiryRst.size(); i++) {
					Map<String, String> mapRow = listInquiryRst.get(i);
		    		StringBuffer sb = new StringBuffer();
		    		sb.append(i + 1);
		    		sb.append("|");
		    		sb.append(mapRow.get("mch_id") == null ? "" : mapRow.get("mch_id"));
		    		sb.append("|");
		    		sb.append(mapRow.get("sub_mch_id") == null ? "" : mapRow.get("sub_mch_id"));
		    		sb.append("|");
		    		sb.append(mapRow.get("orderType") == null ? "" : mapRow.get("orderType"));
		    		sb.append("|");
		    		sb.append(mapRow.get("trade_state") == null ? "" : mapRow.get("trade_state"));
		    		sb.append("|");
		    		sb.append(mapRow.get("out_trade_no") == null ? "" : mapRow.get("out_trade_no"));
		    		sb.append("|");
		    		sb.append(mapRow.get("transaction_id") == null ? "" : mapRow.get("transaction_id"));
		    		sb.append("|");
		    		sb.append(mapRow.get("trans_time") == null ? "" : mapRow.get("trans_time"));
		    		sb.append("|");
		    		sb.append(mapRow.get("total_fee") == null ? "" : mapRow.get("total_fee"));
		    		sb.append("|");
		    		sb.append(mapRow.get("fee_type") == null ? "" : mapRow.get("fee_type"));
		    		sb.append("|");
		    		sb.append(mapRow.get("rate") == null ? "" : mapRow.get("rate"));
		    		// д��һ�к���ӻ��з�
		    		sb.append(strEnterForWin);
		    		
					fos.write(sb.toString().getBytes("UTF-8"));
		    	}
			}
			
			// �ļ�����д�������ļ�������ʶ��EOF
			fos.write("EOF".getBytes("UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	
    	return txtFile;
	}
	
	/**
	 * ��.txt�ļ�ѹ����Ϊ.zip�ļ���
	 * @param txtFile
	 * @param strTxtAndZipFilePath
	 * @param exportFileName
	 * @return
	 * @throws IOException
	 */
	private boolean castTxtToZipFile(File txtFile, File zipFile) {
		if (txtFile == null || zipFile == null) {
			System.out.println("castTxtToZipFile�����������Ϊ��");
			return false;
		}
		
		// д��.txt�ļ���.zip�ļ�
		int count = 0;
		byte data[] = new byte[1024];
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
        try {
        	// ��������ļ�����ر���
        	fos = new FileOutputStream(zipFile);
//        	cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());  
        	zos = new ZipOutputStream(fos);  
        	zos.putNextEntry(new ZipEntry(txtFile.getName()));
        	bis = new BufferedInputStream(new FileInputStream(txtFile));
        	int bufferLen = data.length;
			while ((count = bis.read(data, 0, bufferLen)) != -1) {  
			    zos.write(data, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
        return true;
	}
	
	/**
	 * ɾ���ɵ�.txt��.zip�ļ���
	 * @param zipDir
	 * @param txtFile
	 * @param zipFile
	 */
	private void deleteOldDirAndFiles(File zipDir, File txtFile, File zipFile) {
		if (zipDir == null || txtFile == null || zipFile == null) {
			return;
		}
		
		if (zipDir.exists() && zipDir.isDirectory()) {
			File[] lstFileAndDirect = zipDir.listFiles();
			if (lstFileAndDirect != null) {
				for (File file : lstFileAndDirect) {
					System.out.println(file);
					if (!(file.getName()).equals(txtFile.getName()) && !(file.getName()).equals(zipFile.getName())) {
						file.delete();
					}
				}
			}
		}
	}
}
