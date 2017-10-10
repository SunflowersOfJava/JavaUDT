/**
 * 
 */
package judp;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import udt.UDTServerSocket;
import udt.UDTSocket;

/**
 * @author jinyu
 * 服务端接收封装
 * 服务端
 */
public class judpServer  {
private UDTServerSocket server=null;
private final SynchronousQueue<judpSocket> sessionHandoff=new SynchronousQueue<judpSocket>();
private boolean isStart=true;
private boolean isSucess=true;
private static final Logger logger=Logger.getLogger(judpServer.class.getName());

/**
 * 关闭服务端
 */
public void close()
{
	isStart=false;
	server.getEndpoint().stop();
}
public judpServer(int port)
{
	
	try {
		server=new UDTServerSocket(port);
	} catch (SocketException e) {
		logger.log(Level.WARNING, "绑定失败："+e.getMessage());
		isSucess=false;
	} catch (UnknownHostException e) {
		isSucess=false;
		e.printStackTrace();
	}
}
public judpServer(String localIP,int port)
{
	try {
		InetAddress  addr=	InetAddress.getByName(localIP);
		server=new UDTServerSocket(addr,port);
		
	} catch (SocketException e) {
		logger.log(Level.WARNING, "绑定失败："+e.getMessage());
		isSucess=false;
	} catch (UnknownHostException e) {
		isSucess=false;
		e.printStackTrace();
	}
}

/**
 * 启动接收
 */
public boolean Start()
{
  if(!isStart||!isSucess)
  {
	  logger.log(Level.WARNING, "已经关闭的监听或监听端口不能使用");
	  return false;
  }
	Thread serverThread=new Thread(new Runnable() {

		@Override
		public void run() {
			while(isStart)
			{
			try {
				UDTSocket csocket=	server.accept();
				judpSocket jsocket=new judpSocket(csocket);
				sessionHandoff.put(jsocket);
				SocketManager.getInstance().addGC(jsocket,csocket);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
		}
		
	});
	serverThread.setDaemon(true);
	serverThread.setName("judpServer_"+System.currentTimeMillis());
	serverThread.start();
	return true;
}

/**
 * 返回连接的socket
 */
public judpSocket accept()
{
try {
	judpSocket jsocket=	sessionHandoff.take();
	return jsocket;
} catch (InterruptedException e) {
    logger.info("judpSocket接收中断："+e.getMessage());
	e.printStackTrace();
}
return null;
}
}
