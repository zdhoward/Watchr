/**
 * Created by Seutonious on 1/12/2016.
 */


import java.awt.Desktop;
import java.io.IOException;
import java.io.File;

public class Tools {
    public static void open(File document) throws IOException {
        Desktop dt = Desktop.getDesktop();
        dt.open(document);
    }
}
