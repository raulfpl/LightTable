(ns lt.objs.notifos
  (:require [lt.object :as object]
            [lt.objs.statusbar :as statusbar]
            [lt.objs.command :as cmd]
            [lt.util.js :refer [wait]]
            [crate.binding :refer [map-bound bound deref?]])
  (:require-macros [lt.macros :refer [behavior defui]]))

(def standard-timeout 10000)

(defn msg* [m & [opts]]
  (let [m (if (string? m)
            m
            (pr-str m))]
    (object/merge! statusbar/statusbar-loader (merge {:message m :class ""} opts))))

(declare cur-timeout)

(defn set-msg!
  ([msg]
   (msg* msg)
   (js/clearTimeout cur-timeout)
   (set! cur-timeout (wait standard-timeout #(msg* ""))))
  ([msg opts]
   (msg* msg opts)
   (js/clearTimeout cur-timeout)
   (set! cur-timeout (wait (or (:timeout opts)
                               standard-timeout) #(msg* "")))))

(defn working
  ([] (working nil))
  ([msg]
    (when msg
      (set-msg! msg))
    (statusbar/loader-inc)))

(defn done-working
  ([]
   (statusbar/loader-dec))
  ([msg]
   (set-msg! msg)
   (statusbar/loader-dec)))

(cmd/command {:command :reset-working
              :desc "Status Bar: Reset working indicator"
              :exec (fn []
                      (statusbar/loader-set)
                      )})
