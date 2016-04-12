package ch.avocado.share.servlet;

import ch.avocado.share.controller.ResourceBean;
import ch.avocado.share.model.data.AccessControlObjectBase;

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


public abstract class ResourceServlet<E extends AccessControlObjectBase> extends GenericServlet{

    ResourceBean<E> resourceBean = null;

    protected abstract Class<? extends ResourceBean<E>> getBeanClass();

    private ResourceBean<E> getResourceBean() {
        if(resourceBean == null) {
            try {
                resourceBean = getBeanClass().newInstance();
            } catch (InstantiationException | IllegalAccessException ignored) {
            }
        }
        return resourceBean;
    }

    private String getSetterName(String parameter) {
        return "set" + parameter.substring(0,1).toUpperCase() + parameter.substring(1);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("includes/header.jsp").include(request, response);
        ResourceBean<E> bean = getResourceBean();
        setBeanAttributes(request, bean);
        bean.renderRequest(request, response);
        request.getRequestDispatcher("includes/footer.jsp").include(request, response);
    }

    private boolean tryCallSetterOfBean(ResourceBean<E> bean, String value, String setterName) throws InvocationTargetException, IllegalAccessException {
        if(bean == null) throw new IllegalArgumentException("bean is null");
        if(value == null) throw new IllegalArgumentException("value is null");
        Class<?> classOrSuperclass = bean.getClass();
        Method setter = null;
        while(ResourceBean.class.isAssignableFrom(classOrSuperclass)) {
            Method[] methods = classOrSuperclass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    System.out.println("found " + setterName);
                    method.invoke(bean, value);
                    System.out.println("invoked");
                    return true;
                }
            }
        }
        return false;
    }

    private void setBeanAttributes(HttpServletRequest request, ResourceBean<E> bean) throws ServletException {
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            try {
                System.out.println("trying " + getSetterName(paramName));
                tryCallSetterOfBean(bean, request.getParameter(paramName), getSetterName(paramName));
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                throw new ServletException(e.getMessage());
            }
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if(!(req instanceof HttpServletRequest) || !(res instanceof  HttpServletResponse)) {
            // ??
            throw new ServletException("Not a HTTP request or response");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        service(httpRequest, httpResponse);
    }
}
