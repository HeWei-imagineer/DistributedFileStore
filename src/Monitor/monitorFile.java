package Monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

import java.util.*;
import java.util.Timer;


public class monitorFile extends JFrame {
   
    public  static DefaultTableModel tableModel;   
    public  static JTable table;
    public monitorFile()
    {
        super();
        setTitle("文件管理器");
        setBounds(100,100,900,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] columnNames = {"UUID","文件名称","文件大小","主存储节点信息","备份节点信息"};   
        tableModel = new DefaultTableModel(null,columnNames);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);   
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(table);
        final JPanel panel = new JPanel();
        getContentPane().add(panel,BorderLayout.SOUTH);
       
    }
    public static void MFile() {
        monitorFile fileMonitor = new monitorFile();
        fileMonitor.setVisible(true);
        startmonitor();
    }

    private static void startmonitor() {
        new Thread(){ 
        	public void run() {
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
						// TODO: handle exception
						System.out.println("table initial");
					}
					System.out.println("display");
				   display();
					
				}
			}; 
			timer.schedule(myTask, 1000, 1000*10);
                }
        }.start();
           
       
    }
    
    private static void display() {
    	
    	 Iterator<Map.Entry<String,Map<String, String>>> iterator = server.Server.FileInformation.entrySet().iterator();
    	 while(iterator.hasNext()){
    	 Map.Entry<String,Map<String, String>> entry = iterator.next();
    		
    		String[] ss = new String[5];
    		ss[0] = entry.getValue().get("FileName");
            ss[1] = entry.getValue().get("UUID"); 
            ss[2] = String.valueOf(entry.getValue().get("FileLength"));
            ss[3] = entry.getValue().get("mainPort");
            ss[4] = entry.getValue().get("vicePort");
            System.out.println(ss[0]);
            tableModel.addRow(ss);
    	}
    	
 
    }
}
