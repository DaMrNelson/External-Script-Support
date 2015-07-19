package twitter_at.damrnelson.roblox.external_script_support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import twitter_at.damrnelson.general.Errors;

public class Files {

	public static File baseDir;
	
	public static Properties props;
	public static File propsFile;
	
	public static File scriptFile;
	public static ArrayList<ScriptInfo> scriptList = new ArrayList<ScriptInfo>();
	
	public static void init() throws IOException {
		if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
			baseDir = new File(System.getenv("appdata") + "/RobloxExternalScriptSupport");
		} else {
			baseDir = new File(System.getProperty("user.home") + "/Library/Application Support/RobloxExternalScriptSupport");
		}
		
		baseDir.mkdirs();
		
		propsFile = new File(baseDir, "properties.dat");
		if (!propsFile.exists()) {
			propsFile.createNewFile();
		}
		
		props = new Properties();
		props.load(new FileInputStream(propsFile));
		
		scriptFile = new File(baseDir, "scripts.list");
		
		// Parse script file
		if (!scriptFile.exists()) {
			scriptFile.createNewFile();
		}
		
		FileInputStream in = new FileInputStream(scriptFile);
		
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			String file = "";
			
			while ((len = in.read(buffer)) != -1) {
				file += new String(buffer, 0, len);
			}
			
			if (!file.isEmpty()) {
				for (String line : file.split("\n")) {
					String[] subs = line.split(Pattern.quote("*"));
					scriptList.add(new ScriptInfo(new File(subs[1]), subs[0]));
				}
			}
		} catch (Exception e) {
			Errors.error("Unable to load save files. Program will terminate.\nIf you recieve this error more than once, try deleting " + scriptFile.getAbsolutePath(), e);
		} finally {
			in.close();
		}
	}
	
	public static void saveProperties() {
		try {
			props.store(new FileOutputStream(propsFile), String.valueOf(System.currentTimeMillis()));
		} catch (Exception e) {
			Errors.error("Unable to save properties.", e);
		}
	}
	
	public static void saveScripts() {
		String file = "";
		ScriptInfo info;
		
		for (int i = 0; i < scriptList.size(); i++) {
			info = scriptList.get(i);
			file += info.id;
			file += "*";
			file += info.script.getAbsolutePath();
			
			if (i < scriptList.size() - 1) {
				file += "\n";
			}
		}
		
		try {
			scriptFile.delete();
			scriptFile.createNewFile();
			
			FileOutputStream out = new FileOutputStream(scriptFile);
			out.write(file.getBytes());
			out.close();
		} catch (Exception e) {
			Errors.error("Unable to save files. Please try saving again, or all data may be lost.", e);
		}
	}
	
}
