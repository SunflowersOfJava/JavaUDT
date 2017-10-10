/**
 * 
 */
package judp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import udt.UDTClient;

/**
 * @author jinyu
 * �ͻ��˷���
 */
public class judpClient {
  private	UDTClient client=null;
  private  final int bufSize=65535;
  private  long sumLen=0;
  private PackagetCombin pack=new PackagetCombin();
  public int dataLen=0;
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
  public int sendSplitData(byte[] data)
  {
	  if(data==null)
	  {
		  return 0;
	  }
	  int r=0;
	   byte[][]sendData=null;
	    if(dataLen==0)
	    {
	    	sendData=PackagetSub.splitData(data);
	    }
	    else
	    {
	    	PackagetSub sub=new PackagetSub();
	    	sendData=sub.split(data, dataLen);
	    }
	    for(int i=0;i<sendData.length;i++)
	    {
		   r+=sendData(sendData[i]);
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
  
  /**
   * ��ȡ����
   * ֻ��split���Ͷ�Ӧ
   * @return
   */
  public byte[]  readALL()
  {
	  byte[] result=null;
	  if(client!=null)
	  {
		  byte[]  readBytes=new byte[bufSize];//������
		  int r=0;
		  try {
			  while(true)
			  {
				  if(client.isClose())
					{
						return null;
					}
		          r=client.getInputStream().read(readBytes);
		          if(r==-1)
		          {
		        	  result=pack.getData();
		        	  break;
		          }
		          else
		          {
		             
		        	  if(r==0)
		        	  {
		        		  try {
							TimeUnit.MILLISECONDS.sleep(100);
							
							continue;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		        	  }
		        	 //
		        		  byte[] buf=new byte[r];
		        		  System.arraycopy(readBytes, 0, buf, 0, r);
		        		  if(pack.addData(buf))
		        		  {
		        			  result=pack.getData();
		        			  break;
		        		  }
		        	 
		        	  
		          }
			  }
		     
		} catch (IOException e) {
		
			e.printStackTrace();
		} 
		
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
  
  /**
   * �ر�
   */
  public void close()
  {
	  if(client!=null)
	  {
		  if(sumLen==0)
		  {
			  //û�з�������
			  //�����ر�
			  try {
			      if(!client.isClose())
				    client.shutdown();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		  }
		  else
		  {
			  //��ʼ����
			  //SocketManager.getInstance().add(client);
			  if(!client.isClose())
					client.close();
		  }
	  }
  }
  
  /**
   * �Ƿ�ر�
   * @return
   */
 public boolean isClose()
 {
	 return client.isClose();
 }
 /**
  * �����Ƕ�ȡΪ������д��Ϊ��
  * �����д��Ϊ��������ȡ�ٶ���ʱ�����ݸ��Ƕ�ʧ
  * Ĭ�϶�ȡΪ������û�ж�ȡ���������ǣ��������ݣ��ȴ��ظ�
  * islagerRead=true��������
  * @param isRead
  */
 public void  resetBufMaster(boolean isRead)
 {
	 try {
		client.getInputStream().resetBufMaster(isRead);
	} catch (IOException e) {
		
		e.printStackTrace();
	}
 }
 
 /**
  * ���ô����ݶ�ȡ
  * Ĭ�� false
  * @param islarge
  */
 public void setLargeRead(boolean islarge)
 {
	 try {
		client.getInputStream().setLargeRead(islarge);
	} catch (IOException e) {
		
		e.printStackTrace();
	}
 }
}