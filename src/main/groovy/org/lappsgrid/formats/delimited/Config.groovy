package org.lappsgrid.formats.delimited

import org.lappsgrid.formats.delimited.extra.Feature

class Config {
    String RABBIT_HOST = "rabbitmq.lappsgrid.org/nlp"
    String EXCHANGE = "formats"
    String MAILBOX = "delimited"

    String USERNAME = "anonymous"
    String PASSWORD = "anonymous"

    Properties secrets
    Properties ini

    Config() {
        // Order of precedence when looking for configuration settings.
        // 1. Java System properties
        // 2. Environment variables
        // 3. Docker secret in /run/secrets/
        // 4. Ini file in /etc/lapps
        // 5. Use default value.
        secrets = new Properties()
        File secretFile = new File("/run/secrets/formats-delimited.ini")
        if (secretFile.exists()) {
            secrets.load(new FileReader(secretFile))
        }
        ini = new Properties()
        File iniFile = new File("/etc/lapps/formats-delimited.ini")
        if (iniFile.exists()) {
            ini.load(new FileReader(iniFile))
        }
        RABBIT_HOST = getSetting("RABBIT_HOST", RABBIT_HOST)
        EXCHANGE = getSetting("EXCHANGE", EXCHANGE)
        MAILBOX = getSetting("MAILBOX", MAILBOX)
        USERNAME = getSetting("USERNAME", USERNAME)
        PASSWORD = getSetting("PASSWORD", PASSWORD)
    }

    String getSetting(String name, String defaultValue) {
        String value = System.getProperty(name)
        if (value) return value
        value = System.getenv(name)
        if (value) return value
        value = secrets.getProperty(name)
        if (value) return value
        value = ini.getProperty(name)
        if (value) return value
        return defaultValue
    }
}
