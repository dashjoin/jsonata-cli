package com.dashjoin.jsonata.cli;

import java.io.InputStream;
import java.util.Properties;

/**
 * Version extraction helpers
 */
public class Version {
    private Version() {}

    /**
     * Returns the version string.
     * Read from pom.properties of jsonata-java and jsonata-cli
     * 
     * @return version
     */
    public static String getVersion() {
        String verCli = getVersion("/META-INF/maven/com.dashjoin/jsonata-cli/pom.properties");
        if (verCli==null)
            verCli = "dev";
        String verJsonata = getVersion("/META-INF/maven/com.dashjoin/jsonata/pom.properties");
        if (verJsonata==null)
            verJsonata = "dev";
        return verJsonata+"-"+(verCli!=null ? verCli : "dev");
    }

    /**
     * Reads version from the given pom.properties
     * 
     * @param name
     * @return
     */
    private static String getVersion(String name) {
        try (InputStream in = Version.class.getResourceAsStream(name)) {
            Properties prop = new Properties();
            prop.load(in);
            return prop.getProperty("version");
        } catch (Exception ex) {
            return null;
        }
    }
}
