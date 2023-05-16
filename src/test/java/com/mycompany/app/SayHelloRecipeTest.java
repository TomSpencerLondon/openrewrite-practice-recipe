package com.mycompany.app;

import com.mycompany.app.SayHelloRecipe;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class SayHelloRecipeTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe( new SayHelloRecipe("com.mycompany.app.App") );
    }

    @Test
    void addsHelloToFooBar() {
        rewriteRun(
                java(
                        """
                                    package com.mycompany.app;
                                        
                                    class App {
                                    }
                                """,
                        """
                                    package com.mycompany.app;
                                        
                                    class App {
                                        public String hello() {
                                            return "Hello there from com.mycompany.app.App!!!!!";
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