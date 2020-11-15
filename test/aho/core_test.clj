(ns aho.core-test
  (:require [clojure.test :refer :all]
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
               {:index 2, :pattern 2}})))))
