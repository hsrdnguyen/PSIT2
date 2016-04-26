package ch.avocado.share.servlet.resources;

import ch.avocado.share.controller.FileBean;
import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.File;
import ch.avocado.share.servlet.resources.base.HtmlRenderer;
import ch.avocado.share.servlet.resources.base.ResourceServlet;
import ch.avocado.share.servlet.resources.base.ViewRenderer;

import javax.servlet.annotation.WebServlet;

@WebServlet(value = "/file", name = "File")
public class FileServlet extends ResourceServlet<File> {
    @Override
    protected ResourceBean<File> getBean() {
        return new FileBean();
    }

    @Override
    protected ViewRenderer getHtmlRenderer() {
        return new HtmlRenderer("templates/file/");
    }
}
