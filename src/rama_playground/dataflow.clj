(ns rama-playground.dataflow
  (:import
   (com.rpl.rama Block Expr LoopVars)
   (com.rpl.rama.ops Ops)))

(comment
  ;; Running dataflow directly
  ;; https://redplanetlabs.com/docs/~/intermediate-dataflow.html#_ops_explode_ops_tuple_and_ops_expand
  (let [data [1 2 3 4]]
    (-> (Block/each Ops/EXPLODE data)
        (.out (into-array String ["*v"]))
        (.each Ops/PRINTLN "Elem:" "*v")
        (.each Ops/PRINTLN "X")
        (.execute)))

  ;; Looping
  ;; https://redplanetlabs.com/docs/~/tutorial4.html#_looping
  (-> (Block/loopWithVars
       (LoopVars/var "*i" 0)
       (Block/ifTrue
        (Expr. Ops/NOT_EQUAL "*i" 5)
        (-> (Block/emitLoop (into-array String ["*i"]))
            (.each Ops/PRINTLN " *i is not 5 yet")
            (.continueLoop (to-array [(Expr. Ops/INC "*i")])))))
      (.out (into-array String ["*loopValue"]))
      (.each Ops/PRINTLN "Emitted:" "*loopValue")
      (.execute)))
