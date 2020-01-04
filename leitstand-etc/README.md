# Leitstand Environment

_Leitstand Environment_ provides access to environment variables and the Leitstand configuration.


## System Properties and Process Environment
Leitstand environment supports JVM system properties and process environment properties,
with precedence for JVM system properties.

For example, the listing below reads the `leitstand.etc.root` system property first.
If no `leitstand.etc.root` system property exists, environment searches for a `leitstand.etc.root` process environment variable. 
The value defaults to `/etc/leitstand` if the environment variable does not exist either
 
```Java
String dir = Environment.getSystemProperty("leitstand.etc.root","/etc/leitstand");
```

## Leitstand Configuration Directory
Leitstand defines a base configuration directory. 
The directory location is specified by the `leitstand.etc.root` property and defaults to `/etc/leitstand`. 

Leitstand modules can use the `Environment` to read configuration files from the configuration directory.
Say a _Leitstand Telemetry_ application wants to read the `GrafanaConfig` from the `grafana.yaml` file in the configuration directory ([Grafana](https://grafana.com) is a popular tool for metric visualization), then the environment can be used as follows to read the configuration:

```Java
import static io.leitstand.commons.etc.FileProcessor.yaml;
...
@Inject
Environment env;
...
GrafanaConfig config = env.loadConfig("grafana.yaml",
                                      yaml(GrafanaConfig.class),
                                      () -> new GrafanaConfig());

```

The first parameter specifies the location of the file in the configuration directory.
The second parameter specifies the processor to process the file.
The specified yaml processor translates the `grafana.yaml` to a `GrafanaConfig` instance.
The third parameter is optional and specifies the default configuration if the specified file does not exist.

The following default `FileProcessor` implementations exist:

- `properties()` loads `java.util.Properties` from a file.
- `yaml()` loads a YAML file and translates is to a Java object or to a `Map` if the Java type is omitted.

