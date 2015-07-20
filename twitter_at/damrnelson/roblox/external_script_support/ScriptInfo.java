package twitter_at.damrnelson.roblox.external_script_support;

import java.io.File;

public class ScriptInfo {
	
	public File script;
	public String id;
	
	private long lastModified;
	private boolean isRunning = true;
	
	public ScriptInfo(File s, String id) {
		this.script = s;
		this.id = id;
		
		lastModified = s.lastModified();
		
		new Thread() {
			public void run() {
				while (isRunning) {
					try {
						if (script.lastModified() != lastModified) {
							lastModified = script.lastModified();
							Files.scriptsChanged = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public String getListString() {
		return id + " - " + script.getAbsolutePath();
	}
	
	public void disconnect() {
		isRunning = false;
	}
	
}
