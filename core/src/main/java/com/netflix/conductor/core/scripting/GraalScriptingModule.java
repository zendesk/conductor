package com.netflix.conductor.core.scripting;

import com.google.inject.AbstractModule;

public class GraalScriptingModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(GraalConfiguration.class).to(SystemPropertiesGraalConfiguration.class);
    bind(ScriptEvaluator.class).to(GraalEvaluator.class);
  }
}
