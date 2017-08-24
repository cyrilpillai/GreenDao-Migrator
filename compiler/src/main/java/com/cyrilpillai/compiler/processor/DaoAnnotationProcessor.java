package com.cyrilpillai.compiler.processor;

import com.cyrilpillai.compiler.util.BaseUtil;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import org.greenrobot.greendao.annotation.Entity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class DaoAnnotationProcessor extends AbstractProcessor {

    private Filer filer;

    /**
     * Package where the DaoHelper file should be generated
     */
    private static final String packageName = "com.cyrilpillai.greendao_migrator.dao";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Entity.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        String daoListQualifiedName = "java.util.List<Class<? extends " +
                "org.greenrobot.greendao.AbstractDao<?, ?>>>";
        StringBuilder daoCode = new StringBuilder();
        daoCode.append("\n")
                .append(daoListQualifiedName)
                .append(" daoClasses = new java.util.ArrayList();\n\n");
        for (Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                String className = packageName + "." + element.getSimpleName()
                        + "Dao.class";
                daoCode.append("daoClasses.add(")
                        .append(className)
                        .append(");\n");
            }
        }
        daoCode.append("\nreturn daoClasses;\n");

        TypeVariableName typeVariableName = TypeVariableName.get(daoListQualifiedName, List.class);
        MethodSpec createSpec = MethodSpec.methodBuilder("getAllDaos")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(typeVariableName)
                .addJavadoc("Returns a List of all the DAOs classes available")
                .addCode(daoCode.toString())
                .build();


        TypeSpec daoSpec = TypeSpec.classBuilder("DaoHelper")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(BaseUtil.getGenerationDetails(DaoAnnotationProcessor.class))
                .addMethod(createSpec)
                .build();


        JavaFile javaFile = JavaFile.builder(packageName, daoSpec)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}