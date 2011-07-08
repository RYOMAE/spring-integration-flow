/*
 * Copyright 2002-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.integration.flow.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.util.ResourceUtils;

/**
 * @author David Turanski
 * 
 */
public class FlowUtils {
    /**
     * Create a bridge
     * 
     * @param inputChannel
     * @param outputChannel
     */

    public static void bridgeChannels(SubscribableChannel inputChannel, MessageChannel outputChannel) {
        BridgeHandler bridgeHandler = new BridgeHandler();
        bridgeHandler.setOutputChannel(outputChannel);
        inputChannel.subscribe(bridgeHandler);
    }

    /**
     * Register a bean with "flow" prefix
     * 
     * @param beanDefinition
     * @param registry
     * @return
     */
    public static String registerBeanDefinition(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
        beanName = "flow." + beanName;
        String strIndex = StringUtils.substringAfter(beanName, "#");
        int index = Integer.valueOf(strIndex);
        while (registry.isBeanNameInUse(beanName)) {
            index++;
            beanName = beanName.replaceAll("#\\d$", "#" + (index));
        }
        registry.registerBeanDefinition(beanName, beanDefinition);
        return beanName;
    }

    public static String getDocumentation(String flowName) {

        String path = String.format("classpath:META-INF/spring/integration/flows/%s/flow.doc", flowName);

        try {
            File file = ResourceUtils.getFile(path);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            br.close();
            
            return result.toString();

        } catch (FileNotFoundException e) {
            return "no help available";
        } catch (IOException e) {
            e.printStackTrace();
            return "no help available";
        }
    }

}