### Open rewrite Hello world recipe
This link is useful for creating recipes:
https://docs.openrewrite.org/authoring-recipes/recipe-development-environment


### Project setup

```bash
mvn -B archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

This is the file before running the rewrite recipe we create:
```java
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
```