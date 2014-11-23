
// REGISTRATION SERVER

import java.io.*;
import java.net.*;
import java.util.*;

public class RSTest
{
	String hostname;
	int cookie;
	boolean flag;
	int TTL;
	int portno_rfc_server;
	int peer_time_active=0;
	String date_reg_peer;
        String rfc_ip;
	static int cookie_count = 1;
	static int i=0;	
	
        
        ServerSocket server = null;
        Socket client = null;
                
                
	public void regPeer(String hname,String port_rfc,String client_ip,boolean state)
	{
                cookie = cookie_count++;
		flag=state;
		TTL=7200;
      		peer_time_active = 1;
		Date d1 = new Date();
		date_reg_peer = d1.toString();
                hostname=hname;
                portno_rfc_server=Integer.parseInt(port_rfc);
                rfc_ip=client_ip;
        }

        
        public void printValues(RSTest host[], int count)
        {
            System.out.println("Printing values - >");
            System.out.println("Hostname "+count+" : " + host[count].hostname);
            System.out.println("Cookie "+count+" : " + host[count].cookie);
            System.out.println("Flag "+count+" : " + host[count].flag);
            System.out.println("TTL "+count+" : " + host[count].TTL);
            System.out.println("No of times u r active "+count+" : " + host[count].peer_time_active);
            System.out.println("Port no "+count+" : " + host[count].portno_rfc_server);
            System.out.println("IP "+count+" : " + host[count].rfc_ip);
            System.out.println("Most recent registration "+count+" : " +host[count].date_reg_peer);
        }
        
       
        public void regAgainPeer(RSTest host[], int host_count)
	{
                host[host_count].flag=true;
		host[host_count].TTL=7200;
      		host[host_count].peer_time_active +=1;
		Date d1 = new Date();
		host[host_count].date_reg_peer = d1.toString();
        }
              
        public void peerQuery(RSTest host[])
        {
            
        }
	RSTest[] host= new RSTest[100];

        
	public void start_fun()
	{
		for(int j=0;j<100;j++)
		{
			host[j]=new RSTest();
		}
                
                try
                {
                    server = new ServerSocket(65423);
                    while (true)
                    {

                        client = server.accept();
                        Thread t = new Thread(new MyCon(client));
                	t.start();
                        
                    }
                }
	
                catch(Exception e)
                {
                    System.out.println(e);
                }
	}
	

	public static void main(String args[]) throws Exception
	{
            System.out.println("Starting Registration Server");
            new RSTest().start_fun();
        }  


        
            

        class MyCon implements Runnable 
        {
            Socket client;
            public MyCon(Socket client)
            {
        	this.client = client;
            }
            
                       
            public void run()
            {
                boolean present=false;
                int getHostNo=0;
                try
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        	 	PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                	int l=0,k=0;
                	
                	out.println("Message Received");
			out.flush();
			String test=in.readLine();
			String[] token=test.split("<cr>");
			String command_value[]=token[0].split("<sp>");
			         	                        
                        
                        
                        class DecrementTTL implements Runnable
                        {
                            int ab=0;
                            RSTest temp_host[];
                            public DecrementTTL(RSTest host1[], int temp)
                            {
                                temp_host=host1;
                                ab=temp;
                            }
                            
                            public void run()
                            {
                                try
                                {
                                    
                                    long t= System.currentTimeMillis();
                                    long end = t+7200000;
                                    while(System.currentTimeMillis() <= end)
                                    while(host[ab].TTL>=0 && host[ab].flag==true)
                                    {
                                        if(host[ab].TTL==0)
                                            host[ab].flag=false;
                                   
                                        if(host[ab].flag==true)
                                            host[ab].TTL-=60; 
                                    
                                        System.out.println("New value of TTL + " + host[ab].TTL);
                                        Thread.sleep(60000);
                                    } 
                                }
                                catch(Exception e)
                                {
                                    System.out.println(e);
                                }
                             }
                        }
                        
                        
                        if(command_value[0].equalsIgnoreCase("REGISTER"))
			{
                            System.out.println("Registering Peer");
                            String address_value[]=token[2].split("<sp>");
                            String port_value[]=token[3].split("<sp>");
                            String cookie_value[]=token[4].split("<sp>");
                            for (int t=0;t<token.length;t++)
                        
                                
                                for(int host_count=0;host_count<=i;host_count++)
                                {
                                    if(command_value[1].equalsIgnoreCase(host[host_count].hostname))
                                    {   
                                        present=true;
                                        getHostNo=host_count;
                                        break;
                                    }
                                    else
                                        present=false;
                                    
                                }
                                if(present==false)
                                {
                                    host[i].regPeer(command_value[1],port_value[1],address_value[1],true);

                                    host[i].printValues(host, i);
                                    Thread ttl_thread = new Thread(new DecrementTTL(host,i));
                                    ttl_thread.start();
                                    out.println("Cookie : " + host[i].cookie+"\n");
                                    out.println("Flag : " + host[i].flag+"\n");
                                    out.println("TTL : " + host[i].TTL+"\n");
                                    out.println("No of times u r active : " + host[i].peer_time_active+"\n");
                                    out.println("Most recent registration " +host[i].date_reg_peer);
                                    out.println("quit");
                                    out.flush();
                                    
                                    i+=1;
                                    
                                }
                                else
                                {
                                    host[getHostNo].regAgainPeer(host, getHostNo);
                                    host[getHostNo].printValues(host, getHostNo);
                                    out.println("Cookie : " + host[getHostNo].cookie+"\n");
                                    out.println("Flag : " + host[getHostNo].flag+"\n");
                                    out.println("TTL : " + host[getHostNo].TTL+"\n");
                                    out.println("No of times u r active : " + host[getHostNo].peer_time_active+"\n");
                                    out.println("Most recent registration " +host[getHostNo].date_reg_peer);
                                    out.println("quit");
                                    
                                }
                                
                               
                                client.close();
                                boolean client_state = client.isClosed();
                                
                }
                        
                        
                //LEAVE     LEAVE       LEAVE       LEAVE       LEAVE
                                
                                if(command_value[0].equalsIgnoreCase("LEAVE"))
                                {
                                    
                                    for(int host_count=0;host_count<=i;host_count++)
                                    {
                                    if(command_value[1].equalsIgnoreCase(host[host_count].hostname))
                                    {   
                                        present=true;
                                        getHostNo=host_count;
                                        break;
                                    }
                                    else
                                        present=false;
                                    
                                }
                                    
                                if(present==false)
                                {
                                    System.out.println("Client Not Registered");
                                    out.println("Client Not Registered");
                                    out.println("quit");
                                     
                                }
                                else
                                {
                                    System.out.println(host[getHostNo].hostname+" Leaving the system");
                               
                                    host[getHostNo].flag=false;
                                    host[getHostNo].printValues(host, getHostNo);
                                    out.println("Cookie : " + host[getHostNo].cookie+"\n");
                                    out.println("Flag : " + host[getHostNo].flag+"\n");
                                    out.println("TTL : " + host[getHostNo].TTL+"\n");
                                    out.println("No of times u r active : " + host[getHostNo].peer_time_active+"\n");
                                    out.println("Most recent registration " +host[getHostNo].date_reg_peer);
                                    out.println("quit");
                                    
                                }    
                                    
                            }        
                        
                //// PQUERY  /// PQUERY             // PQUERY       ////PQUERY
                                if(command_value[0].equalsIgnoreCase("PQUERY"))
                                {
					int count=0;
					
                                    String peer_records=null;
                                    for(int temp=0; temp<i;temp++ )
                                    {
                                        if(host[temp].flag==true&&!((host[temp].hostname).equalsIgnoreCase(command_value[1])))
                                        {
                                            if(peer_records==null)
                                                peer_records=host[temp].hostname+"<sp>"+host[temp].rfc_ip+"<sp>"+host[temp].portno_rfc_server+"<cr>";
                                            else
                                                peer_records=peer_records+host[temp].hostname+"<sp>"+host[temp].rfc_ip+"<sp>"+host[temp].portno_rfc_server+"<cr>";
					   count++;
                                        }
					
                                    }
					if(count==0)
						peer_records="null";
					System.out.println(peer_records);
                                    out.println(peer_records);
                                    out.flush();
                                        
                                }
                        
                //KEEP ALIVE        KEEP ALIVE      KEEP ALIVE      KEEP ALIVE
                                
                                if(command_value[0].equalsIgnoreCase("KEEPALIVE"))
                                {
                                    
                                    for(int host_count=0;host_count<=i;host_count++)
                                {
                                    if(command_value[1].equalsIgnoreCase(host[host_count].hostname))
                                    {   
                                        present=true;
                                        getHostNo=host_count;
                                        break;
                                    }
                                    else
                                        present=false;
                                    
                                }
                                    
                                if(present==false)
                                {
                                    System.out.println("Client Not Registered/ACTIVE");
                                    out.println("Client Not Registered/ACTIVE");
                                    out.println("quit");
                                     
                                }
                                else
                                {
                                    System.out.println("Client Registered/ACTIVE");
                                    host[getHostNo].flag=true;
                                    host[getHostNo].TTL=7200;
                                    //Uncomment below line to see values after Keep Alive message
                                    //host[getHostNo].printValues(host, getHostNo);
                                    out.println("Keep Alive Message Received");
                                    out.println("quit");
                                    
                                }    
                                    
                            }        
                                    
                }
		catch(Exception e)
		{
          			System.out.println(e);
      		}
	}
      }
}  