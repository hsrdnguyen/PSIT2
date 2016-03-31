package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.PreviewerExceptionPreviewer;
import ch.avocado.share.model.data.File;

import java.util.HashMap;
import java.util.Map;


public class DefaultPreviewFactory  extends PreviewFactory{

    static Map<String, PreviewFactory> previewerClasses;

    static {
        previewerClasses = new HashMap<>();
        setupPreviewClassMapping();
    }

    static public void registerFactory(String mimeType, PreviewFactory factory) {
        previewerClasses.put(mimeType, factory);
    }

    static private void setupPreviewClassMapping() {
        registerFactory("video", new VideoPreviewerFactory());
        registerFactory("text", new TextPreviewFactory());
        registerFactory("application/pdf", new FramePreviewFactory());
    }


    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException{
        String contentType, topLevelType;
        PreviewFactory previewFactory = null;
        contentType = getMimeType(file);

        if(previewerClasses.containsKey(contentType)) {
            previewFactory = previewerClasses.get(contentType);
        }
        if(contentType.contains("/")) {
            topLevelType = contentType.split("/",2)[0];
            if(previewerClasses.containsKey(topLevelType)) {
                previewFactory = previewerClasses.get(topLevelType);
            }
        }
        if(previewFactory != null) {
            return previewFactory.getInstance(file);
        }
        throw new PreviewException("Mime-Type wird nicht unterstützt.");
    }

    public IPreviewGenerator getInstanceAndHandleErrors(File file) {
        try {
            return getInstance(file);
        } catch (PreviewException e) {
            return new PreviewerExceptionPreviewer(e);
        }
    }
}
