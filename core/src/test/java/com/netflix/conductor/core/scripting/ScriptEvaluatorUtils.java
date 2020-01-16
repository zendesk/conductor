package com.netflix.conductor.core.scripting;

import com.netflix.conductor.core.execution.TestConfiguration;

public class ScriptEvaluatorUtils {

  public static class TestGraalConfiguration extends TestConfiguration implements GraalConfiguration {

    private final boolean asNashorn;

    public TestGraalConfiguration(boolean asNashorn) {
      this.asNashorn = asNashorn;
    }

    @Override
    public String getProperty(String string, String def) {
      return def;
    }

    @Override
    public boolean getBooleanProperty(String name, boolean defaultValue) {
      return defaultValue;
    }

    @Override
    public int getIntProperty(String string, int def) {
      return def;
    }

    @Override
    public long getLongProperty(String name, long defaultValue) {
      return defaultValue;
    }

    @Override
    public boolean getBoolProperty(String name, boolean defaultValue) {
      return defaultValue;
    }

    @Override
    public boolean enableNashornCompat() {
      return asNashorn;
    }
  }

  public static ScriptEvaluator create() {
    return new GraalEvaluator(new TestGraalConfiguration(true));
  }

  public static ScriptEvaluator create(GraalConfiguration configuration) {
    return new GraalEvaluator(configuration);
  }
}
