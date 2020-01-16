package com.netflix.conductor.core.scripting;

import com.netflix.conductor.core.config.Configuration;
import org.graalvm.polyglot.EnvironmentAccess;

/**
 * @author davidzzzzz
 */
public interface GraalConfiguration extends Configuration {

  String GRAAL_LANGUAGE_PROPERTY_NAME = "scripting.graal.language";
  String GRAAL_LANGUAGE_DEFAULT_VALUE = "js";

  String GRAAL_ENABLE_NASHORN_PROPERTY_NAME = "scripting.graal.enable.nashorn";
  boolean GRAAL_ENABLE_NASHORN_DEFAULT_VALUE = true;

  String GRAAL_TIMEOUT_PROPERTY_NAME = "scripting.graal.timeout";
  long GRAAL_TIMEOUT_DEFAULT_VALUE = -1;

  String GRAAL_BINDING_VAR_PROPERTY_NAME = "scripting.graal.binding.name";
  String GRAAL_BINDING_VAR_DEFAULT_VALUE = "$";

  String GRAAL_ALLOW_ENV_ACCESS_PROPERTY_NAME = "scripting.graal.allow.environment_access";
  boolean GRAAL_ALLOW_ENV_ACCESS_DEFAULT_VALUE = false;

  String GRAAL_ALLOW_CREATE_THREAD_PROPERTY_NAME = "scripting.graal.allow.create_thread";
  boolean GRAAL_ALLOW_CREATE_THREAD_DEFAULT_VALUE = false;

  String GRAAL_ALLOW_CREATE_PROCESS_PROPERTY_NAME = "scripting.graal.allow.create_process";
  boolean GRAAL_ALLOW_CREATE_PROCESS_DEFAULT_VALUE = false;

  String GRAAL_ALLOW_IO_PROPERTY_NAME = "scripting.graal.allow.io";
  boolean GRAAL_ALLOW_IO_DEFAULT_VALUE = false;

  String GRAAL_ALLOW_NATIVE_ACCESS_PROPERTY_NAME = "scripting.graal.allow.native_access";
  boolean GRAAL_ALLOW_NATIVE_ACCESS_DEFAULT_VALUE = false;

  /**
   * @return The maximum execution time of an evaluated script. Disabled if the time is less than 0. Disabled by default.
   */
  default Long getScriptEvaluatorTimeout() {
    return getLongProperty(GRAAL_TIMEOUT_PROPERTY_NAME, GRAAL_TIMEOUT_DEFAULT_VALUE);
  }

  /**
   * @return The language of the scripting engine. Defaults to <code>js</code>.
   */
  default String getScriptingLanguage() {
    return getProperty(GRAAL_LANGUAGE_PROPERTY_NAME, GRAAL_LANGUAGE_DEFAULT_VALUE);
  }

  /**
   * @return The variable name used to bind values into the executing script.
   */
  default String getBindingVariableName() {
    return getProperty(GRAAL_BINDING_VAR_PROPERTY_NAME, GRAAL_BINDING_VAR_DEFAULT_VALUE);
  }

  /**
   * @return Indicates whether our scripts can access environment variables. Defaults to <code>false</code>.
   * @see org.graalvm.polyglot.Context.Builder#allowEnvironmentAccess(EnvironmentAccess) 
   */
  default boolean allowEnvironmentAccess() {
    return getBooleanProperty(GRAAL_ALLOW_ENV_ACCESS_PROPERTY_NAME, GRAAL_ALLOW_ENV_ACCESS_DEFAULT_VALUE);
  }

  /**
   * @return Indicates whether our scripts create threads. Defaults to <code>false</code>.
   * @see org.graalvm.polyglot.Context.Builder#allowCreateThread(boolean) 
   */
  default boolean allowCreateThread() {
    return getBooleanProperty(GRAAL_ALLOW_CREATE_THREAD_PROPERTY_NAME, GRAAL_ALLOW_CREATE_THREAD_DEFAULT_VALUE);
  }

  /**
   * @return Indicates whether our scripts create processes. Defaults to <code>false</code>.
   * @see org.graalvm.polyglot.Context.Builder#allowCreateProcess(boolean) 
   */
  default boolean allowCreateProcess() {
    return getBooleanProperty(GRAAL_ALLOW_CREATE_PROCESS_PROPERTY_NAME, GRAAL_ALLOW_CREATE_PROCESS_DEFAULT_VALUE);
  }

  /**
   * @return Indicates whether our scripts have access to IO. Defaults to <code>false</code>.
   * @see org.graalvm.polyglot.Context.Builder#allowIO(boolean) 
   */
  default boolean allowIo() {
    return getBooleanProperty(GRAAL_ALLOW_IO_PROPERTY_NAME, GRAAL_ALLOW_IO_DEFAULT_VALUE);
  }

  /**
   * @return Indicates whether our scripts have native access. Defaults to <code>false</code>.
   * @see org.graalvm.polyglot.Context.Builder#allowNativeAccess(boolean)
   */
  default boolean allowNativeAccess() {
    return getBooleanProperty(GRAAL_ALLOW_NATIVE_ACCESS_PROPERTY_NAME, GRAAL_ALLOW_NATIVE_ACCESS_DEFAULT_VALUE);
  }

  /**
   * Sets whether Graal should emulate the behavior of Nashorn, including access to Java objects and environment. Enabled by default for backwards compatibility.
   * @return whether we emulate Nashorn.
   */
  default boolean enableNashornCompat() {
    return getBooleanProperty(GRAAL_ENABLE_NASHORN_PROPERTY_NAME, GRAAL_ENABLE_NASHORN_DEFAULT_VALUE);
  }
}
