package com.netflix.conductor.core.scripting;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyDate;
import org.graalvm.polyglot.proxy.ProxyDuration;
import org.graalvm.polyglot.proxy.ProxyInstant;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.graalvm.polyglot.proxy.ProxyTime;
import org.graalvm.polyglot.proxy.ProxyTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraalEvaluator implements ScriptEvaluator {

  public static final Logger logger = LoggerFactory.getLogger(GraalEvaluator.class);

  private final GraalConfiguration configuration;

  public GraalEvaluator(GraalConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Boolean evalBool(String script, Object input) throws ScriptException {
    final Object eval = eval(script, input);
    if (eval instanceof Boolean) {
      return (Boolean) eval;
    }
    throw new ScriptException("Evaluation did not yield a boolean value");
  }

  @Override
  public Object eval(String script, Object input) throws ScriptException {
    Timer timer = null;
    try(Context context = createSafeContext(configuration).build()) {
      final Source source = Source.newBuilder(configuration.getScriptingLanguage(), script, "<eval>").buildLiteral();
      final Value bindings = context.getBindings(configuration.getScriptingLanguage());
      bindings.putMember(configuration.getBindingVariableName(), convertGraphToProxy(input));
      final Long timeout = configuration.getScriptEvaluatorTimeout();
      if (timeout != null && timeout > 0) {
        timer = new Timer("graal-timeout-thread");
        timer.schedule(new TimerTask() {
              @Override
              public void run() {
                context.close(true);
              }
            }, timeout);

      }
      final Value value = context.eval(source);
      return value.as(Object.class);
    }  catch(PolyglotException ex) {
      if (ex.isCancelled()) {
        logger.info("Script evaluation could not be run before timeout", ex);
      }
      throw new ScriptException(ex);
    } finally {
      if (timer != null) {
        timer.cancel();
      }
    }
  }

  private static Object convertGraphToProxy(Object source) {
    if (source == null) {
      return null;
    }
    if (source instanceof Map) {
      final LinkedHashMap<String, Object> proxiedMap = ((Map<?, ?>) source).entrySet()
          .stream()
          .filter(e -> e.getValue() != null)
          .collect(Collectors
              .toMap(e -> e.getKey().toString(), v -> convertGraphToProxy(v.getValue()), (x, y) -> y,
                  LinkedHashMap::new));
      return ProxyObject.fromMap(proxiedMap);
    } else if (source instanceof List) {
      return ProxyArray.fromList(((List<?>) source).stream().map(GraalEvaluator::convertGraphToProxy).collect(Collectors.toList()));
    } else if (source instanceof Instant) {
      return ProxyInstant.from((Instant) source);
    } else if (source instanceof Date) {
      return ProxyInstant.from(((Date) source).toInstant());
    } else if (source instanceof Duration) {
      return ProxyDuration.from((Duration) source);
    } else if (source instanceof ZoneId) {
      return ProxyTimeZone.from((ZoneId) source);
    } else if (source instanceof LocalTime) {
      return ProxyTime.from((LocalTime) source);
    } else if (source instanceof LocalDate) {
      return ProxyDate.from((LocalDate) source);
    }
    return source;
  }

  private static Builder createSafeContext(GraalConfiguration configuration) {
    // if we are nashorn, don't bother with the other settings as they may break code not written with Graal in mind
    Builder builder = null;
    if (configuration.enableNashornCompat()) {
      builder = Context.newBuilder("js")
          .allowAllAccess(true)
          .allowExperimentalOptions(true).option("js.nashorn-compat", "true");
    } else {
      builder = Context.newBuilder(configuration.getScriptingLanguage())
          .allowHostAccess(HostAccess.EXPLICIT)
          .allowCreateProcess(configuration.allowCreateProcess())
          .allowEnvironmentAccess(configuration.allowEnvironmentAccess() ? EnvironmentAccess.INHERIT : EnvironmentAccess.NONE)
          .allowIO(configuration.allowIo())
          .allowNativeAccess(configuration.allowNativeAccess())
          .allowCreateThread(configuration.allowCreateThread());
    }

    return builder.logHandler(
            new Handler() {
              @Override
              public void publish(LogRecord record) {
                logger.info("Error executing JavaScript", record.getThrown());
              }

              @Override
              public void flush() {}

              @Override
              public void close() {}
            });
  }
}
