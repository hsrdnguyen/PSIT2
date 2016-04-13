package ch.avocado.share.servlet;

import ch.avocado.share.controller.FileBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.File;

import javax.servlet.annotation.WebServlet;

@WebServlet("/file")
public class FileServlet extends ResourceServlet<File> {
    @Override
    protected Class<? extends ResourceBean<File>> getBeanClass() {
        return FileBean.class;
    }
}
