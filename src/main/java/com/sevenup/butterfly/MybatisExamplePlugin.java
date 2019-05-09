package com.sevenup.butterfly;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
@Deprecated
public class MybatisExamplePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.getInnerClasses().stream()
                .flatMap(innerClass -> innerClass.getMethods().stream())
                .filter(method -> "addCriterion".equals(method.getName()))
                .forEach(method -> { method.setVisibility(JavaVisibility.PUBLIC); });
        return true;
    }
}
