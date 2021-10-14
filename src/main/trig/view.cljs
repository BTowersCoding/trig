(ns trig.view
  (:require [trig.pythagoras :as pythagoras]
            [trig.law-of-sines :as los]
            [trig.triangle :as tri]
            [trig.sin :as sin :refer [points]]))

(defn app []
  [:div
   [tri/app]
   #_[:div [sin/points-input]
    [sin/render-fn @points]
    [:p]
    [sin/calc-graph]
    [sin/x-slider -10 10 0.01]
    [sin/y-slider -1 1 0.01]]
     ;[pythagoras/pythagorean-identity-sin "\\theta_1" "\\text{I}" 3 8]
   ]
  )