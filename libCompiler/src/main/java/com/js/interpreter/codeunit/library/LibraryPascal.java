/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.js.interpreter.codeunit.library;

import android.support.annotation.Nullable;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.exceptions.syntax.ExpectedTokenException;
import com.duy.pascal.backend.exceptions.syntax.MisplacedDeclarationException;
import com.duy.pascal.backend.function_declaretion.AbstractFunction;
import com.duy.pascal.backend.function_declaretion.FunctionDeclaration;
import com.duy.pascal.backend.tokens.Token;
import com.duy.pascal.backend.tokens.basic.FinalizationToken;
import com.duy.pascal.backend.tokens.basic.FunctionToken;
import com.duy.pascal.backend.tokens.basic.ImplementationToken;
import com.duy.pascal.backend.tokens.basic.InitializationToken;
import com.duy.pascal.backend.tokens.basic.InterfaceToken;
import com.duy.pascal.backend.tokens.basic.PeriodToken;
import com.duy.pascal.backend.tokens.basic.ProcedureToken;
import com.duy.pascal.backend.tokens.basic.UnitToken;
import com.duy.pascal.backend.tokens.closing.EndToken;
import com.duy.pascal.backend.tokens.grouping.GrouperToken;
import com.duy.pascal.frontend.activities.RunnableActivity;
import com.google.common.collect.ListMultimap;
import com.js.interpreter.codeunit.ExecutableCodeUnit;
import com.js.interpreter.instructions.Executable;
import com.js.interpreter.runtime.codeunit.RuntimePascalLibrary;
import com.js.interpreter.source_include.ScriptSource;

import java.io.Reader;
import java.util.List;

public class LibraryPascal extends ExecutableCodeUnit {
    private RunnableActivity handler;

    public LibraryPascal(Reader program,
                         String sourceName,
                         ListMultimap<String, AbstractFunction> functionTable,
                         List<ScriptSource> includeDirectories,
                         @Nullable RunnableActivity handler)
            throws ParsingException {
        super(program, functionTable, sourceName, includeDirectories, handler);
        this.handler = handler;
    }

    @Override
    protected LibraryExpressionContext getExpressionContextInstance(
            ListMultimap<String, AbstractFunction> functionTable, RunnableActivity handler) {
        return new LibraryExpressionContext(functionTable, handler);
    }

    @Override
    public RuntimePascalLibrary run() {
        return new RuntimePascalLibrary(this);
    }

    public class LibraryExpressionContext extends CodeUnitExpressionContext {
        private Executable initInstruction;
        private Executable finalInstruction;

        public LibraryExpressionContext(ListMultimap<String, AbstractFunction> function,
                                        RunnableActivity handler) {
            super(function, handler, true);
        }

        public void declareInterface(GrouperToken i) throws ParsingException {
            while (!(i.peek() instanceof ImplementationToken)) {
                this.addNextDeclaration(i);
            }
        }

        @Override
        protected boolean handleUnrecognizedDeclarationImpl(Token next, GrouperToken i)
                throws ParsingException {
            if (next instanceof UnitToken) {
                programName = i.nextWordValue();
                i.assertNextSemicolon(i);
                return true;
            } else if (next instanceof InterfaceToken) {
                declareInterface(i);
                return true;

            } else if (next instanceof ImplementationToken) {
                declareImplementation(i);
                return true;

            } else if (next instanceof InitializationToken) {

                declareInit(i);
                return true;

            } else if (next instanceof FinalizationToken) {
                declareFinal(i);
                return true;

            }


            //end region
            return false;
        }

        private void declareImplementation(GrouperToken i) throws ParsingException {
            Token next = i.peek();
            while (!(next instanceof InitializationToken ||
                    next instanceof EndToken || next instanceof FinalizationToken)) {
                super.addNextDeclaration(i);
                next = i.peek();
            }
        }

        public void declareInit(GrouperToken grouperToken) throws ParsingException {
            this.initInstruction = grouperToken.getNextCommand(this);
            grouperToken.assertNextSemicolon(null);
        }

        public void declareFinal(GrouperToken grouperToken) throws ParsingException {
            this.finalInstruction = grouperToken.getNextCommand(this);
            grouperToken.assertNextSemicolon(null);
           /* if (grouperToken.peek() instanceof EndToken) {
                grouperToken.take();
            } else {
                throw new ExpectedTokenException("end", grouperToken.peek());
            }*/

            if (grouperToken.peek() instanceof PeriodToken) {
                grouperToken.take();
            } else {
                throw new ExpectedTokenException(".", grouperToken.peek());
            }
        }

        @Override
        public void addNextDeclaration(GrouperToken i) throws ParsingException {
            Token next = i.peek();
            if (next instanceof ProcedureToken || next instanceof FunctionToken) {
                i.take();
                boolean is_procedure = next instanceof ProcedureToken;
                FunctionDeclaration declaration = new FunctionDeclaration(this, i, is_procedure);
                checkExistFunction(declaration);
            } else {
                super.addNextDeclaration(i);
            }
        }

        @Override
        public void handleBeginEnd(GrouperToken i) throws ParsingException {
            throw new MisplacedDeclarationException(i.peek().lineInfo,
                    "main function", LibraryPascal.this.context);
        }
    }

}
