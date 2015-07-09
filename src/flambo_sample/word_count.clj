(ns flambo-sample.word_count
  (:require [flambo.api :as f]
            [clojure.string :as str]))

(def sc (f/local-spark-context "word-count"))

(-> (f/text-file sc "sample.txt")
    (f/flat-map (f/fn [s] (str/split s #" +")))
    (f/map (f/fn [s] [s 1]))
    (f/reduce-by-key +)
    (f/collect)
    (clojure.pprint/pprint))
