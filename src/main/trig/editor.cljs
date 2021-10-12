(ns trig.editor
  (:require ["@codemirror/fold" :as fold]
            ["@codemirror/closebrackets" :refer [closeBrackets]]
            ["@codemirror/gutter" :refer [lineNumbers]]
            ["@codemirror/highlight" :as highlight]
            ["@codemirror/history" :refer [history historyKeymap]]
            ["@codemirror/state" :refer [EditorState]]
            ["@codemirror/view" :as view :refer [EditorView]]
            [trig.sci :as sci]
            [applied-science.js-interop :as j]
            [nextjournal.clojure-mode.extensions.close-brackets :as close-brackets]
            [nextjournal.clojure-mode :as cm-clj]
            [nextjournal.clojure-mode.live-grammar :as live-grammar]
            [nextjournal.clojure-mode.test-utils :as test-utils]
            [reagent.core :as r]))


(def theme
  (.theme
   EditorView
   (j/lit {".cm-content" {:white-space "pre-wrap", :padding "10px 0"}
           "&.cm-focused" {:outline "none"}
           ".cm-line" {:padding "0 10px"
                       :line-height "1.6"
                       :font-size "16px"
                       :font-family "var(--code-font)"}
           ".cm-matchingBracket" {:border-bottom "1px solid var(--teal-color)"
                                  :color "inherit"}
           ".cm-gutters" {:background "transparent", :border "none"}
           ".cm-gutterElement" {:margin-left "5px"}
           ;; only show cursor when focused
           ".cm-cursor" {:visibility "hidden"}
           "&.cm-focused .cm-cursor" {:visibility "visible"}})))

(defonce extensions
  #js
   [theme
    (history)
    highlight/defaultHighlightStyle
    (view/drawSelection)
    ;(lineNumbers)
    ;(fold/foldGutter)
    (.. EditorState -allowMultipleSelections (of true))
    (if false
     ;; use live-reloading grammar
      #js [(cm-clj/syntax live-grammar/parser)
           (.slice cm-clj/default-extensions 1)]
      cm-clj/default-extensions)
    (.of view/keymap cm-clj/complete-keymap)
    (.of view/keymap historyKeymap)])


(defn editor
  [source !view {:keys [eval?]}]
  (r/with-let
    [last-result (when eval? (r/atom (sci/eval-string source)))
     mount! (fn [el]
              (when el
                (reset! !view (new EditorView
                                   (j/obj :state (test-utils/make-state
                                                  (cond-> #js [extensions]
                                                    eval? (.concat
                                                           #js
                                                            [(sci/extension
                                                              {:modifier "Alt"
                                                               :on-result
                                                               (fn [result]
                                                                 (reset! last-result result))})]))
                                                  source)


                                          :parent el)))))]
    [:div
     {:ref mount!
      :style {:background-color "white"
              :width 200}}]
    (finally (j/call @!view :destroy))))
