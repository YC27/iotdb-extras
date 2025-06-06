/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.iotdb.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

public class SerializablePlugin extends PluginAdapter {

  private FullyQualifiedJavaType serializable;
  private FullyQualifiedJavaType gwtSerializable;
  private boolean addGWTInterface;
  private boolean suppressJavaInterface;

  public SerializablePlugin() {
    super();
    serializable = new FullyQualifiedJavaType("java.io.Serializable");
    gwtSerializable = new FullyQualifiedJavaType("com.google.gwt.user.client.rpc.IsSerializable");
  }

  @Override
  public boolean validate(List<String> warnings) {
    // this plugin is always valid
    return true;
  }

  @Override
  public void setProperties(Properties properties) {
    super.setProperties(properties);
    addGWTInterface = Boolean.valueOf(properties.getProperty("addGWTInterface"));
    suppressJavaInterface = Boolean.valueOf(properties.getProperty("suppressJavaInterface"));
  }

  @Override
  public boolean modelBaseRecordClassGenerated(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    makeSerializable(topLevelClass, introspectedTable);
    return true;
  }

  @Override
  public boolean modelPrimaryKeyClassGenerated(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    makeSerializable(topLevelClass, introspectedTable);
    return true;
  }

  @Override
  public boolean modelRecordWithBLOBsClassGenerated(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    makeSerializable(topLevelClass, introspectedTable);
    return true;
  }

  @Override
  public boolean modelExampleClassGenerated(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

    makeSerializable(topLevelClass, introspectedTable);

    for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
      if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
        addSerialVersionUIDField(innerClass);
      } else if ("Criteria".equals(innerClass.getType().getShortName())) {
        addSerialVersionUIDField(innerClass);
      } else if ("Criterion".equals(innerClass.getType().getShortName())) {
        addSerialVersionUIDField(innerClass);
      }
    }

    return true;
  }

  private void addSerialVersionUIDField(InnerClass innerClass) {
    innerClass.addSuperInterface(serializable);
    Field field = getSerialVersionUIDField();
    innerClass.addField(field);
  }

  private Field getSerialVersionUIDField() {
    final FullyQualifiedJavaType qualifiedJavaType = new FullyQualifiedJavaType("long");
    Field field = new Field("serialVersionUID", qualifiedJavaType);
    field.setFinal(true);
    field.setInitializationString("1L");
    field.setName("serialVersionUID");
    field.setStatic(true);
    field.setType(qualifiedJavaType);
    field.setVisibility(JavaVisibility.PRIVATE);
    return field;
  }

  protected void makeSerializable(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    if (addGWTInterface) {
      topLevelClass.addImportedType(gwtSerializable);
      topLevelClass.addSuperInterface(gwtSerializable);
    }

    List<Field> fields = topLevelClass.getFields();
    if (null != fields && fields.size() > 0) {
      for (Field field : fields) {
        if ("serialVersionUID".equals(field.getName())) {
          return;
        }
      }
    }

    if (!suppressJavaInterface) {
      topLevelClass.addImportedType(serializable);
      topLevelClass.addSuperInterface(serializable);

      Field field = getSerialVersionUIDField();
      context.getCommentGenerator().addFieldComment(field, introspectedTable);

      topLevelClass.addField(field);
    }
  }
}
