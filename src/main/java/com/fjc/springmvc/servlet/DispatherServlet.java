package com.fjc.springmvc.servlet;

import com.fjc.springmvc.annotation.*;
import com.fjc.springmvc.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name="dispatherServlet", urlPatterns="/*", loadOnStartup=1,
        initParams={@WebInitParam(name="base-package", value="com.fjc.springmvc")})
public class DispatherServlet extends HttpServlet {

    //扫描基本包
    private String basePackage = "";

    //基本包下所有的带包路径权限的类名
    private List<String> packageNames = new ArrayList<String>();

    //注解实例化  注解上的名称：实例化对象
    private Map<String, Object> instanceMap = new HashMap<String, Object>();

    //注解上的名称  类路径  注解名
    private Map<String, String> nameMap = new HashMap<String, String>();

    //url地址和方法的映射关系
    private Map<String, Method> urlMethodMap = new HashMap<String, Method>();

    //方法和类名映射关系
    private Map<Method, String> methodPackageMap = new HashMap<Method, String>();

    @Override
    public void init(ServletConfig config) throws ServletException{

        basePackage = config.getInitParameter("base-package");

        try{
            scanBasePackage(basePackage);
            instance(packageNames);
            springIOC();
            hanlerUrlMethodMap();

        }catch(ClassNotFoundException e){

        }catch (IllegalAccessException e){

        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }

    private void scanBasePackage(String basePackage){
        //获取基本包 并将完整包名放入list    路径+类名
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File basePackageFile = new File(url.getPath());
        File[] childFiles = basePackageFile.listFiles();
        for(File file : childFiles){
            if(file.isDirectory()){
                scanBasePackage(basePackage + "." + file.getName());
            }else if(file.isFile()){
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
                System.out.println("扫码包：" + basePackage + "." + file.getName().split("\\.")[0]);
            }
        }

    }

    private void instance(List<String> packageNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(packageNames.size() < 1){
            return;
        }
        for(String packageName : packageNames){
            Class clas = Class.forName(packageName);
            if(clas.isAnnotationPresent(Controller.class)){
                Controller controller = (Controller) clas.getAnnotation(Controller.class);
                String controllerName = controller.value();
                instanceMap.put(controllerName, clas.newInstance());
                nameMap.put(packageName, controllerName);
                System.out.println("controllerName：" + controllerName);
            } else if(clas.isAnnotationPresent(Service.class)){
                Service service = (Service) clas.getAnnotation(Service.class);
                String serviceName = service.value();
                instanceMap.put(serviceName, clas.newInstance());
                nameMap.put(packageName, serviceName);
                System.out.println("serviceName：" + serviceName);
            } else if(clas.isAnnotationPresent(Repository.class)){
                Repository repository = (Repository) clas.getAnnotation(Repository.class);
                String repositoryName = repository.value();
                instanceMap.put(repositoryName, clas.newInstance());
                nameMap.put(packageName, repositoryName);
                System.out.println("repositoryName：" + repositoryName);
            }
        }
    }

    /**
     * TO KNOW 将每个类内的注入类  从容器中取出   放入  （所有的注入类都是引用，实际放在容器中）
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private void springIOC() throws IllegalAccessException, ClassNotFoundException {
        for(Map.Entry<String, Object> entry : instanceMap.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(field.isAnnotationPresent(Qualifier.class)){
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(), instanceMap.get(name));
                    System.out.println("IOC：" + name);
                }
            }


        }
    }

    private void hanlerUrlMethodMap() throws ClassNotFoundException {
        if(packageNames.size() < 1){
            return;
        }
        for(String packageName : packageNames){
            Class clas = Class.forName(packageName);
            if(clas.isAnnotationPresent(Controller.class)){

                Method[] methods = clas.getMethods();
                StringBuffer baseUrl = new StringBuffer();
                if(clas.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping = (RequestMapping) clas.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }

                for(Method method : methods){
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());

                        urlMethodMap.put(baseUrl.toString(), method);
                        methodPackageMap.put(method, packageName);

                        System.out.println("hanler url：" + baseUrl.toString());
                    }
                }
            }

        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("get .....");
        doPost(request, response);
    }

    @Override
    public  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("post .....");
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();//基本
        String path = uri.replace(contextPath, "");

        Method method = urlMethodMap.get(path);
        if(method != null){
            String packageName = methodPackageMap.get(method);
            String controllerName = nameMap.get(packageName);

            //拿到Web层类
            UserController clas = (UserController)instanceMap.get(controllerName);

            try{
                method.setAccessible(true);
                method.invoke(clas);
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }catch(InvocationTargetException e){
                e.printStackTrace();
            }
        }
    }
}
