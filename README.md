### Open rewrite Hello world recipe
This link is useful for creating recipes:
https://docs.openrewrite.org/authoring-recipes/recipe-development-environment

Our recipe will add a hello function to our class. This is the file before running the rewrite recipe we create:
```java
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
```

This will be the class in our user application when we run the recipe:
```java
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public String hello() {
        return "Hello there from com.mycompany.app2.App!!!!!";
    }
}
```

### Recipe development environment
This link is useful for setting up the recipe development environment:
https://docs.openrewrite.org/authoring-recipes/recipe-development-environment

This command sets up the project for us:
```bash
mvn -B archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

We can then edit the dependencies with:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.testSource>17</maven.compiler.testSource>
    <maven.compiler.testTarget>17</maven.compiler.testTarget>
</properties>

<dependencyManagement>
  <dependencies>
      <dependency>
          <groupId>org.openrewrite.recipe</groupId>
          <artifactId>rewrite-recipe-bom</artifactId>
          <version>1.18.0</version>
          <type>pom</type>
          <scope>import</scope>
      </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
    <!-- rewrite-java depedencies only necessary for Java Recipe development -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-java</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- You only need the version that corresponds to your current
    Java version. It is fine to add all of them, though, as
    they can coexist on a classpath. -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-java-8</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-java-11</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-java-17</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- rewrite-maven dependency only necessary for Maven Recipe development -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-maven</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- rewrite-yaml dependency only necessary for Yaml Recipe development -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-yaml</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- rewrite-properties dependency only necessary for Properties Recipe development -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-properties</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- rewrite-xml dependency only necessary for XML Recipe development -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-xml</artifactId>
        <scope>compile</scope>
    </dependency>

    <!-- lombok is optional, but recommended for authoring recipes -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

    <!-- For authoring tests for any kind of Recipe -->
    <dependency>
        <groupId>org.openrewrite</groupId>
        <artifactId>rewrite-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M9</version>
        </plugin>
    </plugins>
</build>
```
We can also change the language level:
```xml
  <!-- see https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-source-and-target.html -->
  <properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>
```

We can deploy this recipe locally with:
```bash
mvn deploy
```
This is because we have this in our pom.xml:
```xml
  <distributionManagement>
    <repository>
      <id>dev</id>
      <name>Local repository</name>
      <url>file://${user.home}/.m2/repository</url>
    </repository>

  </distributionManagement>
```
We don't need the above we could just add the Recipe to our local repository with:
```bash
mvn install
```

In our the application which will use the recipe we then add our recipe:

```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.openrewrite.maven</groupId>
                <artifactId>rewrite-maven-plugin</artifactId>
                <version>4.44.0</version>
                <configuration>
                    <activeRecipes>
                        <recipe> [your recipe name] </recipe>
                    </activeRecipes>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId> [your recipe module's groupId] </groupId>
                        <artifactId> [your recipe module's artifactId] </artifactId>
                        <version> [your recipe module's version] </version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
</project>
```

For our app we used:
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.openrewrite.maven</groupId>
                <artifactId>rewrite-maven-plugin</artifactId>
                <version>4.44.0</version>
                <configuration>
                    <activeRecipes>
                        <recipe>com.mycompany.sayHelloToFooBar</recipe>
                    </activeRecipes>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>com.mycompany.app</groupId>
                        <artifactId>my-app</artifactId>
                        <version>1.5-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```

and a yaml file to specify the name of the class to which we wanted to add our hello() function:
```yml
---
type: specs.openrewrite.org/v1beta/recipe
name: com.mycompany.sayHelloToFooBar
recipeList:
  - com.mycompany.app.SayHelloRecipe:
      fullyQualifiedClassName: com.mycompany.app2.App
```

### Creating the SayHelloRecipe

For our recipe class we extend org.openrewrite.Recipe. For our recipe we need:
- An Option that is the fully qualified name of the class we want to add the hello() method to.
- A serializable constructor that takes in the fully qualified class name.
- A getDisplayName() method that returns the display name for this recipe
- A getDescription() method to return the description for the recipe

This is what our class looks like to start with:

```java
package com.yourorg;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.NonNull;

// Making your recipe immutable helps make them idempotent and eliminates a variety of possible bugs.
// Configuring your recipe in this way also guarantees that basic validation of parameters will be done for you by rewrite.
// Also note: All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
@Value
@EqualsAndHashCode(callSuper = false)
public class SayHelloRecipe extends Recipe {
    @Option(displayName = "Fully Qualified Class Name",
            description = "A fully qualified class name indicating which class to add a hello() method to.",
            example = "com.yourorg.FooBar")
    @NonNull
    String fullyQualifiedClassName;

    // All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
    @JsonCreator
    public SayHelloRecipe(@NonNull @JsonProperty("fullyQualifiedClassName") String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    @Override
    public String getDisplayName() {
        return "Say Hello";
    }

    @Override
    public String getDescription() {
        return "Adds a \"hello\" method to the specified class.";
    }

    // TODO: Override getVisitor() to return a JavaIsoVisitor to perform the refactoring
}
```

Next we write some tests to show what we expect from our Recipe class:
```java
package com.yourorg;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class SayHelloRecipeTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SayHelloRecipe("com.yourorg.FooBar"));
    }

    @Test
    void addsHelloToFooBar() {
        rewriteRun(
            java(
                """
                    package com.yourorg;

                    class FooBar {
                    }
                """,
                """
                    package com.yourorg;

                    class FooBar {
                        public String hello() {
                            return "Hello from com.yourorg.FooBar!";
                        }
                    }
                """
            )
        );
    }

    @Test
    void doesNotChangeExistingHello() {
        rewriteRun(
            java(
                """
                    package com.yourorg;
        
                    class FooBar {
                        public String hello() { return ""; }
                    }
                """
            )
        );
    }

    @Test
    void doesNotChangeOtherClasses() {
        rewriteRun(
            java(
                """
                    package com.yourorg;
        
                    class Bash {
                    }
                """
            )
        );
    }
}
```

This test file implements RewriteTest which provides the function rewriteRun() for us to test the Recipe.
We can also set defaults for the tests via defaults(). Each test will define the "before" state.

### Implementing the visitor

We now override Recipe.getVisitor() and implement our SayHelloVisitor class:

```java
package com.yourorg;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;

// Making your recipe immutable helps make them idempotent and eliminates categories of possible bugs.
// Configuring your recipe in this way also guarantees that basic validation of parameters will be done for you by rewrite.
// Also note: All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
@Value
@EqualsAndHashCode(callSuper = false)
public class SayHelloRecipe extends Recipe {
    @Option(displayName = "Fully Qualified Class Name",
            description = "A fully qualified class name indicating which class to add a hello() method to.",
            example = "com.yourorg.FooBar")
    @NonNull
    String fullyQualifiedClassName;

    @Override
    public String getDisplayName() {
        return "Say Hello";
    }

    @Override
    public String getDescription() {
        return "Adds a \"hello\" method to the specified class.";
    }

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        // getVisitor() should always return a new instance of the visitor to avoid any state leaking between cycles
        return new SayHelloVisitor();
    }

    public class SayHelloVisitor extends JavaIsoVisitor<ExecutionContext> {
        private final JavaTemplate helloTemplate =
                JavaTemplate.builder(this::getCursor, "public String hello() { return \"Hello from #{}!\"; }")
                        .build();

        @Override
        public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
            // Don't make changes to classes that don't match the fully qualified name
            if (classDecl.getType() == null || !classDecl.getType().getFullyQualifiedName().equals(fullyQualifiedClassName)) {
                return classDecl;
            }

            // Check if the class already has a method named "hello".
            boolean helloMethodExists = classDecl.getBody().getStatements().stream()
                    .filter(statement -> statement instanceof J.MethodDeclaration)
                    .map(J.MethodDeclaration.class::cast)
                    .anyMatch(methodDeclaration -> methodDeclaration.getName().getSimpleName().equals("hello"));

            // If the class already has a `hello()` method, don't make any changes to it.
            if (helloMethodExists) {
                return classDecl;
            }

            // Interpolate the fullyQualifiedClassName into the template and use the resulting LST to update the class body
            classDecl = classDecl.withBody(
                    classDecl.getBody().withTemplate(
                            helloTemplate,
                            classDecl.getBody().getCoordinates().lastStatement(),
                            fullyQualifiedClassName
                    ));

            return classDecl;
        }
    }
}
```
This only changes the class if the helloMethod does not exist and if the class matches the name given by our yaml file:

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: com.mycompany.sayHelloToFooBar
recipeList:
  - com.mycompany.app.SayHelloRecipe:
      fullyQualifiedClassName: com.mycompany.app2.App
```
The class we are looking for in this case is com.mycompany.app2.App.

