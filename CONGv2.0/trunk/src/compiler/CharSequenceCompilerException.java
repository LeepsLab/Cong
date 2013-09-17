/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package compiler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class CharSequenceCompilerException extends Exception {

    private static final long serialVersionUID = 1L;
    /**
     * The fully qualified name of the class that was being compiled.
     */
    private Set<String> classNames;
    // Unfortunately, Diagnostic and Collector are not Serializable, so we can't
    // serialize the collector.
    transient private DiagnosticCollector<JavaFileObject> diagnostics;

    public CharSequenceCompilerException(String message,
            Set<String> qualifiedClassNames, Throwable cause,
            DiagnosticCollector<JavaFileObject> diagnostics) {
        super(message, cause);
        setClassNames(qualifiedClassNames);
        setDiagnostics(diagnostics);
    }

    public CharSequenceCompilerException(String message,
            Set<String> qualifiedClassNames,
            DiagnosticCollector<JavaFileObject> diagnostics) {
        super(message);
        setClassNames(qualifiedClassNames);
        setDiagnostics(diagnostics);
    }

    public CharSequenceCompilerException(Set<String> qualifiedClassNames,
            Throwable cause, DiagnosticCollector<JavaFileObject> diagnostics) {
        super(cause);
        setClassNames(qualifiedClassNames);
        setDiagnostics(diagnostics);
    }

    private void setClassNames(Set<String> qualifiedClassNames) {
        // create a new HashSet because the set passed in may not
        // be Serializable. For example, Map.keySet() returns a non-Serializable
        // set.
        classNames = new HashSet<String>(qualifiedClassNames);
    }

    private void setDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
        this.diagnostics = diagnostics;
    }

    /**
     * Gets the diagnostics collected by this exception.
     * 
     * @return this exception's diagnostics
     */
    public DiagnosticCollector<JavaFileObject> getDiagnostics() {
        return diagnostics;
    }

    /**
     * @return The name of the classes whose compilation caused the compile
     *         exception
     */
    public Collection<String> getClassNames() {
        return Collections.unmodifiableSet(classNames);
    }
}
