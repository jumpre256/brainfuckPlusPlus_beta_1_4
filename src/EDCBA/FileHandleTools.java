package EDCBA;

import java.io.File;

public class FileHandleTools {
    public static boolean fileExists(String filePath)
    {
        File someFile = new File(filePath);
        return someFile.exists();
    }
}
