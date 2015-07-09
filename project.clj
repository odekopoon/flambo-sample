(defproject flambo-sample "0.1.0-SNAPSHOT"
  :description "Flambo sample"
  :url "https://github.com/hiroftp/flambo-sample"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [yieldbot/flambo "0.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]]
  :main flambo-sample.twitter_stream
  :profiles {:dev
             {:aot [flambo-sample.word_count
                    flambo-sample.twitter_stream]}
             :provided
             {:dependencies
              [[org.apache.spark/spark-core_2.10 "1.3.0"]
               [org.apache.spark/spark-streaming_2.10 "1.3.0"]
               [org.apache.spark/spark-streaming-kafka_2.10 "1.3.0"]
               [org.apache.spark/spark-streaming-flume_2.10 "1.3.0"]
               [org.apache.spark/spark-streaming-twitter_2.10 "1.3.0"]]}
             :uberjar
             {:aot :all}
             }
  :jvm-opts ["-Dspark.driver.allowMultipleContexts=true"])
