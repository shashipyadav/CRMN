package com.example.myapplication.function;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;

public class EncodeURIEngine {

    String encodedString = "";

    public String encodeURIComponent (String string) {

        Object[] functionParams = new Object[]{string};

        //The js function
        String script = "function encodeURI(value){  return encodeURIComponent(value)    ;} ";

        Context rhino = Context.enter();

        //disabling the optimizer to better support Android.
        rhino.setOptimizationLevel(-1);

        try {

            Scriptable scope = rhino.initStandardObjects();

            /**
             * evaluateString(Scriptable scope, java.lang.String source, java.lang.String sourceName,
             * int lineno, java.lang.Object securityDomain)
             *
             */
            rhino.evaluateString(scope, script , "JavaScript", 1, null);


            Function function = (Function) scope.get("encodeURI", scope);
            encodedString = (String) function.call(rhino, scope, scope, functionParams);


        } catch (RhinoException e) {

            e.printStackTrace();

        } finally {
            Context.exit();
        }

        return String.valueOf(encodedString);

    }




}
