(ns rama-playground.rama-helpers
  (:import
   (com.rpl.rama CompoundAgg Path)
   (com.rpl.rama.ops RamaFunction0 RamaFunction1)))

(defn function0 [f]
  (reify RamaFunction0
    (invoke [_] (f))))

(defn function1 [f]
  (reify RamaFunction1
    (invoke [_ arg0] (f arg0))))

(defn out [block-out & vars]
  (.out block-out (into-array String vars)))

(defn compound-agg-map [& args]
  (CompoundAgg/map (to-array args)))

(defn path-key [& arg-keys]
  (Path/key (into-array String arg-keys)))
