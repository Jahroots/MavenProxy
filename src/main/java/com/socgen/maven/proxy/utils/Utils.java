package com.socgen.maven.proxy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

public final class Utils {
	public static final String METHOD = "method";
	public static final String URI = "uri";
	public static final String PROTOCOL = "protocol";
	public static final Calendar c = Calendar.getInstance();
	
	public enum ResponseType{
		HTTP_200, HTTP_404;
	}
	public static void sendResponseToSocket(final OutputStream outputStream, final String fileName, final ResponseType responseType){
		final byte[] fileContent = Utils.fileContent(fileName);
		try{
			byte[] response = null;
			if (ResponseType.HTTP_200.equals(responseType))
				response = Utils.get200(fileContent).getBytes();
			else response = Utils.get404(fileContent).getBytes();
			outputStream.write(response);
			outputStream.write(fileContent);
			outputStream.flush();
		}catch(IOException io){
			io.printStackTrace();
		}
	}
	
	public static String get200(final byte[] fileContent){
		final StringBuilder sb = new StringBuilder("HTTP/1.1 200 OK\r\nContent-length : ");
		sb.append(fileContent.length);
		final String date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
		sb.append("\r\nDate : ");
		sb.append(date);
		sb.append("\r\nContent-Type : text/html\r\n\r\n");
		return sb.toString();
	}
	public static String get404(final byte[] fileContent){
		final StringBuilder sb = new StringBuilder("HTTP/1.1 404 OK\r\nDate : ");
		final String date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
		sb.append(date);
		sb.append("\r\nContent-Type : text/html\r\n\r\n");
		return sb.toString();
	}
	
	public static byte[] fileContent(final String fileName){
		File file = new File(fileName);
//		System.out.println(file.exists() + " " + file.getAbsolutePath());
		byte fileContent[] = new byte[(int) file.length()];
		try{
			FileInputStream fin = new FileInputStream(file);
			fin.read(fileContent);
			fin.close();
			file.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
		return fileContent;
	}
	public static String getFileName(final String url){
		final StringTokenizer tokenizer = new StringTokenizer(url, "/");
		String fileName = null;
		while(tokenizer.hasMoreElements()){
			fileName = (String) tokenizer.nextElement();
		}
		
		return fileName;
	}
	
	public static Map<String, String> getRequestMap(final String request) {
		final Map<String, String> mapRequest = new Hashtable<String, String>();
		final StringTokenizer tokenizer = new StringTokenizer(request, " ");
		if (tokenizer.countTokens() == 3) {
			final String method = (String) tokenizer.nextElement();
			final String uri = (String) tokenizer.nextElement();
			final String protocol = (String) tokenizer.nextElement();

			mapRequest.put(METHOD, method);
			mapRequest.put(URI, uri);
			mapRequest.put(PROTOCOL, protocol);
		}
		return mapRequest;
	}
	
	public static void printProgBar(int percent, String txt) {
		StringBuilder bar = new StringBuilder("[");

		for (int i = 0; i < 50; i++) {
			if (i < (percent / 2)) {
				bar.append("=");
			} else if (i == (percent / 2)) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}

		bar.append("] " + txt + "  " + percent + "%     ");
		System.out.print("\r" + bar.toString());
	}
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;
	public static String convertToStringRepresentation(final long value){
	    final long[] dividers = new long[] { T, G, M, K, 1 };
	    final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
	    if(value < 1)
	        throw new IllegalArgumentException("Invalid file size: " + value);
	    String result = null;
	    for(int i = 0; i < dividers.length; i++){
	        final long divider = dividers[i];
	        if(value >= divider){
	            result = format(value, divider, units[i]);
	            break;
	        }
	    }
	    return result;
	}

	private static String format(final long value,
	    final long divider,
	    final String unit){
	    final double result =
	        divider > 1 ? (double) value / (double) divider : (double) value;
//	    return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	    return new DecimalFormat("###.####").format(result) + " " + unit;
	}
	
}
