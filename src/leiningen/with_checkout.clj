(ns leiningen.with-checkout
  (require [leiningen.core.main :as main]
           (clojure.java [shell :as sh] [io :as io])
           [clojure.string :as s]))

(set! *warn-on-reflection* true)

(defn abort [fmt & args] (main/abort (apply format fmt args)))

(defn sh! [& args]
  (apply println "$" args)
  (let [p (-> (ProcessBuilder. ^"[Ljava.lang.String;" (into-array args))
              (.redirectErrorStream true)
              .start)]
    (io/copy (.getInputStream p) System/out)
    (let [res (.waitFor p)]
      (when-not (zero? res) (abort "Command failed with exit code %s: %s" res args))
      res)))

(defn with-checkout
  "Check out a revision from git and apply tasks on it"
  [project tag & args]
  (let [checkout-dir "target/lein-with-checkout"
        tag (if (= tag ":latest")
              (let [r (sh/sh "git" "describe" "--tags" "--abbrev=0")]
                (when (or (not (zero? (:exit r))) (s/blank? (:out r)))
                  (abort "Cannot determine the latest tag: %s" (str (:err r) (:out r))))
                (s/trim (:out r)))
              tag)]
    (sh! "mkdir" "-p" checkout-dir)
    (try
      (sh! "sh" "-c" (format "git archive %s | tar -xC %s" tag checkout-dir))
      (sh! "sh" "-c" (format "cd %s ; lein %s" checkout-dir (s/join " " args)))
      (finally (sh! "rm" "-rf" checkout-dir)))))
