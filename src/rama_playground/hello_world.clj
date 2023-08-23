(ns rama-playground.hello-world
  (:import
   (com.rpl.rama Depot RamaModule)
   (com.rpl.rama.ops Ops)
   (com.rpl.rama.test InProcessCluster LaunchConfig)))

;; https://github.com/redplanetlabs/rama-examples/blob/master/src/main/java/rama/examples/tutorial/HelloWorldModule.java

(deftype HelloWorldModule []
  RamaModule
  (define [_ setup topologies]
    (.declareDepot setup "*depot" (Depot/random))
    (let [s (.stream topologies "s")]
      (-> s
          (.source "*depot")
          (.out (into-array String ["*data"]))
          (.each Ops/PRINTLN "*data")))))

(defn -main []
  (with-open [cluster (InProcessCluster/create)]
    (.launchModule cluster (->HelloWorldModule) (LaunchConfig. 1 1))
    (let [module-name (.getName HelloWorldModule)
          depot (.clusterDepot cluster module-name "*depot")]
      (.append depot "Hello, world!!"))))

(comment
  ;; Does not seem to work in REPL, getting `Failed to generate constants class` exception
  (-main))
