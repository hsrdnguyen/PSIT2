package ch.avocado.share.common.preview.factory;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.common.preview.IPreviewGenerator;
import ch.avocado.share.common.preview.PreviewException;
import ch.avocado.share.common.preview.generator.PreviewerExceptionPreviewer;
import ch.avocado.share.model.data.File;

import java.util.HashMap;
import java.util.Map;

/**
 * The DefaultPreviewFactory returns an instance of the previewer
 * suitable for the file type.
 * For more information visit about mime-types visit
 * <a href="https://en.wikipedia.org/wiki/Internet_media_type">Wilkipedia</a>

 */
public class DefaultPreviewFactory  extends PreviewFactory{

    static Map<String, PreviewFactory> previewerFactories;

    static {
        previewerFactories = new HashMap<>();
        initPreviewMapping();
    }

    /**
     * Register a new factory for a given mime-type
     * @param mimeType The mime-type to be matched. This can be a top-level type or a full mime-type.
     * @param factory The factory
     */
    static public void registerFactory(String mimeType, PreviewFactory factory) {
        if(mimeType == null) throw new IllegalArgumentException("mimeType is null");
        if(factory == null) throw new IllegalArgumentException("factory is null");
        previewerFactories.put(mimeType, factory);
    }

    /**
     * Set up mime-type mappings
     */
    static private void initPreviewMapping() {
        registerFactory("video", new VideoPreviewerFactory());
        registerFactory("text", new TextPreviewFactory());
        registerFactory("application/pdf", new FramePreviewFactory());
        registerFactory("image", new ImagePreviewerFactory());
    }


    @Override
    public IPreviewGenerator getInstance(File file) throws PreviewException{
        if(file == null) throw new IllegalArgumentException("file is null");
        String contentType, topLevelType;
        PreviewFactory previewFactory = null;
        contentType = file.getMimeType();

        if(contentType.equals("application/x-matroska")) {
            contentType = "video/webm";
        }

            System.out.println("Searching content-type: " + contentType);
        if(previewerFactories.containsKey(contentType)) {
            previewFactory = previewerFactories.get(contentType);
        }


        if(contentType.contains("/")) {
            topLevelType = contentType.split("/",2)[0];
            System.out.println("Searching top-level type: " + topLevelType);
            if(previewerFactories.containsKey(topLevelType)) {
                previewFactory = previewerFactories.get(topLevelType);
            }
        }
        if(previewFactory != null) {
            return previewFactory.getInstance(file);
        }
        throw new PreviewException(ErrorMessageConstants.ERROR_NO_PREVIEW_FACTORY_FOR_TYPE);
    }

    public IPreviewGenerator getInstanceAndHandleErrors(File file) {
        try {
            return getInstance(file);
        } catch (PreviewException e) {
            return new PreviewerExceptionPreviewer(e);
        }
    }
}
