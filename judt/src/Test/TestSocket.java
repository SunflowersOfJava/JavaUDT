/**    
 * �ļ�����TestSocket.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2019��5��24��    
 * Copyright ���� Corporation 2019     
 * ��Ȩ����    
 *    
 */
package Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**    
 *     
 * ��Ŀ���ƣ�judt    
 * �����ƣ�TestSocket    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2019��5��24�� ����2:35:04    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2019��5��24�� ����2:35:04    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TestSocket {

	/**
	* @Title: main
	* @Description: TODO(������һ�仰�����������������)
	* @param @param args    ����
	* @return void    ��������
	 * @throws SocketException 
	 * @throws UnknownHostException 
	*/
	public static void main(String[] args) throws SocketException, UnknownHostException {
		DatagramSocket dgp=new DatagramSocket(0);
		int port=dgp.getLocalPort();
		InetAddress local=	dgp.getLocalAddress();
		SocketAddress addr=dgp.getLocalSocketAddress();
		
		System.out.println("sss");

	}

}
