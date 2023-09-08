(ns rama-playground.rama-helpers
  (:import
   (com.rpl.rama CompoundAgg)
   (com.rpl.rama.ops RamaFunction1)))

(defn function1 [f]
  (reify RamaFunction1
    (invoke [_ arg0]
      (f arg0))))

(defn out [block-out & vars]
  (.out block-out (into-array String vars)))

(defn compound-agg-map [& args]
  (CompoundAgg/map (to-array args)))
