/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ballerina.plugins.idea.formatter;

import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import io.ballerina.plugins.idea.BallerinaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static io.ballerina.plugins.idea.psi.BallerinaTypes.ABORT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ACTION_INVOCATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ADD;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ALL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.AND;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ANNOTATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ANNOTATION_ATTACHMENT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ANY_IDENTIFIER_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ARRAY_TYPE_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.AS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ASSIGN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ATTACHMENT_POINT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.AWAIT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.BIND;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.BLOCK;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.BREAK;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.BUT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.BY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CALLABLE_UNIT_BODY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CALLABLE_UNIT_SIGNATURE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CATCH;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CATCH_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CATCH_CLAUSES;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CHECK;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.COLON;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.COMMA;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.COMPLETE_PACKAGE_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.COMPOUND_OPERATOR;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.CONTINUE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DAY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DECIMAL_INTEGER_LITERAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DEPRECATED;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DIV;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DOCUMENTATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DOCUMENTATION_TEMPLATE_ATTRIBUTE_DESCRIPTION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DOT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.DOUBLE_COLON;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ELLIPSIS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ELSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ELSE_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ELSE_IF_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ELVIS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ENDPOINT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ENDPOINT_INITIALIZATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ENUM;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EQUAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EQUAL_GT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EVENTS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EVERY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EXPRESSION_LIST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.EXTERN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FAIL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FIELD;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FIELD_DEFINITION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FINALLY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FINALLY_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FINITE_TYPE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FIRST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FLOATING_POINT_LITERAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FOLLOWED;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FOR;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FOREACH;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FOREVER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FORK;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FROM;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FULL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FUNCTION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FUNCTION_INVOCATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FUNCTION_NAME_REFERENCE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.FUTURE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.GROUP;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.GT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.GT_EQUAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.HALF_OPEN_RANGE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.HAVING;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.HOUR;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.IDENTIFIER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.IF;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.IF_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.IMPORT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.IMPORT_DECLARATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.IN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INDEX;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INNER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INTEGER_LITERAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INT_RANGE_EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INVOCATION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INVOCATION_ARG;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.INVOCATION_ARG_LIST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.JOIN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.JOIN_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.JSON;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LARROW;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LAST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LEFT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LEFT_BRACE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LEFT_BRACKET;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LEFT_PARENTHESIS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LENGTHOF;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LOCK;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.LT_EQUAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MAP;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MATCH;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MATCH_EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MATCH_EXPRESSION_PATTERN_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MATCH_STATEMENT_BODY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MINUTE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MOD;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MONTH;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.MUL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.NAMED_PATTERN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.NAME_REFERENCE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.NEW;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.NOT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.NOT_EQUAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.NULLABLE_TYPE_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OBJECT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OBJECT_BODY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OBJECT_INITIALIZER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OBJECT_INITIALIZER_PARAMETER_LIST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ON;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ONABORT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ONCOMMIT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ONRETRY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ON_RETRY_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OR;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.ORDER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OUTER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.OUTPUT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.PACKAGE_REFERENCE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.PARAMETER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.PARAMETER_LIST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.PIPE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.POW;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.PRIVATE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.PUBLIC;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RANGE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RARROW;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RECORD_KEY_VALUE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RECORD_LITERAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RECORD_LITERAL_BODY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RESOURCE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RESOURCE_DEFINITION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.REST_PARAMETER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RETRIES;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RETURN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RETURNS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RETURN_PARAMETER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RETURN_TYPE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RIGHT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RIGHT_BRACE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RIGHT_BRACKET;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.RIGHT_PARENTHESIS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SAFE_ASSIGNMENT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SECOND;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SELECT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SEMICOLON;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SERVICE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SERVICE_BODY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SERVICE_ENDPOINT_ATTACHMENTS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SET;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SIMPLE_LITERAL_EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SIMPLE_TYPE_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SIMPLE_VARIABLE_REFERENCE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SNAPSHOT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SOME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.START;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.STATEMENT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.STREAM;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.SUB;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TABLE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.THROW;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TIMEOUT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TIMEOUT_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TRANSACTION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TRANSACTION_PROPERTY_INIT_STATEMENT_LIST;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TRIGGER_WORKER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TRY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TUPLE_TYPE_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.TYPE_CONVERSION_EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.UNARY_EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.UNIDIRECTIONAL;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.UNION_TYPE_NAME;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.UNTAINT;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.VAR;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.VARIABLE_REFERENCE_EXPRESSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.VERSION;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WHERE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WHERE_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WHILE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WHILE_STATEMENT_BODY;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WINDOW;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WINDOW_CLAUSE;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WITH;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WITHIN;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.WORKER;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.XMLNS;
import static io.ballerina.plugins.idea.psi.BallerinaTypes.YEAR;

/**
 * Builds the Ballerina file formatting model.
 */
public class BallerinaFormattingModelBuilder implements FormattingModelBuilder {

    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        BallerinaBlock rootBlock = new BallerinaBlock(
                element.getNode(), null, Indent.getNoneIndent(), null, settings, createSpaceBuilder(settings),
                new HashMap<>()
        );
        return FormattingModelProvider.createFormattingModelForPsiFile(
                element.getContainingFile(), rootBlock, settings
        );
    }

    // Note - In case of multiple matching rules, top rule is the one which will get applied.
    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, BallerinaLanguage.INSTANCE)
                // Keywords
                .around(IMPORT).spaceIf(true)
                .around(AS).spaceIf(true)
                .around(PUBLIC).spaceIf(true)
                .around(PRIVATE).spaceIf(true)
                .around(EXTERN).spaceIf(true)
                .around(RESOURCE).spaceIf(true)
                .around(OBJECT).spaceIf(true)
                .around(ENUM).spaceIf(true)
                .around(WORKER).spaceIf(true)
                .around(ENDPOINT).spaceIf(true)
                .around(BIND).spaceIf(true)
                .around(XMLNS).spaceIf(true)
                .around(RETURNS).spaceIf(true)
                .around(VERSION).spaceIf(true)
                .around(DOCUMENTATION).spaceIf(true)
                .around(DEPRECATED).spaceIf(true)

                .around(VAR).spaceIf(true)
                .around(IF).spaceIf(true)
                .around(MATCH).spaceIf(true)
                .around(ELSE).spaceIf(true)
                .around(FOREACH).spaceIf(true)
                .around(WHILE).spaceIf(true)
                .before(CONTINUE).spaceIf(true)
                .after(CONTINUE).spaceIf(false)
                .around(BREAK).spaceIf(false)
                .around(JOIN).spaceIf(true)
                .around(SOME).spaceIf(true) // Todo
                .around(ALL).spaceIf(true) // Todo
                .around(TIMEOUT).spaceIf(true)
                .around(TRY).spaceIf(true)
                .around(CATCH).spaceIf(true)
                .around(FINALLY).spaceIf(true)
                .around(THROW).spaceIf(true)
                .around(TRANSACTION).spaceIf(true)
                .around(ABORT).spaceIf(false)
                .around(FAIL).spaceIf(true)
                .around(ONRETRY).spaceIf(true)
                .around(RETRIES).spaceIf(true)
                .around(ONABORT).spaceIf(true)
                .around(ONCOMMIT).spaceIf(true)
                .around(LENGTHOF).spaceIf(true)
                .around(WITH).spaceIf(true)
                .around(IN).spaceIf(true)
                .around(LOCK).spaceIf(true)
                .around(UNTAINT).spaceIf(true) // Todo
                .around(START).spaceIf(true)
                .around(AWAIT).spaceIf(true)
                .around(CHECK).spaceIf(true)
                .around(BUT).spaceIf(true)

                // Streaming keywords
                .around(FROM).spaceIf(true)
                .around(ON).spaceIf(true)
                .around(SELECT).spaceIf(true)
                .around(GROUP).spaceIf(true)
                .around(BY).spaceIf(true)
                .around(HAVING).spaceIf(true)
                .around(ORDER).spaceIf(true)
                .around(WHERE).spaceIf(true)
                .around(FOLLOWED).spaceIf(true)
                .around(SET).spaceIf(true)
                .around(FOR).spaceIf(true)
                .around(WINDOW).spaceIf(true)
                .around(EVENTS).spaceIf(true)
                .around(EVERY).spaceIf(true)
                .around(WITHIN).spaceIf(true)
                .around(LAST).spaceIf(true)
                .around(FIRST).spaceIf(true)
                .around(SNAPSHOT).spaceIf(true)
                .around(OUTPUT).spaceIf(true)
                .around(INNER).spaceIf(true)
                .around(OUTER).spaceIf(true)
                .around(RIGHT).spaceIf(true)
                .around(LEFT).spaceIf(true)
                .around(FULL).spaceIf(true)
                .around(UNIDIRECTIONAL).spaceIf(true)
                .around(SECOND).spaceIf(true)
                .around(MINUTE).spaceIf(true)
                .around(HOUR).spaceIf(true)
                .around(DAY).spaceIf(true)
                .around(MONTH).spaceIf(true)
                .around(YEAR).spaceIf(true)
                .around(FOREVER).spaceIf(true)

                // Common tokens
                .before(COMMA).spaceIf(false)
                .after(COMMA).spaceIf(true)
                .around(SEMICOLON).spaceIf(false)
                .after(LEFT_BRACKET).spaceIf(false)
                .before(RIGHT_BRACKET).spaceIf(false)
                .around(EQUAL_GT).spaceIf(true)

                // Function signature
                .between(LEFT_PARENTHESIS, RIGHT_PARENTHESIS).spaceIf(false)
                .around(RETURN_PARAMETER).spaceIf(true)
                .between(SIMPLE_TYPE_NAME, IDENTIFIER).spaceIf(true)
                .between(SIMPLE_TYPE_NAME, EQUAL_GT).spaceIf(true)
                .after(ANNOTATION_ATTACHMENT).spaceIf(true)
                .between(FUNCTION, SIMPLE_TYPE_NAME).spaceIf(true)
                .around(SIMPLE_TYPE_NAME).spaceIf(false)
                .between(NAME_REFERENCE, RECORD_LITERAL).spaceIf(true)
                .around(NAME_REFERENCE).spaceIf(false)
                .before(RETURN_TYPE).spaceIf(false)
                .after(RETURN_TYPE).spaceIf(true)
                .between(UNION_TYPE_NAME, IDENTIFIER).spaceIf(true)
                .between(FUNCTION_NAME_REFERENCE, LEFT_PARENTHESIS).spaceIf(false)

                .around(UNION_TYPE_NAME).spaceIf(false)
                .between(TUPLE_TYPE_NAME, IDENTIFIER).spaceIf(true)
                .around(TUPLE_TYPE_NAME).spaceIf(false)
                .between(PARAMETER, ASSIGN).spaceIf(true)
                .around(PARAMETER).spaceIf(false)

                .aroundInside(DIV, IMPORT_DECLARATION).spaceIf(false)
                .aroundInside(DOT, COMPLETE_PACKAGE_NAME).spaceIf(false)
                .aroundInside(COLON, PACKAGE_REFERENCE).spaceIf(false)
                .afterInside(IDENTIFIER, CALLABLE_UNIT_SIGNATURE).spaceIf(false)
                .between(DOUBLE_COLON, CALLABLE_UNIT_SIGNATURE).spaceIf(false)
                .around(CALLABLE_UNIT_SIGNATURE).spaceIf(true)
                .after(PACKAGE_REFERENCE).spaceIf(false)
                .aroundInside(NAME_REFERENCE, FUNCTION_INVOCATION).spaceIf(false)
                .around(INVOCATION_ARG_LIST).spaceIf(false)
                .before(CALLABLE_UNIT_BODY).spaceIf(true)

                .before(NEW).spaceIf(true)
                .between(NEW, SEMICOLON).spaceIf(false)
                .between(NEW, OBJECT_INITIALIZER_PARAMETER_LIST).spaceIf(false)
                .between(NEW, LEFT_PARENTHESIS).spaceIf(false)
                .after(NEW).spaceIf(true)

                // Record Literals
                .beforeInside(COLON, RECORD_KEY_VALUE).spaceIf(false)
                .afterInside(COLON, RECORD_KEY_VALUE).spaceIf(true)
                .beforeInside(COMMA, RECORD_LITERAL).spaceIf(false)
                .afterInside(COMMA, RECORD_LITERAL).spaceIf(true)
                .between(RECORD_KEY_VALUE, RIGHT_BRACE).spaceIf(false)
                .between(LEFT_BRACE, RIGHT_BRACE).spaceIf(false)
                .around(RECORD_LITERAL_BODY).spaceIf(true)

                // Statements
                .between(LEFT_BRACE, RIGHT_BRACE).spaceIf(false)
                .between(LEFT_BRACKET, RIGHT_BRACKET).spaceIf(false)
                .between(SIMPLE_VARIABLE_REFERENCE, ASSIGN).spaceIf(true)
                .between(SIMPLE_VARIABLE_REFERENCE, SAFE_ASSIGNMENT).spaceIf(true)
                .between(SIMPLE_VARIABLE_REFERENCE, COMPOUND_OPERATOR).spaceIf(true)
                .between(SIMPLE_VARIABLE_REFERENCE, WHERE_CLAUSE).spaceIf(true)
                .between(SIMPLE_VARIABLE_REFERENCE, WINDOW_CLAUSE).spaceIf(true)
                .after(SIMPLE_VARIABLE_REFERENCE).spaceIf(false)
                .aroundInside(DOT, INVOCATION).spaceIf(false)
                .between(INVOCATION_ARG, COMMA).spaceIf(false)
                .between(ANY_IDENTIFIER_NAME, LEFT_PARENTHESIS).spaceIf(false)
                .between(EXPRESSION_LIST, LARROW).spaceIf(true)
                .around(EXPRESSION_LIST).spaceIf(false)
                .between(ARRAY_TYPE_NAME, IDENTIFIER).spaceIf(true)
                .aroundInside(GT, TYPE_CONVERSION_EXPRESSION).spaceIf(false)
                .after(LEFT_PARENTHESIS).spaceIf(false)
                .before(RIGHT_PARENTHESIS).spaceIf(false)

                .between(ADD, INTEGER_LITERAL).spaceIf(false)
                .between(SUB, INTEGER_LITERAL).spaceIf(false)
                .between(ADD, FLOATING_POINT_LITERAL).spaceIf(false)
                .between(SUB, FLOATING_POINT_LITERAL).spaceIf(false)

                .between(SERVICE, IDENTIFIER).spaceIf(true)
                .before(SERVICE_BODY).spaceIf(true)
                .between(SERVICE, LT).spaceIf(false)
                .before(SERVICE_ENDPOINT_ATTACHMENTS).spaceIf(true)

                .afterInside(IDENTIFIER, RESOURCE_DEFINITION).spaceIf(false)

                .between(ANNOTATION, LT).spaceIf(false)
                .between(FUNCTION, LT).spaceIf(false)
                .between(FUTURE, LT).spaceIf(false)
                .between(JSON, LT).spaceIf(false)
                .between(MAP, LT).spaceIf(false)
                .between(STREAM, LT).spaceIf(false)
                .between(TABLE, LT).spaceIf(false)

                .around(PIPE).spaceIf(false)
                .between(NULLABLE_TYPE_NAME, IDENTIFIER).spaceIf(true)
                .between(NULLABLE_TYPE_NAME, EQUAL_GT).spaceIf(true)
                .around(NULLABLE_TYPE_NAME).spaceIf(false)

                .around(PARAMETER_LIST).spaceIf(false)

                .before(CATCH_CLAUSE).spaceIf(true)
                .before(CATCH_CLAUSES).spaceIf(true)
                .before(FINALLY_CLAUSE).spaceIf(true)
                .before(JOIN_CLAUSE).spaceIf(true)
                .before(TIMEOUT_CLAUSE).spaceIf(true)

                .before(ELSE_IF_CLAUSE).spaceIf(true)
                .before(ELSE_CLAUSE).spaceIf(true)

                .before(ON_RETRY_CLAUSE).spaceIf(true)

                .betweenInside(DOT, MUL, FIELD).spaceIf(false)

                .around(DECIMAL_INTEGER_LITERAL).spaceIf(false)

                //ELLIPSIS operator
                .between(ELLIPSIS, VARIABLE_REFERENCE_EXPRESSION).spaceIf(false)
                .between(EXPRESSION, ELLIPSIS).spaceIf(false)
                .between(ELLIPSIS, EXPRESSION).spaceIf(false)
                .between(SIMPLE_LITERAL_EXPRESSION, ELLIPSIS).spaceIf(false)
                .between(ELLIPSIS, SIMPLE_LITERAL_EXPRESSION).spaceIf(false)
                .betweenInside(ELLIPSIS, IDENTIFIER, REST_PARAMETER).spaceIf(true)

                .before(INDEX).spaceIf(false)

                .before(MATCH_EXPRESSION).spaceIf(true)
                .between(VARIABLE_REFERENCE_EXPRESSION, MATCH_STATEMENT_BODY).spaceIf(true)

                .between(RETURN, SEMICOLON).spaceIf(false)
                .after(RETURN).spaceIf(true)
                .around(RANGE).spaceIf(false)

                .around(OBJECT_BODY).spaceIf(true)
                .around(FIELD_DEFINITION).spaceIf(true)
                .around(OBJECT_INITIALIZER).spaceIf(true)

                .around(ANNOTATION_ATTACHMENT).spaceIf(true)
                .around(ATTACHMENT_POINT).spaceIf(false)

                .betweenInside(ADD, VARIABLE_REFERENCE_EXPRESSION, UNARY_EXPRESSION).spaceIf(false)
                .betweenInside(SUB, VARIABLE_REFERENCE_EXPRESSION, UNARY_EXPRESSION).spaceIf(false)

                .between(FORK, SEMICOLON).spaceIf(false)
                .around(FORK).spaceIf(true)

                .between(IDENTIFIER, FINITE_TYPE).spaceIf(true)
                .between(IDENTIFIER, ENDPOINT_INITIALIZATION).spaceIf(true)

                .around(ARRAY_TYPE_NAME).spaceIf(false)

                .around(TRANSACTION_PROPERTY_INIT_STATEMENT_LIST).spaceIf(true)

                // Streaming
                .before(WHERE_CLAUSE).spaceIf(true)
                .before(WINDOW_CLAUSE).spaceIf(true)
                .around(INT_RANGE_EXPRESSION).spaceIf(true)
                .betweenInside(DOT, IDENTIFIER, FIELD).spaceIf(false)
                .betweenInside(NOT, IDENTIFIER, FIELD).spaceIf(false)
                .before(FIELD).spaceIf(false)

                // Operators
                .around(ASSIGN).spaceIf(true)
                .around(ADD).spaceIf(true)
                .around(SUB).spaceIf(true)
                .around(DIV).spaceIf(true)
                .around(MUL).spaceIf(true)
                .around(POW).spaceIf(true)
                .around(MOD).spaceIf(true)

                .around(HALF_OPEN_RANGE).spaceIf(false)

                .around(DOUBLE_COLON).spaceIf(false)

                .around(EQUAL).spaceIf(true)
                .around(NOT_EQUAL).spaceIf(true)
                .around(GT).spaceIf(true)
                .around(LT).spaceIf(true)
                .around(GT_EQUAL).spaceIf(true)
                .around(LT_EQUAL).spaceIf(true)
                .around(AND).spaceIf(true)
                .around(OR).spaceIf(true)

                .aroundInside(RARROW, ACTION_INVOCATION).spaceIf(false)
                .aroundInside(RARROW, TRIGGER_WORKER).spaceIf(true)
                .between(EXPRESSION_LIST, RARROW).spaceIf(true)
                .around(RARROW).spaceIf(false)

                .around(LARROW).spaceIf(true)
                .around(ELVIS).spaceIf(true)

                .around(COMPOUND_OPERATOR).spaceIf(true)

                .around(SAFE_ASSIGNMENT).spaceIf(true)

                .around(STATEMENT).lineBreakOrForceSpace(true, true)
                .between(MATCH_EXPRESSION_PATTERN_CLAUSE, COMMA).lineBreakOrForceSpace(false, false)
                 //TODO verify whether this should be removed
                 // .around(MATCH_EXPRESSION_PATTERN_CLAUSE).lineBreakOrForceSpace(true, true)
                .aroundInside(BLOCK, NAMED_PATTERN).lineBreakOrForceSpace(true, true)

                .betweenInside(RIGHT_PARENTHESIS, LEFT_BRACE, IF_CLAUSE).spaceIf(true)
                .betweenInside(RIGHT_PARENTHESIS, LEFT_BRACE, ELSE_IF_CLAUSE).spaceIf(true)

                .betweenInside(EXPRESSION, LEFT_BRACE, IF_CLAUSE).spaceIf(true)
                .betweenInside(SIMPLE_LITERAL_EXPRESSION, LEFT_BRACE, IF_CLAUSE).spaceIf(true)
                .betweenInside(EXPRESSION, LEFT_BRACE, ELSE_IF_CLAUSE).spaceIf(true)
                .betweenInside(SIMPLE_LITERAL_EXPRESSION, LEFT_BRACE, ELSE_IF_CLAUSE).spaceIf(true)

                .before(WHILE_STATEMENT_BODY).spaceIf(true)
                .between(EXPRESSION, WHILE_STATEMENT_BODY).spaceIf(true)
                .between(SIMPLE_LITERAL_EXPRESSION, WHILE_STATEMENT_BODY).spaceIf(true)

                // Docs
                .aroundInside(IDENTIFIER, DOCUMENTATION_TEMPLATE_ATTRIBUTE_DESCRIPTION).spaceIf(false)
                ;
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }
}
