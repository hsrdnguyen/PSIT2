package ch.avocado.share.model.factory;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileFactory {

    public static File getDefaultFile()
    {
        return new File(null, new ArrayList<Category>(), new Date(0), 0, "", "", "", "", new Date(0), "", "", "");
    }

}
