package com.fjc.springmvc.servlet;

import com.fjc.springmvc.annotation.Controller;
import com.fjc.springmvc.annotation.Repository;
import com.fjc.springmvc.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name="dispatherServlet", urlPatterns="/*", loadOnStartup=1,
        initParams={@WebInitParam(name="base-pacage", value="com.fjc.springmvc")})
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
                packageNames.add(basePackage + file.getName().split("\\.")[0]);
            }
        }

    }

    private void instance(List<String> packageNames) throws ClassNotFoundException{
        if(packageNames.size() < 1){
            return;
        }
        for(String packageName : packageNames){
            Class clas = Class.forName(packageName);
            if(clas.isAnnotationPresent(Controller.class)){
                Controller controller = (Controller) clas.getAnnotation(Controller.class);
                String controllerName = controller.value();
                instanceMap.put(controllerName, controller);
                nameMap.put(packageName, controllerName);
            } else if(clas.isAnnotationPresent(Service.class)){

            } else if(clas.isAnnotationPresent(Repository.class)){

            }
        }
    }

    private void springIOC(){

    }

    private void hanlerUrlMethodMap(){

    }
}
