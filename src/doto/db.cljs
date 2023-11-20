(ns doto.db
  (:require
   [cljs.reader]
   [cljs.spec.alpha :as s]
   [re-frame.core :as rf]))

(s/def ::id int?)
(s/def ::title string?)
(s/def ::complete? boolean?)
(s/def ::todo (s/keys :req-un [::id ::title ::complete?]))
(s/def ::todos (s/and
                (s/map-of ::id ::todo)
                #(instance? PersistentTreeMap %)))
(s/def ::showing #{:all
                   :active
                   :completed})
(s/def ::db (s/keys :req-un [::todos ::showing]))

(def db
  {:todos (sorted-map)
   :showing :all})

(defonce todos-key "todos")

(defn todos->local-storage [todos]
  (.setItem js/localStorage todos-key (str todos)))

(defn local-storage->todos []
  (let [raw-todos (some->> (.getItem js/localStorage todos-key)
                           (cljs.reader/read-string))]
    (into (sorted-map) raw-todos)))

(rf/reg-cofx
 :local-storage-todos
 (fn [cofx _]
   (assoc cofx :local-storage-todos (local-storage->todos))))
