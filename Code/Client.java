
//CLIENT - B

import java.io.*;
import java.net.*;


public class Client implements Runnable
{
        static int client_port=65200;
        static String rs_server_ip="localhost";
        static String file_path="D:\\IP_project\\client2\\Files";
	int cookie=-1;
	    
	Socket client_socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
	BufferedReader stdIn = null;
        String hostname=null;
        int active_peers=0;
	int no_of_active_peers=0;
        String client_ip=null;
	RFCIndex rfc_Index_DB[]=new RFCIndex[100];
	Client client_obj=null;
	int global_count=0;
        String OSName = System.getProperty("os.name");
        Thread keepAlive_thread;

	public Client()
	{

	}


	public Client(Client obj)
	{
    		client_obj=obj;
	}



	public class ActivePeerRecords
        {
    		String hostname;
    		String server_ip;
    		int server_port;
    
	}



	public class RFCIndex
	{
    		int rfc_no;
    		String rfc_name;
    		String owner_host_name;
    		String owner_IP;
    		int TTL;
	}



	public String RFCQuery(PrintWriter out,BufferedReader in, String user_input) throws Exception
	{
                out.println(user_input);
    		out.flush();
    		String rfc_index=null;
                rfc_index=in.readLine();
                return rfc_index;
	}



	public void registerClient(PrintWriter out,String user_input,Client client) throws Exception
	{
    		int i=0;
    		InetAddress ip = InetAddress.getLocalHost();
    		client.client_ip=ip.getHostAddress();
                for(int n=0;n<global_count;n++)
                {
                    rfc_Index_DB[n].owner_IP=client.client_ip;
                }
                
                String register_command=user_input+"<cr>OS:<sp>"+OSName+"<cr>ADDRESS<sp>"+client_ip+"<cr>PORT<sp>"+client_port+"<cr>REQ-COOKIE<sp>"+cookie;
                System.out.println("Register Command : "+register_command);
                out.println(register_command);
                out.flush();
         }        
        
        
        
        public void populateRFC_DB(Client client) throws Exception
        {
            File path = new File(file_path);
            File[] listOfFiles = path.listFiles();

            
            for(int n=0;n<listOfFiles.length;n++)
            {
               
                String file_name=(listOfFiles[n].getName()).toString();
                
                String rfc_file[]=file_name.split(".txt");
                String rfc_name_no[]=rfc_file[0].split("_");
                
                rfc_Index_DB[n]=new RFCIndex();
                rfc_Index_DB[n].rfc_no=Integer.parseInt(rfc_name_no[0]);
                rfc_Index_DB[n].rfc_name=rfc_name_no[1];
                rfc_Index_DB[n].owner_host_name=client.hostname;
                
                rfc_Index_DB[n].owner_IP=client_ip;
                rfc_Index_DB[n].TTL=7200;
            }
        	
            
            global_count=listOfFiles.length;
               	
        }
                
        
        
        


	public void leaveClient(PrintWriter out,String user_input) throws Exception
	{
    		String leave_command=user_input+"<cr>OS:<sp>"+OSName;
                System.out.println("Leave Command : "+leave_command);
            	out.println(leave_command);
                out.flush();
	}



	public ActivePeerRecords[] pQuery(PrintWriter out,String user_input) throws Exception
	{
    		String temp_list=null;
		String token[]=null;
    		String temp_split[]=new String[3];
    		String peer_query=user_input+"<cr>OS:<sp>"+OSName;
                System.out.println("PQUERY Command : "+peer_query);
    		out.println(peer_query);
    		out.flush();
                String reply=in.readLine();
		ActivePeerRecords active_peers[]=new ActivePeerRecords[100];
    		temp_list=in.readLine();
		
    		if(temp_list.equals("null")==true)
		{
			active_peers[0]=new ActivePeerRecords();
			no_of_active_peers=0;
			return active_peers;
		}
		else
		{
		token=temp_list.split("<cr>");
   	 	for(int count=0;count<token.length;count++)
        	{
            		active_peers[count]=new ActivePeerRecords();
            		temp_split=token[count].split("<sp>");
            		active_peers[count].hostname=temp_split[0];
            		active_peers[count].server_ip=temp_split[1];
            		active_peers[count].server_port=Integer.parseInt(temp_split[2]);
            	}
        	
    		for(int cnt=0;cnt<token.length;cnt++)
    		{
        		System.out.println("Host Name : "+active_peers[cnt].hostname+" Server IP :"+active_peers[cnt].server_ip+" Port:  "+active_peers[cnt].server_port);
    		}
    
		no_of_active_peers=token.length;
    		return active_peers;
		}
	}


	public void keepAlive(PrintWriter out,String user_input) throws Exception
	{
                String keepal_command=user_input+"<cr>OS:<sp>"+OSName;
                System.out.println("Keep Alive Command : "+keepal_command);
            	out.println(keepal_command);
                out.flush();
	}



    	public void openConnection(String server_ip,int server_port)
    	{
        	try
           	{
                	client_socket = new Socket(server_ip,server_port);
                	out = new PrintWriter(client_socket.getOutputStream(), true);
         		in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
                        stdIn = new BufferedReader(new InputStreamReader(System.in));
           	}
           
		catch(Exception e)
           	{
               		System.out.println(e);
           	}
        
    	}
    
        
        
        public void closeCon() throws Exception
        {
                client_socket.close();
        }
        
        
        class DecrementTTL implements Runnable
                        {
                            Client client;
                            public DecrementTTL(Client client)
                            {
                                this.client=client;
                            }
                            
                            public void run()
                            {
                                try
                                {
                                    
                                    long t= System.currentTimeMillis();
                                    long end = t+7200000;
                                    while(System.currentTimeMillis() <= end)
                                    {
                                        
                                        client.openConnection(rs_server_ip,65423);     //Open Connection with Registration Server
                                        String keep_alive="KEEPALIVE<sp>"+client.hostname;
                                        client.keepAlive(out,keep_alive);
                                        while(!(in.readLine().equalsIgnoreCase("quit")))
                                        {
                                            System.out.println("Server reply : "+in.readLine());
                                        }
                                        client_socket.close();
                                        Thread.sleep(180000);
                                    } 
                                }
                                catch(Exception e)
                                {
                                    System.out.println(e);
                                }
                             }
                        }
        
        
        
        
    
    	public void perform_fun(Client client_obj, int opt) throws Exception
    	{
        	switch(opt)
        	{
            		case 1: System.out.println("Registering with Server");
                   		client_obj.openConnection(rs_server_ip,65423);     //Open Connection with Registration Server
                   		
                   		String register="REGISTER<sp>"+client_obj.hostname;
                   		client_obj.registerClient(out,register,client_obj);
                                                                
                  		while(!(in.readLine().equalsIgnoreCase("quit")))
                   		{
                        		System.out.println("Server reply : "+in.readLine());
                   		}
                                keepAlive_thread = new Thread(new DecrementTTL(client_obj));
                                keepAlive_thread.start();
				client_socket.close();
                   		break;
            
			case 2: 
                                String leave="LEAVE<sp>"+client_obj.hostname;
                                System.out.println("Leaving from Server");
                   		client_obj.openConnection(rs_server_ip,65423);     //Open Connection with Registration Server
                                
                  		client_obj.leaveClient(out,leave);
                                keepAlive_thread.stop();
                                
				while(!(in.readLine().equalsIgnoreCase("quit")))
                   		{
                        		System.out.println("Server reply : "+in.readLine());
                   		}                   
                   		client_socket.close();
                   		break;




            		case 3: System.out.println("Enter RFC Number : ");
                   		BufferedReader stdInRFC = new BufferedReader(new InputStreamReader(System.in));
                   		boolean found_locally=false;
                                int search=0;
                   		int rfc_no=Integer.parseInt(stdInRFC.readLine());
                                int temp_port=0;
                                String temp_ip=null;
                       		String temp_hostname=null;
                       		
                   		for(search=0;search<global_count;search++)
                   		{
                        		if(rfc_no==rfc_Index_DB[search].rfc_no && (rfc_Index_DB[search].owner_host_name.equalsIgnoreCase(client_obj.hostname))==true)
                   			{  
						found_locally=true;
						break;
                   			}
                   		}
                   
                                if(found_locally==false)        
                   		{
                                        client_obj.openConnection(rs_server_ip,65423);     //Open Connection with Registration Server
                                        ActivePeerRecords active_peers[];
                   		
                       			String temp=null;
                   			System.out.println(" Searching for available Peers");
                   			String query="PQUERY<sp>"+client_obj.hostname;
                                        active_peers=client_obj.pQuery(out,query);
					
                   			String new_rfc_index=null;
                   			String rfc_entry_holder[]=null;
                   			String rfc_records[]=null;
                   			String each_record[]=null;
                   			boolean add_entry=false;
                   			for (int loop=0;loop<no_of_active_peers;loop++)          //active_peers.length
                   			{
                      				temp_port=active_peers[loop].server_port;
                       				temp_ip=active_peers[loop].server_ip;
                       				temp_hostname=active_peers[loop].hostname;
                       				
                       				client_obj.openConnection(temp_ip,temp_port);
                       				
                                                String user_input="GET<sp>RFC-INDEX<sp>P2P-DI/1.1<cr><tr>HOST : <sp>"+client_obj.hostname+"<cr><tr>OS : <sp>"+OSName+"<cr><tr>";
                                                temp=client_obj.RFCQuery(out,in, user_input);
                                                
                                                rfc_entry_holder=temp.split("<tr>");
                                                
                                                System.out.println(rfc_entry_holder[0]);
                                                System.out.println(rfc_entry_holder[1]+"\n");
                                                
                       				for (int i=2;i<rfc_entry_holder.length;i++)
                       				{
                                                    
                                                       System.out.println("each RFC index Entry : "+rfc_entry_holder[i]);
                                                        
                        				rfc_records=rfc_entry_holder[i].split("<cr>");
                                                        
                                                               String[] match_rfc_no=rfc_records[0].split("<sp>");
                                                               String[] match_owner_name=rfc_records[2].split("<sp>");
                                                               
                                                               
                                                               if(global_count==0)
                                                                   add_entry=true;

                                                            	for(int iter=0;iter<global_count;iter++)
                                                             	{
                                                                            if(rfc_Index_DB[iter].rfc_no==Integer.parseInt(match_rfc_no[1])&&rfc_Index_DB[iter].owner_host_name.equals(match_owner_name[1]))
                                                                            {                                                                            
                                                                                add_entry=false;
                                                                                iter=global_count;
                                                                            }
                                                                            else
                                                                                 add_entry=true;
                                                             	}
                                                        
    
                                                            	if(add_entry==true)
                                                            	{
                                                                	rfc_Index_DB[global_count]=new RFCIndex();   
                           				
                                                                        for (int j=0;j<rfc_records.length;j++)
                                                                        {
                                                                            each_record=rfc_records[j].split("<sp>");
                               					                               
                                                                            if(j==0)
                                                                            {
                                                                                rfc_Index_DB[global_count].rfc_no=Integer.parseInt(each_record[1]);
                                                                            }

                                                                            else if(j==1)
                                                                            {
                                                                                rfc_Index_DB[global_count].rfc_name=each_record[1];
                                                                            }
			
                                                                            else if(j==2)
                                                                            {
                                                                                rfc_Index_DB[global_count].owner_host_name=each_record[1];
                                                                            }

                                                                            else if(j==3)
                                                                            {
                                                                                rfc_Index_DB[global_count].owner_IP=each_record[1];
                                                                                rfc_Index_DB[global_count].TTL=7200;
                                                                            }

                                                                          }
                                                                          global_count++;
                                                                    }
                                                
                                                                    else
                                                                            System.out.println("Entry already present, hence not added!");
                            				
                                                        }
                                                        //Searching after merging
                                                        for(search=0;search<global_count;search++)
                                                        {
                                                            if(rfc_no==rfc_Index_DB[search].rfc_no)
                                                            {  
                                                                found_locally=true;
                                                                break;
                                                            }
                                                        }
                                        
                                                        if(found_locally==true)
                                                        {
                                                            client_socket.close();
                                                            break;
                                                        }
                                                        client_socket.close();
                                          }
                                        
                        
                                        if(no_of_active_peers>0)
                                        {
                                                //GET RFC
                                                for(int m=0;m<global_count;m++)
                                                {
                                                    if((rfc_Index_DB[m].owner_host_name).equalsIgnoreCase(client_obj.hostname)==false)
                                                    {
                                                        Thread ttl_thread = new Thread(new DecreaseTTL(m));
                                                        ttl_thread.start();
                                                    }
                                                }
                         
                                                System.out.println("Fetching File from Database");
                                        
                                                String getRFC="GET<sp>RFC<sp>"+rfc_Index_DB[search].rfc_no+"<sp>P2P-DI/1.1<cr><tr>HOST : <sp>"+rfc_Index_DB[search].owner_host_name+"<cr><tr>OS : <sp>"+OSName+"<cr><tr>";
                                                client_obj.openConnection(temp_ip,temp_port);
                                                String file_name=file_path+"\\"+rfc_Index_DB[search].rfc_no+"_"+rfc_Index_DB[search].rfc_name+".txt";
                                                PrintWriter file_writer = new PrintWriter(file_name);
                                                String get_file_content=null;
                                                out.println(getRFC);
                                                out.flush();		
                                                while((get_file_content=in.readLine())!=null)
                                                {
                                                        file_writer.println(get_file_content);
                                                        file_writer.flush();
                                                }
                                        
                                                rfc_Index_DB[global_count]=new RFCIndex();
                                                rfc_Index_DB[global_count].rfc_no=rfc_Index_DB[search].rfc_no;
                                                rfc_Index_DB[global_count].rfc_name=rfc_Index_DB[search].rfc_name;
                                                rfc_Index_DB[global_count].owner_host_name=client_obj.hostname;
                                                rfc_Index_DB[global_count].owner_IP=client_ip;
                                                rfc_Index_DB[global_count].TTL=7200;
                              		
                                                global_count+=1;
                                        }
                                        else
                                            System.out.println("No active peers!");
                                                client_socket.close();
                                }
             	
                                else
                                    System.out.println("RFC Found Locally!!");
                                
                                break;
                       
                        default: System.out.println("Not a valid option");
                }
        }
    
        
        class DecreaseTTL implements Runnable
                        {
                            int ab=0;
                            public DecreaseTTL(int temp)
                            {
                                ab=temp;
                            }
                            
                            public void run()
                            {
                                try
                                {
                                    long t= System.currentTimeMillis();
                                    long end = t+7200000;
                                    while(System.currentTimeMillis() <= end)
                                    while(rfc_Index_DB[ab].TTL>=0)
                                    {
                                        if(rfc_Index_DB[ab].TTL==0)
                                        {
                                            for(int ttl_swap=ab;ttl_swap<global_count;ttl_swap++)
                                            {
                                                if(ab+1==global_count)
                                                {
                                                    rfc_Index_DB[ttl_swap].rfc_no=0;
                                                rfc_Index_DB[ttl_swap].rfc_name=null;
                                                rfc_Index_DB[ttl_swap].owner_host_name= null;
                                                rfc_Index_DB[ttl_swap].owner_IP=null;
                                                global_count-=1;
                                                }
                                                else
                                                {
                                                    rfc_Index_DB[ttl_swap].rfc_no=rfc_Index_DB[ttl_swap+1].rfc_no;
                                                rfc_Index_DB[ttl_swap].rfc_name=rfc_Index_DB[ttl_swap+1].rfc_name;
                                                rfc_Index_DB[ttl_swap].owner_host_name= rfc_Index_DB[ttl_swap+1].owner_host_name;
                                                rfc_Index_DB[ttl_swap].owner_IP=rfc_Index_DB[ttl_swap+1].owner_IP;
                                                }
                                            }
                                        }
                                        else
                                            rfc_Index_DB[ab].TTL-=60; 
                                    
                                        System.out.println("New TTL for RFC's received from other clients + " + rfc_Index_DB[ab].TTL);
                                        Thread.sleep(60000);
                                    } 
                                }
                                catch(Exception e)
                                {
                                    System.out.println(e);
                                }
                             }
                        }
        
        
        
        
   
	public static void main(String args[]) throws Exception
	{
                    
        	Client client_obj=new Client();
        	Thread client_thread = new Thread(new Client(client_obj));
        	client_thread.start();
        	client_obj.RfcServerFun();
        }        


        public void run()
        {
        	String ans="no";
        
        	try
		{
                	int i=0;
        		String user_input=null;
                        
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        System.out.print("Enter your hostname : ");
                        client_obj.hostname=br.readLine();
                        client_obj.populateRFC_DB(client_obj);
                        do
                        {
 				System.out.print("\nWhat do you want to do ?"+"\n1.Register\n2.Leave\n3.Get RFC\n->");
                            	user_input = br.readLine();
                            	int opt = Integer.parseInt(user_input);
                            	client_obj.perform_fun(client_obj,opt);
                            	System.out.print("\nDo you want to continue ? (Yes/No) : ");
                            	ans=br.readLine();
                                if(ans.equalsIgnoreCase("no")==true)
                                    break;
                                else while((ans.equalsIgnoreCase("yes")==false)&&(ans.equalsIgnoreCase("no")==false))
                                {
                                    System.out.println("Invalid Input!");
                                    System.out.print("\nDo you want to continue ? (Yes/No) : ");
                                    ans=br.readLine();
                                }
                        }while(ans.equalsIgnoreCase("yes"));  
                                        
            	}
        
        	catch(Exception e)
        	{
                	System.out.println(e);	 
        	}
                try
                {
                        client_obj.closeCon();
                        System.exit(0);
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
	}
        
        
        
        public void RfcServerFun()
        {
            ClientRFCServer RFCServerOBJ = new ClientRFCServer();
            Thread server_thread = new Thread(RFCServerOBJ);
            server_thread.start();
         
        }
        
        
//////////////-------------- CLIENT RFC SERVER -----------///////////////////////


	class ClientRFCServer implements Runnable
	{
    
    		Client client_obj=new Client();
    
    		Socket rfc_client_socket=null;
    		ServerSocket rfc_server=null;
    		ClientRFCServer[] RFCserverOBJ= new ClientRFCServer[100];
 
                public void run()              
    		{
            		for(int j=0;j<100;j++)
			{
				RFCserverOBJ[j]=new ClientRFCServer();
			}

                	try
                	{
                            
                   		rfc_server = new ServerSocket(client_port);
                    		while (true)
                    		{    
                        		rfc_client_socket = rfc_server.accept();
                        		System.out.println("Connection Successful from RFC Server");
                			Thread rfc_server_thread = new Thread(new ClientRFCServer.ServerCon(rfc_client_socket));
                			rfc_server_thread.start();
                        
                    		}
                	}
                
			catch(Exception e)
                	{
                    		System.out.println(e);
                	}
           
		}
        
                    
                
                class ServerCon implements Runnable 
                {
                	Socket rfc_client;
                	public ServerCon(Socket client)
                	{
                    		rfc_client = client;
                	}
                
                	public void run()
                	{
                                 
                		try
                    		{
                        
					BufferedReader in = new BufferedReader(new InputStreamReader(rfc_client.getInputStream()));
        	 			PrintWriter out = new PrintWriter(rfc_client.getOutputStream(), true);
                	
                	
                			System.out.println("Message Received RFC Server");
					out.flush();
			
                        		String read_rfc_request=in.readLine();
                                        String[] request_msg=read_rfc_request.split("<cr><tr>");
                                        
					String command_value[]=request_msg[0].split("<sp>");
			         	
                        		if(command_value[0].equals("GET")&&command_value[1].equalsIgnoreCase("RFC-INDEX"))
					{
                                        	String returnDB="P2P-DI/1.1<sp>200<sp>OK<cr><tr>RFCResponse<cr><tr>";
                                                for(int rfc_count=0;rfc_count<global_count;rfc_count++)
                                                {
                                                    returnDB+="RFCNO<sp>"+rfc_Index_DB[rfc_count].rfc_no+"<cr>RFCNAME<sp>"+rfc_Index_DB[rfc_count].rfc_name+"<cr>HOSTNAME<sp>"+rfc_Index_DB[rfc_count].owner_host_name+"<cr>IP<sp>"+rfc_Index_DB[rfc_count].owner_IP+"<cr><tr>";
                                                }

                                   		out.println(returnDB);
                                    		out.flush();
                                    
                        		}  
                                        // FOR FETCHING RFC
                                        
                                        if(command_value[0].equals("GET")&&command_value[1].equalsIgnoreCase("RFC"))
					{
                                                int rfc_no=Integer.parseInt(command_value[2]);
                                                String rfc_name=null;
                                                File path = new File(file_path);
                                                File[] listOfFiles = path.listFiles();

                                                
                                                for(int n=0;n<listOfFiles.length;n++)
                                                {
                                                    String file_name=(listOfFiles[n].getName()).toString();
                
                                                    String rfc_file[]=file_name.split(".txt");
                                                    String rfc_name_no[]=rfc_file[0].split("_");
                                                    if(rfc_no==Integer.parseInt(rfc_name_no[0]))
                                                    {
                                                        rfc_name=rfc_name_no[1];
                                                        break;
                                                    }
                                                }
                                                
                                        	System.out.println("Sending file to client");
                                    		String file_contents=null;
                                                String file_name=file_path+"\\"+rfc_no+"_"+rfc_name+".txt";
                                                BufferedReader read_file=new BufferedReader(new FileReader(file_name));
                                                while((file_contents=read_file.readLine())!=null)
                                                {
                                                    out.println(file_contents);
                                                    out.flush();
                                    
                                                }
                                   		
                        		}  
                                        
                    		}                        
                 
               
                		catch(Exception e)
                		{
                    			System.out.println(e);
                		}
                                
                                finally
                                {
                                    try
                                    {
                                        rfc_client_socket.close();
                                    }
                                    catch(Exception e)
                                            {
                                                System.out.println(e);
                                            }
                                }
               		}
             	}
	}
}
