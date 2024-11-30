package com.moneydance.modules.features.tasemaya.utils;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;




public class Utils {



    public static Logger getLogger(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        SimpleFormatter formatter = new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord lr) {
                return String.format("[%1$tF %1$tT %2$s: %3$s [in %4$s:%5$d]%n",
                        lr.getMillis(), lr.getLevel(), lr.getMessage(), lr.getSourceClassName(), lr.getSourceMethodName());
            }
        };
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
        return logger;
    }

    public enum Language {
        HEBREW(0),
        ENGLISH(1);

        private final int value;

        Language(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

