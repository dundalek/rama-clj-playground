(ns rama-playground.page-analytics-module
  (:import
   (com.rpl.rama Agg CompoundAgg Depot Path PState RamaModule)
   (com.rpl.rama.test InProcessCluster LaunchConfig)
   (com.rpl.rama.ops RamaFunction1)
   (java.util HashMap)))

;; Based on https://github.com/redplanetlabs/rama-examples/blob/master/src/main/java/rama/examples/tutorial/PageAnalyticsModule.java

(defn function1 [f]
  (reify RamaFunction1
    (invoke [_ arg0]
      (f arg0))))

(deftype PageAnalyticsModule []
  RamaModule
  (define [_ setup topologies]
    (.declareDepot setup "*depot" (Depot/random))
    (let [s (.stream topologies "s")]
      (.pstate s "$$pageViewCount" (PState/mapSchema String Long))
      (.pstate s "$$sessionHistory" (PState/mapSchema
                                     String
                                     (PState/listSchema (PState/mapSchema String Long))))

      (-> (.source s "*depot")
          (.out (into-array String ["*pageVisit"]))
          (.each (function1 (fn [visit] (.get visit "sessionId"))) "*pageVisit")
          (.out (into-array String ["*sessionId"]))
          (.each (function1 (fn [visit] (.get visit "path"))) "*pageVisit")
          (.out (into-array String ["*path"]))
          (.compoundAgg "$$pageViewCount" (CompoundAgg/map (to-array ["*path" (Agg/count)])))
          (.compoundAgg "$$sessionHistory" (CompoundAgg/map (to-array ["*sessionId" (Agg/list "*pageVisit")])))))))

(defn -main []
  (with-open [cluster (InProcessCluster/create)]
    (.launchModule cluster (->PageAnalyticsModule) (LaunchConfig. 1 1))
    (let [module-name (.getName PageAnalyticsModule)
          depot (.clusterDepot cluster module-name "*depot")
          pvc (.clusterPState cluster module-name "$$pageViewCount")
          sh (.clusterPState cluster module-name "$$sessionHistory")]
      (.append depot (doto (HashMap.)
                       (.put "sessionId" "abc123")
                       (.put "path" "/posts")
                       (.put "duration" 4200)))

      (.append depot (doto (HashMap.)
                       (.put "sessionId" "abc123")
                       (.put "path" "/users")
                       (.put "duration" 2400)))

      (println "pageViewCount:" (.select pvc (Path/all)))
      (println "sessionHistory:" (.select sh (Path/all))))))
