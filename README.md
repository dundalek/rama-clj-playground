
Experiments using [Red Planet Labs' Rama](https://redplanetlabs.com/) with Clojure. Based on the [tutorial](https://redplanetlabs.com/docs/~/tutorial1.html) and [rama-examples](https://github.com/redplanetlabs/rama-examples).

Run Hello World example:

```
clojure -M -m rama-playground.hello-world
```

Run Word Counter example:

```
clojure -M -m rama-playground.simple-word-count
```

Note on working in REPL:

When I had [io.github.nubank/morse](https://github.com/nubank/morse) on the classpath in my repl profile the examples failed to run with `Failed to generate constants class` exception. But using just plain nREPL worked:

```
clojure -Sdeps '{:deps {nrepl/nrepl {:mvn/version "RELEASE"}}}' -M -m nrepl.cmdline
```
