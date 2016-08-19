package com.stormpath.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @since 1.1.0
 */
public class MessageSourceDefinitionPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public static final String I18N_PROPERTIES_BASENAME = "com.stormpath.sdk.servlet.i18n";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(ResourceBundleMessageSource.class)
            // Fix for https://github.com/stormpath/stormpath-sdk-java/issues/811
            // Force same behavior as servlet i18n - all single quotes need to have double quotes.
            // http://stackoverflow.com/a/19187306/65681
            .addPropertyValue("alwaysUseMessageFormat", true)
            .addPropertyValue("basename", I18N_PROPERTIES_BASENAME)
            .addPropertyValue("defaultEncoding", "UTF-8")
            .getBeanDefinition();

        String beanName = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME;

        if (registry.containsBeanDefinition(beanName)) {

            // An existing definition exists, but we need our i18n supported too.  So, we replace the discovered
            // definition with a composite bean definition that wraps both.  The existing one has precedence:
            BeanDefinition original = registry.getBeanDefinition(beanName);
            registry.removeBeanDefinition(beanName);

            ManagedArray array = new ManagedArray(MessageSource.class.getName(), 2);
            array.add(original);
            array.add(bd);

            BeanDefinition composite = BeanDefinitionBuilder.genericBeanDefinition(CompositeMessageSource.class)
                .addConstructorArgValue(array)
                .getBeanDefinition();

            //register the composite, replacing the previous definition:
            registry.registerBeanDefinition(beanName, composite);

        } else {
            //no existing one, so we can safely use ours as the app default:
            registry.registerBeanDefinition(beanName, bd);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
