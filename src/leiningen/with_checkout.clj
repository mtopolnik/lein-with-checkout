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

(defn with-checkout [project tag & args]
  (let [tag (if (= tag :latest) "`git tag | tail -1`" tag)
        checkout-dir "target/lein-with-checkout"]
    (sh! "mkdir" "-p" checkout-dir)
    (try
      (sh! "sh" "-c" (format "git archive %s | tar -xC %s" tag checkout-dir))
      (sh! "sh" "-c" (format "cd %s ; lein %s" checkout-dir (s/join " " args)))
      (finally (sh! "rm" "-rf" checkout-dir)))))
