(ns rama-playground.simple-word-count
  (:import
   (com.rpl.rama Agg CompoundAgg Depot PState Path RamaModule)
   (com.rpl.rama.test InProcessCluster LaunchConfig)))

;; https://github.com/redplanetlabs/rama-examples/blob/master/src/main/java/rama/examples/tutorial/SimpleWordCountModule.java

(deftype SimpleWordCountModule []
  RamaModule
  (define [_ setup topologies]
    (.declareDepot setup "*depot" (Depot/random))
    (let [s (.stream topologies "s")]
      (.pstate s "$$wordCounts" (PState/mapSchema String Long))
      (-> s
          (.source "*depot")
          (.out (into-array String ["*token"]))
          (.hashPartition "*token")
          (.compoundAgg "$$wordCounts" (CompoundAgg/map (to-array ["*token" (Agg/count)])))))))

(defn -main []
  (with-open [cluster (InProcessCluster/create)]
    (.launchModule cluster (->SimpleWordCountModule) (LaunchConfig. 1 1))
    (let [module-name (.getName SimpleWordCountModule)
          depot (.clusterDepot cluster module-name "*depot")
          wc (.clusterPState cluster module-name "$$wordCounts")]
      (doto depot
        (.append "one")
        (.append "two")
        (.append "two")
        (.append "three")
        (.append "three")
        (.append "three"))

      (println "one:" (.selectOne wc (Path/key (into-array String ["one"]))))
      (println "two:" (.selectOne wc (Path/key (into-array String ["two"]))))
      (println "three:" (.selectOne wc (Path/key (into-array String ["three"])))))))
