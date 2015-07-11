(ns flambo-sample.word_count
  (:require [flambo.api :as f]
            [flambo.tuple :as ft]
            [clojure.string :as str]))

(def sc (f/local-spark-context "word-count"))

(-> (f/text-file sc "sample.txt")
    ;JavaRDD -> JavaRDD
    (f/flat-map (f/fn [l] (str/split l #" +")))
    ;JavaRDD -> JavaPairRDD
    (f/map-to-pair (f/fn [s] (ft/tuple s 1)))
    ;JavaPairRDD -> JavaPairRDD
    (f/reduce-by-key +)
    ;JavaRDD or JavaPairRDD
    (f/collect)
    ;JavaRDD or JavaPairRDD
    (clojure.pprint/pprint))
