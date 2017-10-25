package Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import server.Server;
import storageNode.storageNode;

import java.awt.*;
import java.io.*;

import java.util.*;
import java.util.Timer;


public class monitorNode extends JFrame {
   
    public  static DefaultTableModel tableModel;  
    public  JTable table;
    public monitorNode()
    {
        super();
        setTitle("节点管理器");
        setBounds(100,100,900,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] columnNames = {"节点名称","IP","端口","文件数量","文件仓库名称","容量","剩余容量","是否可用"};   
        tableModel = new DefaultTableModel(null,columnNames);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);   
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(table);
        final JPanel panel = new JPanel();
        getContentPane().add(panel,BorderLayout.SOUTH);
       
    }
    
    public static void Mnode() {
    	System.out.println("monitor node");
        monitorNode fileMonitor = new monitorNode();
        fileMonitor.setVisible(true);
        startmonitor();
    }

    private static void startmonitor() {
        
        	
	     Timer timer = new Timer();
	       TimerTask myTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(tableModel.getRowCount()>0){
					tableModel.removeRow(tableModel.getRowCount()-1);
				   }
				} catch (Exception e) {
					// TODO: handle no row exception 
					System.out.println("table initial");
				}
				System.out.println("display");
			   display();
				
			}
		};  
		timer.schedule(myTask, 1000, 1000*10);
	   
    }
    
    private static void display() {
    	 System.out.println("start");
    	 Iterator<storageNode> iterator = server.Server.NodeInformation.iterator();
    	 System.out.println(server.Server.NodeInformation.size());
    	 while(iterator.hasNext()){
    		System.out.println("1");
    		storageNode node = iterator.next();
    		String[] ss = new String[9];
    		
    		ss[0] = node.NodeName;
            ss[1] = node.NodeIP; 
            ss[2] = String.valueOf(node.NodePort);
            ss[3] = String.valueOf(node.fileSum);
            ss[4] = node.RootFolder.getName();
           // ss[6] = String.valueOf(node.trueVolume/8000000)+"GB";
           // ss[7] = String.valueOf(node.leftVolume/8000000)+"GB"; 
            ss[6] = String.valueOf(node.trueVolume);
            ss[7] = String.valueOf(node.leftVolume); 
            ss[8] = String.valueOf(node.IsAlive); 
            System.out.println(ss[0]);        
            tableModel.addRow(ss);
    	}
    	
 
    }
}
