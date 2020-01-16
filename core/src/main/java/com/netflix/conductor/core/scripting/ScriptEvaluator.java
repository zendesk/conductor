package com.netflix.conductor.core.scripting;

import javax.script.ScriptException;

public interface ScriptEvaluator {

  Boolean evalBool(String script, Object input) throws ScriptException;

  Object eval(String script, Object input) throws ScriptException;

}
