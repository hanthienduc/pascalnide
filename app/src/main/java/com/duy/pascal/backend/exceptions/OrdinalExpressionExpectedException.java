package com.duy.pascal.backend.exceptions;

import com.duy.pascal.backend.linenumber.LineInfo;
import com.js.interpreter.runtime.exception.RuntimePascalException;

/**
 * Created by Duy on 06-Apr-17.
 */

public class OrdinalExpressionExpectedException extends RuntimePascalException {
    String msg = "Error: ordinal expression expected";

    public OrdinalExpressionExpectedException(LineInfo line) {
        super(line);
    }

    public OrdinalExpressionExpectedException() {
        super(null);
    }

    public OrdinalExpressionExpectedException(LineInfo line, String mes) {
        super(line, mes);
    }
}