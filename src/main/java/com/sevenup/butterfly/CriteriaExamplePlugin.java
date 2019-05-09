package com.sevenup.butterfly;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.*;
import java.util.List;


public class CriteriaExamplePlugin extends PluginAdapter {


    public static final String CRITERIA_CLASS = "Criteria.java";
    DefaultShellCallback shellCallback;

    private String targetProject;
    private String targetPackage;
    private String criteriaFilePath;

    public CriteriaExamplePlugin() {
        shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        try {
            String path = shellCallback.getDirectory(targetProject, targetPackage).getAbsolutePath();
            criteriaFilePath = path + File.separator + CRITERIA_CLASS;
        } catch (ShellException e) {
            e.printStackTrace();
        }
        super.initialized(introspectedTable);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        ClassLoader currentClassLoader = this.getClass().getClassLoader();
//        System.out.println(currentClassLoader.getResource(CRITERIA_CLASS).getPath());
//        String currentPath = MybatisExamplePlugin.class.getResource("").getPath();
        String importPackage = targetPackage;

        File criteriaClassFile = new File(criteriaFilePath);
        System.out.println(criteriaClassFile.getPath());
        if (criteriaClassFile.exists() && !criteriaClassFile.isDirectory()) {
            //Do Nothing
        } else {
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("/Criteria.java");
                writeInputStreamToDestination(inputStream, criteriaFilePath, "package " + importPackage + ";\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        topLevelClass.addImportedType(new FullyQualifiedJavaType(targetPackage + ".Criteria"));
        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
        innerClasses.clear();

        //generate the final condition
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        allColumns.stream().map(column -> {
            Field field = new Field();
            field.setName(column.getJavaProperty());
            field.setType(FullyQualifiedJavaType.getStringInstance());
            field.setInitializationString("\"" + column.getActualColumnName() + "\"");
            field.setVisibility(JavaVisibility.PUBLIC);
            field.setStatic(true);
            field.setFinal(true);
            return field;
        }).forEach(field -> {
            topLevelClass.addField(field);
        });
        return true;
    }

    public static void writeInputStreamToDestination(InputStream inputStream, String destinationFilePath, String importPackage)
            throws IOException {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destinationFilePath));
        bufferedOutputStream.write(importPackage.getBytes(), 0, importPackage.getBytes().length);
        int length = -1;
        byte[] byteBuffer = new byte[1024];
        while ((length = bufferedInputStream.read(byteBuffer)) != -1) {
            bufferedOutputStream.write(byteBuffer, 0, length);
        }
        bufferedInputStream.close();
        bufferedOutputStream.close();
    }
}

