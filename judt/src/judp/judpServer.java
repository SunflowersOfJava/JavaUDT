/**
 * 
 */
package judp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
//private final SynchronousQueue<judpSocket> sessionHandoff=new SynchronousQueue<judpSocket>();
private boolean isStart=true;
private boolean isSucess=true;
private boolean isRWMaster=true;//与默认值一致
private boolean islagerRead=false;
private static final Logger logger=Logger.getLogger(judpServer.class.getName());

/**
 * 关闭服务端
 */
public void close()
{
	isStart=false;
	server.getEndpoint().stop();
}

/**
 * 
 * @param port 端口
 */
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

/**
 * 
 * @param localIP 本地IP
 * @param port  端口
 */
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
public boolean start()
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
				try {
					csocket.getInputStream().setLargeRead(islagerRead);
					csocket.getInputStream().resetBufMaster(isRWMaster);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				SocketControls.getInstance().addSocket(csocket);
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
 * 设置是读取为主还是写入为主
 * 如果是写入为主，当读取速度慢时，数据覆盖丢失
 * 默认读取为主，还没有读取则不允许覆盖，丢掉数据，等待重复
 * 设置大数据读取才有意义
 * @param isRead
 */
public void  setBufferRW(boolean isRead)
{
	this.isRWMaster=isRead;
	
}

/**
 * 设置大数据读取
 * 默认 false
 * @param islarge
 */
public void setLargeRead(boolean islarge)
{
	this.islagerRead=islarge;
}
/**
 * 返回连接的socket
 */
public judpSocket accept()
{
  UDTSocket socket=SocketControls.getInstance().getSocket();
  if(socket==null)
  {
	  socket=SocketControls.getInstance().getSocket();
  }
  judpSocket jsocket=new judpSocket(socket);
  judpSocketManager.getInstance(socket.getEndpoint()).addSocket(jsocket);
  return jsocket;

}
}
