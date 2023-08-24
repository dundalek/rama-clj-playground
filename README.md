
Experiments using [Red Planet Labs' Rama](https://redplanetlabs.com/) with Clojure. Based on the [tutorial](https://redplanetlabs.com/docs/~/tutorial1.html) and [rama-examples](https://github.com/redplanetlabs/rama-examples).

Run Hello World example:

```
clojure -M -m rama-playground.hello-world
```

Run Word Counter example:

```
clojure -M -m rama-playground.simple-word-count
```

Troubleshooting:

If you include more libraries and encouter `Execution error (IllegalArgumentException) at org.objectweb.asm.Type/getTypeInternal` or `Failed to generate constants class` exceptions when running examples, there is likely a dependency which pulls in incompatible version of `org.ow2.asm/asm`. Run `clojure -Stree` and search for `org.ow2.asm/asm` to diagnose.

A known examples causing issues are [morse](https://github.com/nubank/morse) or [aws-api](https://github.com/cognitect-labs/aws-api/tree/main) which both depends on `core.async.` The issue is that `core.async` depends on `tools.analyzer.jvm` which pulls in `org.ow2.asm/asm` version 9.2, whereas Rama depends on version 4.2. To make Rama happy you can add `org.ow2.asm/asm {:mvn/version "4.2"}` to deps.edn, although it might cause issues with those other libraries.
