package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.Closeable;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

public class Csv {
    public static class Writer {
        private Appendable appendable;

        private char delimiter = ';';

        private boolean first = true;

        public Writer(String fileName) { this(new File(fileName)); }
        public Writer(File file) {
            try {
                appendable = new FileWriter(file);
            } catch (java.io.IOException e) { throw new IOException(e); }
        }
        public Writer(Appendable appendable) { this.appendable = appendable; }

        public Writer value(String value) {
            if (!first) string("" + delimiter);
            string(escape(value));
            first = false;
            return this;
        }

        public Writer newLine() {
            first = true;
            return string("\n");
        }

        public Writer comment(String comment) {
            if (!first) throw new FormatException("invalid csv: misplaced comment");
            return string("#").string(comment).newLine();
        }

        public Writer flush() {
            try {
                if (appendable instanceof Flushable) {
                    Flushable flushable = (Flushable) appendable;
                    flushable.flush();
                }
            } catch (java.io.IOException e) { throw new IOException(e); }
            return this;
        }

        public void close() {
            try {
                if (appendable instanceof Closeable) {
                    Closeable closeable = (Closeable) appendable;
                    closeable.close();
                }
            } catch (java.io.IOException e) { throw new IOException(e); }
        }

        private Writer string(String s) {
            try {
                appendable.append(s);
            } catch (java.io.IOException e) { throw new IOException(e); }
            return this;
        }

        private String escape(String value) {
            if (value == null) return "";
            if (value.length() == 0) return "\"\"";

            boolean needQuoting = value.startsWith(" ") || value.endsWith(" ") || (value.startsWith("#") && first);
            if (!needQuoting) {
                for (char ch : new char[]{'\"', '\\', '\r', '\n', '\t', delimiter}) {
                    if (value.indexOf(ch) != -1) {
                        needQuoting = true;
                        break;
                    }
                }
            }

            String result = value.replace("\"", "\"\"");
            if (needQuoting) result = "\"" + result + "\"";
            return result;
        }

        public Writer delimiter(char delimiter) { this.delimiter = delimiter; return this; }
    }


    public static class Reader {
        private static final String impossibleString = "$#%^&*!xyxb$#%&*!^";
        private BufferedReader reader;

        private char delimiter = ';';
        private boolean preserveSpaces = true;
        private boolean ignoreEmptyLines = false;
        private boolean ignoreComments = false;

        public Reader(java.io.Reader reader) { this.reader = new BufferedReader(reader); }

        public List<String> readLine() {
            String line;
            try {
                line = reader.readLine();
            } catch (java.io.IOException e) { throw new IOException(e); }
            if (line == null) return null;
            if (!preserveSpaces) line = removeLeadingSpaces(line);
            if (ignoreComments && line.startsWith("#")) return readLine();
            if (ignoreEmptyLines && line.length() == 0) return readLine();

            List<String> result = new ArrayList<String>();

            while (line != null) {
                String token = "";
                int nextDelimiterIndex = line.indexOf(delimiter);
                int openQuoteIndex = line.indexOf("\"");

                if ((nextDelimiterIndex > openQuoteIndex || nextDelimiterIndex == -1) && openQuoteIndex != -1) {
                    token = line.substring(0, openQuoteIndex + 1);
                    line = markDoubleQuotes(line.substring(openQuoteIndex + 1));

                    int closeQuoteIndex = line.indexOf("\"");

                    while (closeQuoteIndex == -1) {
                        token += line + "\n";
                        try {
                            line = reader.readLine();
                        } catch (java.io.IOException e) { throw new IOException(e); }
                        if (line == null) throw new FormatException("invalid csv: premature end of csv");
                        closeQuoteIndex = line.indexOf("\"");
                    }

                    nextDelimiterIndex = line.indexOf(delimiter, closeQuoteIndex);
                }

                if (nextDelimiterIndex == -1) {
                    token += line;
                    line = null;
                } else {
                    token += line.substring(0, nextDelimiterIndex);
                    line = unmarkDoubleQuotes(line.substring(nextDelimiterIndex + 1, line.length()));
                }

                result.add(unescape(token));
            }

            return result;
        }

        public void close() {
            try {
                reader.close();
            } catch (java.io.IOException e) { throw new IOException(e); }
        }

        private String unescape(String s) {
            String result = s;
            if (!preserveSpaces || result.contains("\"")) result = result.trim();
            if (result.startsWith("\"") ^ result.endsWith("\"")) throw new FormatException("invalid csv: misplaced quote");
            if (result.startsWith("\"")) result = result.substring(1, result.length() - 1);
            result = markDoubleQuotes(result);
            if (result.contains("\"")) throw new FormatException("invalid csv: misplaced quote"); // could this ever happen at all?
            result = unmarkDoubleQuotes(result);
            return result;
        }

        private String unmarkDoubleQuotes(String s) { return s.replace(impossibleString, "\""); }
        private String markDoubleQuotes(String s) { return s.replace("\"\"", impossibleString); }

        private String removeLeadingSpaces(String s) { return s.replaceFirst(" +", ""); }

        public Reader delimiter(char delimiter) { this.delimiter = delimiter; return this; }
        public Reader preserveSpaces(boolean preserveSpaces) { this.preserveSpaces = preserveSpaces; return this; }
        public Reader ignoreEmptyLines(boolean ignoreEmptyLines) { this.ignoreEmptyLines = ignoreEmptyLines; return this; }
        public Reader ignoreComments(boolean ignoreComments) { this.ignoreComments = ignoreComments; return this; }
    }


    public static class Exception extends RuntimeException {
        public Exception() { }
        public Exception(String message) { super(message); }
        public Exception(String message, Throwable cause) { super(message, cause); }
        public Exception(Throwable cause) { super(cause); }
    }

    public static class IOException extends Exception {
        public IOException() { }
        public IOException(String message) { super(message); }
        public IOException(String message, Throwable cause) { super(message, cause); }
        public IOException(Throwable cause) { super(cause); }
    }

    public static class FormatException extends Exception {
        public FormatException() { }
        public FormatException(String message) { super(message); }
        public FormatException(String message, Throwable cause) { super(message, cause); }
        public FormatException(Throwable cause) { super(cause); }
    }
}