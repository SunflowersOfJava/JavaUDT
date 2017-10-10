/**
 * 
 */
package judp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import udt.UDTClient;

/**
 * @author jinyu
 * 客户端发送
 */
public class judpClient {
  private	UDTClient client=null;
  private  final int bufSize=1500;
  private  long sumLen=0;
  public judpClient(String lcoalIP,int port)
  {
	  InetAddress addr = null;
	try {
		addr = InetAddress.getByName(lcoalIP);
	} catch (UnknownHostException e1) {
		
		e1.printStackTrace();
	}
	  
	  try {
		client=new UDTClient(addr,port);
	} catch (SocketException e) {
		e.printStackTrace();
	} catch (UnknownHostException e) {
	
		e.printStackTrace();
	}
	  SocketManager.getInstance().addGC(this,client);
  }
  public judpClient()
  {
	  try {
		client=new UDTClient(null,0);
	} catch (SocketException e) {
		e.printStackTrace();
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	  SocketManager.getInstance().addGC(this,client);
  }
  public judpClient(int port)
  {
	  try {
		client=new UDTClient(null,port);
	} catch (SocketException e) {
		e.printStackTrace();
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	  SocketManager.getInstance().addGC(this,client);
  }
  public boolean connect(String ip,int port)
  {
	  boolean isSucess=false;
	  if(client!=null)
	  {
		  try {
			client.connect(ip, port);
			isSucess=true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	  }
	  
	  return isSucess;
  }
  public int sendData(byte[] data)
  {
	  if(data==null)
	  {
		  return 0;
	  }
	  int r=0;
	  if(client!=null)
	  {
		  try {
			client.sendBlocking(data);
			r=data.length;
		    sumLen+=r;
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	  }
	  return r;
  }
  public void pauseOutput()
  {
      try {
        client.getOutputStream().pauseOutput();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
  public byte[]  read()
  {
	  byte[] result=null;
	  if(client!=null)
	  {
		  byte[]  readBytes=new byte[bufSize];//接收区
		  byte[] buf=new byte[bufSize];//数据区
		  int index=0;
		  int r=0;
		  try {
			  while(true)
			  {
		           r=client.read(readBytes);
		          if(r==-1)
		          {
		        	  break;
		          }
		          else
		          {
		        	  if(r<=bufSize)
		        	  {
		        		  //result=new byte[r];
		        		  //System.arraycopy(readBytes, 0, result, 0, r);
		        		  if(index+r<buf.length)
		        		  {
		        			  System.arraycopy(readBytes, 0, buf, index, r);
		        			  index+=r;
		        		  }
		        		  else
		        		  {
		        			  //扩展数据区
		        			  int len=(int) (buf.length*0.75);
		        			  if(len<bufSize)
		        			  {
		        				  len=bufSize;
		        			  }
		        			  //最小扩展bufSize；一定比r大
		        			  byte[] tmp=new byte[buf.length+len];
		        			  System.arraycopy(buf, 0, tmp, 0, index+1);//拷贝数据
		        			  System.arraycopy(readBytes, 0, tmp, index, r);//拷贝数据
		        			  buf=tmp;
		        			  index+=r;
		        		  }
		        	  }
		          }
			  }
		     
		} catch (IOException e) {
		
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		  result=new byte[index+1];//长度
		  System.arraycopy(buf, 0, result, 0,index+1);//拷贝数据
	  }
	  
	  return result;
  }
  public int read(byte[]data)
  {
	   try {
		return client.read(data);
	} catch (IOException e) {
		e.printStackTrace();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	return -1;
  }
  public void close()
  {
	  if(client!=null)
	  {
		  if(sumLen==0)
		  {
			  //没有发送数据
			  try {
			      if(!client.isClose())
				client.shutdown();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  else
		  {
			  //开始缓存
			  SocketManager.getInstance().add(client);
		  }
	  }
  }
 
}
