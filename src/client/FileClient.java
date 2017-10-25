package client;

import java.io.File;
import java.util.Scanner;

public class FileClient {
	public static void main(String[] args) {
		try {
			ClientModle1 c = new ClientModle1("localhost", 4001);
			
			Scanner sc = new Scanner(System.in);
			System.out.println("Please type the command:");
			String command = sc.next();
			System.out.println("Please input fileName:");
			String fileName = sc.next();
		    File file = new File(fileName);
	    
		    switch(command){
				case "upload" :
					while (fileName!="exit"&&!file.exists()) {
						System.out.println("file not exit or not file, please input fileName(or input exit then exit):");
						fileName = sc.next();
					    file = new File(fileName);
					}
					if(fileName=="exit"){
						return;
					}
					c.upload(fileName);
					break;
					
					
				case "download" :
					/*while (fileName!="exit"&&!file.isFile()) {
						System.out.println("NOT file, please input fileName again(or input exit then exit):");
						fileName = sc.next();
					    file = new File(fileName);
					}
					if(fileName=="exit"){
						return;
					}*/
					c.download(fileName);
					break;
					
					
				case "delete" :
					/*while (fileName!="exit"&&!file.isFile()) {
						System.out.println("NOT file, please input fileName again(or input exit then exit):");
						fileName = sc.next();
					    file = new File(fileName);
					}
					if(fileName=="exit"){
						return;
					}*/
					c.delete(fileName);
					break;
				default:
					break;	
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
}
