package com.example.myapplication.function;



import android.util.Log;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;

public class EvaluateEngine {
    private Context rhino;
    private Scriptable scope;

    /**
     * This function evaluates the string when it is passed as a parameter.
     *
     * @param question The expression is passed to the method
     * @return Returns the evaluated answer in a double variable
     */
    public String eval (String question) {
        String result = "";
        Object[] functionParams = new Object[]{question};

        //The js function
        String script = "function evaluate(arithmetic){  return eval(arithmetic);} ";

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


            Function function = (Function) scope.get("evaluate", scope);
        //    answer = (Double) function.call(rhino, scope, scope, functionParams);
            Object answer =  function.call(rhino, scope, scope, functionParams);
            if(answer instanceof String){
                result = (String) answer;
            }else if(answer instanceof  Double){
                result = String.valueOf(answer);
            }else if(answer instanceof  Integer){
                result = String.valueOf(answer);
            }

        } catch (RhinoException e) {

            e.printStackTrace();

        } finally {
            Context.exit();
        }
        return result;

    }
}


