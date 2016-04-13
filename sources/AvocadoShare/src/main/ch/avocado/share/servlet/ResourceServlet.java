package ch.avocado.share.servlet;

import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.exceptions.HttpBeanException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;


public abstract class ResourceServlet<E extends AccessControlObjectBase> extends GenericServlet {

    ResourceBean<E> resourceBean = null;

    protected abstract Class<? extends ResourceBean<E>> getBeanClass();

    private ResourceBean<E> getResourceBean() throws HttpBeanException {
        try {
            return getBeanClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
        throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create bean");
    }

    private String getSetterName(String parameter) {
        return "set" + parameter.substring(0, 1).toUpperCase() + parameter.substring(1);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        request.getRequestDispatcher("includes/header.jsp").include(request, response);
        try {
            try {
                ResourceBean<E> bean = getResourceBean();
                setBeanAttributes(request, bean);
                bean.renderRequest(request, response);
            } catch (ServletException e) {
                throw new HttpBeanException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Fehler in header oder footer.");
            }
        } catch (HttpBeanException e) {
            // we cannot call sendError() after the response has been committed
            request.setAttribute("ErrorStatus", e.getStatusCode());
            request.setAttribute("ErrorMessage", e.getDescription());
            request.getRequestDispatcher("includes/error.jsp").include(request, response);
        }
        request.getRequestDispatcher("includes/footer.jsp").include(request, response);
    }

    private boolean tryInvokeSetterOfBean(ResourceBean<E> bean, String value, String setterName) throws InvocationTargetException, IllegalAccessException {
        if (bean == null) throw new IllegalArgumentException("bean is null");
        if (value == null) throw new IllegalArgumentException("value is null");
        Class<?> classOrSuperclass = bean.getClass();
        // Search for method in this class or super-classes
        while (ResourceBean.class.isAssignableFrom(classOrSuperclass)) {
            Method[] methods = classOrSuperclass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    method.invoke(bean, value);
                    return true;
                }
            }
        }
        return false;
    }

    private void setBeanAttributes(HttpServletRequest request, ResourceBean<E> bean) throws ServletException {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            try {
                System.out.println("trying " + getSetterName(paramName));
                tryInvokeSetterOfBean(bean, request.getParameter(paramName), getSetterName(paramName));
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                throw new ServletException(e.getMessage());
            }
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            // ??
            throw new ServletException("Not a HTTP request or response");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        service(httpRequest, httpResponse);
    }
}
