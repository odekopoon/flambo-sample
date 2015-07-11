(ns flambo-sample.twitter_stream
  (:require  [flambo.api :as f]
             [flambo.tuple :as ft]
             [flambo.streaming :as fs]
             [flambo.conf :as conf])
  (:import   [org.apache.spark.streaming.twitter TwitterUtils])
  (:gen-class))

(defn streaming-context []
  (let [sc (-> (conf/spark-conf)
               (conf/master "local[*]")
               (conf/app-name "flambo-stream-sample"))]
    (fs/streaming-context sc 10000)))

(defonce ssc (streaming-context))

(fs/checkpoint ssc "/tmp/flambo-stream-sample")

(defonce stream (TwitterUtils/createStream ssc))

(defn get-hashtags [tweet]
  (re-seq #"#[Ａ-Ｚａ-ｚA-Za-z一-鿆0-9０-９ぁ-ヶｦ-ﾟー]+" tweet))

(defonce hashtag-count
  (-> (fs/flat-map stream (fn[tweet] (filter #(< 0 (count %)) (get-hashtags (.getText tweet)))))
      ;JavaRDD -> JavaPairRDD
      (fs/map-to-pair #(ft/tuple % 1))
      ;JavaPairRDD -> JavaPairRDD
      (fs/reduce-by-key-and-window + 30000 10000)
      ;JavaPairRDD -> JavaPairRDD
      ;(fs/map-to-pair (ft/key-val-fn (f/fn [tag cnt] (ft/tuple cnt tag))))
      ;JavaPairRDD -> JavaRDD
      (fs/map (ft/key-val-fn (f/fn [tag cnt] [cnt tag])))
      ;JavaRDD -> JavaRDD (not streaming)
      ;(fs/transform (f/fn [s]
      ;    (clojure.pprint/pprint s)
      ;    s))
      ;JavaRDD -> JavaRDD (not streaming)
      (fs/transform (f/fn [s]
          (-> s
              ;JavaRDD -> JavaPairRDD
              (f/map-to-pair (f/fn [[cnt tag]] (ft/tuple cnt tag)))
              ;JavaPairRDD -> JavaPairRDD
              (f/sort-by-key >)
              ;JavaPairRDD -> JavaRDD
              (fs/map (ft/key-val-fn (f/fn [cnt tag] [cnt tag])))
              )
          ))
      ))
(defn -main []
  (fs/print hashtag-count)
  (.start ssc)
  (.awaitTermination ssc))