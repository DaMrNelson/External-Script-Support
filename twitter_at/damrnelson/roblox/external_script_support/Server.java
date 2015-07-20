package twitter_at.damrnelson.roblox.external_script_support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import twitter_at.damrnelson.general.Errors;

public class Server {
	
	boolean isRunning = false;
	public ServerSocket serverSocket;
	
	public Server() throws IOException {
		serverSocket = new ServerSocket(8440);
	}
	
	public void start() throws IOException {
		isRunning = true;
		
		while (isRunning) {
			Socket socket = serverSocket.accept();
			
			if (isRunning) {
				handle(socket);
			}
		}
	}
	
	public void stop() throws IOException {
		isRunning = false;
		serverSocket.close();
	}
	
	public void handle(final Socket s) {
		new Thread() {
			public void run() {
				try {
					InputStream in = s.getInputStream();
					byte[] buffer = new byte[1024];
					int len;
					String input = "";
					
					OutputStream out = s.getOutputStream();
					
					while ((len = in.read(buffer)) != -1) {
						input += new String(buffer, 0, len);
						
						if (input.endsWith("\r\n\r\n")) {
							break;
						}
					}
					
					int index = input.indexOf("\r\n");
					
					if (index != -1) {
						String firstLine = input.substring(0, index);
						String[] sections = firstLine.split(" ");
						
						if (sections[0].equals("GET")) {
							String page = sections[1].substring(1);
							
							if (page.isEmpty()) {
								out.write(getResponse(sections[2], "ERROR: No page requested."));
							} else {
								String lowerPage = page.toLowerCase();
								
								if (lowerPage.equals("wait-page")) {
									s.setKeepAlive(true);
									
									try {
										for (int i = 0; i < 100; i++) { // 100 * 100 = 10,000ms = 10s timeout
											Thread.sleep(100);
											
											if (Files.scriptsChanged || !s.isConnected()) {
												break;
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									if (Files.scriptsChanged && s.isConnected()) {
										Files.scriptsChanged = false;
									}
									
									out.write(getResponse(sections[2], String.valueOf(Files.scriptsChanged)));
								} else {
									ScriptInfo info = null;
									
									for (ScriptInfo inf : Files.scriptList) {
										if (inf.id.toLowerCase().equals(lowerPage)) {
											info = inf;
											break;
										}
									}
									
									if (info == null) {
										out.write(getResponse(sections[2], "ERROR: No match for ID " + page + "."));
									} else {
										FileInputStream in2 = new FileInputStream(info.script);
										byte[] buffer2 = new byte[1024];
										int len2;
										String file = "";
										
										while ((len2 = in2.read(buffer2)) != -1) {
											file += new String(buffer2, 0, len2);
										}
										
										out.write(getResponse(sections[2], "SCRIPT: " + file));
										in2.close();
									}
								}
							}
						} else {
							out.write(getResponse(sections[2], "ERROR: Was not a GET request."));
						}
					}
					
					in.close();
					out.close();
					s.close();
				} catch (IOException e) {
					Errors.error("Unable to handle socket.", e);
				}
			}
		}.start();
	}
	
	public byte[] getResponse(String ver, String data) {
		String response = ver + " 200 OK\r\n";
		
		response += "Content-Length: " + data.length() + "\r\n";
		response += "Content-Type: text/plain\r\n";
		response += "\r\n";
		response += data;
		
		return response.getBytes();
	}
	
}
