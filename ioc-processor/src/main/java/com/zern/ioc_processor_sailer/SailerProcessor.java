package com.zern.ioc_processor_sailer;

import com.google.auto.service.AutoService;
import com.zern.ioc_annotation.SailerRegisterAnnotation;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
//@SupportedAnnotationTypes("com.zern.ioc_annotation.SailerRegisterAnnotation")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SailerProcessor extends AbstractProcessor {
    private static final String proxyPackName = "com.sailer";
    private static final String proxyClassName = "SailerActionRegister$$Zern";
    private static final String proxyClassMethod = "initSailerActionsMap";
    private static final String ActionManagerPackName = "com.jkys.sailerxwalkview.action";
    private static final String ActionManagerClassName = "ActionManager";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(SailerRegisterAnnotation.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    private Elements mElementUtils; //基于元素进行操作的工具方法
    private Filer mFileCreator;     //代码创建者
    private Messager mMessager;     //日志，提示者，提示错误、警告
//    private Map<String, SailerProxyInfo> mProxyMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process...");
        // 避免生成重复的代理类
//        mProxyMap.clear();
        //拿到被 @SailerRegisterAnnotation 注解修饰的元素，应该是
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SailerRegisterAnnotation.class);
        if (elements.size() > 0) {
            // 1. 拼接Java代码
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("// Generate code. Do not modify it 我是Zern编译自动生成装载代码!\n")
                    .append("package ").append(proxyPackName).append(";\n\n")
//                .append("import com.zern.ioc.*;\n\n")
                    .append("public class ").append(proxyClassName).append("{\n")
                    .append("\tpublic void ").append(proxyClassMethod).append("() {\n");

            //2. 收集信息 把要处理的 也就是被标记的类全部罗列出来 拼接到代码中
            Iterator<? extends Element> iterator = elements.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                if (!checkAnnotationValid(element, SailerRegisterAnnotation.class)) {
                    continue;
                }
                // 拿到被SailerRegisterAnnotation注解标记的类 或者接口
                TypeElement typeElement = (TypeElement) element;
                //完整的名称
                String qualifiedName = typeElement.getQualifiedName().toString();
                stringBuilder.append("\t\t")
                        .append(ActionManagerPackName)
                        .append(".")
                        .append(ActionManagerClassName)
                        .append(".")
                        .append("getActions().add(")
                        .append("new ").append(qualifiedName).append("()")
                        .append(");");
                if (iterator.hasNext()) stringBuilder.append("\n");
            }
            stringBuilder.append("\n\t}");
            stringBuilder.append("\n}");
            // 3. 生成Java代码
            generateJavaFile(stringBuilder.toString());
        }
        return true;
    }

    // 生成Java文件
    public void generateJavaFile(String code) {
        // 生成Java 文件
        try {
            JavaFileObject sourceFile = mFileCreator.createSourceFile(
                    proxyPackName + "." + proxyClassName,
                    null);
            Writer writer = sourceFile.openWriter();
            writer.write(code);
            writer.flush();
            writer.close();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "生成文件成功 ^&^...");
        } catch (IOException e) {
            e.printStackTrace();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "生成文件失败 Fail Fail Fail!!!");
        }
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFileCreator = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }


    /**
     * 检查 element 类型是否规范
     *
     * @param element
     * @param clazz
     * @return
     */
    private boolean checkAnnotationValid(final Element element, final Class<?> clazz) {
        if (element == null || element.getKind() != ElementKind.CLASS || element.getSimpleName().toString().equals("SailerActionHandler")) {
            error(element, "%s must be declared on field !", clazz.getSimpleName());
            return false;
        }
        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            error(element, "%s must be public !", element.getSimpleName());
            return false;
        }
        return true;
    }


    private void error(final Element element, String msg, final Object... args) {
        if (args != null && args.length > 0) {
            msg = String.format(msg, args);
            mMessager.printMessage(Diagnostic.Kind.ERROR, msg, element);
        }
    }

}
