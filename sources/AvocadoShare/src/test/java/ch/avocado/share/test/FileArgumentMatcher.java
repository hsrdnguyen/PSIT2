package ch.avocado.share.test;

import ch.avocado.share.model.data.File;
import org.mockito.ArgumentMatcher;

/**
 * Created by coffeemakr on 17.04.16.
 */
public class FileArgumentMatcher implements ArgumentMatcher<File> {

    private File file;

    public FileArgumentMatcher(File file) {
        this.file = file;
    }

    @Override
    public boolean matches(Object o) {
        if(!(o instanceof File)) {
            return false;
        }
        File otherFile = (File) o;
        return otherFile.getId().equals(file.getId());
    }
}
