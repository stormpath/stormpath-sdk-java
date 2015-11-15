## Tutorials

Each part of the tutorial code can be run completely standalone - inside the sdk here or in a folder that you create.

You can use the tutorials as a starting point for your own projects.

For instance, if you wanted to build a Spring Boot WebMVC project, including Spring Security integrated with Stormpath, you
could do the following:

```
mkdir MyProject
cd MyProject
cp -r <path to Stormpath sdk>/tutorials/spring-boot/03-spring-security-refined/* .
mvn clean package
java -jar target/*.jar
```

For more information, refer to the links found on the [homepage](https://github.com/stormpath/stormpath-sdk-java) of the Java SDK Project.