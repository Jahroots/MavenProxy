package com.socgen.maven.proxy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class CookieReader {
	public static String VALUE;
	public static File FILE = new File("cookies.txt");
	public static long lastRead;
	
	public static long getLastRead(){
		return lastRead;
	}
	public static void readCookies() {
		lastRead = FILE.lastModified();
		try{
			final FileInputStream fis = new FileInputStream(FILE);
			final Scanner scanner = new Scanner(fis);
			while (scanner.hasNextLine()){
				VALUE = scanner.nextLine();
			}
			scanner.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
