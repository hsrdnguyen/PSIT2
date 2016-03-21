package ch.avocado.share.model.factory;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.model.data.Rating;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bergm on 21/03/2016.
 */
public class FileFactory {

    public static File getDefaultFile()
    {
        return new File("", new ArrayList<Category>(), new Date(), null, "", "", "", "", "", new Date(), "", "");
    }

}
