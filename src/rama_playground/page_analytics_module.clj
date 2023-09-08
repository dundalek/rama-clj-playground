(ns rama-playground.page-analytics-module
  (:require
   [rama-playground.rama-helpers :as rh])
  (:import
   (com.rpl.rama Agg Depot Path PState RamaModule)
   (com.rpl.rama.test InProcessCluster LaunchConfig)))

;; Based on https://github.com/redplanetlabs/rama-examples/blob/master/src/main/java/rama/examples/tutorial/PageAnalyticsModule.java

(deftype PageAnalyticsModule []
  RamaModule
  (define [_ setup topologies]
    (.declareDepot setup "*depot" (Depot/random))
    (let [s (.stream topologies "s")]
      (.pstate s "$$pageViewCount" (PState/mapSchema String Long))
      (.pstate s "$$sessionHistory" (PState/mapSchema
                                     String
                                     (PState/listSchema clojure.lang.IPersistentMap)))

      (-> (.source s "*depot")
          (rh/out "*pageVisit")
          (.each (rh/function1 :session-id) "*pageVisit")
          (rh/out "*sessionId")
          (.each (rh/function1 :path) "*pageVisit")
          (rh/out "*path")
          (.compoundAgg "$$pageViewCount" (rh/compound-agg-map "*path" (Agg/count)))
          (.compoundAgg "$$sessionHistory" (rh/compound-agg-map "*sessionId" (Agg/list "*pageVisit")))))))

(defn -main []
  (with-open [cluster (InProcessCluster/create)]
    (.launchModule cluster (->PageAnalyticsModule) (LaunchConfig. 1 1))
    (let [module-name (.getName PageAnalyticsModule)
          depot (.clusterDepot cluster module-name "*depot")
          pvc (.clusterPState cluster module-name "$$pageViewCount")
          sh (.clusterPState cluster module-name "$$sessionHistory")]
      (.append depot {:session-id "abc123"
                      :path "/posts"
                      :duration 4200})
      (.append depot {:session-id "abc123"
                      :path "/users"
                      :duration 2400})

      (println "pageViewCount:" (.select pvc (Path/all)))
      (println "sessionHistory:" (.select sh (Path/all))))))
