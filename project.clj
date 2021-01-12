(defproject aho "0.3.0"
  :description "Aho Corasick implementation"
  :url "https://github.com/Tyruiop/aho"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :repositories [["releases" {:url "https://repo.clojars.org"
                              :creds :gpg}]]
  :repl-options {:init-ns aho.core})
