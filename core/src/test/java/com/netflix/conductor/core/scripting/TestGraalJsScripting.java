package com.netflix.conductor.core.scripting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import javax.script.ScriptException;
import org.graalvm.polyglot.PolyglotException;
import org.junit.Test;

public class TestGraalJsScripting extends BaseGraalScriptEvaluationTest {

  @Override
  protected boolean runAsNashorn() {
    return false;
  }

  @Test
  public void testAccessToJava() {
    try {
      evaluator.eval(" Java.type('java.io.File');", new LinkedHashMap<String, Object>());
      fail("Should not have allowed Java class access in GraalJs mode");
    } catch (ScriptException e) {
      assertTrue(e.getCause() instanceof PolyglotException);
    }
  }
}
