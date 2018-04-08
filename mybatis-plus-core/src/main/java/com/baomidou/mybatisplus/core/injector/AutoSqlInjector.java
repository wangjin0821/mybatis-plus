/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.injector;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.injector.methods.AbstractMethod;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;


/**
 * <p>
 * SQL 自动注入器
 * </p>
 *
 * @author hubin
 * @since 2018-04-07
 */
public class AutoSqlInjector implements ISqlInjector {

    private List<AbstractMethod> methodList;


    /**
     * <p>
     * CRUD 注入后给予标识 注入过后不再注入
     * </p>
     *
     * @param builderAssistant
     * @param mapperClass
     */
    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
            inject(builderAssistant, mapperClass);
            mapperRegistryCache.add(className);
        }
    }


    /**
     * <p>
     * CRUD 注入后给予标识 注入过后不再注入
     * </p>
     *
     * @param builderAssistant
     * @param mapperClass
     */
    @Override
    public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
            List<AbstractMethod> methodList = this.getMethodList();
            if (CollectionUtils.isEmpty(methodList)) {
                throw new MybatisPlusException("No effective injection method was found.");
            }
            // 循环注入自定义方法
            for (AbstractMethod method : methodList) {
                method.inject(builderAssistant, mapperClass);
            }
            mapperRegistryCache.add(className);
        }
    }


    @Override
    public void injectSqlRunner(Configuration configuration) {
        // TODO 这里考虑控制是否初始化 SqlRuner
        new SqlRunnerInjector().inject(configuration);
    }


    public List<AbstractMethod> getMethodList() {
        return methodList;
    }


    public void setMethodList(List<AbstractMethod> methodList) {
        this.methodList = methodList;
    }


}