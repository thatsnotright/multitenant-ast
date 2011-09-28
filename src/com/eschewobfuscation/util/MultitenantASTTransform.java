package com.eschewobfuscation.util;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

/**
 * A Groovy AST Transform that adds the @Multitenant annotation to all classes in the com.eschewobfuscation
 * package.  This is done because the normal use case of these classes is not multitenant mode, however
 * for the ETVSM test harness, we need external entities to configure all of their own data without
 * interfering with other vendors.
 * <p/>
 * User: RobertElsner
 * Date: 7/15/11
 * Time: 12:57 PM
 */
@GroovyASTTransformation(phase = CompilePhase.CONVERSION)
public class MultitenantASTTransform implements ASTTransformation {
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        for (ASTNode topnode : astNodes) {
            if (topnode instanceof ModuleNode) {
                ModuleNode mod = (ModuleNode) topnode;
                for (ASTNode node : mod.getClasses()) {
                    if (node instanceof ClassNode) {
                        String nodeName = ((ClassNode) node).getName();
                        if (nodeName.startsWith("com.eschewobfuscation") &&
                                (sourceUnit.getName().contains("grails-app\\domain") ||
                                        sourceUnit.getName().contains("grails-app/domain")) &&
                                !(nodeName.contains("User") || nodeName.contains("Role"))) {
                            System.out.println("Making class " + ((ClassNode) node).getName() + " multitenant aware!");
                            ((ClassNode) node).addAnnotation(createAnnotationNode());
                        }
                    }
                }
            }
        }
    }

    private AnnotationNode createAnnotationNode() {
        AnnotationNode an = new AnnotationNode(ClassHelper.make("grails.plugin.multitenant.core.groovy.compiler.MultiTenant"));
        an.setRuntimeRetention(true);
        return an;
    }
}
