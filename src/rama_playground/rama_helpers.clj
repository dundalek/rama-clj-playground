(ns rama-playground.rama-helpers
  (:import
   (com.rpl.rama Block CompoundAgg Path)
   (com.rpl.rama.ops RamaFunction0 RamaFunction1)))

(defn function0 [f]
  (reify RamaFunction0
    (invoke [_] (f))))

(defn function1 [f]
  (reify RamaFunction1
    (invoke [_ arg0] (f arg0))))

(defn each
  ([block op]
   (.each block (function0 op)))
  ([block op arg0]
   (.each block (function1 op) arg0)))
  ;; TODO: add more variants

(defn out [block-out & vars]
  (.out block-out (into-array String vars)))

(defn extract-map-fields [from & field-vars]
  (reduce
   (fn [ret field]
      ;; Implemented for keywords for now, but strings might make sense too.
     (assert (keyword? field) (str "Expected keyword, got: " field))
     (-> ret
         (each field from)
         (out (str "*" (name field)))))
   (Block/create)
   field-vars))

(defn compound-agg-map [& args]
  (CompoundAgg/map (to-array args)))

(defn path-key [& arg-keys]
  (Path/key (into-array String arg-keys)))


