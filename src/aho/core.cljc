(ns aho.core)

(defn- trie-add-pattern
  [trie starting-point id [c & suffix :as s]]
  (let [cs (str c)
        new-trie
        (update-in
         trie (conj starting-point :children)
         (fn [children]
           (if (nil? suffix)
             (update children c merge {:value cs :id id})
             (update children c merge {:value cs}))))]
    (if (nil? suffix)
      new-trie
      (recur new-trie (into [] (concat starting-point [:children c])) id suffix))))

(defn build-trie
  [patterns]
  (let [empty-trie {:value ""}
        sorted-patterns (sort-by second patterns)]
    (reduce
     (fn [trie [id pattern]]
       (trie-add-pattern trie [] id pattern))
     empty-trie
     sorted-patterns)))

(defn- build-backlinks
  [trie]
  (let [atrie (atom (assoc trie :fail-edge []))
        queue (atom [])
        i (atom 0)]
    
    ;; Get all children from root (designated by their path from the root)
    (doseq [[c _] (:children @atrie)]
      (swap! atrie #(assoc-in % [:children c :fail-edge] []))
      (swap! queue conj [:children c]))

    (while (and (not-empty @queue) (< @i 20))
      ;; process queue
      (let [node-path (first @queue)]

        ;; Set suffixes
        (doseq [[c _] (get-in @atrie (concat node-path [:children]))]
          (let [tmp-path (atom (get-in @atrie (conj node-path :fail-edge)))]
            ;; Find longest suffix
            (while (and (nil? (get-in @atrie (concat @tmp-path [:children c])))
                        (not= @tmp-path []))
              (reset! tmp-path (into [] (get-in @atrie (conj @tmp-path :fail-edge)))))

            ;; Set suffix as failed edge
            (swap!
             atrie
             #(assoc-in
               % (concat node-path [:children c :fail-edge])
               (if (get-in % (concat @tmp-path [:children c]))
                 (into [] (concat @tmp-path [:children c]))
                 [])))

            ;; Add child to queue
            (swap! queue conj (into [] (concat node-path [:children c])))))
        
        ;; Set output edges
        (swap!
         atrie
         #(assoc-in
           % (conj node-path :output-edge)
           (if (get-in % (conj (into [] (get-in % (conj node-path :fail-edge))) :id))
             (get-in % (conj node-path :fail-edge))
             (get-in % (conj (into [] (get-in % (conj node-path :fail-edge))) :output-edge))))))
      
      (swap! queue #(->> % rest (into [])))
      (swap! i inc))

    @atrie))

(defn build-automaton
  [patterns]
  (-> patterns
      build-trie
      build-backlinks))

(defn- get-next-node
  [trie cur-node c]
  (if (or (= cur-node [])
          (get-in trie (concat cur-node [:children c])))
    (if (get-in trie (concat cur-node [:children c]))
      (into [] (concat cur-node [:children c]))
      cur-node)
    (recur trie (get-in trie (conj cur-node :fail-edge)) c)))

(defn- get-outputs
  [trie cur-node index acc]
  (if-let [next-node (get-in trie (conj cur-node :output-edge))]
    (recur
     trie next-node index
     (conj acc {:index index :pattern (get-in trie (conj next-node :id))}))
    acc))

(defn search
  [trie l]
  (->> l
       (reduce
        (fn [{:keys [index cur-node matches]} c]
          (let [node (get-next-node trie cur-node c)
                new-matches
                (into
                 (if-let [id (get-in trie (conj node :id))]
                   (conj matches {:index index :pattern id})
                   matches)
                 (get-outputs trie node index []))]
            {:index (inc index)
             :cur-node node
             :matches new-matches}))
        {:index 0
         :cur-node []
         :matches []})
       :matches))
