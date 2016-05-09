package ch.avocado.share.service.Mock;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.IFileDataHandler;
import ch.avocado.share.service.exceptions.DataHandlerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bergm on 19/03/2016.
 */
public class FileDataHandlerMock extends DataHandlerMockBase<File> implements IFileDataHandler {


    @Override
    public String addFile(File file) throws DataHandlerException {
        if(file.getTitle() == null) throw new IllegalArgumentException("file.title is null");
        if(file.getModuleId() == null) throw new IllegalArgumentException("file.moduleId is null");
        if(file.getPath() == null) throw new IllegalArgumentException("file.path is null");
        if(file.getLastChanged() == null) throw new IllegalArgumentException("file.lastChanged is null");
        return add(file);
    }

    @Override
    public boolean deleteFile(File file) throws DataHandlerException {
        return delete(file);
    }

    @Override
    public File getFile(String fileId) {
        return get(fileId);
    }

    @Override
    public List<File> getFiles(List<String> ids) throws DataHandlerException {
        ArrayList<File> files = new ArrayList<>(ids.size());
        for(String id: ids) {
            File file = getFile(id);
            if(file != null) {
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public List<File> searchFiles(String searchString) throws DataHandlerException {
        return Arrays.asList(getAll(File.class));
    }

    @Override
    public File getFileByTitleAndModule(String fileTitle, String moduleId) {
        if(fileTitle == null) throw new IllegalArgumentException("fileTitle is null");
        if(moduleId == null) throw new IllegalArgumentException("moduleId is null");
        for(File file: objects.values()) {
            if(fileTitle.equals(file.getTitle()) && moduleId.equals(file.getModuleId())) {
                return file;
            }
        }
        return null;
    }

    @Override
    public boolean updateFile(File file){
        return update(file);
    }

    public static void use() throws Exception {
        if(!ServiceLocator.getService(IFileDataHandler.class).getClass().equals(FileDataHandlerMock.class)) {
            ServiceLocatorModifier.setService(IFileDataHandler.class, new FileDataHandlerMock());
        }
    }
}
