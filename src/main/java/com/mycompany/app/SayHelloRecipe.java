package com.mycompany.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;

// Making your recipe immutable helps make them idempotent and eliminates a variety of possible bugs.
// Configuring your recipe in this way also guarantees that basic validation of parameters will be done for you by rewrite.
// Also note: All recipes must be serializable. This is verified by RewriteTest.rewriteRun() in your tests.
@Value
@EqualsAndHashCode(callSuper = false)
public class SayHelloRecipe extends Recipe {
    @Option(displayName = "Fully Qualified Class Name",
            description = "A fully qualified class name indicating which class to add a hello() method to.",
            example = "com.mycompany.app2.App")
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

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        // getVisitor() should always return a new instance of the visitor to avoid any state leaking between cycles
        return new SayHelloVisitor();
    }

    public class SayHelloVisitor extends JavaIsoVisitor<ExecutionContext> {
        private final JavaTemplate helloTemplate =
                JavaTemplate.builder(this::getCursor, "public String hello() { return \"Hello there from #{}!!!!!\"; }")
                        .build();

        @Override
        public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
            if (classDecl.getType() == null || !classDecl.getType().getFullyQualifiedName().equals(fullyQualifiedClassName)) {
                return classDecl;
            }

            boolean helloMethodExists = classDecl.getBody().getStatements().stream()
                    .filter(statement -> statement instanceof J.MethodDeclaration)
                    .map(J.MethodDeclaration.class::cast)
                    .anyMatch(methodDeclaration -> methodDeclaration.getName().getSimpleName().equals("hello"));

            if (helloMethodExists) {
                return classDecl;
            }

            classDecl = classDecl.withBody(
                    classDecl.getBody().withTemplate(
                            helloTemplate,
                            classDecl.getBody().getCoordinates().lastStatement(),
                            fullyQualifiedClassName
                    )
            );

            return classDecl;
        }
    }
}