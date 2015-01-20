package com.griddynamics.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author lzakharova
 */
public class CropImageUtil {

    // TODO move path to imagemagick to the properties file
    public static final String CROP_COMMAND = "/opt/local/bin/convert -verbose -crop %sx%s+%s+%s %s %s";

    public static boolean cropImage(String baseFileFullPath, String croppedFileFullPath, int x, int y, int width, int height) {
        String result = executeCommand(String.format(CROP_COMMAND,
                width, height, x, y, baseFileFullPath, croppedFileFullPath));
        System.out.println(result);
        //TODO add check for result
        return true;
    }

    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);

            BufferedReader stReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line;
            while ((line = stReader.readLine())!= null) {
                output.append(line + "\n");
            }

            while ((line = errReader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return output.toString();
    }
}
