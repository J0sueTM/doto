(ns doto.core
  (:require
   [doto.db]
   [doto.events]
   [re-frame.core :as rf]
   [reagent.dom.client :as rgdc]))

(enable-console-print!)

(defonce root-container
  (rgdc/create-root (js/document.getElementById "app")))

(defn temp-home []
  [:div
   [:h1 "Hello World!"]])

(defn mount []
  (rgdc/render root-container [temp-home]))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (mount))

(defn ^:export init []
  (rf/dispatch-sync [:initialize-db])
  (mount))
