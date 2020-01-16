package com.netflix.conductor.core.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.conductor.core.scripting.ScriptEvaluatorUtils.TestGraalConfiguration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptException;
import org.graalvm.polyglot.PolyglotException;
import org.junit.Before;
import org.junit.Test;

public abstract class BaseGraalScriptEvaluationTest {

  protected ScriptEvaluator evaluator;

  @Before
  public void beforeEach() {
    evaluator = ScriptEvaluatorUtils.create(new TestGraalConfiguration(runAsNashorn()));
  }

  protected abstract boolean runAsNashorn();

  @Test
  public void testParseSimpleExpression() {
    try {
      assertFalse(evaluator.evalBool("true == false", new LinkedHashMap<String, Object>()));
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testParseStringBindings() {
    try {
      final Map<String, Object> input = new LinkedHashMap<>();
      input.put("bindingTest", "123");
      assertEquals(evaluator.eval("$.bindingTest == \"123\" ? \"success\" : \"failure\"", input), "success");
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testParseIntBindings() {
    try {
      final Map<String, Object> input = new LinkedHashMap<>();
      input.put("bindingTest", 123);
      assertEquals(evaluator.eval("$.bindingTest == 123 ? \"success\" : \"failure\"", input), "success");
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testParseBooleanBindings() {
    try {
      final Map<String, Object> input = new LinkedHashMap<>();
      input.put("bindingTest", Boolean.TRUE);
      assertEquals(evaluator.eval("$.bindingTest ? \"success\" : \"failure\"", input), "success");
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testParseArrayBindings() {
    try {
      final Map<String, Object> input = new LinkedHashMap<>();
      final List<String> inputValue = new ArrayList<>();
      inputValue.add("123");
      input.put("bindingTest", inputValue);
      assertEquals(evaluator.eval("$.bindingTest[0] == \"123\" ? \"success\" : \"failure\"", input), "success");
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testParseObjectBindings() {
    try {
      final Map<String, Object> input = new LinkedHashMap<>();
      final Map<String, Object> inputValue = new LinkedHashMap<>();
      inputValue.put("key", "123");
      input.put("bindingTest", inputValue);
      assertEquals(
          evaluator.eval(
              "$.bindingTest.key == \"123\" ? \"success\" : \"failure\"", input), "success");
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testNestedStructureBindings() {
    /*
     {
       "bindingTest": {
         "key1": {
           "key2": [
             [
               "321",
               {
                 "key3": "123"
               }
             ]
           ]
         }
       }
     }
    */
    try {
      final Map<String, Object> input = new LinkedHashMap<>();
      final Map<String, Object> map1 = new LinkedHashMap<>();
      final Map<String, Object> map2 = new LinkedHashMap<>();
      final Map<String, Object> map3 = new LinkedHashMap<>();
      final List<Object> array1 = new ArrayList<>();
      final List<Object> array2 = new ArrayList<>();
      map3.put("key3", "123");
      array2.add("321");
      array2.add(map3);
      array1.add(array2);
      map2.put("key2", array1);
      map1.put("key1", map2);
      input.put("bindingTest", map1);
      final ScriptEvaluator evaluator = ScriptEvaluatorUtils.create();
      assertEquals(evaluator.eval("$.bindingTest.key1.key2[0][0] == \"321\" ? \"success\" : \"failure\"", input), "success");
      assertEquals(evaluator.eval("$.bindingTest.key1.key2[0][1].key3 == \"123\" ? \"success\" : \"failure\"", input), "success");
    } catch (ScriptException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testTimeout() {
    final GraalConfiguration config = new TestGraalConfiguration(runAsNashorn()) {
      @Override
      public Long getScriptEvaluatorTimeout() {
        return 10L;
      }
    };
    try {
      ScriptEvaluator timeBased = ScriptEvaluatorUtils.create(config);
      timeBased.eval("while(true) {}", new LinkedHashMap<String, Object>());
    } catch (ScriptException e) {
      assertNotNull(e);
    }
  }
}
