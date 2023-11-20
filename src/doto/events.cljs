(ns doto.events
  (:require
   [cljs.spec.alpha :as s]
   [doto.db :as doto-db]
   [re-frame.core :as rf]))

(defn check-spec-and-throw [spec db]
  (when-not (s/valid? spec db)
    (throw (ex-info (str "spec check failed: "
                         (s/explain-str spec db))
                    {}))))

(def check-spec-interceptor
  (rf/after (partial check-spec-and-throw ::doto-db/db)))

(def ->local-storage-interceptor
  (rf/after doto-db/todos->local-storage))

(def todo-interceptors
  [check-spec-interceptor (rf/path :todos)
   ->local-storage-interceptor])

(defn get-next-id [todos]
  ((fnil inc 0) (last (keys todos))))

(rf/reg-event-fx
 :initialize-db
 [(rf/inject-cofx :local-storage-todos)
  check-spec-interceptor]
 (fn [{:keys [local-storage-todos]} _]
   {:db (assoc doto-db/db :todos local-storage-todos)}))

(rf/reg-event-db
 :set-showing-filter
 [check-spec-interceptor (rf/path :showing)]
 (fn [_ [_ new-filter-kw]]
   new-filter-kw))

(rf/reg-event-db
 :add-todo
 todo-interceptors
 (fn [todos [_ text]]
   (let [id (get-next-id todos)]
     (assoc todos id {:id id
                      :title text
                      :complete? false}))))

(rf/reg-event-db
 :toggle-todo-complete
 todo-interceptors
 (fn [todos [_ id]]
   (update-in todos [id :done] not)))

(rf/reg-event-db
 :save-todo
 todo-interceptors
 (fn [todos [_ id title]]
   (assoc todos [id :title] title)))

(rf/reg-event-db
 :delete-todo
 todo-interceptors
 (fn [todos [_ id]]
   (dissoc todos id)))
