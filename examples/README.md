## Examples

Each of these examples can be run completely standalone - inside the sdk here or in a folder that you create.

You can use the examples as a starting point for your own projects.

For instance, if you wanted to build a Spring Boot WebMVC project, including Spring Security integrated with Stormpath, you
could do the following:

```
mkdir MyProject
cd MyProject
cp -r <path to Stormpath sdk>/examples/spring-boot-default/* .
mvn clean package
java -jar target/*.jar
```

For more information, refer to the links found on the [homepage](https://github.com/stormpath/stormpath-sdk-java) of the Java SDK Project.


Examples:

| Name | Description | Deploy |
| ---- | ----------- | ------ |
| [quickstart](./quickstart) | Standalone CLI Example| N/A |
| [servlet](./servlet) | Basic Servlet Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-war-runner&env\[GROUP_ID\]=com.stormpath.sdk&env\[ARTIFACT_ID\]=stormpath-sdk-examples-servlet) |
| [spring](./spring) | Standalone Spring CLI Example | N/A |
| [spring-boot](./spring-boot) | Standalone Spring Boot CLI Example | N/A |
| [spring-boot-default](./spring-boot-default) | Basic Spring Boot Webapp Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-spring-boot-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-boot-default) |
| [spring-boot-webmvc](./spring-boot-webmvc) | Spring Boot Web MVC Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-spring-boot-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-boot-web) |
| [spring-boot-webmvc-angular](./spring-boot-webmvc-angular) | Spring Boot Web MVC AngularJS Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-spring-boot-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-boot-web-angular) |
| [spring-security-spring-boot-webmvc](./spring-security-spring-boot-webmvc) | Spring Security + Spring Boot Web MVC Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-spring-boot-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-security-spring-boot-webmvc) |
| [spring-security-spring-boot-webmvc-bare-bones](./spring-security-spring-boot-webmvc-bare-bones) | Basic Spring Security Web MVC Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-spring-boot-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-security-spring-boot-webmvc-bare-bones) |
| [spring-security-webmvc](./spring-security-webmvc) | Spring Security Web MVC Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-war-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-security-webmvc) |
| [spring-webmvc](./spring-webmvc) | Spring Web MVC Example | [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-war-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-webmvc) |
| [zuul-spring-cloud-starter](./zuul-spring-cloud-starter) | Zuul Spring Cloud Example | N/A |






