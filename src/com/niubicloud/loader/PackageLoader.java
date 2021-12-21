package com.niubicloud.loader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.niubicloud.anno.RuntimeController;
import com.niubicloud.base.Controller;
import com.niubicloud.service.MainService;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageLoader {
	public String packageName;
	public MainService service;
	
	public PackageLoader(MainService service,String packageName) {
		this.service = service;
		this.packageName = packageName;
		init();
	}
	
	
	@SuppressWarnings("unchecked")
	private void init() {
		for(Class<?> clazz : getClasses(packageName)) {
			if(Controller.class.isAssignableFrom(clazz)) {
				RuntimeController rc = clazz.getAnnotation(RuntimeController.class);
				if(rc == null)
					continue;
				try {
					service.registerController(rc.name(), (Class<? extends Controller>) clazz);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
     * �Ӱ�package�л�ȡ���е�Class
     * @return
     */
    public static List<Class<?>> getClasses(String packageName){
        
        //��һ��class��ļ���
        List<Class<?>> classes = new ArrayList<Class<?>>();
        //�Ƿ�ѭ������
        boolean recursive = true;
        //��ȡ�������� �������滻
        String packageDirName = packageName.replace('.', '/');
        //����һ��ö�ٵļ��� ������ѭ�����������Ŀ¼�µ�things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //ѭ��������ȥ
            while (dirs.hasMoreElements()){
                //��ȡ��һ��Ԫ��
                URL url = dirs.nextElement();
                //�õ�Э�������
                String protocol = url.getProtocol();
                //��������ļ�����ʽ�����ڷ�������
                if ("file".equals(protocol)) {
                    //��ȡ��������·��
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //���ļ��ķ�ʽɨ���������µ��ļ� �����ӵ�������
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)){
                    //�����jar���ļ� 
                    //����һ��JarFile
                    JarFile jar;
                    try {
                        //��ȡjar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        //�Ӵ�jar�� �õ�һ��ö����
                        Enumeration<JarEntry> entries = jar.entries();
                        //ͬ���Ľ���ѭ������
                        while (entries.hasMoreElements()) {
                            //��ȡjar���һ��ʵ�� ������Ŀ¼ ��һЩjar����������ļ� ��META-INF���ļ�
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //�������/��ͷ��
                            if (name.charAt(0) == '/') {
                                //��ȡ������ַ���
                                name = name.substring(1);
                            }
                            //���ǰ�벿�ֺͶ���İ�����ͬ
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                //�����"/"��β ��һ����
                                if (idx != -1) {
                                    //��ȡ���� ��"/"�滻��"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //������Ե�����ȥ ������һ����
                                if ((idx != -1) || recursive){
                                    //�����һ��.class�ļ� ���Ҳ���Ŀ¼
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        //ȥ�������".class" ��ȡ����������
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            //���ӵ�classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                      }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        return classes;
    }
    
    /**
     * ���ļ�����ʽ����ȡ���µ�����Class
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes){
        //��ȡ�˰���Ŀ¼ ����һ��File
        File dir = new File(packagePath);
        //��������ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //������� �ͻ�ȡ���µ������ļ� ����Ŀ¼
        File[] dirfiles = dir.listFiles(new FileFilter() {
        //�Զ�����˹��� �������ѭ��(������Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
              public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
              }
            });
        //ѭ�������ļ�
        for (File file : dirfiles) {
            //�����Ŀ¼ �����ɨ��
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                                      file.getAbsolutePath(),
                                      recursive,
                                      classes);
            }
            else {
                //�����java���ļ� ȥ�������.class ֻ��������
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //���ӵ�������ȥ
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}