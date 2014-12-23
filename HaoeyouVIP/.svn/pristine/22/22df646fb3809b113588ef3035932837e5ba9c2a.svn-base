package com.chc;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;

/**
 * Runs after every change made to the data classes, for updating ORMLite config file in raw folder
 * If not updated to the latest configuration, the database might not perform as intended. 
 * After run, the config file needs to be moved to the library project, because the package
 * name of this base project is changed for every individual application.
 *
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	public static void main(String[] args) throws Exception {
        File targetDir = new File("I:\\Coding\\android\\DochooCN\\Dochoo\\src\\main\\res\\raw");
        if (targetDir.exists() && targetDir.isDirectory()) {
            File targetFile = new File(targetDir, "ormlite_config.txt");
		    writeConfigFile(targetFile);
        } else {
            System.out.println("Could not find raw folder");
        }
	}
}