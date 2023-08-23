
Experiments using [Red Planet Labs' Rama](https://redplanetlabs.com/) with Clojure. Based on the [tutorial](https://redplanetlabs.com/docs/~/tutorial1.html) and [rama-examples](https://github.com/redplanetlabs/rama-examples).

Run Hello World example:
```
clojure -M -m rama-playground.hello-world
```

Run Word Counter example:
```
clojure -M -m rama-playground.simple-word-count
```

For some reason examples only work when running via CLI, when running in REPL it fails with `Failed to generate constants class` exception.
