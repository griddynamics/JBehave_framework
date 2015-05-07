package com.griddynamics.imwrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static com.griddynamics.qa.tools.StringParser.getMatchString;

/**
 * @author lzakharova
 */
public class ImageMagickWrapper {

    public static String[] CROP_COMMAND;
    public static String[] COMPARE_COMMAND;
    public final static String CROP_PARAMS = "%sx%s+%s+%s";

    public static final String FUZZ_VALUE = "50%";
    public static final String CORRECT_RESPONSE_CONTENT = "Channel distortion: AE";

    private static String imageMagickPath;

    private final static int CROP_IND_CROP_PARAMS = 3;
    private final static int CROP_IND_BASE_FILE = 4;
    private final static int CROP_IND_CROPPED_FILE = 5;

    private final static int COMPARE_IND_FUZZ = 2;
    private final static int COMPARE_IND_COMPARE_THIS = 6;
    private final static int COMPARE_IND_COMPARE_THAT = 7;
    private final static int COMPARE_IND_RESULT = 8;

    private ImageMagickWrapper() {
        CROP_COMMAND = new String[6];
        CROP_COMMAND[0] = imageMagickPath + "convert";
        CROP_COMMAND[1] = "-verbose";
        CROP_COMMAND[2] = "-crop";
        CROP_COMMAND[CROP_IND_CROP_PARAMS] = "";
        CROP_COMMAND[CROP_IND_BASE_FILE] = "";
        CROP_COMMAND[CROP_IND_CROPPED_FILE] = "";

        COMPARE_COMMAND = new String[9];
        COMPARE_COMMAND[0] = imageMagickPath + "compare";
        COMPARE_COMMAND[1] = "-fuzz";
        COMPARE_COMMAND[COMPARE_IND_FUZZ] = "";
        COMPARE_COMMAND[3] = "-metric";
        COMPARE_COMMAND[4] = "AE";
        COMPARE_COMMAND[5] = "-verbose";
        COMPARE_COMMAND[COMPARE_IND_COMPARE_THIS] = "";
        COMPARE_COMMAND[COMPARE_IND_COMPARE_THAT] = "";
        COMPARE_COMMAND[COMPARE_IND_RESULT] = "";
    }

    public void setImageMagickPath(String path) {
        this.imageMagickPath = path;
    }

    public void crop(String baseFileFullPath, String croppedFileFullPath, int x, int y, int width, int height) {

        CROP_COMMAND[0] = imageMagickPath + "convert";
        CROP_COMMAND[CROP_IND_CROP_PARAMS] = String.format(CROP_PARAMS, width, height, x, y);
        CROP_COMMAND[CROP_IND_BASE_FILE] = baseFileFullPath;
        CROP_COMMAND[CROP_IND_CROPPED_FILE] = croppedFileFullPath;

        getLogger().info("ImageMagick command to be executed: " + CROP_COMMAND);
        String result = executeCommand(CROP_COMMAND);
        getLogger().info(result);
    }

    public int compare(String compareThis, String compareTo, String resultFile) {

        COMPARE_COMMAND[0] = imageMagickPath + "compare";
        COMPARE_COMMAND[COMPARE_IND_FUZZ] = FUZZ_VALUE;
        COMPARE_COMMAND[COMPARE_IND_COMPARE_THIS] = compareThis;
        COMPARE_COMMAND[COMPARE_IND_COMPARE_THAT] = compareTo;
        COMPARE_COMMAND[COMPARE_IND_RESULT] = resultFile;

        getLogger().info("ImageMagick command to be executed: " + COMPARE_COMMAND);
        String result = executeCommand(COMPARE_COMMAND);
        getLogger().info(result);

        // Check that compare command run contains expected response
        if (!result.contains(CORRECT_RESPONSE_CONTENT)) {
            getLogger().error("Wrong output from comparison command:\n" + result);
            return -1;
        }

        // Get number of pixels which differ
        result = getMatchString(result, "all: (\\d+)");
        result = getMatchString(result, "\\d+");
        return Integer.parseInt(result);
    }

    private static String executeCommand(String[] command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader stReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line;

            while ((line = stReader.readLine()) != null) {
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
