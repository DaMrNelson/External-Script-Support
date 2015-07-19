package twitter_at.damrnelson.roblox.external_script_support;

import java.io.File;

public class ScriptInfo {
	
	public File script;
	public String id;
	
	public ScriptInfo(File script, String id) {
		this.script = script;
		this.id = id;
	}
	
	public String getListString() {
		return id + " - " + script.getAbsolutePath();
	}
	
}
