.. _quickstart-spring:

Traditional Spring Application
------------------------------

If you have a traditional non-web Spring application, you don't have to do much - just use one annotation and ensure a ``PropertySourcesPlaceholderConfigurer`` is configured.  For example, using Java Config:

  .. code-block:: java

      @Configuration
      @EnableStormpath //enables the @Autowired beans below
      @PropertySource("classpath:application.properties")
      public class AppConfig {

          @Autowired
          private Application stormpathApplication; //the REST resource in Stormpath that represents this app

          @Autowired
          private Client client; //can be used to interact with all things in your Stormpath tenant

          @Bean
          public String startupMessage() {
              return "Welcome to the '" + stormpathApplication.getName() + "' application!";
          }

          /**
           * This bean and the @PropertySource annotation above allow you to configure Stormpath beans with properties
           * prefixed with {@code stormpath.}, i.e. {@code stormpath.application.href}, {@code stormpath.apiKey.file}, etc.
           *
           * <p>Combine this with Spring's Profile support to override property values based on specific runtime environments,
           * e.g. development, production, etc.</p>
           *
           * @return the application's PropertySourcesPlaceholderConfigurer that enables property placeholder substitution.
           */
          @Bean
          public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
              return new PropertySourcesPlaceholderConfigurer();
          }
      }

