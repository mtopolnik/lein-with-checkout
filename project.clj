(defproject com.ingemark/lein-with-checkout "0.1.1-SNAPSHOT"
  :description "Check out a revision from git and apply tasks on it"
  :url "https://github.com/Inge-mark/lein-with-checkout"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in :leiningen
  :lein-release {:deploy-via :clojars}

  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[com.ingemark/lein-release "2.1.1"]])
