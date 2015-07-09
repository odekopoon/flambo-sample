(ns flambo-sample.word_count
  (:require [flambo.api :as f]
            [flambo.tuple :as ft]
            [clojure.string :as str]))

(def sc (f/local-spark-context "word-count"))

(-> (f/text-file sc "sample.txt")
    (f/flat-map (f/fn [l] (str/split l #" +")))
    (f/map-to-pair (f/fn [s] (ft/tuple s 1)))
    (f/reduce-by-key +)
    (f/collect)
    (clojure.pprint/pprint))
