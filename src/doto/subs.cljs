(ns doto.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :showing
 (fn [db _]
   (:showing db)))

(rf/reg-sub
 :raw-todos
 (fn [db _]
   (:todos db)))

(rf/reg-sub
 :todos
 :<- [:raw-todos]
 (fn [raw-todos _]
   (vals raw-todos)))

(rf/reg-sub
 :visible-todos
 :<- [:todos]
 :<- [:showing]
 (fn [[todos showing] _]
   (let [filter-fn (case showing
                     :active (complement :complete?)
                     :complete? :complete?
                     :all identity)]
     (filter filter-fn todos))))

(rf/reg-sub
 :all-complete?
 :<- [:todos]
 (fn [todos _]
   (every? :complete? todos)))
