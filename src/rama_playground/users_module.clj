(ns rama-playground.users-module
  (:require
   [rama-playground.rama-helpers :as rh])
  (:import
   (com.rpl.rama Depot Path PState RamaModule)
   (com.rpl.rama.ops Ops RamaFunction1)
   (com.rpl.rama.test InProcessCluster LaunchConfig)))

;; Inspired by https://github.com/redplanetlabs/rama-examples/blob/master/src/main/java/rama/examples/ramaspace/RamaSpaceModule.java

(defn declare-users-topology [topologies]
  (let [users (.stream topologies "users")]
    (.pstate users "$$profiles"
             (PState/mapSchema String clojure.lang.IPersistentMap))

    (-> (.source users "*userRegistrationsDepot")
        (rh/out "*registration")
        ;; TODO: try macro ala extractJavaFields
        (rh/each :userId "*registration")
        (rh/out "*userId")
        (rh/each :displayName "*registration")
        (rh/out "*displayName")
        (rh/each #(System/currentTimeMillis))
        (rh/out "*joinedAtMillis")
        (.localTransform "$$profiles"
                         (-> (rh/path-key "*userId")
                             (.filterPred Ops/IS_NULL)
                             (.multiPath (into-array
                                          Path
                                          [(-> (rh/path-key "displayName") (.termVal "*displayName"))
                                           (-> (rh/path-key "joinedAtMillis") (.termVal "*joinedAtMillis"))])))))

    (-> (.source users "*profileEditsDepot")
        (rh/out "*edit")
        ;; TODO: try macro ala extractJavaFields
        (rh/each :userId "*edit")
        (rh/out "*userId")
        (rh/each :field "*edit")
        (rh/out "*field")
        (rh/each :value "*edit")
        (rh/out "*value")
        (.localTransform "$$profiles" (-> (rh/path-key "*userId" "*field") (.termVal "*value"))))))

(deftype UserIdExtractor []
  RamaFunction1
  (invoke [_ data]
    (:userId data)))

(deftype UsersModule []
  RamaModule
  (define [_ setup topologies]
    (.declareDepot setup "*userRegistrationsDepot" (Depot/hashBy UserIdExtractor))
    (.declareDepot setup "*profileEditsDepot" (Depot/hashBy UserIdExtractor))

    (declare-users-topology topologies)))

(defn -main []
  (with-open [cluster (InProcessCluster/create)]
    (.launchModule cluster (->UsersModule) (LaunchConfig. 1 1))
    (let [module-name (.getName UsersModule)
          userRegistrationsDepot (.clusterDepot cluster module-name "*userRegistrationsDepot")
          profileEditsDepot (.clusterDepot cluster module-name "*profileEditsDepot")
          profiles (.clusterPState cluster module-name "$$profiles")
          append-user-registration (fn [user-id display-name]
                                     (.append userRegistrationsDepot {:userId user-id :displayName display-name}))
          append-profile-edit (fn [user-id field value]
                                (.append profileEditsDepot {:userId user-id :field field :value value}))]

      (append-user-registration "alice" "Alice")
      (append-user-registration "alice" "Alice2")

      (println (.selectOne profiles (rh/path-key "alice")))

      (append-profile-edit "alice" "displayName" "Alice Alice")
      (println (.selectOne profiles (rh/path-key "alice"))))))
