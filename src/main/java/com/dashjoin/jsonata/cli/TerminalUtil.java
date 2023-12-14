package com.dashjoin.jsonata.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import com.dashjoin.jsonata.json.Json;

/**
 * Terminal utility functions + JSONata extensions.
 */
public class TerminalUtil {

    /**
     * Supported input formats from file/stdin
     */
    public static enum InputFormat {
        /**
         * Automatically detect input format
         */
        auto,
        /**
         * Input is in JSON format
         */
        json,
        /**
         * Input is a text string
         */
        string;
    }

    /**
     * Reads the given input stream with the specified format.
     * 
     * Note: reads the whole stream into memory (no "streaming mode" yet!)
     * 
     * @param in
     * @param format
     * @return Input object
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static Object readInput(InputStream in, InputFormat format)
            throws IOException, UnsupportedEncodingException {
        Object input = null;
        switch (format) {
            case auto: {
                if (in.markSupported())
                    in.mark(65536);
                try {
                    // First try JSON, if there is an error,
                    // reset the stream and use string format
                    return readInput(in, InputFormat.json);
                } catch (Exception ex) {
                    if (in.markSupported()) {
                        in.reset();
                        return readInput(in, InputFormat.string);
                    }
                    // We cannot reset the stream: throw the exception
                    // Need to specify the format manually in this case
                    throw ex;
                }
            }
            case json: {
                input = Json.parseJson(new InputStreamReader(in));
                break;
            }
            case string: {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length; (length = in.read(buffer)) != -1;) {
                    result.write(buffer, 0, length);
                }
                input = result.toString("UTF-8");
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported input format: " + format);
        }
        return input;
    }
}
