import java.io.*;

import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class anoop_small 
{
	public static void main(String[] args) throws IOException, InterruptedException, Exception
	{
		// Load the Properties File with AWS Credentials
		Properties properties = new Properties();
		properties.load(anoop_small.class
				.getResourceAsStream("/AwsCredentials.properties"));

		BasicAWSCredentials bawsc = new BasicAWSCredentials(
				properties.getProperty("accessKey"),
				properties.getProperty("secretKey"));

		// Create an Amazon EC2 Client
		AmazonEC2Client ec2 = new AmazonEC2Client(bawsc);

		// Create Instance Request
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		
		// Configure Instance Request
		
		runInstancesRequest.withImageId("ami-700e4a19")
		.withInstanceType("m1.medium").withMinCount(1).withMaxCount(1)
		.withKeyName("ec2privatekey");
		
		RunInstancesResult runInstancesResult = ec2
				.runInstances(runInstancesRequest);
		
		runInstancesRequest.withImageId("ami-700e4a19")
		.withInstanceType("m1.small").withMinCount(1).withMaxCount(1)
		.withKeyName("ec2privatekey");
		
		RunInstancesResult runInstancesResult1 = ec2
				.runInstances(runInstancesRequest);
		
		runInstancesRequest.withImageId("ami-700e4a19")
				.withInstanceType("m1.large").withMinCount(1).withMaxCount(1)
				.withKeyName("ec2privatekey");
				
				
				
				RunInstancesResult runInstancesResult2 = ec2
						.runInstances(runInstancesRequest);
			
		Instance instance = runInstancesResult.getReservation().getInstances()
				.get(0);
		
		Instance instance1 = runInstancesResult1.getReservation().getInstances()
				.get(0);
		Instance instance2 = runInstancesResult2.getReservation().getInstances()
				.get(0);
		System.out.println("Launchpad instance is running:");
		String medium = instance.getInstanceId();
		System.out.println("Medium instance : " + medium);
		
		System.out.println("Launchpad instance is running:");
		String small = instance1.getInstanceId();
		System.out.println("Small Instance: " + small);
		
		System.out.println("Launchpad instance is running:");
		String large = instance2.getInstanceId();
		System.out.println("Large Instance: " + large);
		
		Thread.sleep(600000);
		
		//Launch an EC2 Client
		DescribeInstanceStatusRequest describeInstanceRequest = new DescribeInstanceStatusRequest().withInstanceIds(medium);
		
		DescribeInstanceStatusResult describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
		List<InstanceStatus> state = describeInstanceResult.getInstanceStatuses();
		while (state.size()< 1) 
		{ 
		    // Do nothing, just wait, have thread sleep if needed
		    describeInstanceResult = ec2.describeInstanceStatus(describeInstanceRequest);
		    state = describeInstanceResult.getInstanceStatuses();
		}
		String status =  state.get(0).getInstanceState().getName();
		       
		if(status.equals("running"))
		{
			AmazonEC2Client amazonEC2Client = new AmazonEC2Client(bawsc);	
			List<Reservation> reservations = amazonEC2Client.describeInstances().getReservations();
			
			 int reservationCount = reservations.size();
			   
			  for(int i = 0; i < reservationCount; i++) 
			  {   
			      List<Instance> i_all = reservations.get(i).getInstances();
			      int instanceCount = i_all.size();
			      for(int j = 0; j < instanceCount; j++) 
			      {
			          Instance medium_instance = i_all.get(j);
			          String i_id=medium_instance.getInstanceId();
			          if(medium.equals(i_id))
			          {
			        	  System.out.println("Public DNS: "+medium_instance.getPublicDnsName());
			          try 
			          {
			        	  
			        	  for (int k = 0; k < 1; k++) 
			        	  {
			        	  JSch jsch = new JSch();
			              String host = "ec2-54-224-97-89.compute-1.amazonaws.com";
			              String keyFile = "lib/ec2privatekey.pem";
			              jsch.addIdentity(keyFile);
			              Session session = jsch.getSession("ubuntu", host, 22);
			              java.util.Properties config = new java.util.Properties(); 
			              config.put("StrictHostKeyChecking", "no");
			              session.setConfig(config);
			              session.connect();
			              
			              ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			              
			              //InputStream in = channelExec.getInputStream();
			              
			              channelExec
			                .setCommand("cd $HOME/benchmark && ./apache_bench.sh sample.jpg 100000 100 "+medium_instance.getPublicDnsName()+" anoopwithone");
			              channelExec.connect();

			              
			               InputStream in = channelExec.getInputStream();  
			                     byte[] tmp=new byte[1024];  
			                     while(true){  
			                         while(in.available()>0){  
			                           int b=in.read(tmp, 0, 1024);  
			                           if(b<0)break;  
			                           System.out.print(new String(tmp, 0, b));  
			                         }  
			                         if(channelExec.isClosed()){  
			                           System.out.println("exit-status: "+channelExec.getExitStatus());  
			                           break;  
			                         }  
			                         try
			                         {
			                        	 Thread.sleep(1000);
			                         }
			                         
			                         catch(Exception ee1)
			                         {
			                        	System.out.print("Inside medium" + ee1);
			                        	 } 
			                         
			                     }
			        	  }
			          }
			     catch(Exception exception)
		          {
		                        	 System.out.println("Exception"  + exception);
		          }
			                     }
			          }
			          
	  DescribeInstanceStatusRequest describeInstanceRequest1 = new DescribeInstanceStatusRequest().withInstanceIds(small);
			      		
	DescribeInstanceStatusResult describeInstanceResult1 = ec2.describeInstanceStatus(describeInstanceRequest1);
	List<InstanceStatus> state1 = describeInstanceResult1.getInstanceStatuses();
			      		while (state1.size()< 1) 
			      		{ 
			      		    // Do nothing, just wait, have thread sleep if needed
			      		    describeInstanceResult1 = ec2.describeInstanceStatus(describeInstanceRequest1);
			      		    state1 = describeInstanceResult1.getInstanceStatuses();
			      		}
			      		String status1 =  state1.get(0).getInstanceState().getName();
			      		       
			      		if(status1.equals("running"))
			      		{
			      			AmazonEC2Client amazonEC2Client1 = new AmazonEC2Client(bawsc);	
			      			List<Reservation> reservations1 = amazonEC2Client1.describeInstances().getReservations();
			      			
			      			 int reservationCount1 = reservations1.size();
			      			   
			      			  for(int i1 = 0; i1 < reservationCount1; i1++) 
			      			  {   
			      			      List<Instance> instance_all1 = reservations1.get(i).getInstances();
			      			      int instanceCount1 = instance_all1.size();
			      			      for(int j1 = 0; j1 < instanceCount1; j1++) 
			      			      {
			      			          Instance small_instance = instance_all1.get(j1);
			      			          String instance_id1=small_instance.getInstanceId();
			      			          if(small.equals(instance_id1))
			      			          {
			      			        	  System.out.println("Public DNS: "+small_instance.getPublicDnsName());
			      			          try 
			      			          {
			      			        	  
			      			        	  for (int k = 0; k < 1; k++) 
			      			        	  {
			      			        	  JSch jsch = new JSch();
			      			              String host = "ec2-54-224-97-89.compute-1.amazonaws.com";
			      			              String keyFile = "lib/ec2privatekey.pem";
			      			              jsch.addIdentity(keyFile);
			      			              Session session = jsch.getSession("ubuntu", host, 22);
			      			              java.util.Properties config = new java.util.Properties(); 
			      			              config.put("StrictHostKeyChecking", "no");
			      			              session.setConfig(config);
			      			              session.connect();
			      			              
			      			              ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			      			              
			      			              //InputStream in = channelExec.getInputStream();
			      			              
			      			              channelExec
			      			                .setCommand("cd $HOME/benchmark && ./apache_bench.sh sample.jpg 100000 100 "+small_instance.getPublicDnsName()+" anoopwithone");
			      			              channelExec.connect();

			      			              
			      			               InputStream in = channelExec.getInputStream();  
			      			                     byte[] tmp=new byte[1024];  
			      			                     while(true){  
			      			                         while(in.available()>0){  
			      			                           int b=in.read(tmp, 0, 1024);  
			      			                           if(b<0)break;  
			      			                           System.out.print(new String(tmp, 0, b));  
			      			                         }  
			      			                         if(channelExec.isClosed()){  
			      			                           System.out.println("exit-status: "+channelExec.getExitStatus());  
			      			                           break;  
			      			                         }  
			      			                         try
			      			                         {
			      			                        	 Thread.sleep(1000);
			      			                         }
			      			                         
			      			                         catch(Exception ee2)
			      			                         {
			      			                        	 System.out.print("Inside Small" + ee2);
			      			                        	 }  
			      			                   }
			    			        	  }
			    			          }
			    			     catch(Exception exception1)
			    		          {
			    		                        	 System.out.println("Exception in small instance"  + exception1);
			    		          }
			    			                     }
			    			          }
			    			          
			      			        	  
			DescribeInstanceStatusRequest describeInstanceRequest2 = new DescribeInstanceStatusRequest().withInstanceIds(large);
			      			  		
			DescribeInstanceStatusResult describeInstanceResult2 = ec2.describeInstanceStatus(describeInstanceRequest2);
		List<InstanceStatus> state2 = describeInstanceResult2.getInstanceStatuses();
			      			  		while (state2.size()< 1) 
			      			  		{ 
			      			  		    // Do nothing, just wait, have thread sleep if needed
			      			  		    describeInstanceResult2 = ec2.describeInstanceStatus(describeInstanceRequest2);
			      			  		    state2 = describeInstanceResult2.getInstanceStatuses();
			      			  		}
			      			  		String status2 =  state2.get(0).getInstanceState().getName();
			      			  		       
			      			  		if(status2.equals("running"))
			      			  		{
			      			  			AmazonEC2Client amazonEC2Client2 = new AmazonEC2Client(bawsc);	
			      			  			List<Reservation> reservations2 = amazonEC2Client2.describeInstances().getReservations();
			      			  			
			      			  			 int reservationCount2 = reservations2.size();
			      			  			   
			      			  			  for(int i2 = 0; i2 < reservationCount2; i2++) 
			      			  			  {   
			      			  			      List<Instance> instance_all2 = reservations2.get(i).getInstances();
			      			  			      int instanceCount2 = instance_all2.size();
			      			  			      for(int j2 = 0; j2 < instanceCount2; j2++) 
			      			  			      {
			      			  			          Instance large_instance = instance_all2.get(j2);
			      			  			          String instance_id2=large_instance.getInstanceId();
			      			  			          if(large.equals(instance_id2))
			      			  			          {
			      			  			        	  System.out.println("Public DNS: "+large_instance.getPublicDnsName());
			      			  			          try 
			      			  			          {
			      			  			        	  
			      			  			        	  for (int k = 0; k < 1; k++) 
			      			  			        	  {
			      			  			        	  JSch jsch = new JSch();
			      			  			              String host = "ec2-54-224-97-89.compute-1.amazonaws.com";
			      			  			              String keyFile = "lib/ec2privatekey.pem";
			      			  			              jsch.addIdentity(keyFile);
			      			  			              Session session = jsch.getSession("ubuntu", host, 22);
			      			  			              java.util.Properties config = new java.util.Properties(); 
			      			  			              config.put("StrictHostKeyChecking", "no");
			      			  			              session.setConfig(config);
			      			  			              session.connect();
			      			  			              
			      			  			              ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			      			  			              
			      			  			              //InputStream in = channelExec.getInputStream();
			      			  			              
			      			  			              channelExec
			      			  			                .setCommand("cd $HOME/benchmark && ./apache_bench.sh sample.jpg 100000 100 "+large_instance.getPublicDnsName()+" anoopwithone");
			      			  			              channelExec.connect();

			      			  			              
			      			  			               InputStream in = channelExec.getInputStream();  
			      			  			                     byte[] tmp=new byte[1024];  
			      			  			                     while(true){  
			      			  			                         while(in.available()>0){  
			      			  			                           int b=in.read(tmp, 0, 1024);  
			      			  			                           if(b<0)break;  
			      			  			                           System.out.print(new String(tmp, 0, b));  
			      			  			                         }  
			      			  			                         if(channelExec.isClosed()){  
			      			  			                           System.out.println("exit-status: "+channelExec.getExitStatus());  
			      			  			                           break;  
			      			  			                         }  
			      			  			                         try
			      			  			                         {
			      			  			                        	 Thread.sleep(1000);
			      			  			                         }
			      			  			                         
			      			  			                         catch(Exception ee3)
			      			  			                         {
			      			  			                        System.out.print("Inside Large : " + ee3);
			      			  			                        	 }  
			      			  			                 }
			      						        	  }
			      						          }
			      						     catch(Exception exception2)
			      					          {
			      					          System.out.println("Exception"  + exception2);
			      					          }
			      						                     }
			      			  			      }
			      			  			  }
			      			  		}
			      			  }
			      		}
			  }
		}
	}
}
			      						          
//			           ProcessBuilder processBuilder = new ProcessBuilder("ssh", 
//			             "ubuntu@ec2-54-221-139-210.compute-1.amazonaws.com", 
//			             "/.benchmark/apache_bench.sh sample.jpg 100000 100 " +  medium_instance.getPublicDnsName() 
//				         + " anoop");
//			             processBuilder.redirectErrorStream(); 
//			             Process p = processBuilder.start();
//			             InputStream inputStream = p.getInputStream();
//			             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//			             String line;
//			             while((line = reader.readLine())!= null) 
//			             {
//			              System.out.println(line);
//			             }
//			             p.waitFor(
			      			  			          
			      			  			          
			      			  			        
			      			  			      
			      			  			  
			      			  		
			      			          
			      			          
			      			      
