(ns aho.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [aho.core :refer :all]))

(def autom-struct
  {:value "",
   :fail-edge [],
   :children
   {\a
    {:value "a",
     :fail-edge [],
     :output-edge nil,
     :children
     {\a
      {:value "a",
       :fail-edge [:children \a],
       :output-edge nil,
       :children
       {\b
        {:value "b",
         :id 2,
         :fail-edge [:children \a :children \b],
         :output-edge [:children \a :children \b],
         :children
         {\a
          {:value "a",
           :id 3,
           :fail-edge [:children \a],
           :output-edge nil}}}}},
      \b {:value "b", :id 1, :fail-edge [], :output-edge nil}}}}})

(def autom (build-automaton [[1 "ab"] [2 "aab"] [3 "aaba"]]))

(deftest build-automaton-test
  (testing "Proper build automaton"
    (is (= autom autom-struct))))

(deftest search-test
  (testing "Hit correct matches"
    (let [hits (search autom "aababa")]
      (is (= (into #{} hits)
             #{{:index 4, :pattern 1} {:index 2, :pattern 1} {:index 3, :pattern 3}
               {:index 2, :pattern 2}}))))
  (testing "Working on very long text"
    (let [txt (str/join "" (repeatedly 100000 (fn [] (if (= 0 (rand-int 2)) "a" "b"))))
          start (System/nanoTime)
          _ (search (build-automaton [[:aa "aa"] [:bb "bb"] [:aab "aab"]]) txt)
          end (System/nanoTime)]
      (is (> 1 (/ (- end start) 1000000000))))))



