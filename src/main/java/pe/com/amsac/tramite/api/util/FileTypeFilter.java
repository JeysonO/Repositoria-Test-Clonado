package pe.com.amsac.tramite.api.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileTypeFilter implements FilenameFilter {
    private String filetype;

    public FileTypeFilter(String filetype) {
        this.filetype = filetype;
    }

    public final boolean accept(File dir, String name) {
        if (name.length() > this.filetype.length()) {
            String currentFiletype = name.substring(name.length() - this.filetype.length());
            if (currentFiletype.equalsIgnoreCase(this.filetype)) {
                return true;
            }
        }

        return false;
    }
}
