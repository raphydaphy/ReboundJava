package com.raphydaphy.rebound.util;

import com.raphydaphy.rebound.Rebound;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Logging {
    public static Logger main;
    public static Logger glfw;

    public static void setLevel(Level level) {
        main = Logger.getLogger("Rebound");
        main.setLevel(level);
        main.setUseParentHandlers(false);
        Formatter formatter = new LogFormatter();
        LogHandler err = new LogHandler(System.err, formatter);

        Filter notError = (logLevel) -> logLevel.getLevel().intValue() < Level.WARNING.intValue();
        err.setFilter((logLevel) -> !notError.isLoggable(logLevel));
        err.setLevel(level);
        main.addHandler(err);

        if (level.intValue() < Level.WARNING.intValue()) {
            LogHandler out = new LogHandler(System.out, formatter);
            out.setFilter(notError);
            out.setLevel(level);
            main.addHandler(out);
        }

        Path runDir = Paths.get(Rebound.NAMESPACE, "logs");

        try {
            if (!Files.exists(runDir)) {
                Files.createDirectories(runDir);
                main.log(Level.INFO, "Created logging directory!");
            }
            FileHandler handler = new FileHandler(Paths.get(runDir.toString(), "latest.log").toString());
            handler.setFilter(null);
            handler.setFormatter(formatter);
            main.addHandler(handler);
        } catch (IOException e) {
            main.log(Level.SEVERE, "Failed to create logging directory! Please ensure that you have write access.", e);
        }

        glfw = Logger.getLogger("GLFW");
        glfw.setParent(main);
    }

    public static class LogHandler extends StreamHandler {
        LogHandler(OutputStream stream, Formatter format) {
            super(stream, format);
        }

        @Override
        public final synchronized void publish(LogRecord record) {
            super.publish(record);
            super.flush();
        }
    }

    public static class LogFormatter extends Formatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        private final Date date = new Date();

        @Override
        public final String format(LogRecord record) {
            this.date.setTime(record.getMillis());
            String formatted = "[" + record.getLoggerName() + "] [" + this.dateFormat.format(this.date) + "] [" + record.getLevel() + "] " + record.getMessage() + System.lineSeparator();
            Throwable exception = record.getThrown();
            if (exception != null) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);
                printWriter.close();
                formatted += stringWriter.toString();
            }
            return formatted;
        }
    }
}
