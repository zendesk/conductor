package com.netflix.conductor.core.scripting;

import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import javax.script.ScriptException;
import org.junit.Test;

public class TestNashornScripting extends BaseGraalScriptEvaluationTest {

  @Override
  protected boolean runAsNashorn() {
    return true;
  }

  @Test
  public void testAccessToJava() {
    try {
      evaluator.eval(" Java.type('java.io.File');", new LinkedHashMap<String, Object>());
    } catch (ScriptException e) {
      fail("Should have allowed Java class access in Nashorn");
    }
  }
}
